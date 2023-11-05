package com.example.a75dayshardchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;


import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView Go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //firebaseAuth=FirebaseAuth.getInstance();
        //firebaseUser=firebaseAuth.getCurrentUser();
        Go=findViewById(R.id.start);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        SharedPreferences sharedPreferences=getSharedPreferences("firstttt",MODE_PRIVATE);
        boolean isFirsttime=sharedPreferences.getBoolean("isfirst",true);

        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirsttime) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isfirst", false);
                    editor.apply(); // Make sure to apply changes

                    startActivity(new Intent(MainActivity.this, databsecreate.class)); // Navigate to the first-time activity
                     finish();
                } else {
                    // It's not the first time, navigate to your main activity
                    startActivity(new Intent(MainActivity.this, homeeActivity.class));
                     finish();
                }
            }
        });

    }
}

