package com.example.a75dayshardchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    private TextView donthaveAccount;
    private Button loginbtn;
    private EditText email,password;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

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


        if(password.length()>=6)
        {
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
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
