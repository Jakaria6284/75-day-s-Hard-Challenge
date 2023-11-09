package com.example.a75dayshardchallenge.Adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.Image;
import com.example.a75dayshardchallenge.photoview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class topcontributorAdapter extends RecyclerView.Adapter<topcontributorAdapter.viewHolder> {
    List<Image> topcontributorModelList;

    public topcontributorAdapter(List<Image> topcontributorModelList) {
        this.topcontributorModelList = topcontributorModelList;
    }

    @NonNull
    @Override
    public topcontributorAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contributor, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull topcontributorAdapter.viewHolder holder, int position) {
        Image imagEntity = topcontributorModelList.get(position);
        byte[] image = imagEntity.getImageData();
        int id= imagEntity.getId();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.imageView.setImageBitmap(bitmap);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // openImageView(bitmap, holder.itemView.getContext());

                Intent intent=new Intent(holder.itemView.getContext(), photoview.class);
                intent.putExtra("ID",id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
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

    private void openImageView(Bitmap bitmap, android.content.Context context) {
        // Save the Bitmap to a file
        File imageFile = saveBitmapToFile(bitmap, context);

        // Create an Intent and pass the file path
        Intent intent = new Intent(context, photoview.class);
        intent.putExtra("imagePath", imageFile.getAbsolutePath());
        context.startActivity(intent);
    }

    private File saveBitmapToFile(Bitmap bitmap, android.content.Context context) {
        try {
            // Create a file in the app's cache directory
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // Don't forget to make the directory
            File imageFile = new File(cachePath, "image.png");

            FileOutputStream stream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
