package com.example.a75dayshardchallenge.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a75dayshardchallenge.Alarm.AlarmReceiver;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.Manifest;

public class uploadActivity extends AppCompatActivity {

    private TextView timerTextView, Submitbtn;
    private Button startPauseButton, Setreminder, Cancelreminder;
    ImageView timepickerimg;
    AppDatabase database;

    private static final String CHANNEL_ID = "channel_id";


    private MaterialTimePicker timePicker;
    private Calendar calendar;
    day da;
    DayDao dayDao;

    String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        timerTextView = findViewById(R.id.timer);
        startPauseButton = findViewById(R.id.drinkaddbtn);
        Submitbtn = findViewById(R.id.submitbtn);
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }




        Cancelreminder = findViewById(R.id.cancelreminder);
        timepickerimg = findViewById(R.id.time);
        calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        formattedDate = sdf.format(currentDate);

        database = AppDatabase.getInstance(this);
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_database").build();
        dayDao = database.days75Dao();


        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Intent intent = new Intent(uploadActivity.this, homeeActivity.class);


                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.left, R.anim.right);
                finish();

            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);


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


        Submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final day da = dayDao.getDayById(formattedDate);

                        if (da != null && !da.isField1()) {
                            da.setField1(true);
                            long point=da.getPoinCount()+20;
                            da.setPoinCount(point);

                            dayDao.updateDay(da);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(uploadActivity.this, "Entry Add Successfully and Earn 20 point", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // Toast.makeText(uploadActivity.this, "Entry Add Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

            }
        });
    }


        private void createAlarm () {
            if (calendar != null) {
                long timeUntilAlarm = calendar.getTimeInMillis() - System.currentTimeMillis();


                Data inputData = new Data.Builder()
                        .putString("title", "Alarm Set")
                        .putString("text", "Your alarm is set for the selected time")
                        .build();

                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AlarmReceiver.class)
                        .setInitialDelay(timeUntilAlarm, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

                WorkManager.getInstance(this).enqueue(workRequest);

                Toast.makeText(uploadActivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(uploadActivity.this, "Please select a reminder time.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onStart () {
            super.onStart();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);

                } else {
                    // repeat the permission or open app details
                }
            }
        }


    }


        //-------------------------------------------------------------------------------











