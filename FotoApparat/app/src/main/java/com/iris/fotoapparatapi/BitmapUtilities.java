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
        //spliceChannels();

    }
    private void spliceChannels(){
        for (int x = 0; x < bitmap.getWidth(); x++)
        {
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                /*bmpR.setPixel(x, y, (bitmap.getPixel(x, y) & 0xff) << 24 );
                bmpG.setPixel(x, y, (bitmap.getPixel(x, y) & 0xff) << 16);
                bmpB.setPixel(x, y, (bitmap.getPixel(x, y) & 0xff) << 8);
                bmpR.setPixel(x, y, (bitmap.getPixel(x, y) & 0xFF));*/
                int color = bitmap.getColor(x,y).toArgb();
                int A = (color >> 24) & 0xff; // or color >>> 24
                int R = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = (color      ) & 0xff;
                Log.d("ARGB JAVA","[A="+A+"],[R="+R+"],[G="+G+"],[B="+B+"]");
            }
        }
    }
    public Bitmap spliceR(){
        Bitmap bmpR = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++)
        {
            for (int y = 0; y < bitmap.getHeight(); y++)
            {
                /*bmpR.setPixel(x, y, (bitmap.getPixel(x, y) & 0xff) << 24 );
                bmpG.setPixel(x, y, (bitmap.getPixel(x, y) & 0xff) << 16);
                bmpB.setPixel(x, y, (bitmap.getPixel(x, y) & 0xff) << 8);
                bmpR.setPixel(x, y, (bitmap.getPixel(x, y) & 0xFF));*/
                int color = bitmap.getColor(x,y).toArgb();
                int A = (color >> 24) & 0xff; // or color >>> 24
                int R = (color >> 16) & 0xff;
                int mask = Color.argb(A,R,0,0);
                //Log.d("ARGB["+color+"]","[R="+R+"]");
                bmpR.setPixel(x, y, mask);
                //Log.d("ARGB JAVA","[A="+A+"],[R="+R+"],[G="+G+"],[B="+B+"]");
            }
        }
        return bmpR;
    }
}
