package com.example.a75dayshardchallenge;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a75dayshardchallenge.Adapter.topcontributorAdapter;

import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.Image;
import com.example.a75dayshardchallenge.RoomDatabase.ImgDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.firebase.auth.FirebaseAuth;


import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;



public class homeeActivity extends AppCompatActivity {
    TextView textView, wish, Name, Day, Position, Coin;
    ProgressBar progressBar;

    AppDatabase database;



    private topcontributorAdapter cachedAdapter;


    ImageView threeBar;
    CircleImageView userProfile;
    RecyclerView rrecyclerView;
    LinearLayout drinkwater, eat, outdoor, indoor, readbook, progress;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_CODE_WAKE_LOCK = 100;
    private static final int REQUEST_NETWORK_STATE_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);
        // requestPermissionss();
        wish = findViewById(R.id.wish);
        Name = findViewById(R.id.username);
        Day = findViewById(R.id.day);
        Position = findViewById(R.id.position);
        Coin = findViewById(R.id.point);
        userProfile = findViewById(R.id.profilehomepage);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(75);
        database = AppDatabase.getInstance(this);

        //schedule
        PeriodicWorkRequest createDocumentWork = new PeriodicWorkRequest.Builder(
                CreateDocumentWorker.class,
                1, // Repeat interval, 1 minute
                TimeUnit.DAYS
        ).build();

        WorkManager.getInstance(this).enqueue(createDocumentWork);
        //end schedule


        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        final String currentDateeeee = dateFormat.format(new Date());


        database = Room.databaseBuilder(getApplicationContext()
                        , AppDatabase.class, "app_database").allowMainThreadQueries()
                .build();


        DayDao dayDao = database.days75Dao();


        //room retrive
        new Thread(new Runnable() {
            @Override
            public void run() {
                day da = dayDao.getDayById(currentDateeeee);
                int datee = 0; // Initialize datee here, in case dayDao returns null
                if (da != null) {
                    datee = da.getDaycount();
                }

                final int finalDatee = datee; // Create a final variable to use inside runOnUiThread

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI elements on the main thread
                        Day.setText(String.valueOf(finalDatee)); // Use finalDatee here
                        progressBar.setProgress(finalDatee); // Use finalDatee here
                    }
                });
            }
        }).start();




        //room retribe


        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            wish.setText("Good morning!");
        } else if (hour >= 12 && hour < 17) {
            wish.setText("Good afternoon!");
        } else if (hour >= 17 && hour < 21) {
            wish.setText("Good evening!");
        } else {
            wish.setText("Good night!");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }


        threeBar = findViewById(R.id.threebar);
        threeBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(homeeActivity.this, signupActivity.class));
                finish();

            }
        });


        drinkwater = findViewById(R.id.waterdrink);
        eat = findViewById(R.id.eatlayout);
        outdoor = findViewById(R.id.outdoorworkoutlayout);
        indoor = findViewById(R.id.indoorworkoutlayout);
        readbook = findViewById(R.id.readbooklayout);
        progress = findViewById(R.id.takephotolayout);

        drinkwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, uploadActivity.class));

            }
        });


        eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, eathealthyActivity.class));

            }
        });


        outdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, outdoorworkoutActivity.class));

            }
        });


        indoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, indooractivity.class));

            }
        });


        readbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, readActivity.class));

            }
        });


        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, progressphotoActivity.class));

            }
        });


        calendar = Calendar.getInstance();
        textView = findViewById(R.id.currentdate);
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);
        textView.setText(formattedDate);





        rrecyclerView = findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rrecyclerView.setLayoutManager(layoutManager);

        ImgDao imgDao = database.Img75Dao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Image> imageList = imgDao.getAllImages();
                cachedAdapter = new topcontributorAdapter(imageList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rrecyclerView.setAdapter(cachedAdapter);
                    }
                });
            }
        }).start();


    }








}
