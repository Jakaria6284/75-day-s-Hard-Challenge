package com.example.a75dayshardchallenge.Activity;


import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a75dayshardchallenge.Adapter.topcontributorAdapter;

import com.example.a75dayshardchallenge.CreateDocumentWorker;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.Image;
import com.example.a75dayshardchallenge.RoomDatabase.ImgDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;



public class homeeActivity extends AppCompatActivity {
    Bitmap bitmap;
    TextView textView, wish, Name, Day, Position, Coin;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    ShimmerFrameLayout shimmerFrameLayout;
    ImageView ModeChange;

    AppDatabase database;
    boolean value1,value2,value3,value4,value5,value6;



    private topcontributorAdapter cachedAdapter;


    ImageView threeBar;
    CircleImageView userProfile;
    RecyclerView rrecyclerView;
    TextView gallonwater,EatHealthy,outdorrworouttxt,indoorworkouttxt,read10page,progrsstxt;
    DayDao dayDao;
    day da;
    LinearLayout drinkwater, eat, outdoor, indoor, readbook, progress;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_CODE_WAKE_LOCK = 100;
    private static final int REQUEST_NETWORK_STATE_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);
        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        shimmerFrameLayout=findViewById(R.id.shimmer);
        gallonwater=findViewById(R.id.gallonwater);
        wish = findViewById(R.id.wish);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        EatHealthy=findViewById(R.id.diet);
        outdorrworouttxt=findViewById(R.id.outdoortxt);
        indoorworkouttxt=findViewById(R.id.indoortxt);
        read10page=findViewById(R.id.readtxt);
        progrsstxt=findViewById(R.id.phototxt);
        ModeChange=findViewById(R.id.modechange);
        Name = findViewById(R.id.username);
        Day = findViewById(R.id.day);
        Position = findViewById(R.id.position);
        Coin = findViewById(R.id.point);
        userProfile = findViewById(R.id.profilehomepage);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(75);
        database = AppDatabase.getInstance(this);
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

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


         dayDao = database.days75Dao();




        //room retrive
        new Thread(new Runnable() {
            @Override
            public void run() {
                 da = dayDao.getDayById(currentDateeeee);
                int datee = 0;
                long coin=0;// Initialize datee here, in case dayDao returns null
                if (da != null) {
                    datee = da.getDaycount();
                    coin=da.getPoinCount();

                }


                // Check if the day object (da) is not null before accessing its methods



                final int finalDatee = datee; // Create a final variable to use inside runOnUiThread

                long finalCoin = coin;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI elements on the main thread
                        Day.setText(String.valueOf(finalDatee)); // Use finalDatee here
                        progressBar.setProgress(finalDatee);
                       // Use finalDatee here
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                Coin.setText(String.valueOf("Point: "+ finalCoin));

                                if (da != null && da.isField1()) {
                                    gallonwater.setTextColor(Color.GREEN);

                                } else {
                                    // Optionally, set a default color if the condition is not satisfied
                                    gallonwater.setTextColor(Color.WHITE);
                                }

                                if(da != null && da.isField2())
                                {
                                    EatHealthy.setTextColor(Color.GREEN);


                                }else
                                {
                                    EatHealthy.setTextColor(Color.WHITE);
                                }


                                if(da != null && da.isField3())
                                {
                                    outdorrworouttxt.setTextColor(Color.GREEN);


                                }else
                                {
                                    outdorrworouttxt.setTextColor(Color.WHITE);
                                }


                                if(da != null && da.isField4())
                                {
                                    indoorworkouttxt.setTextColor(Color.GREEN);


                                }else
                                {
                                    indoorworkouttxt.setTextColor(Color.WHITE);
                                }



                                if(da != null && da.isField5())
                                {
                                    read10page.setTextColor(Color.GREEN);


                                }else
                                {
                                    read10page.setTextColor(Color.WHITE);
                                }


                                if(da != null && da.isField6())
                                {
                                    progrsstxt.setTextColor(Color.GREEN);


                                }else
                                {
                                    progrsstxt.setTextColor(Color.WHITE);
                                }

                                if(da != null && da.isField1() && da != null && da.isField2()
                                && da != null && da.isField3() && da != null && da.isField4()
                                && da != null && da.isField5() && da != null && da.isField6())
                                {
                                    Position.setText("Status:Challenge Done ");
                                }else
                                {
                                    Position.setText("Status:Pending....");
                                }
                            }
                        });


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
           // dayDao.deleteDay();
               // FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(homeeActivity.this, progress.class));

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
              Intent intent=new Intent(homeeActivity.this, uploadActivity.class);



                startActivity(intent);
                overridePendingTransition(R.anim.left, R.anim.right);

            }
        });

        ModeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(homeeActivity.this, StudyActivity.class);



                //startActivity(intent);
                //overridePendingTransition(0,R.anim.flip_out);
            }
        });


        eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, eathealthyActivity.class));
                overridePendingTransition(R.anim.left, R.anim.right);

            }
        });


        outdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, outdoorworkoutActivity.class));
                overridePendingTransition(R.anim.left, R.anim.right);

            }
        });


        indoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, indooractivity.class));
                overridePendingTransition(R.anim.left, R.anim.right);

            }
        });


        readbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, readActivity.class));
                overridePendingTransition(R.anim.left, R.anim.right);

            }
        });


        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, progressphotoActivity.class));
                overridePendingTransition(R.anim.left, R.anim.right);

            }
        });

        OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

               finishAffinity();

            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);


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

                if (imageList != null && !imageList.isEmpty()) {
                    // Data is available, stop shimmer
                    Image firstImage = imageList.get(0);

                    // Use Glide to load the image asynchronously
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(homeeActivity.this)
                                    .load(firstImage.getImageData())
                                    .into(userProfile);

                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            cachedAdapter = new topcontributorAdapter(imageList);
                            rrecyclerView.setAdapter(cachedAdapter);
                            cachedAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    // Data is still loading, start shimmer
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.startShimmer();
                        }
                    });
                }
            }
        }).start();



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true); // Simulate the swipe-down gesture

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Image> imageList = imgDao.getAllImages();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (imageList != null && !imageList.isEmpty()) {
                                    // Data is available, stop shimmer
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    cachedAdapter = new topcontributorAdapter(imageList);
                                    rrecyclerView.setAdapter(cachedAdapter);
                                    cachedAdapter.notifyDataSetChanged();
                                } else {
                                    // Data is still loading, start shimmer
                                    imageList.clear();
                                    rrecyclerView.setVisibility(View.GONE);
                                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                                    shimmerFrameLayout.startShimmer();
                                }

                                // Stop the refreshing animation
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });




    }






}
