package com.iris.fotoapparatapi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BitmapUtilities {
    private Bitmap bitmap;
    private int mOtsuThreshold;
    private Bitmap mThresholded;
    private int[][] pxl;

    private final int width;
    private final int height;

    public BitmapUtilities(@NotNull Bitmap bmpOg){
        this.bitmap = bmpOg;
        width = bmpOg.getWidth();
        height = bmpOg.getHeight();
        pxl = new int[width][height];
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Bitmap doMaskWithThreshold(){
        Bitmap bmpMasked = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        int[][] ogValues = new int[width][height];
        int[][] binValues = new int[width][height];
        for(int x = 0;x<width;x++){
            for(int y=0;y<height;y++){
                int colorPixel = bitmap.getPixel(x, y);
                int binaryPixel = mThresholded.getPixel(x, y);
                System.out.println("PIXEL["+x+"]["+y+"]="+binaryPixel);
                if(binaryPixel==-1){
                    bmpMasked.setPixel(x,y,colorPixel);
                }else{
                    bmpMasked.setPixel(x,y,0);
                }
            }
        }
        return bmpMasked;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private int[] computeHistogram() {

        int[] histo = new int[256];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int colorPixel = bitmap.getPixel(x, y);

                int alpha = Color.alpha(colorPixel);
                int R = Color.red(colorPixel);
                int G = Color.green(colorPixel);
                int B = Color.blue(colorPixel);
                int gray = (int) ( (0.2126 * R) + (0.7152 * G) + (0.0722 * B) ); // (int) ( (0.299 * R) + (0.587 * G) + (0.114 * B) );

                pxl[x][y] = gray;
                histo[luminance(colorPixel)]++;
            }
        }
        return histo;
    }
    private int luminance(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (r + b + g) / 3;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void createThreshold(){
        int[] histogram = computeHistogram();
        int total = height * width;

        float sum = 0;
        for(int i=0; i<256; i++) sum += i * histogram[i];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for(int i=0 ; i<256 ; i++) {
            wB += histogram[i];
            if(wB == 0) continue;
            wF = total - wB;

            if(wF == 0) break;

            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if(varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }
        mOtsuThreshold = threshold;
        //return threshold;
    }
    public int getThreshold(){
        return mOtsuThreshold;
    }

    public Bitmap doBinarization(){

        Bitmap bmpBinarized = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                int fnpx = pxl[x][y];
                int colorPixel = bitmap.getPixel(x,y);
                int A = Color.alpha(colorPixel);
                // Calculate the brightness
                //System.out.println("VALOR DE PIXEL=["+fnpx+"] UMBRAL OTSU = ["+mOtsuThreshold+"]");
                if(fnpx>mOtsuThreshold)
                {
                    // Return the result
                    fnpx = 255;
                    bmpBinarized.setPixel(x, y, Color.argb(A,fnpx,fnpx,fnpx));
                }
                else
                {
                    // Return the result
                    fnpx = 0;
                    bmpBinarized.setPixel(x, y, Color.argb(A,fnpx,fnpx,fnpx));
                }
            }
        }
        mThresholded = bmpBinarized;
        return bmpBinarized;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Bitmap getGrayscale(){
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayscale;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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
    @RequiresApi(api = Build.VERSION_CODES.Q)
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
    @RequiresApi(api = Build.VERSION_CODES.Q)
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
    @RequiresApi(api = Build.VERSION_CODES.Q)
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
