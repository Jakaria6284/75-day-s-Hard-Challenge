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
import com.example.a75dayshardchallenge.Alarm.photo;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class progressphotoActivity extends AppCompatActivity {

    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    ImageView timepickerimg;
    private Button  Cancelreminder;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressphoto);

        timepickerimg = findViewById(R.id.time);
        Cancelreminder = findViewById(R.id.cancelreminder);

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


}
