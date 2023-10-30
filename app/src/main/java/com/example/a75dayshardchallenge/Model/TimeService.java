package com.example.a75dayshardchallenge.Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.indooractivity;

public class TimeService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "timer_channel";

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private SharedPreferences sharedPreferences;
    private boolean timerRunning;

    public TimeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        // Load timer state from shared preferences
        sharedPreferences = getSharedPreferences("IndoorActivityPrefs", MODE_PRIVATE);
        timeLeftInMillis = sharedPreferences.getLong("timeLeftInMillis", 1500000);
        timerRunning = sharedPreferences.getBoolean("timerRunning", false);
        startTimer();
        updateNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification();
                // Save timer state in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("timeLeftInMillis", timeLeftInMillis);
                editor.putBoolean("timerRunning", true);
                editor.apply();
            }

            @Override
            public void onFinish() {
                // Reset the timer to 1500000 (20 minutes)
                timeLeftInMillis = 1500000;
                updateNotification();
                // Save timer state in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("timeLeftInMillis", timeLeftInMillis);
                editor.putBoolean("timerRunning", false);
                editor.apply();

                Intent intent = new Intent("TIMER_FINISHED");
                sendBroadcast(intent);
               // stopForeground(true);

                stopForeground(true);

            }
        };

        if (timerRunning) {
            countDownTimer.start();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "indoor service";
            String description = "Channel for Timer Service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, indooractivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Indoor working")
                .setContentText("keep it up! we know you complete this challenge")
                .setSmallIcon(R.drawable.bar)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void updateNotification() {
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
    }
}
