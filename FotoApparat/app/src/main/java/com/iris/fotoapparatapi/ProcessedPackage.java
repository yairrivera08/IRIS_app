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
    private Bitmap mask;
    private Bitmap binary;
    private Bitmap greyScale;
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

    public Bitmap getRedChannel() {
        return redChannel;
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

    public Bitmap getGreenChannel() {
        return greenChannel;
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

    public Bitmap getBlueChannel() {
        return blueChannel;
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

    public Bitmap getAlphaChannel() {
        return alphaChannel;
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

    public Bitmap getMask() {
        return mask;
    }

    public void setMask(Bitmap mask) {
        this.mask = mask;
    }

    public Bitmap getBinary() {
        return binary;
    }

    public void setBinary(Bitmap binary) {
        this.binary = binary;
    }

    public Bitmap getGreyScale() {
        return greyScale;
    }

    public void setGreyScale(Bitmap greyScale) {
        this.greyScale = greyScale;
    }

    public long getTimeTaken() {
        return mTimeTaken;
    }

    public void setTimeTaken(long mTimeTaken) {
        this.mTimeTaken = mTimeTaken;
    }
}
