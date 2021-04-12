package com.iris.fotoapparatapi

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    val permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)
    var fotoapparat: Fotoapparat? = null
    var fileName = "AssemblyRequired"
    val sd = Environment.getExternalStorageDirectory()
    val dest = File(sd,fileName)
    var fotoapparatState : FotoapparatState? = null
    var cameraStatus : CameraState? = null
    var flashState: FlashState? = null
    val parentJob = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    var bmpChannel : BitmapUtilities ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createFotoapparat()

        cameraStatus = CameraState.BACK
        flashState = FlashState.OFF
        fotoapparatState = FotoapparatState.OFF

        fab_camera.setOnClickListener { takePhoto() }
        fab_flash.setOnClickListener { changeFlashState() }
        fab_switch_camera.setOnClickListener { switchCamera() }

    }

    private fun Splicer(channel: Int): Deferred<Bitmap> =
        coroutineScope.async(Dispatchers.Main){
            var bmp = Bitmap.createBitmap(bmpChannel!!.getBitmap().width,bmpChannel!!.getBitmap().height,Bitmap.Config.ARGB_8888)
            when(channel){
                0 -> if (bmpChannel != null) {
                    bmp =  bmpChannel!!.spliceR()
                }
                1 -> if (bmpChannel != null) {
                    bmp =  bmpChannel!!.spliceG()
                }
                2 -> if (bmpChannel != null) {
                    bmp =  bmpChannel!!.spliceB()
                }
                3 -> if (bmpChannel != null) {
                    bmp =  bmpChannel!!.spliceA()
                }
            }
            return@async bmp

        }

    private fun takePhoto(){

        if(hasNoPermissions()){
            requestPermission()
        }else{
            /*fotoapparat
                ?.takePicture()
                ?.saveToFile(dest)*/
            val photoOg = fotoapparat?.takePicture()
            photoOg?.toBitmap()?.whenAvailable {
                bitmapPhoto ->
                    if(bitmapPhoto != null){
                        //val uri = bmpToFile(bitmapPhoto.bitmap)
                        val name:String = UUID.randomUUID().toString()
                        saveImage(bitmapPhoto.bitmap,name+"Assembly")
                        //saveImage(bmpChannel.getA(),name+"AssemblyAlpha")
                        try{
                            /*Llamamos nuestras utilidades para obtener los 4 canales*/
                            val exeTime = measureTimeMillis {
                                coroutineScope.launch(Dispatchers.Main) {
                                    bmpChannel = BitmapUtilities(bitmapPhoto.bitmap)
                                    val red = Splicer(0).await()
                                    val green = Splicer(1).await()
                                    val blue = Splicer(2).await()
                                    val alpha = Splicer(3).await()
                                    saveImage(red, name + "AssemblyRed")
                                    saveImage(green, name + "AssemblyGreen")
                                    saveImage(blue, name + "AssemblyBlue")
                                    saveImage(alpha, name + "AssemblyAlpha")

                                }
                            }
                            Log.i("Exec Time -->", "$exeTime")
                            /*Log.i("Splice Green","Begin")

                            saveImage(bmpChannel.spliceG(),name+"AssemblyGreen")
                            Log.i("Splice Green","End")
                            Log.i("Splice Blue","Begin")
                            saveImage(bmpChannel.spliceB(), name + "AssemblyBlue")
                            Log.i("Splice Blue","End")
                            Log.i("Splice Alpha","Begin")
                            saveImage(bmpChannel.spliceA(), name + "AssemblyAlpha")
                            Log.i("Splice Alpha","End")*/
                        }catch (e:IOException){
                            e.printStackTrace()
                        }

                        //saveImage(bmpChannel.getG(),name+"AssemblyGreen")
                        //saveImage(bmpChannel.getB(),name+"AssemblyBlue")
                        //toast("Bitmap Guardado en $uri")
                    }
            }
        }
    }

    private fun bmpToFile(bmp : Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream:OutputStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
    // Method to save an image to gallery and return uri
    private fun saveImage(bmp : Bitmap, title: String){

        // Get the bitmap from drawable object
        val bitmap = bmp.rotate(90f)

        // Save image to gallery
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            title,
            "Image of $title"
        )

        /* Parse the gallery image url to uri
        return Uri.parse(savedImageURL)*/
    }

    private fun switchCamera(){
        fotoapparat?.switchTo(
            lensPosition = if(cameraStatus == CameraState.BACK) front() else back(),
            cameraConfiguration = CameraConfiguration()
        )
        if(cameraStatus == CameraState.BACK) cameraStatus = CameraState.FRONT
        else cameraStatus = CameraState.BACK
    }

    private fun changeFlashState(){
        fotoapparat?.updateConfiguration(
            CameraConfiguration(
                flashMode = if(flashState == FlashState.TORCH) off() else torch()
            )
        )
        if(flashState == FlashState.TORCH) flashState = FlashState.OFF
        else flashState = FlashState.TORCH
    }

    private fun createFotoapparat(){
        val cameraView = findViewById<CameraView>(R.id.camera_view)

        fotoapparat = Fotoapparat(
            context = this,
            view = cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = back(),
            logger = loggers(
                logcat()
            ),
            cameraErrorCallback = {error ->
                println("Errors in camera: $error")
            }
        )
    }

    override fun onStart() {
        super.onStart()
        if(hasNoPermissions()){
            requestPermission()
        }else{
            fotoapparat?.start()
            fotoapparatState = FotoapparatState.ON
        }
    }

    override fun onStop(){
        super.onStop()
        fotoapparat?.stop()
        fotoapparatState = FotoapparatState.OFF
    }

    override fun onResume() {
        super.onResume()
        if(!hasNoPermissions() && fotoapparatState == FotoapparatState.OFF){
            val intent = Intent(baseContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hasNoPermissions():Boolean{
        return ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(this,permissions,0)
    }

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    enum class CameraState{
        FRONT, BACK
    }

    enum class FlashState{
        TORCH, OFF
    }

    enum class FotoapparatState{
        ON, OFF
    }
}