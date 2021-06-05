package com.iris.photocapture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailedRecyclerViewAdapter extends RecyclerView.Adapter<DetailedRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;


    // data is passed into the constructor
    DetailedRecyclerViewAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_splitimage, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name ="";
        switch (position){
            case 0:
                name = "RED CHANNEL";
                break;
            case 1:
                name = "Grayscale";
                break;
            case 2:
                name = "Masked A";
                break;
            case 3:
                name = "Masked B";
                break;
        }
        holder.mNameField.setText(name);
        Glide.with(mContext)
                .load(mData.get(position))
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


        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.ChannelImage);
            mNameField = itemView.findViewById(R.id.ChannelName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
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