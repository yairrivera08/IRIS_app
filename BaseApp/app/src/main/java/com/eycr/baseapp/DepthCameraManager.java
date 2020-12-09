package com.eycr.baseapp;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.util.Log;
import android.util.SizeF;

import java.util.Set;

public class DepthCameraManager {
    private static final String TAG = DepthCameraManager.class.getSimpleName();

    private static int FPS_MIN = 15;
    private static int FPS_MAX = 30;

    private Context context;
    private CameraManager cameraManager;
    private ImageReader previewReader;
    private CaptureRequest.Builder previewBuilder;
    //private DepthFrameAvailableListener imageAvailableListener;
    DepthCameraManager(Context context){
        //TODO: move camera management to this class from MainActivity
        this.context = context;
        cameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
    }

    String getMainDepthCamera(){
        try {
            Log.e(TAG,"getMainDepthCamera");
            for (String camera : cameraManager.getCameraIdList()) {
                //Log.e(TAG,"within for");
                CameraCharacteristics chars = cameraManager.getCameraCharacteristics(camera);
                final int[] capabilities = chars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                boolean facingBack = chars.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK;
                boolean depthCapable = false;
                for (int capability : capabilities) {
                    boolean capable = capability == CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT;
                    depthCapable = depthCapable || capable;
                }
                if (depthCapable && facingBack) {
                    // Note that the sensor size is much larger than the available capture size
                    SizeF sensorSize = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                    Log.e(TAG, "Sensor size: " + sensorSize);

                    // Since sensor size doesn't actually match capture size and because it is
                    // reporting an extremely wide aspect ratio, this FoV is bogus
                    float[] focalLengths = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    if (focalLengths.length > 0) {
                        float focalLength = focalLengths[0];
                        double fov = 2 * Math.atan(sensorSize.getWidth() / (2 * focalLength));
                        Log.e(TAG, "Calculated FoV: " + fov);
                    }
                    Log.e(TAG,"DEPTH CAM IS ["+camera+"]");
                    return camera;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Could not initialize Camera Cache");
            e.printStackTrace();
        }
        return null;
    }
}
