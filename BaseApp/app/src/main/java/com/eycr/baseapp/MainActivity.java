package com.eycr.baseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eycr.baseapp.DepthCameraManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private DepthCameraManager depthCamMg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView =(TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton =(Button) findViewById(R.id.buttonCamera);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        Button ma_Button = (Button) findViewById(R.id.buttonPrincipal);
        ma_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ma_Text = (TextView) findViewById(R.id.textoPrincipal);
                ma_Text.setText("He sido presionado");
            }
        });

        depthCamMg = new DepthCameraManager(MainActivity.this);


    }
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            //we open camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            //Change image size from sensor to textureViewSize
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //Gets called once we open the camera
            Log.e(TAG,"onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            //IMPORTANT! always close the camera when not in use
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(MainActivity.this,"Saved: "+file,Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };
    protected void startBackgroundThread(){
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread(){
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    protected void takePicture(){
        if(null == cameraDevice){
            Log.e(TAG,"cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if(characteristics != null){
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.DEPTH16);
            }
            int width = 640;
            int height = 480;
            if(jpegSizes != null && 0 < jpegSizes.length){
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.DEPTH16,1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //Now we set the orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getRootDirectory()+"/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try{
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }finally {
                        if(image != null){
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException{
                    OutputStream output = null;
                    try{
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    }finally {
                        if(null != output){
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this,"Saved"+file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try{
                        session.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                    }catch(CameraAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mBackgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    protected void createCameraPreview(){
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //check if camera was closed
                    if(null == cameraDevice){
                        return;
                    }
                    //Start preview if session is ready
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(MainActivity.this,"Configuration changed",Toast.LENGTH_SHORT).show();
                }
            },null);
        }catch(CameraAccessException e){
            e.printStackTrace();
        }
    }
    private void openCamera(){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG,"Is Camera open?");
        try{
            cameraId = manager.getCameraIdList()[4];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            /*for(int i = 0; i < characteristics.getKeys().size();i++){
                Log.e(TAG, "CAMERA ["+cameraId+"] Characteristics "+ String.valueOf(characteristics.getKeys().get(i)));
            }*/
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] mapsizes = map.getOutputSizes(ImageFormat.DEPTH_JPEG);

            for(int i=0;i<mapsizes.length;i++){
                Log.e(TAG,"Map sizes "+mapsizes[i]);
            }

            //imageDimension = mapsizes[0];
            //Ask for camera and storage permission
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);
        }catch(CameraAccessException e){
            for(int i = 0; i < e.getStackTrace().length; i++){
                System.out.println(e.getStackTrace()[i].toString());
            }
        }
        Log.e(TAG,"openCamera");
    }
    protected void updatePreview(){
        if(null == cameraDevice){
            Log.e(TAG,"updatePreview error");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    private void closeCamera(){
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == REQUEST_CAMERA_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                //close de app
                Toast.makeText(MainActivity.this,"Can't use the app without permissions",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.e(TAG,"onResume");
        startBackgroundThread();
        if(textureView.isAvailable()){
            openCamera();
        }else{
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause(){
        Log.e(TAG,"onPause");
        stopBackgroundThread();
        super.onPause();
    }
}