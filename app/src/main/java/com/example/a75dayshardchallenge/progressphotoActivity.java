package com.example.a75dayshardchallenge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a75dayshardchallenge.Alarm.AlarmReceiver;
import com.example.a75dayshardchallenge.Alarm.photo;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.Image;
import com.example.a75dayshardchallenge.RoomDatabase.ImgDao;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import android.Manifest;

public class progressphotoActivity extends AppCompatActivity {

    private MaterialTimePicker timePicker;
    private Calendar calendar;
    AppDatabase database;
    ImageView imageView;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri imageUri;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    ImageView timepickerimg;
    private Button  Cancelreminder,submitnt;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressphoto);

        timepickerimg = findViewById(R.id.time);
        Cancelreminder = findViewById(R.id.cancelreminder);
        imageView=findViewById(R.id.imagee);
        submitnt=findViewById(R.id.submitbtnn);
        database=AppDatabase.getInstance(this);
        timepickerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cancelreminder.setVisibility(View.VISIBLE);
                timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0).setTitleText("Select reminder time")
                        .build();

                timePicker.show(getSupportFragmentManager(), "jakaria");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                        calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        createAlarm();
                    }
                });
            }
        });


        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                       openImagePicker();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            Bitmap compressImg = compressImage(bitmap);
                            imageView.setImageBitmap(compressImg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        imageView.setOnClickListener(v -> {
            if (checkPermission()) {
                openImagePicker();
            } else {
                requestPermission();
            }
        });

        database= Room.databaseBuilder(getApplicationContext()
                        ,AppDatabase.class,"app_database")
                .build();
      //  ImgDao imgDao=database.Img75Dao();

        submitnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              new Thread(new Runnable() {
                  @Override
                  public void run() {

                      try {
                          // Convert the selected image to a byte array
                          Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                          byte[] imageBytes = convertBitmapToByteArray(bitmap);

                          // Create an ImageEntity and insert it into the Room database
                          Image imageEntity = new Image(imageBytes);
                          long imageId = database.Img75Dao().insertImage(imageEntity);


                      } catch (IOException e) {
                          e.printStackTrace();

                      }

                  }
              }).start();
            }
        });











    }

    private Bitmap compressImage(Bitmap image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        int targetWidth = originalWidth / 1;
        int targetHeight = originalHeight / 1;
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // Changed compression quality to 70
        byte[] imageBytes = outputStream.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }






    private void createAlarm() {
        if (calendar != null) {
            long timeUntilAlarm = calendar.getTimeInMillis() - System.currentTimeMillis();


            Data inputData = new Data.Builder()
                    .putString("title", "Alarm Set")
                    .putString("text", "Your alarm is set for the selected time")
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(photo.class)
                    .setInitialDelay(timeUntilAlarm, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);

            Toast.makeText(progressphotoActivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(progressphotoActivity.this, "Please select a reminder time.", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImageLauncher.launch(intent);
    }



    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);

            }
            else {
                // repeat the permission or open app details
            }
        }
    }
}
