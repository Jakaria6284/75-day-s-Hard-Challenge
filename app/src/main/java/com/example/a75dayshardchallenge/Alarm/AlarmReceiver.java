package com.example.a75dayshardchallenge.Alarm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.homeeActivity;
import com.example.a75dayshardchallenge.uploadActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123;
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent nextintent = new Intent(context, homeeActivity.class);
        nextintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Set flags on 'nextintent'

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextintent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "jakaria")
                .setSmallIcon(R.drawable.baseline_access_alarm_24)
                .setContentTitle("75 Hard")
                .setContentText("It's time to complete 1 milestone either you fail 75 hard challenge")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
      //  private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123; // Replace 123 with your desired request code

// ...

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // You don't have the required permission, show a dialog to request it
            new AlertDialog.Builder(context)
                    .setTitle("Permission Request")
                    .setMessage("This app requires permission to post notifications. Would you like to grant the permission?")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Request the notification permission using the defined request code
                            ActivityCompat.requestPermissions(
                                    (Activity) context,
                                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                    NOTIFICATION_PERMISSION_REQUEST_CODE
                            );
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle the case where the user declines permission
                            // You might want to display a message or take other actions
                        }
                    })
                    .show();
            return;
        }

        notificationManagerCompat.notify(123, builder.build());

    }
}
