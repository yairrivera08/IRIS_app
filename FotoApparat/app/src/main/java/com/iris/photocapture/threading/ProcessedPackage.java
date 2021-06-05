package com.iris.photocapture.threading;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.iris.photocapture.MyApp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ProcessedPackage implements Serializable {
    private int id;
    private String name;
    //private ArrayList<Uri> imgUri = new ArrayList<>(9);
    private ArrayList<String> imgRPath = new ArrayList<>(9);
    //private final Context mCtx;
    private int mThreshold;
    private long mTimeTaken;
    private String mSessionName;
    private String mRelativePath;

    public ProcessedPackage(int id, String name, String sesion) {
        this.id = id;
        this.name = name;
        //Log.d("PROCESSED_PACKAGE","ID["+id+"] NOMBRE DE SESION "+sesion);
        mRelativePath = "DCIM/IRIS3D/" + sesion + "/";
        mSessionName=sesion;
        //Log.d("PROCESSED_PACKAGE","ID["+id+"] RELATIVE PATH "+mRelativePath);
        //this.mCtx = ctx;
        /*for(int i = 0;i<8;i++){
            imgUri.add(Uri.EMPTY);
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveImage(Bitmap bitmap, @NonNull String scanName, int x) {
        boolean saved;
        OutputStream fos = null;

        try {
            ContentResolver resolver = MyApp.getmAppContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            String fileName = name + "_" + scanName;
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, mRelativePath);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            //imgUri.add(x, imageUri);
            imgRPath.add(getPath(MyApp.getmAppContext(),imageUri));
            fos = resolver.openOutputStream(imageUri);
            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
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

    /*public ArrayList<Uri> getImgUri() {
        return imgUri;
    }*/

    public ArrayList<String> getImgRPath() {
        return imgRPath;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setOriginal(Bitmap bitmap) {
        saveImage(bitmap,"Original", 0);
        setThumbnail(bitmap);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setThumbnail(Bitmap bitmap) {
        int width = bitmap.getWidth() / 6;
        int height = bitmap.getHeight() / 6;
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, height);
        saveImage(bitmap,"Thumbnail", 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setAlphaChannel(Bitmap alphaChannel) {
        if (alphaChannel != null) {
            saveImage(alphaChannel,"ALPHA_CHANNEL", 2);
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setRedChannel(Bitmap redChannel) {
        if (redChannel != null) {
            saveImage(redChannel,"RED_CHANNEL", 3);
            return true;
        } else {
            return false;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setGreenChannel(Bitmap greenChannel) {
        if (greenChannel != null) {

            saveImage(greenChannel,"GREEN_CHANNEL", 4);
            return true;
        } else {
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setBlueChannel(Bitmap blueChannel) {
        if (blueChannel != null) {

            saveImage(blueChannel,"BLUE_CHANNEL", 5);
            return true;
        } else {
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setGrayScale(Bitmap grayScale) {
        if (grayScale != null) {
            saveImage(grayScale,"GrayScale", 6);
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setBinary(Bitmap[] binary) {
        if (binary != null) {

            saveImage(binary[0],"MaskedA", 7);
            saveImage(binary[1],"MaskedB", 8);
            return true;
        } else {
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean setMasked(Bitmap masked) {
        if (masked != null) {
            saveImage(masked,"Masked Image", 8);
            return true;
        } else {
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

    public void setSessionName(String nombre) {
        this.mSessionName = nombre;
        mRelativePath = "DCIM/IRIS3D/" + mSessionName + "/";
        //Log.d("PROCESSED_PACKAGE", "NOMBRE DE SESION=>" + mSessionName);
    }

    public String getmSessionName() {
        return mSessionName;
    }

    public String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

}
