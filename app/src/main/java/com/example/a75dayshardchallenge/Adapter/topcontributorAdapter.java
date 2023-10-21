package com.example.a75dayshardchallenge.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a75dayshardchallenge.Model.topcontributorModel;
import com.example.a75dayshardchallenge.R;

import java.util.List;

public class topcontributorAdapter extends RecyclerView.Adapter<topcontributorAdapter.viewHolder> {
    List<topcontributorModel>topcontributorModelList;

    public topcontributorAdapter(List<topcontributorModel> topcontributorModelList) {
        this.topcontributorModelList = topcontributorModelList;
    }

    @NonNull
    @Override
    public topcontributorAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contributor,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull topcontributorAdapter.viewHolder holder, int position) {
       String Image=topcontributorModelList.get(position).getImage();
       holder.setData(Image);
    }

    @Override
    public int getItemCount() {
        return topcontributorModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.topcontributor);
        }
        public void setData(String img)
        {
            Glide.with(itemView.getContext()).load(img).into(imageView);
        }
    }
}
