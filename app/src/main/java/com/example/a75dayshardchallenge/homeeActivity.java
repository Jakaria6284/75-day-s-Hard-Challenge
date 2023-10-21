package com.example.a75dayshardchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.a75dayshardchallenge.Adapter.topcontributorAdapter;
import com.example.a75dayshardchallenge.Model.topcontributorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class homeeActivity extends AppCompatActivity {
TextView textView;
ProgressBar progressBar;
Calendar calendar;
ImageView threeBar;
RecyclerView rrecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homee);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bblack));
        }
        threeBar=findViewById(R.id.threebar);
        threeBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(homeeActivity.this, signupActivity.class));
                finish();
            }
        });
        progressBar=findViewById(R.id.progress_bar);
        calendar=Calendar.getInstance();
        textView=findViewById(R.id.currentdate);
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);
        textView.setText(formattedDate);
        rrecyclerView=findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rrecyclerView.setLayoutManager(layoutManager);




        progressBar.setMax(75); // Set the maximum progress value (100%)
        progressBar.setProgress(50);

        FirebaseFirestore.getInstance().collection("USER")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<topcontributorModel>topcontributorModelList=new ArrayList<>();

                            for(QueryDocumentSnapshot documentSnapshot: task.getResult())
                            {
                                String imag=documentSnapshot.getString("photo");
                                topcontributorModel model=new topcontributorModel(imag);
                                topcontributorModelList.add(model);
                            }
                            topcontributorAdapter adapter=new topcontributorAdapter(topcontributorModelList);
                            rrecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });




    }
}