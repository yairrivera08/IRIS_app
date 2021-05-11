package com.iris.fotoapparatapi;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<ProcessedPackage> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;


    // data is passed into the constructor
    RecyclerViewAdapter(Context context, ArrayList<ProcessedPackage> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_processedimage, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mNameField.setText(mData.get(position).getName());
        long timeMillis = mData.get(position).getTimeTaken();
        long seconds = (timeMillis / 1000) % 60;
        String tiempo = seconds +" segundos de procesamiento";
        holder.mTimeField.setText(tiempo);
        String filePath = mData.get(position).getImgRPath().get(1);
        Log.d("RECYCLERVIEWADAPTER=>","FILEPATH="+filePath);
        Glide.with(mContext)
                .load(new File(filePath))
                .thumbnail(
                        Glide.with(mContext)
                        .load(R.drawable.ic_camera)
                        .override(200,300)
                )
                .into(holder.mImageView);
        //holder.mImageView
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageView;
        TextView mNameField;
        TextView mTimeField;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.BitmapMiniature);
            mNameField = itemView.findViewById(R.id.BitmapName);
            mTimeField = itemView.findViewById(R.id.ProcessingTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ProcessedPackage getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}