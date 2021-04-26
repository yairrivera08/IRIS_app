package com.iris.fotoapparatapi;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class BitmapUtilities {
    Bitmap bitmap;

    int width;
    int height;

    public BitmapUtilities(Bitmap bmpOg){
        this.bitmap = bmpOg;
        width = bmpOg.getWidth();
        height = bmpOg.getHeight();
    }
    private void spliceChannels(){
        for (int x = 0; x < bitmap.getWidth(); x++)
        {
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                int color = bitmap.getColor(x,y).toArgb();
                int A = (color >> 24) & 0xff; //Alpha siempre se necesita para evitar transparencias
                int R = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = (color      ) & 0xff;
                Log.d("ARGB JAVA","[A="+A+"],[R="+R+"],[G="+G+"],[B="+B+"]");
            }
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap spliceR(){
        Bitmap bmpR = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++)
        {
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                int color = bitmap.getColor(x,y).toArgb();
                int A = (color >> 24) & 0xff; // or color >>> 24
                int R = (color >> 16) & 0xff;
                int mask = Color.argb(A,R,0,0);
                bmpR.setPixel(x, y, mask);
            }
        }
        return bmpR;
    }
    public Bitmap spliceG(){
        Bitmap bmpG = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++)
        {
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                int color = bitmap.getColor(x,y).toArgb();
                int A = (color >> 24) & 0xff; // or color >>> 24
                int G = (color >>  8) & 0xff;
                int mask = Color.argb(A,0,G,0);
                bmpG.setPixel(x, y, mask);
            }
        }
        return bmpG;
    }
    public Bitmap spliceB(){
        Bitmap bmpB = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++)
        {
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                int color = bitmap.getColor(x,y).toArgb();
                int A = (color >> 24) & 0xff; // or color >>> 24
                int B = (color      ) & 0xff;
                int mask = Color.argb(A,0,0,B);
                bmpB.setPixel(x, y, mask);
            }
        }
        return bmpB;
    }
    public Bitmap spliceA(){
        Bitmap bmpA = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++)
        {
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                int color = bitmap.getColor(x,y).toArgb();
                int A = (color >> 24) & 0xff; // or color >>> 24
                int mask = Color.argb(A,0,0,0);
                bmpA.setPixel(x, y, mask);
            }
        }
        return bmpA;
    }
}
