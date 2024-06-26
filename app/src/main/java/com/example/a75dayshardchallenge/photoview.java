package com.example.a75dayshardchallenge;

import static kotlinx.coroutines.BuildersKt.withContext;
import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.example.a75dayshardchallenge.Adapter.ImagePagerAdapter;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.Image;
import com.example.a75dayshardchallenge.RoomDatabase.ImgDao;

import java.util.List;


public class photoview extends AppCompatActivity {
    ImageView imageView;
    AppDatabase database;
    ImgDao imgDao;
    Image img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        imageView = findViewById(R.id.daily);
        String imagePath = getIntent().getStringExtra("imagePath");
        int id = getIntent().getIntExtra("ID", 0);
        database = AppDatabase.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_database").allowMainThreadQueries().build();
        imgDao = database.Img75Dao();
        img = imgDao.getDayByIdd(id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Log the ID for debugging purposes
                Log.d("Debug", "ID: " + id);

                // Retrieve the Image object from the database based on the ID
                img = imgDao.getDayByIdd(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (img != null) {
                            // Log to check if the image retrieval is successful
                            Log.d("Debug", "Image retrieved successfully");

                            // Convert the image byte array to a Bitmap
                            byte[] imageData = img.getImageData();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                            // Set the Bitmap in the ImageView
                            imageView.setImageBitmap(bitmap);
                        } else {
                            // Log if the image retrieval fails
                            Log.d("Debug", "Image retrieval failed");
                        }
                    }
                });
            }
        }).start();


      imageView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              imgDao.deletebyid(id);
             
          }
      });


    }
}