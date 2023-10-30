package com.example.a75dayshardchallenge;

import static androidx.core.app.ServiceCompat.stopForeground;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.a75dayshardchallenge.Alarm.AlarmReceiver;
import com.example.a75dayshardchallenge.Alarm.eatreciver;
import com.example.a75dayshardchallenge.Alarm.indoor;
import com.example.a75dayshardchallenge.Model.TimeService;
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

public class indooractivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startPauseButton;
    TextView Submitbtn;
    String formattedDate;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "IndoorActivityPrefs"; // Different SharedPreferences file
    private static final String PREF_TIME_LEFT = "timeLeftInMillis";
    private static final String PREF_TIMER_RUNNING = "timerRunning";
    private static final long DEFAULT_TIME = 1500000; // Default time, 20 minutes

    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    ImageView timepickerimg;
    private Button  Cancelreminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indooractivity);
        timerTextView = findViewById(R.id.timer);
        startPauseButton = findViewById(R.id.drinkaddbtn);
        Submitbtn = findViewById(R.id.submitbtn);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        timepickerimg = findViewById(R.id.time);
        Cancelreminder = findViewById(R.id.cancelreminder);

        calendar=Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        formattedDate = sdf.format(currentDate);




















        // Check if the service is running; if not, start it
        if (!isServiceRunning(TimeService.class)) {
            Intent serviceIntent = new Intent(this, TimeService.class);
            startService(serviceIntent);
        }





        startTimer();

        timepickerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePickerDialog();



            }
        });



        Submitbtn.setBackgroundColor(getResources().getColor(R.color.bbblack));
        Submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submitbtn.setEnabled(false);
                Toast.makeText(indooractivity.this, "Start your timer. After 20 minutes, the submit button will enable automatically.", Toast.LENGTH_LONG).show();

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

        // Load timer state from shared preferences
        if (sharedPreferences.contains(PREF_TIME_LEFT)) {
            timeLeftInMillis = sharedPreferences.getLong(PREF_TIME_LEFT, DEFAULT_TIME);
            timerRunning = sharedPreferences.getBoolean(PREF_TIMER_RUNNING, false);
            // If the timer was running, start it
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
                Toast.makeText(indooractivity.this, "Start your timer. After 20 minutes, the submit button will enable automatically.", Toast.LENGTH_LONG).show();
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

                        Map<String,Object> fileldvalueUpdate=new HashMap<>();
                        fileldvalueUpdate.put("field4",true);


                        FirebaseFirestore.getInstance()
                                .collection("USER")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("days")
                                .document(formattedDate)
                                .update(fileldvalueUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(indooractivity.this, "Submit", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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

    private BroadcastReceiver timerFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the timer finished event
            if ("TIMER_FINISHED".equals(intent.getAction())) {
                // Update your UI here, e.g., change button color or enable the button
                Submitbtn.setEnabled(true);
                Submitbtn.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("TIMER_FINISHED");
        registerReceiver(timerFinishedReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save timer state in shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_TIME_LEFT, timeLeftInMillis);
        editor.putBoolean(PREF_TIMER_RUNNING, timerRunning);
        editor.apply();
        unregisterReceiver(timerFinishedReceiver);
    }







    private void createAlarm() {
        if (calendar != null) {
            long timeUntilAlarm = calendar.getTimeInMillis() - System.currentTimeMillis();


            Data inputData = new Data.Builder()
                    .putString("title", "Alarm Set")
                    .putString("text", "Your alarm is set for the selected time")
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(indoor.class)
                    .setInitialDelay(timeUntilAlarm, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);

            Toast.makeText(indooractivity.this, "Reminder set", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(indooractivity.this, "Please select a reminder time.", Toast.LENGTH_SHORT).show();
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

    private void requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            // Explain the need for the permission to the user
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("We need the POST_NOTIFICATIONS permission to send you notifications about your alarm.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Request the permission after explaining
                            ActivityCompat.requestPermissions(indooractivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle the case when the user cancels the permission request
                            Toast.makeText(indooractivity.this, "Permission denied. You won't receive notifications.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } else {
            // Request the permission directly if no explanation is needed
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }


    private void showTimePickerDialog() {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, TimeService.class));
    }
}
