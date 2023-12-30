package com.example.a75dayshardchallenge.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.a75dayshardchallenge.Alarm.outdoorrecevier;
import com.example.a75dayshardchallenge.Model.TimeService;
import com.example.a75dayshardchallenge.Model.outerTimeService;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.Manifest;

public class outdoorworkoutActivity extends AppCompatActivity {

    private MaterialTimePicker timePicker;
    private outerTimeService mBoundService;
    private boolean mIsBound = false;
    private Calendar calendar;
    ImageView timepickerimg;
    private long updatedDefaultTime = DEFAULT_TIME;
    day da;
    DayDao dayDao;

  //  private TextView timerTextView;
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
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            outerTimeService.LocalBinder binder = (outerTimeService.LocalBinder) service;
            mBoundService = binder.getService();
            mBoundService.updateNotification(); // Ensure notification is updated on connection
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundService = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoorworkout);
       // timerTextView = findViewById(R.id.timer);
        startPauseButton = findViewById(R.id.drinkaddbtn);
        Submitbtn = findViewById(R.id.submitbtn);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        timepickerimg = findViewById(R.id.time);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }
        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);



        bindToService();
        startTimer();
        IntentFilter filter = new IntentFilter("TIMER_FINISHED");
        registerReceiver(timerFinishedReceiver, filter);

















        OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Intent intent=new Intent(outdoorworkoutActivity.this, homeeActivity.class);


                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.left, R.anim.right);
                finish();

            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

        // Cancelreminder = findViewById(R.id.cancelreminder);
        database=AppDatabase.getInstance(this);

        calendar=Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        formattedDate = sdf.format(currentDate);

        database=AppDatabase.getInstance(this);
        database= Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"app_database").build();
        dayDao=database.days75Dao();







        if (!isServiceRunning(outerTimeService.class)) {
            Intent serviceIntent = new Intent(this, outerTimeService.class);
            startService(serviceIntent);
            Log.d(TAG, "TimeService is running");
        }



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
    //----------------------------------------




    private void bindToService() {
        Intent serviceIntent = new Intent(this, outerTimeService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void unbindFromService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }








    private void startTimer() {
        // Log to indicate the start of the timer
       // Log.d(TAG, "startTimer: Starting timer");
        Log.d("outdoorworkoutActivity", "startTimer: Timer running: " + timerRunning);

        // Start the service immediately
        Intent serviceIntent = new Intent(this, outerTimeService.class);
        startService(serviceIntent);

        // Bind to the service
        bindToService();

        if (mBoundService != null) {
            // Set the timer as running in the service
            mBoundService.setTimerRunning(true);
            // Show the foreground notification
            mBoundService.updateNotification();
        }

        // Log to indicate the foreground notification call
        Log.d(TAG, "startTimer: Foreground notification called");

        // Cancel any existing CountDownTimer
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

        // Start a new CountDownTimer
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

    public void pauseTimer() {
        Log.d("outdoorworkoutActivity", "pauseTimer: Pausing timer");
        bindToService();
        if (mBoundService != null) {
            mBoundService.setTimerRunning(false);
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
            updateUI();
            Log.d("outdoorworkoutActivity", "pauseTimer: Timer paused");
        }
        unbindFromService();

        if (!timerRunning) {
            stopBackgroundService();
            Log.d("Outer Service", "Service stopped successfully");
        }
        if (timerFinishedReceiver != null) {
            try {
                unregisterReceiver(timerFinishedReceiver);
            } catch (IllegalArgumentException e) {
                // Receiver not registered, ignore the exception
            }
        }
    }


    private void updateUI() {
        if (mBoundService != null) {
            timeLeftInMillis = mBoundService.getTimeLeftInMillis();
            timerRunning = mBoundService.isTimerRunning();
        }

        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        //timerTextView.setText(timeLeftFormatted);

        startPauseButton.setText(timerRunning ? "Pause" : "Start");
    }

    @Override
    protected void onPause() {
        super.onPause();

      /*  SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_TIME_LEFT, timeLeftInMillis);
        editor.putBoolean(PREF_TIMER_RUNNING, timerRunning);
        editor.apply();

        if (!timerRunning) {
            stopBackgroundService();
        }

        // Unregister the receiver only if the timer is not running
        if (timerFinishedReceiver != null && !timerRunning) {
            try {
                unregisterReceiver(timerFinishedReceiver);
            } catch (IllegalArgumentException e) {
                // Receiver not registered, ignore the exception
            }
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the service is running and has the correct timer state
        if (mBoundService != null) {
            // Get the last known time from the service
            timeLeftInMillis = mBoundService.getTimeLeftInMillis();

            if (mBoundService.isTimerRunning()) {
                // If the timer is running, start it in the activity
                startTimer();
            } else {
                // If the timer is paused, update the UI
                updateUI();
            }
        }


       /* if (sharedPreferences.contains(PREF_TIME_LEFT)) {
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
        registerReceiver(timerFinishedReceiver, filter);*/
        IntentFilter filter = new IntentFilter("TIMER_FINISHED");
        registerReceiver(timerFinishedReceiver, filter);

        if (mBoundService != null && mBoundService.isTimerRunning()) {
            mBoundService.updateNotification();
        }
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
            if ("TIMER_FINISHED".equals(intent.getAction())) {
                // Timer finished, enable Submitbtn
                Submitbtn.setEnabled(true);
                Submitbtn.setBackgroundColor(getResources().getColor(R.color.green));
                Submitbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                da = dayDao.getDayById(formattedDate);

                                if (da != null) {
                                    da.setField3(true);
                                    long point=da.getPoinCount()+20;
                                    da.setPoinCount(point);

                                    dayDao.updateDay(da);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(outdoorworkoutActivity.this, "Entry Add Successfully and Earn 20 point", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });
            }
        }
    };






























    private void stopBackgroundService() {
        Intent stopServiceIntent = new Intent(this, outerTimeService.class);
        stopService(stopServiceIntent);
    }


    protected void onDestroy() {
        super.onDestroy();
       // stopBackgroundService();


        unbindFromService();
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
