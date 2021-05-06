package com.iris.fotoapparatapi;

import android.graphics.Bitmap;

public class ProcessedPackage {
    private int id;
    private String name;
    private Bitmap original;
    private Bitmap redChannel;
    private Bitmap greenChannel;
    private Bitmap blueChannel;
    private Bitmap alphaChannel;
    private Bitmap mask;
    private Bitmap binary;
    private Bitmap greyScale;
    private long mTimeTaken;

    public ProcessedPackage(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Bitmap getRedChannel() {
        return redChannel;
    }

    public Boolean setRedChannel(Bitmap redChannel) {
        if(redChannel != null) {
            this.redChannel = redChannel;
            if(this.redChannel != null){
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

    public void setGreenChannel(Bitmap greenChannel) {
        this.greenChannel = greenChannel;
    }

    public Bitmap getBlueChannel() {
        return blueChannel;
    }

    public void setBlueChannel(Bitmap blueChannel) {
        this.blueChannel = blueChannel;
    }

    public Bitmap getAlphaChannel() {
        return alphaChannel;
    }

    public void setAlphaChannel(Bitmap alphaChannel) {
        this.alphaChannel = alphaChannel;
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
