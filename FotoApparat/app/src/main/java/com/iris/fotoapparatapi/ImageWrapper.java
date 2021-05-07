package com.iris.fotoapparatapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class ImageWrapper {

    private boolean mFinished = false;
    private boolean mRed = false;
    private boolean mGreen = false;
    private boolean mBlue = false;
    private boolean mAlpha= false;
    private ProcessedPackage mProcessedPack;
    private BitmapUtilities bmpUtils;
    private int mFinishedChannels;


    public ImageWrapper(Bitmap bitmap, String name, int index, Context ctx) {
        mFinishedChannels = 0;
        mProcessedPack = new ProcessedPackage(index,name,ctx);
        bmpUtils = new BitmapUtilities(bitmap);

        setRedChannel();
        setGreenChannel();
        setBlueChannel();
        setAlphaChannel();
        checkFinished();
    }

    private void checkFinished() {
        while(!mFinished){
            if(mFinishedChannels == 4){
                //Log.d("IMAGEWRAPPER","HILO ["+Thread.currentThread()+"] mFinishedChannels="+mFinishedChannels);
                mFinished = true;
            }
        }
    }

    public void setRedChannel() {
        if(mProcessedPack.setRedChannel(bmpUtils.spliceR())){
            mFinishedChannels++;
            mRed = true;
        }
    }

    public void setGreenChannel() {
        if(mProcessedPack.setGreenChannel(bmpUtils.spliceG())){
            mFinishedChannels++;
            mGreen = true;
        }
    }

    public void setBlueChannel() {
        if(mProcessedPack.setBlueChannel(bmpUtils.spliceB())){
            mFinishedChannels++;
            mBlue = true;
        }
    }

    public void setAlphaChannel() {
        if(mProcessedPack.setAlphaChannel(bmpUtils.spliceA())){
            mFinishedChannels++;
            mAlpha = true;
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

    public boolean isDone() {
        return mFinished;
    }

    public ProcessedPackage getWrapped() {
        return mProcessedPack;
    }

    public void setTimeTaken(long l) {
        mProcessedPack.setTimeTaken(l);
    }
}
