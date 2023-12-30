package com.example.a75dayshardchallenge.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.a75dayshardchallenge.Alarm.photo;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.Image;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class progressphotoActivity extends AppCompatActivity {

    private MaterialTimePicker timePicker;
    ImageView timepickerimg;
    day da;
    DayDao dayDao;
    AppDatabase database;
    private Calendar calendar;

    private ImageView imageView;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri imageUri;
    ProgressBar loading;
    private TextView submitnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressphoto);

        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        // Initialize UI elements
        timepickerimg = findViewById(R.id.time);
        imageView = findViewById(R.id.imagee);
        submitnt = findViewById(R.id.submitbtnn);
        loading=findViewById(R.id.progressBarload);
        database = AppDatabase.getInstance(this);
        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);

        calendar=Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);

        database=AppDatabase.getInstance(this);
        database= Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"app_database").build();
        dayDao=database.days75Dao();



        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }
        submitnt.setVisibility(View.GONE);

        // Handle back button press
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("ProgressPhotoActivity", "Back button pressed");
                Intent intent = new Intent(progressphotoActivity.this, homeeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left, R.anim.right);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        // Set up time picker
        timepickerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        // Set up permission launchers
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(progressphotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                            submitnt.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        // Set up click listener for image view
        imageView.setOnClickListener(v -> {
            if (checkPermission()) {
                openImagePicker();
                submitnt.setVisibility(View.VISIBLE);
            } else {
                requestPermission();
            }
        });

        // Set up database
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_database").build();

        // Set up click listener for submit button
        submitnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loading.setVisibility(View.VISIBLE);

                                    loading.setProgress(100);
                                    submitnt.setVisibility(View.GONE);
                                }
                            });
                            // Convert the selected image to a byte array
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            Log.d("ImageLog", "Original Image Size: " + bitmap.getByteCount() + " bytes");
                            byte[] imageBytes = convertBitmapToByteArray(bitmap);

                            Bitmap compressedBitmap = compressImage(bitmap);
                            Log.d("ImageLog", "Compressed Image Size: " + compressedBitmap.getByteCount() + " bytes");


                           if (getBitmapSize(compressedBitmap) > 10000 * 1024) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setVisibility(View.GONE);
                                        submitnt.setVisibility(View.VISIBLE);
                                        Toast.makeText(progressphotoActivity.this, "Image size is too large (greater than 700 KB). Please select a smaller image.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return; // Do not upload the image if it's too large
                            }



                            // Create an ImageEntity and insert it into the Room database
                            Image imageEntity = new Image(imageBytes);
                            long imageId = database.Img75Dao().insertImage(imageEntity);
                            da=dayDao.getDayById(formattedDate);

                            if(da!=null)
                            {
                                da.setField6(true);
                                long point=da.getPoinCount()+20;
                                da.setPoinCount(point);

                                dayDao.updateDay(da);


                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    submitnt.setVisibility(View.GONE);
                                    loading.setVisibility(View.GONE);

                                    Toast.makeText(progressphotoActivity.this, "Upload successfully and Earn 20 point", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (IOException e) {
                            Log.e("ProgressPhotoActivity", "Error converting image: " + e.getMessage());
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
    private int getBitmapSize(Bitmap bitmap) {
        // Calculate the size in bytes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else {
            return bitmap.getByteCount();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d("ProgressPhotoActivity", "Permission not granted, requesting...");
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                Log.d("ProgressPhotoActivity", "Permission already granted");
                // repeat the permission or open app details
            }
        }
    }
}
