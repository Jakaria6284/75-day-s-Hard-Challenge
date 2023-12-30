package com.example.a75dayshardchallenge.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class signupActivity extends AppCompatActivity {

    private Button signupbtn;
    TextView alreadyHaveAccount;
    String photoUrl;

    CircleImageView imageView;
    EditText name, email, password, confrmPassword;


    FirebaseAuth firebaseAuth;
     String currentDate;
    FirebaseUser currentUserr;


    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        FirebaseAnalytics firebaseAnalytics=FirebaseAnalytics.getInstance(this);


        confrmPassword = findViewById(R.id.conpassword);
        signupbtn = findViewById(R.id.siognupbtnbtn);
        alreadyHaveAccount = findViewById(R.id.alreadyhaveAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserr = firebaseAuth.getCurrentUser();
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        //imageView = findViewById(R.id.lottieAnimationView2);



        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(signupActivity.this, loginActivity.class));
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInput();
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
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confrmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkEmailAndPassword();
            }
        });
    }
    //-------------------------

    private void checkInput() {
        if (!TextUtils.isEmpty(name.getText())) {
            if (!TextUtils.isEmpty(email.getText())) {
                if (!TextUtils.isEmpty(password.getText())) {
                    if (!TextUtils.isEmpty(confrmPassword.getText())) {

                        signupbtn.setEnabled(true);
                        signupbtn.setBackgroundColor(getResources().getColor(R.color.green));

                    } else {
                        signupbtn.setEnabled(false);
                        signupbtn.setBackgroundColor(getResources().getColor(R.color.gray));
                    }
                } else {
                    signupbtn.setEnabled(false);
                    signupbtn.setBackgroundColor(getResources().getColor(R.color.gray));
                }
            } else {
                signupbtn.setEnabled(false);
                signupbtn.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        } else {
            signupbtn.setEnabled(false);
            signupbtn.setBackgroundColor(getResources().getColor(R.color.gray));
        }
    }


    //--------------------

    private void checkEmailAndPassword() {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confrmPassword.getText().toString();

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(confirmPasswordText)) {
            // Show an error message or handle the case where any field is empty
            Toast.makeText(signupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordText.equals(confirmPasswordText)) {
            // Show an error message or handle the case where passwords don't match
            Toast.makeText(signupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.getText().toString().matches(confrmPassword.getText().toString())) {
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                database = AppDatabase.getInstance(signupActivity.this);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
                                currentDate = dateFormat.format(new Date());


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

                                Toast.makeText(signupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signupActivity.this, homeeActivity.class));
                                finish();
                            }else {
                                String errorMessage = task.getException().getMessage();
                                if (errorMessage != null && errorMessage.contains("email address is already in use")) {
                                    // The email is already taken
                                    Toast.makeText(signupActivity.this, "Email address is already taken", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(signupActivity.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }





}