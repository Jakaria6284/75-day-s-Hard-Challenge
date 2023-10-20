package com.example.a75dayshardchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class homeeActivity extends AppCompatActivity {
TextView textView;
ProgressBar progressBar;
Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }
        progressBar=findViewById(R.id.progress_bar);
        calendar=Calendar.getInstance();
        textView=findViewById(R.id.currentdate);
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);
        textView.setText(formattedDate);


        progressBar.setMax(75); // Set the maximum progress value (100%)
        progressBar.setProgress(50);

    }
}