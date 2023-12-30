package com.example.a75dayshardchallenge.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.a75dayshardchallenge.Adapter.progressAdapter;
import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.ImgDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class progress extends AppCompatActivity {
    DayDao dayDao;
    ScrollView scrollView;
    LinearLayout dailyprogress,privacypolicy,DelteAccount,Answerpermission,Whypermission,Rateus,RateAndRule,Rules,Logout;

    AppDatabase database;


    day da;

    RecyclerView recyclerView;
    String AppUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        database = AppDatabase.getInstance(this);
        recyclerView=findViewById(R.id.recyclerview);
        dailyprogress=findViewById(R.id.progressdaily);
        privacypolicy=findViewById(R.id.privacypolicy);
        RateAndRule=findViewById(R.id.rateandrule);
        Rules=findViewById(R.id.rule);
        scrollView=findViewById(R.id.scrollviews);
        Logout=findViewById(R.id.logout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }

        DelteAccount=findViewById(R.id.Deleteaccount);
        Answerpermission=findViewById(R.id.answerpermission);
        Whypermission=findViewById(R.id.whypermisson);
        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        Rateus=findViewById(R.id.rateus);
        AppUrl="https://play.google.com/store/apps/details?id=";
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        final String currentDateeeee = dateFormat.format(new Date());
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        database = Room.databaseBuilder(getApplicationContext()
                        , AppDatabase.class, "app_database").allowMainThreadQueries()
                .build();


        dayDao = database.days75Dao();
        ImgDao imgDao=database.Img75Dao();

        dailyprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyprogress.setVisibility(View.GONE);
                privacypolicy.setVisibility(View.GONE);
                DelteAccount.setVisibility(View.GONE);
                Whypermission.setVisibility(View.GONE);
                RateAndRule.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Logout.setVisibility(View.GONE);
            }
        });
        Rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyprogress.setVisibility(View.GONE);
                privacypolicy.setVisibility(View.GONE);
                DelteAccount.setVisibility(View.GONE);
                Whypermission.setVisibility(View.GONE);
                RateAndRule.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Logout.setVisibility(View.GONE);
            }
        });

        Rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(AppUrl+getPackageName()));
                    intent.setPackage("com.android.vending");
                    startActivity(intent);


                }catch (ActivityNotFoundException e)
                {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(AppUrl));
                    startActivity(intent);
                }
            }
        });
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sites.google.com/view/75hardfitnesschallenge"));
                startActivity(intent);
            }
        });
        Whypermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyprogress.setVisibility(View.GONE);
                privacypolicy.setVisibility(View.GONE);
                DelteAccount.setVisibility(View.GONE);
                Whypermission.setVisibility(View.GONE);
                RateAndRule.setVisibility(View.GONE);
                Answerpermission.setVisibility(View.VISIBLE);
                Logout.setVisibility(View.GONE);
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();

                if(firebaseUser!=null)
                {
                    firebaseAuth.signOut();
                    startActivity(new Intent(progress.this, signupActivity.class));
                    finish();
                }
            }
        });
        DelteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(progress.this);
                dialog.setContentView(R.layout.alartdilog);
                dialog.setCancelable(true);
                Button button=dialog.findViewById(R.id.deleteparmanently);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dayDao.deleteDay();
                                imgDao.delete();

                                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                                FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();

                                if(firebaseUser!=null)
                                {
                                    firebaseUser.delete();
                                           startActivity(new Intent(progress.this, signupActivity.class));
                                           finish();
                                }

                            }
                        }).start();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        database = Room.databaseBuilder(getApplicationContext()
                        , AppDatabase.class, "app_database").allowMainThreadQueries()
                .build();

        dayDao = database.days75Dao();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<day> dayList = dayDao.getAllDays();

        progressAdapter adapter = new  progressAdapter(dayList);
        recyclerView.setAdapter(adapter);

    }
}