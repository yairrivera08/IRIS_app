package com.iris.photocapture.threading;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.iris.photocapture.utilities.BitmapUtilities;

public class ImageWrapper {

    private boolean mFinished = false;
    private boolean mOrig = false;
    private boolean mRed = false;
    private boolean mGreen = false;
    private boolean mBlue = false;
    private boolean mAlpha= false;
    private boolean mGray= false;
    private boolean mBinary= false;
    private boolean mMasked= false;
    private ProcessedPackage mProcessedPack;
    private BitmapUtilities bmpUtils;
    private int mFinishedChannels;
    private String mSessionName;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ImageWrapper(Bitmap bitmap, String name, int index, Context ctx, String sesion) {
        mFinishedChannels = 0;
        bmpUtils = new BitmapUtilities(bitmap);
        //mSessionName = sesion;
        //Log.d("IMAGE_WRAPPER","NOMBRE DE SESION"+sesion);
        mProcessedPack = new ProcessedPackage(index,name,sesion);
        //setSession();
        setOriginal();
        //setAlphaChannel();
        setRedChannel();
        //setGreenChannel();
        //setBlueChannel();
        setGrayScale();
        setBinarized();
        //setMasked();
        //checkFinished();
    }

    private void checkFinished() {
        while(!mFinished){
            if(mFinishedChannels == 4){
                //Log.d("IMAGEWRAPPER","HILO ["+Thread.currentThread()+"] mFinishedChannels="+mFinishedChannels);
                mFinished = true;
            }
        }
    }

    private void setSession(){
        //Log.d("IMAGE_WRAPPER","NOMBRE DE SESION=>"+mSessionName);
        mProcessedPack.setSessionName(mSessionName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setOriginal() {
        if(mProcessedPack.setOriginal(bmpUtils.getBitmap())){
            mFinishedChannels++;
            mOrig = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setRedChannel() {
        if(mProcessedPack.setRedChannel(bmpUtils.spliceR())){
            mFinishedChannels++;
            mRed = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setGreenChannel() {
        if(mProcessedPack.setGreenChannel(bmpUtils.spliceG())){
            mFinishedChannels++;
            mGreen = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setBlueChannel() {
        if(mProcessedPack.setBlueChannel(bmpUtils.spliceB())){
            mFinishedChannels++;
            mBlue = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setAlphaChannel() {
        if(mProcessedPack.setAlphaChannel(bmpUtils.spliceA())){
            mFinishedChannels++;
            mAlpha = true;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setGrayScale() {
        if(mProcessedPack.setGrayScale(bmpUtils.getGrayscale())){
            mFinishedChannels++;
            mGray = true;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setBinarized() {
        bmpUtils.createThreshold();
        mProcessedPack.setmThreshold(bmpUtils.getThreshold());
        if(mProcessedPack.setBinary(bmpUtils.doBinarization())){
            mFinishedChannels++;
            bmpUtils.disposeBitmaps();
            mBinary = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setMasked() {
        if(bmpUtils.getThreshold() != 0) {
            if (mProcessedPack.setMasked(bmpUtils.doMaskWithThreshold())) {
                mFinishedChannels++;
                mMasked = true;
            }
        }
    }

    public boolean ismRed() {
        return mRed;
    }

    public boolean ismGreen() {
        return mGreen;
    }

    public boolean ismBlue() {
        return mBlue;
    }

    public boolean ismAlpha() {
        return mAlpha;
    }

    public boolean ismGray(){return mGray;}

    public boolean ismBinary(){return mBinary;}

    public boolean ismMasked(){return mMasked;}

    public boolean isDone() {
        return mFinished;
    }

    public ProcessedPackage getWrapped() {
        return mProcessedPack;
    }

    public void setTimeTaken(long l) {
        mProcessedPack.setTimeTaken(l);
    }
    public void setSessionName(String nombre) {
        this.mSessionName = nombre;
    }

    public String getmSessionName() {
        return mSessionName;
    }
}
