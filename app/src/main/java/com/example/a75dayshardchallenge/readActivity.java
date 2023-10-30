package com.example.a75dayshardchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a75dayshardchallenge.Alarm.AlarmReceiver;
import com.example.a75dayshardchallenge.Alarm.indoor;
import com.example.a75dayshardchallenge.Alarm.read;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class readActivity extends AppCompatActivity {
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    ImageView timepickerimg;
    private Button  Cancelreminder;

    private TextView timerTextView;
    private Button startPauseButton;
    TextView Submitbtn;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ReadActivityPrefs"; // Different SharedPreferences file
    private static final String PREF_TIME_LEFT = "timeLeftInMillis";
    private static final String PREF_TIMER_RUNNING = "timerRunning";
    private static final long DEFAULT_TIME = 600000; // Default time, 10 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        timerTextView = findViewById(R.id.timer);
        startPauseButton = findViewById(R.id.drinkaddbtn);
        Submitbtn = findViewById(R.id.submitbtn);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        timepickerimg = findViewById(R.id.time);
        Cancelreminder = findViewById(R.id.cancelreminder);



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





        Submitbtn.setBackgroundColor(getResources().getColor(R.color.bbblack));
        Submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submitbtn.setEnabled(false);
                Toast.makeText(readActivity.this, "Start your timer. After 10 minutes, the submit button will enable automatically.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(readActivity.this, "Start your timer. After 10 minutes, the submit button will enable automatically.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(readActivity.this, "Submit", Toast.LENGTH_SHORT).show();
                    }
                });
                timerRunning = false;
                timeLeftInMillis = DEFAULT_TIME; // Reset the timer to 10 minutes
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
            timeLeftInMillis = DEFAULT_TIME; // Default time, 10 minutes
            timerRunning = false;
            updateUI();
        }
    }






    private void createAlarm() {
        if (calendar != null) {
            long timeUntilAlarm = calendar.getTimeInMillis() - System.currentTimeMillis();


            Data inputData = new Data.Builder()
                    .putString("title", "Alarm Set")
                    .putString("text", "Your alarm is set for the selected time")
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(read.class)
                    .setInitialDelay(timeUntilAlarm, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);

            Toast.makeText(readActivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(readActivity.this, "Please select a reminder time.", Toast.LENGTH_SHORT).show();
        }
    }

}
