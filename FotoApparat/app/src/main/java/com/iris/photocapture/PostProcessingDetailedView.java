package com.iris.photocapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iris.photocapture.threading.ProcessedPackage;

import java.io.File;
import java.util.ArrayList;

public class PostProcessingDetailedView extends AppCompatActivity {

    private ProcessedPackage pp;
    private ArrayList<String> bmpsPath = new ArrayList<>();
    private String mOtsuThreshold;
    private String mTimeExecution;
    private Context ctx;
    private ImageView original;
    private TextView name,threshold,time, sesion, dimensiones, escala, ubicacion;
    private DetailedRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private Button galeria;
    private String mSesion;

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
        dimensiones = findViewById(R.id.Dimensiones);
        escala = findViewById(R.id.Factor);
        ubicacion = findViewById(R.id.Ubicacion);
        galeria = findViewById(R.id.AbrirGaleria);
        /*
        * 0 = Red: Brinda datos del detalle de iluminacion
        * 1 = Grayscale = Da una vista previa de como se maneja la posible binarizacion
        * Según el objeto y el fondo las mascaras A y B pueden destacar la silueta o el objeto aislado del entorno.
        * 2 = Masked Image A
        * 3 = Masked Image B
        * */
        bmpsPath = pp.getImgRPath();


        mOtsuThreshold =  String.valueOf(pp.getmThreshold());
        mTimeExecution = String.valueOf(pp.getTimeTaken());

        mSesion = "Sesión de origen => "+pp.getmSessionName();
        String mUbica = "Ubicación en almacenamiento => /DCIM/IRIS3D/"+pp.getmSessionName();
        String mNombre = "Fotografía => "+pp.getName()+"_Original.png";
        String mTiempo = "Tiempo total de procesamiento hasta enmascarado => "+mTimeExecution+" milisegundos";
        String mUmbral = "Umbral calculado de Otsu para binarizacion y enmascarado => "+mOtsuThreshold;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String separator ="/";

        BitmapFactory.decodeFile(pp.getImgRPath().get(0), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String dimen = "Dimensiones "+imageWidth+" x "+imageHeight+" pixeles";
        String mEscala = "Factor usado para  => 0.25F";

        sesion.setText(mSesion);
        name.setText(mNombre);
        time.setText(mTiempo);
        threshold.setText(mUmbral);
        dimensiones.setText(dimen);
        escala.setText(mEscala);
        ubicacion.setText(mUbica);

        Glide.with(ctx)
                .load(new File(pp.getImgRPath().get(0)))
                .thumbnail(
                        Glide.with(ctx)
                                .load(R.drawable.ic_camera)
                                .override(200,300)
                )
                .into(original);

        bmpsPath.remove(0); //Quitar BMP Original
        bmpsPath.remove(0); //Quitar BMP Thumbnail
        recyclerView = findViewById(R.id.SplitChannelsRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new DetailedRecyclerViewAdapter(this, bmpsPath);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        galeria.setOnClickListener(v -> OpenGallery());
    }
    private void OpenGallery(){
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}