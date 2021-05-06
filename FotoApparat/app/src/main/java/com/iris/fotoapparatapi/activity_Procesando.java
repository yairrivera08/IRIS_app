package com.iris.fotoapparatapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class activity_Procesando extends AppCompatActivity {

    private ArrayList<String> bmps = new ArrayList<>();
    private Context ctx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__procesando);
        ctx = getApplicationContext();
        //Obtener bitmaps
        bmps = (ArrayList<String>) getIntent().getSerializableExtra("bitmaps");
        contarBmps();
    }
    private Bitmap recuperarBitmap(String name){
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(ctx.openFileInput(name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp;
    }
    private void contarBmps(){
        if(bmps.size()!=0) {

            Log.d("ACTIVITY_PROCESANDO","Se recibieron "+bmps.size()+" bitmaps");
            for (int i = 0; i < bmps.size(); i++) {
                Log.d("ACTIVITY_PROCESANDO", "Nombre del Bitmap["+i+"]="+bmps.get(i));
                Bitmap x = recuperarBitmap(bmps.get(i));
                if(x != null){
                   Log.d("ACTIVITY_PROCESANDO", "TAMAÑO DEL BITMAP["+i+"]"+x.getByteCount());
                }
            }
        }else{
            Log.d("ACTIVITY_PROCESANDO","No se recibieron bitmaps");
        }
    }
}