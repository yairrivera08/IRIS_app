package com.iris.fotoapparatapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.Editable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ImagePackager {

    private int IMAGE_TOTAL = DefaultConfiguration.DEFAULT_NUM_OF_IMAGES;
    private int mImagenesProcesadas;
    private final ArrayList<Bitmap> mImagesToProcess;
    private final ArrayList<String> mImageNames;
    private ArrayList<ProcessedPackage> mProcessedResult;
    private static final int BLOCKING_QUEUE_CAPACITY = DefaultConfiguration.DEFAULT_BLOCKING_QUEUE_SIZE;
    private final MyBlockingQueue mBlockingQueue = new MyBlockingQueue(BLOCKING_QUEUE_CAPACITY);
    private final Object LOCK = new Object();
    private ImageWrapper mImageWrapper;
    private long mStartTimestamp;
    private Context mCtx;
    private String mSessionName;

    public ImagePackager(int totalImages, ArrayList<Bitmap> images, ArrayList<String> names, Context ctx, String sesion){
        IMAGE_TOTAL = totalImages;
        mImagesToProcess = images;
        mImageNames = names;
        mCtx = ctx;
        mSessionName = sesion;
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Observable<ArrayList<ProcessedPackage>> procesar(){
        return Observable.fromCallable(
                () -> {
                    synchronized (LOCK){
                        mImagenesProcesadas = 0;
                        mStartTimestamp = System.currentTimeMillis();
                        mProcessedResult = new ArrayList<ProcessedPackage>();
                    }

                    //Inicializar hilos de procesamiento
                    new Thread(()->{
                        for(int i=0;i<IMAGE_TOTAL;i++){
                            iniciarProcesamiento(i);
                        }
                    }).start();

                    //Inicializar hilos de guardado
                    new Thread(()->{
                        for(int i=0;i<IMAGE_TOTAL;i++){
                            empaquetarProcesado();
                        }
                    }).start();

                    synchronized (LOCK){
                        while(mImagenesProcesadas < IMAGE_TOTAL){
                            try{
                                LOCK.wait();
                            }catch (InterruptedException e){
                                return mProcessedResult;
                            }
                        }
                        return mProcessedResult;
                    }
                }
        );
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void iniciarProcesamiento(final int index) {
        new Thread(()->{
            mImageWrapper = new ImageWrapper(mImagesToProcess.get(index),mImageNames.get(index),index,mCtx, mSessionName);
            Log.d("IMAGE_PACKAGER","NOMBRE DE SESION=>"+mSessionName);
            mBlockingQueue.put(mImageWrapper);
        }).start();
    }
    private void empaquetarProcesado(){
        new Thread(()->{
            ImageWrapper result= mBlockingQueue.take();
            synchronized (LOCK){
                if(result.isDone() && result.ismRed() && result.ismGreen() && result.ismBlue() && result.ismAlpha() && result.ismGray() && result.ismBinary() && result.ismMasked()){
                    result.setTimeTaken(System.currentTimeMillis() - mStartTimestamp);
                    mProcessedResult.add(result.getWrapped());
                }
                mImagenesProcesadas++;
                LOCK.notifyAll();
            }
        }).start();
    }

    public void setSessionName(String nombre) {
        this.mSessionName = nombre;
    }

    public String getmSessionName() {
        return mSessionName;
    }
}
