package com.example.a75dayshardchallenge.Adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.Image;
import com.example.a75dayshardchallenge.photoview;

import java.util.List;

public class topcontributorAdapter extends RecyclerView.Adapter<topcontributorAdapter.viewHolder> {
    private List<Image> topcontributorModelList;

    public topcontributorAdapter(List<Image> topcontributorModelList) {
        this.topcontributorModelList = topcontributorModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contributor, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Image imageEntity = topcontributorModelList.get(position);

        if (imageEntity != null && imageEntity.getImageData() != null) {
            byte[] image = imageEntity.getImageData();
            int id = imageEntity.getId();

            // Use Glide to load the image
            Glide.with(holder.itemView.getContext())
                    .load(image)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), photoview.class);
                    intent.putExtra("ID", id);
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return topcontributorModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.topcontributor);
        }
    }
}
