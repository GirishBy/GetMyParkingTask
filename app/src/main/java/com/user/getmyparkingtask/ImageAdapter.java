package com.user.getmyparkingtask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<ImagePojo> imagePojoList;
    private Context context;
    private int width, height;

    public ImageAdapter(Context context, List<ImagePojo> imagePojoList, int width, int height) {
        this.context = context;
        this.imagePojoList = imagePojoList;
        this.width = width;
        this.height = height;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String[] strings_array = imagePojoList.get(position).getUrl().split("/");
        String fileName = strings_array[strings_array.length - 1];
        Glide.with(context).load(MediaManager.get().url().transformation(new Transformation().width(width).height(height)).generate(fileName)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagePojoList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }

}
