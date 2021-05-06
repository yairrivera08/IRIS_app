package com.iris.fotoapparatapi;

import android.graphics.Bitmap;
import android.view.View;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ImageStacking {
    private ArrayList<Bitmap> stack;
    private final String mSessionName;
    private ImagePackager mPackager;
    private Disposable mDisposable;
    private Boolean procesoCompletado;

    public ImageStacking(String name) {
        mSessionName = null;
    }
    public void addToStack(Bitmap bmp){
        stack.add(bmp);
    }

    private int getImageCount(){
        return stack.size();
    }

    public void doSetUp(){
        mPackager = new ImagePackager(this.getImageCount(),stack);
    }

    public void doImageProcessing(){
        mDisposable = mPackager.procesar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onBenchmarkCompleted);
        procesoCompletado = true;
    }

    public void onBenchmarkCompleted(ArrayList<ProcessedPackage> result) {
        //TODO: regresar conteo de imagenes procesadas y cambiar visibilidad del progreso.
    }
}
