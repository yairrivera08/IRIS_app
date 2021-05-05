package com.iris.fotoapparatapi;

import android.graphics.Bitmap;

public class ImageWrapper {

    private boolean mFinished;
    private ProcessedPackage mProcessedPack;
    private long mTimeTaken;

    public ImageWrapper(Bitmap bitmap) {
    }

    public boolean isDone() {
        return mFinished;
    }

    public ProcessedPackage getWrapped() {
        return mProcessedPack;
    }

    public void setTimeTaken(long l) {
        mTimeTaken = l;
    }
}
