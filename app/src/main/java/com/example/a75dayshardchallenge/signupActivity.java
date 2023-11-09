package com.example.a75dayshardchallenge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.day;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class signupActivity extends AppCompatActivity {

    private Button signupbtn;
    TextView alreadyHaveAccount;
    String photoUrl;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    CircleImageView imageView;
    EditText name, email, password, confrmPassword;
    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUserr;


    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        confrmPassword = findViewById(R.id.conpassword);
        signupbtn = findViewById(R.id.siognupbtnbtn);
        alreadyHaveAccount = findViewById(R.id.alreadyhaveAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserr = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        imageView = findViewById(R.id.lottieAnimationView2);

        database = AppDatabase.getInstance(this);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        final String currentDate = dateFormat.format(new Date());


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
        if (password.getText().toString().matches(confrmPassword.getText().toString())) {
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(signupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signupActivity.this, homeeActivity.class));
                                finish();
                            }
                        }
                    });
        }
    }
}