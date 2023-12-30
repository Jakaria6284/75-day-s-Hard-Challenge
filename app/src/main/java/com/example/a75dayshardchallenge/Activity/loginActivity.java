package com.example.a75dayshardchallenge.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a75dayshardchallenge.R;
import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class loginActivity extends AppCompatActivity {
    private TextView donthaveAccount;
    private Button loginbtn;
    private EditText email,password;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        donthaveAccount=findViewById(R.id.donthaveAccount);
        loginbtn=findViewById(R.id.loginbtn);
        email=findViewById(R.id.name);
        password=findViewById(R.id.email);
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser= firebaseAuth.getCurrentUser();
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);

        donthaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(loginActivity.this, signupActivity.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheackEmailAndPasswrd();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cheackInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cheackInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
    private void cheackInput()
    {
        if(!TextUtils.isEmpty(email.getText()))
        {
            if(!TextUtils.isEmpty(password.getText()))
            {
                loginbtn.setEnabled(true);
                loginbtn.setBackgroundColor(getResources().getColor(R.color.green));
            }else
            {
                // signinprogress.setVisibility(View.GONE);
                loginbtn.setEnabled(false);
                //signinbtn.setBackgroundColor(getContext().getResources().getColor(R.color.md_black_1000_20));
                loginbtn.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }else
        {
            loginbtn.setEnabled(false);
            // signinbtn.setBackgroundColor(getContext().getResources().getColor(R.color.md_black_1000_20));
            loginbtn.setBackgroundColor(getResources().getColor(R.color.gray));
        }
    }

    private void cheackEmailAndPasswrd()
    {

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();


        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) ) {
            // Show an error message or handle the case where any field is empty
            Toast.makeText(loginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }




        if(password.length()>=6)
        {
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                if(database==null) {
                                    database = AppDatabase.getInstance(loginActivity.this);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
                                    String currentDate = dateFormat.format(new Date());


                                    database = Room.databaseBuilder(getApplicationContext()
                                                    , AppDatabase.class, "app_database").allowMainThreadQueries()
                                            .build();


                                    day initialEntry = new day();
                                    initialEntry.setId(currentDate);
                                    initialEntry.setField1(false);
                                    initialEntry.setField2(false);
                                    initialEntry.setField3(false);
                                    initialEntry.setField4(false);
                                    initialEntry.setField5(false);
                                    initialEntry.setField6(false);
                                    initialEntry.setDaycount(1);
                                    database.days75Dao().insertDay(initialEntry);
                                }
                                Intent intent=new Intent(loginActivity.this, homeeActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(loginActivity.this, "password or email Incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}
