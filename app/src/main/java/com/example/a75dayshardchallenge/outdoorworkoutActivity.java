package com.example.a75dayshardchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a75dayshardchallenge.Alarm.AlarmReceiver;
import com.example.a75dayshardchallenge.Alarm.outdoorrecevier;
import com.example.a75dayshardchallenge.Model.TimeService;
import com.example.a75dayshardchallenge.Model.outerTimeService;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import android.Manifest;

public class outdoorworkoutActivity extends AppCompatActivity {

    private MaterialTimePicker timePicker;
    private Calendar calendar;
    ImageView timepickerimg;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    day da;
    DayDao dayDao;

    private TextView timerTextView;
    private Button startPauseButton;
    TextView Submitbtn;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning;
    private Button  Cancelreminder;
    String formattedDate;
    AppDatabase database;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UploadActivityPrefs"; // Different SharedPreferences file
    private static final String PREF_TIME_LEFT = "timeLeftInMillis";
    private static final String PREF_TIMER_RUNNING = "timerRunning";
    private static final long DEFAULT_TIME = 1200000; // Default time, 20 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoorworkout);
        timerTextView = findViewById(R.id.timer);
        startPauseButton = findViewById(R.id.drinkaddbtn);
        Submitbtn = findViewById(R.id.submitbtn);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        timepickerimg = findViewById(R.id.time);
        Cancelreminder = findViewById(R.id.cancelreminder);
        database=AppDatabase.getInstance(this);

        calendar=Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        formattedDate = sdf.format(currentDate);

        database=AppDatabase.getInstance(this);
        database= Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"app_database").build();
        dayDao=database.days75Dao();







        if (!isServiceRunning(TimeService.class)) {
            Intent serviceIntent = new Intent(this, outerTimeService.class);
            startService(serviceIntent);
        }

        startTimer();

        Submitbtn.setBackgroundColor(getResources().getColor(R.color.bbblack));

        Submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submitbtn.setEnabled(false);
                Toast.makeText(outdoorworkoutActivity.this, "Start your timer. After 20 minutes, the submit button will enable automatically.", Toast.LENGTH_LONG).show();

            }
        });


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







        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        updateUI();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Submitbtn.setBackgroundColor(getResources().getColor(R.color.bbblack));
        Submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submitbtn.setEnabled(false);
                Toast.makeText(outdoorworkoutActivity.this, "Start your timer. After 20 minutes, the submit button will enable automatically.", Toast.LENGTH_LONG).show();
            }
        });

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateUI();
            }

            @Override
            public void onFinish() {
                Submitbtn.setEnabled(true);
                Submitbtn.setBackgroundColor(getResources().getColor(R.color.green));
                Submitbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                da=dayDao.getDayById(formattedDate);

                                if(da!=null)
                                {
                                    da.setField3(true);

                                    dayDao.updateDay(da);
                                }
                            }
                        }).start();
                    }
                });
                timerRunning = false;
                timeLeftInMillis = DEFAULT_TIME; // Reset the timer to 20 minutes
                updateUI();
            }
        }.start();

        timerRunning = true;
        updateUI();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
            updateUI();
        }
    }

    private void updateUI() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);

        startPauseButton.setText(timerRunning ? "Pause" : "Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_TIME_LEFT, timeLeftInMillis);
        editor.putBoolean(PREF_TIMER_RUNNING, timerRunning);
        editor.apply();

        unregisterReceiver(timerFinishedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPreferences.contains(PREF_TIME_LEFT)) {
            timeLeftInMillis = sharedPreferences.getLong(PREF_TIME_LEFT, DEFAULT_TIME);
            timerRunning = sharedPreferences.getBoolean(PREF_TIMER_RUNNING, false);

            if (timerRunning) {
                startTimer();
            } else {
                updateUI();
            }
        } else {
            timeLeftInMillis = DEFAULT_TIME; // Default time, 20 minutes
            timerRunning = false;
            updateUI();
        }

        IntentFilter filter = new IntentFilter("TIMER_FINISHE");
        registerReceiver(timerFinishedReceiver, filter);
    }

    private void createAlarm() {
        if (calendar != null) {
            long timeUntilAlarm = calendar.getTimeInMillis() - System.currentTimeMillis();


            Data inputData = new Data.Builder()
                    .putString("title", "Alarm Set")
                    .putString("text", "Your alarm is set for the selected time")
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(outdoorrecevier.class)
                    .setInitialDelay(timeUntilAlarm, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);

            Toast.makeText(outdoorworkoutActivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(outdoorworkoutActivity.this, "Please select a reminder time.", Toast.LENGTH_SHORT).show();
        }
    }





    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver timerFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the timer finished event
            if ("TIMER_FINISHE".equals(intent.getAction())) {
                // Update your UI here, e.g., change button color or enable the button
                Submitbtn.setEnabled(true);
                Submitbtn.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, outerTimeService.class));
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
