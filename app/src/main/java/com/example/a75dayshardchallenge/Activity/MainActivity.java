package com.example.a75dayshardchallenge.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a75dayshardchallenge.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView Go;
    private CircleImageView imageView, imageView2;

    private Handler handler;
    private final int refreshInterval = 2000; // Set your desired interval in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Go = findViewById(R.id.start);
        imageView = findViewById(R.id.lottieAnimationView);
        imageView2 = findViewById(R.id.lottieAnimationView9);
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);










        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }

        handler = new Handler();
        checkInternetPeriodically();
    }

    private void showInternetContent() {
        Go.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.GONE);

        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser == null || firebaseUser.getUid() == null) {
                    startActivity(new Intent(MainActivity.this, signupActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, homeeActivity.class));
                    finish();
                }
            }
        });
    }

    private void checkInternetPeriodically() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInternetAvailable(MainActivity.this)) {
                    // Internet is available, update UI
                    showInternetContent();
                } else {
                    // Internet is not available, continue checking
                    showNoInternetMessage();
                    handler.postDelayed(this, refreshInterval);
                }
            }
        }, refreshInterval);
    }


    boolean isInternetAvailable(Context context)

    {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);



        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Use NetworkCapabilities for Android 6.0 (API level 23) and later
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);


                    return capabilities != null &&
                            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                }
            } else {
                // Use deprecated methods for older versions
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }


    private void showNoInternetMessage() {
        Go.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        imageView2.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Check internet availability when the activity is resumed
        if (isInternetAvailable(MainActivity.this)) {
            showInternetContent();
        } else {
            showNoInternetMessage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}
