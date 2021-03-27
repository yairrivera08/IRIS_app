package com.iris.fotoapparatapi

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

class ImageUtilities(val bmp: Bitmap) {
    var width: Int = bmp.width
    var height: Int = bmp.height

    fun getA(): Bitmap{
        val bmpA = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        for(i in 0 until width){
            for(j in 0 until height){
                bmpA.setPixel(i,j,bmp.getColor(i,j).red().toInt())
            }
        }
        return bmpA
    }
    fun getR(): Bitmap{
        var bmpR = bmp

        //for(i in 0 until width){
          //  for(j in 0 until height){
                //Log.i("ARGB"," [$i,$j]=${bmp.getColor(i,j).toArgb()}")
                //Log.i("R CHANNEL"," [$i,$j]=${bmp.getColor(i,j).red()}")
                bmpR.eraseColor(Color.GREEN)
                bmpR.eraseColor(Color.BLUE)
                bmpR.eraseColor(Color.TRANSPARENT)

           // }
        //}
        return bmpR
    }
    fun getG(): Bitmap{
        val bmpG = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        for(i in 0 until width){
            for(j in 0 until height){
                bmpG.setPixel(i,j,bmp.getColor(i,j).green().toInt())
            }
        }
        return bmpG
    }
    fun getB(): Bitmap{
        val bmpB = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        for(i in 0 until width){
            for(j in 0 until height){
                bmpB.setPixel(i,j,bmp.getColor(i,j).blue().toInt())
            }
        }
        return bmpB
    }
}