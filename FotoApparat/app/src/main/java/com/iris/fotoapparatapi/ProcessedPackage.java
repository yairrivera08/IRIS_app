package com.iris.fotoapparatapi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;

public class ProcessedPackage {
    private int id;
    private String name;
    private Context mCtx;
    private Bitmap original;
    private Bitmap redChannel;
    private Bitmap greenChannel;
    private Bitmap blueChannel;
    private Bitmap alphaChannel;
    private Bitmap masked;
    private Bitmap binary;
    private Bitmap grayScale;
    private int mThreshold;
    private long mTimeTaken;

    public ProcessedPackage(int id, String name, Context ctx) {
        this.id = id;
        this.name = name;
        this.mCtx = ctx;
    }

    private void saveImage(Bitmap bitmap, @NonNull String scanName){
        boolean saved;
        OutputStream fos = null;

        try {
            ContentResolver resolver = mCtx.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name+"_"+scanName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/IRIS 3D/");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean setRedChannel(Bitmap redChannel) {
        if(redChannel != null) {
            this.redChannel = redChannel;
            if(this.redChannel != null){
                saveImage(this.redChannel,"RED_CHANNEL");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }

    public Boolean setGreenChannel(Bitmap greenChannel) {
        if(greenChannel != null) {
            this.greenChannel = greenChannel;
            if(this.greenChannel != null){
                saveImage(this.greenChannel,"GREEN_CHANNEL");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public Boolean setBlueChannel(Bitmap blueChannel) {
        if(blueChannel != null) {
            this.blueChannel = blueChannel;
            if(this.blueChannel != null){
                saveImage(this.blueChannel,"BLUE_CHANNEL");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public Boolean setAlphaChannel(Bitmap alphaChannel) {
        if(alphaChannel != null) {
            this.alphaChannel = alphaChannel;
            if(this.alphaChannel != null){
                saveImage(this.alphaChannel,"ALPHA_CHANNEL");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public Bitmap getMasked() {
        return masked;
    }

    public void setMasked(Bitmap masked) {
        this.masked = masked;
    }


    public Boolean setGrayScale(Bitmap grayScale) {
        if(grayScale != null) {
            this.grayScale = grayScale;
            if(this.grayScale != null){
                saveImage(this.grayScale,"GrayScale");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public Boolean setBinary(Bitmap binary){
        if(binary != null) {
            this.binary = binary;
            if(this.binary != null){
                saveImage(this.binary,"Binarized");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public int getmThreshold() {
        return mThreshold;
    }

    public void setmThreshold(int mThreshold) {
        this.mThreshold = mThreshold;
    }

    public long getTimeTaken() {
        return mTimeTaken;
    }

    public void setTimeTaken(long mTimeTaken) {
        this.mTimeTaken = mTimeTaken;
    }
}
