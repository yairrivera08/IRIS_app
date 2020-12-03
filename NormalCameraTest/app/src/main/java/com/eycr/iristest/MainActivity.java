package com.eycr.iristest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.hardware.camera2.*;

public class MainActivity extends AppCompatActivity {

    TextView caracteristicas = (TextView) findViewById(R.id.caracteristicas);
    String loquetiene = "";
    private static Context currentcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.currentcontext = getApplicationContext();
        checkCameraHardware(currentcontext);
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            loquetiene.concat(" Tiene camara");
            return true;
        } else {
            // no camera on this device
            loquetiene.concat(" NO tiene camara");
            return false;
        }
    }
}