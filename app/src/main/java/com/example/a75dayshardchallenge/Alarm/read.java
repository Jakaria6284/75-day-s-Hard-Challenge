package com.example.a75dayshardchallenge.Alarm;

import android.Manifest;
import android.app.Service;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.eathealthyActivity;
import com.example.a75dayshardchallenge.uploadActivity;

public class read extends Worker {
    MediaPlayer mediaPlayer;


    public read(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {





        showNotification();







        return Result.success();
    }


    private void showNotification() {
        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat.from(getApplicationContext()).createNotificationChannel(new NotificationChannel("read", "Read", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // Create an intent to open the uploadActivity
        Intent intent = new Intent(getApplicationContext(), eathealthyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "read")
                .setSmallIcon(R.drawable.bar)
                .setContentTitle("Reading Time")
                .setContentText("it's time to read productive book")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(getApplicationContext()).notify(5, builder.build());
    }


}


