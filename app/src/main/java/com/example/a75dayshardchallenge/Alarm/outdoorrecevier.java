package com.example.a75dayshardchallenge.Alarm;

import android.Manifest;
import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.Activity.outdoorworkoutActivity;

public class outdoorrecevier extends Worker {
    MediaPlayer mediaPlayer;


    public outdoorrecevier(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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
            NotificationManagerCompat.from(getApplicationContext()).createNotificationChannel(new NotificationChannel("outdoor", "outdoor", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // Create an intent to open the uploadActivity
        Intent intent = new Intent(getApplicationContext(), outdoorworkoutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "outdoor")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Out Door workout Time")
                .setContentText("it's time to workout in outdoor")
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
        NotificationManagerCompat.from(getApplicationContext()).notify(3, builder.build());
    }


}


