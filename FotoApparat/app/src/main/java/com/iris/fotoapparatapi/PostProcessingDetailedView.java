package com.iris.fotoapparatapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class PostProcessingDetailedView extends AppCompatActivity {

    private ProcessedPackage pp;
    private ArrayList<String> bmpsPath = new ArrayList<>();
    private String mOtsuThreshold;
    private String mTimeExecution;
    private Context ctx;
    private ImageView original;
    private TextView name,threshold,time, sesion;
    private DetailedRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_processing_detailed_view);
        ctx = getApplicationContext();
        Intent intent = getIntent();
        pp = (ProcessedPackage) intent.getSerializableExtra("PostProcessed");

        original = findViewById(R.id.ImagenOriginal);
        name = findViewById(R.id.NombreImagen);
        threshold = findViewById(R.id.Umbral);
        time = findViewById(R.id.TiempoTotal);
        sesion = findViewById(R.id.SesionOrigen);
        /*
        * 0 = Alpha
        * 1 = Red
        * 2 = Green
        * 3 = Blue
        * 4 = Grayscale
        * 5 = Binary
        * */
        bmpsPath = pp.getImgRPath();

        mOtsuThreshold = String.valueOf(pp.getmThreshold());
        mTimeExecution = String.valueOf(pp.getTimeTaken());

        sesion.setText(pp.getmSessionName());
        name.setText(pp.getName());
        time.setText(mTimeExecution);
        threshold.setText(mOtsuThreshold);

        Glide.with(ctx)
                .load(new File(pp.getImgRPath().get(0)))
                .thumbnail(
                        Glide.with(ctx)
                                .load(R.drawable.ic_camera)
                                .override(200,300)
                )
                .into(original);

        recyclerView = findViewById(R.id.SplitChannelsRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new DetailedRecyclerViewAdapter(this, bmpsPath);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
}