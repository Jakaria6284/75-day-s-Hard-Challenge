package com.example.a75dayshardchallenge;

import static androidx.core.app.ActivityCompat.requestPermissions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a75dayshardchallenge.Adapter.topcontributorAdapter;
import com.example.a75dayshardchallenge.Model.topcontributorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class homeeActivity extends AppCompatActivity {
    TextView textView,wish,name,Day,Position,Coin;
    ProgressBar progressBar;
    Calendar calendar;
    ImageView threeBar;
    CircleImageView userProfile;
    RecyclerView rrecyclerView;
    LinearLayout drinkwater,eat,outdoor,indoor,readbook,progress;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_CODE_WAKE_LOCK = 100;
    private static final int REQUEST_NETWORK_STATE_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);
       // requestPermissionss();
        wish=findViewById(R.id.wish);
        name=findViewById(R.id.username);
        Day=findViewById(R.id.day);
        Position=findViewById(R.id.position);
        Coin=findViewById(R.id.point);
        userProfile=findViewById(R.id.profilehomepage);

        //

        //



        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            wish.setText("Good morning!");
        } else if (hour >= 12 && hour < 17) {
            wish.setText("Good afternoon!");
        } else if (hour >= 17 && hour < 21) {
            wish.setText("Good evening!");
        } else {
            wish.setText("Good night!");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }

        threeBar = findViewById(R.id.threebar);
        threeBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              FirebaseAuth.getInstance().signOut();
              startActivity(new Intent(homeeActivity.this,signupActivity.class));
              finish();
            }
        });

        //schedule
        PeriodicWorkRequest createDocumentWork = new PeriodicWorkRequest.Builder(
                CreateDocumentWorker.class,
                1, // Repeat interval, 1 minute
                TimeUnit.DAYS
        ).build();

        WorkManager.getInstance(this).enqueue(createDocumentWork);
        //end schedule
        drinkwater=findViewById(R.id.waterdrink);
        eat=findViewById(R.id.eatlayout);
        outdoor=findViewById(R.id.outdoorworkoutlayout);
        indoor=findViewById(R.id.indoorworkoutlayout);
        readbook=findViewById(R.id.readbooklayout);
        progress=findViewById(R.id.takephotolayout);

        drinkwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, uploadActivity.class));

            }
        });

        eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, eathealthyActivity.class));

            }
        });

        outdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, outdoorworkoutActivity.class));

            }
        });

        indoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, indooractivity.class));

            }
        });

        readbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, readActivity.class));

            }
        });

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeeActivity.this, progressphotoActivity.class));

            }
        });








        progressBar = findViewById(R.id.progress_bar);
        calendar = Calendar.getInstance();
        textView = findViewById(R.id.currentdate);
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);
        textView.setText(formattedDate);
        //call  method
        dayretrive(formattedDate);
        coinandposition(formattedDate);
        positionretrive();
        //end
        rrecyclerView = findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rrecyclerView.setLayoutManager(layoutManager);

        progressBar.setMax(75);


        FirebaseFirestore.getInstance().collection("USER")
                .orderBy("coin", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<topcontributorModel> topcontributorModelList = new ArrayList<>();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String imag = documentSnapshot.getString("photo");

                                topcontributorModel model = new topcontributorModel(imag);
                                topcontributorModelList.add(model);
                            }

                            topcontributorAdapter adapter = new topcontributorAdapter(topcontributorModelList);
                            rrecyclerView.setAdapter(adapter);
                        }
                    }
                });
    }



    private void signOutAndNavigateToSignUp() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(homeeActivity.this, signupActivity.class));
        finish();
    }






    //day retrive method

    public  void dayretrive(String date)
    {
        FirebaseFirestore.getInstance()
                .collection("USER")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("days")
                .document(date)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists())
                        {
                            String daycount=String.valueOf(value.getLong("daycount"));
                            Day.setText(daycount);
                            progressBar.setProgress(Integer.parseInt(daycount));
                        }
                    }
                });
    }

    //----------------------------------------------------------
    //coin and position rettrive
    public  void coinandposition(String date)
    {
        FirebaseFirestore.getInstance()
                .collection("USER")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists())
                        {
                            String coin=String.valueOf(value.getLong("coin"));
                            String Name=value.getString("User");
                            String profile=value.getString("photo");
                            Glide.with(homeeActivity.this).load(profile).into(userProfile);
                            name.setText(Name);
                            Coin.setText("point: "+coin);
                        }
                    }
                });
    }

    public void positionretrive()
    {
        FirebaseFirestore.getInstance()
                .collection("USER")
                .orderBy("coin", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int userPosition = 1;
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                     for(QueryDocumentSnapshot documentSnapshot:value)
                     {
                         String documentt=documentSnapshot.getId();
                         if(documentt.equals(currentUserId))
                         {
                             Position.setText("position: "+String.valueOf(userPosition));
                             break;
                         }
                         userPosition++;
                     }

                    }
                });
    }

    //coin and position retrive
}
