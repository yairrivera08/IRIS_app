package com.iris.fotoapparatapi;

import android.graphics.Bitmap;

public class ImageWrapper {

    private boolean mFinished;
    private ProcessedPackage mProcessedPack;
    private BitmapUtilities bmpUtils;


    public ImageWrapper(Bitmap bitmap, String name, int index) {
        mProcessedPack = new ProcessedPackage(index,name);
        bmpUtils = new BitmapUtilities(bitmap);
        if(setRedChannel()){
            mFinished = true;
        }
        /*setGreenChannel();
        setBlueChannel();
        setAlphaChannel();*/
    }

    public Boolean setRedChannel() {
        return mProcessedPack.setRedChannel(bmpUtils.spliceR());
    }

    public void setGreenChannel() {
        mProcessedPack.setGreenChannel(bmpUtils.spliceG());
    }

    public void setBlueChannel() {
        mProcessedPack.setBlueChannel(bmpUtils.spliceB());
    }

    public void setAlphaChannel() {
        mProcessedPack.setAlphaChannel(bmpUtils.spliceA());
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
