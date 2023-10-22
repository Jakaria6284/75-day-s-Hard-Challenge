package com.example.a75dayshardchallenge;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class eathealthyActivity extends AppCompatActivity {


    private Button breakfast,dinner,lounch;
    ImageView timepickerimg;
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Button Cancelreminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eathealthy);
        timepickerimg = findViewById(R.id.time);
        Cancelreminder = findViewById(R.id.cancelreminder);
        createnotification();

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

        Cancelreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });







    }
    private void createnotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "eatchannel";
            String desc = "Channel for eat";
            int imp = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("jakaria", name, imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createAlarm() {
        if (calendar != null) {
            // Create an intent to trigger the alarm
            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm to trigger at the selected time
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Toast.makeText(eathealthyActivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(eathealthyActivity.this, "Please select a reminder time.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelAlarm() {
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Toast.makeText(eathealthyActivity.this, "Reminder canceled", Toast.LENGTH_SHORT).show();
        }
    }




}
