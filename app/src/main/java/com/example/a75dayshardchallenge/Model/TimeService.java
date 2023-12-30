package com.example.a75dayshardchallenge.Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.a75dayshardchallenge.Activity.indooractivity; // Change the import here
import com.example.a75dayshardchallenge.Activity.outdoorworkoutActivity;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "timer_channel";
    private final IBinder mBinder = new LocalBinder();
    AppDatabase database;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private SharedPreferences sharedPreferences;
    private boolean timerRunning;
    String  formattedDate;
    DayDao dayDao;

    public TimeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        sharedPreferences = getSharedPreferences("IndoorActivityPrefs", MODE_PRIVATE);
        timeLeftInMillis = sharedPreferences.getLong("timeLeftInMillis", 1200000);
        timerRunning = sharedPreferences.getBoolean("timerRunning", false);
        startTimer();
        updateNotification();
        database=AppDatabase.getInstance(this);

       Calendar calendar=Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        formattedDate = sdf.format(currentDate);

        database=AppDatabase.getInstance(this);
        database= Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"app_database").build();
        dayDao=database.days75Dao();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class LocalBinder extends Binder {
        public TimeService getService() {
            return TimeService.this;
        }
    }

    public void setTimerRunning(boolean isRunning) {
        timerRunning = isRunning;

        // Save timer state in shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timerRunning", timerRunning);
        editor.apply();

        if (timerRunning) {
            // Start foreground service immediately
           // startForeground(NOTIFICATION_ID, createNotification());
        } else {
            // If the timer is not running, remove the foreground status
            stopForeground(true);
        }
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
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

    private Notification createNotification(String contentText) {
        Intent intent = new Intent(this, indooractivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Indoor working")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .build();
    }

    public void updateNotification() {
        if (timerRunning) {
            String timeLeftFormatted = formatTime(timeLeftInMillis);
            Notification notification = createNotification("Time left: " + timeLeftFormatted);
            startForeground(NOTIFICATION_ID, notification);
        }else {
            // Timer finished, send broadcast
            Intent timerFinishedIntent = new Intent("TIMER_FINISHED");
            sendBroadcast(timerFinishedIntent);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopTimer();
        //stopSelf();
    }

    public void startTimer() {
        Log.d("TimeService", "startTimer: Starting timer");

        Intent serviceIntent = new Intent(this, TimeService.class);
        startService(serviceIntent);

        setTimerRunning(true);
        updateNotification();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification();
                saveTimerState();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 1200000;
                updateNotification();
                saveTimerState();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                      day da = dayDao.getDayById(formattedDate);

                        if (da != null) {
                            da.setField4(true);
                            long point=da.getPoinCount()+20;
                            da.setPoinCount(point);

                            dayDao.updateDay(da);

                        }
                    }
                }).start();


                Intent intent = new Intent("TIMER_FINISHED");
                sendBroadcast(intent);


                stopSelf();
            }
        };

        if (timerRunning) {
            countDownTimer.start();
        }
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }
    private String formatTime(long millis) {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void saveTimerState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("timeLeftInMillis", timeLeftInMillis);
        editor.putBoolean("timerRunning", timerRunning);
        editor.apply();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopTimer();
       // stopSelf();
    }
}
