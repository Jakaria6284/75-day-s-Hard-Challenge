package com.example.a75dayshardchallenge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri imageUri;

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

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            Bitmap compressImg = compressImage(bitmap);
                            imageView.setImageBitmap(compressImg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        imageView.setOnClickListener(v -> {
            if (checkPermission()) {
                openImagePicker();
            } else {
                requestPermission();
            }
        });

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
                boolean isImageSelected = imageView.getDrawable() != null;
                if (!isImageSelected) {
                    Toast.makeText(signupActivity.this, "Please select profile", Toast.LENGTH_SHORT).show();
                }
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
                            if (task.isSuccessful()) {
                                String fileName = UUID.randomUUID().toString();
                                StorageReference imageRef = storageReference.child("AllCategory/" + fileName);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                                    byte[] imageData = baos.toByteArray();

                                    // Upload the image to Firebase Storage
                                    UploadTask uploadTask = imageRef.putBytes(imageData);
                                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                                        // Retrieve the download URL of the uploaded image
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            if (uri != null) {
                                                photoUrl = uri.toString();
                                                Map<String, Object> userData = new HashMap<>();
                                                userData.put("User", name.getText().toString());
                                                userData.put("name", email.getText().toString());
                                                userData.put("uID", firebaseAuth.getUid());
                                                userData.put("photo", photoUrl);
                                                userData.put("coin", 10);

                                                firestore.collection("USER").document(firebaseAuth.getUid())
                                                        .set(userData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
                                                                    String currentDate = dateFormat.format(new Date());

                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("field1", false);
                                                                    data.put("field2", false);
                                                                    data.put("field3", false);
                                                                    data.put("field4", false);
                                                                    data.put("field5", false);
                                                                    data.put("field6", false);
                                                                    data.put("daycount", 1);

                                                                    firestore.collection("USER")
                                                                            .document(firebaseAuth.getUid())
                                                                            .collection("days")
                                                                            .document(currentDate)
                                                                            .set(data)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    if (task.isSuccessful()) {
                                                                                        startActivity(new Intent(signupActivity.this, homeeActivity.class));
                                                                                        finish();
                                                                                    }

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // Handle sign-up failure
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                // Handle the case where the email is already in use
                                Toast.makeText(signupActivity.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle other errors
                                // You can display a generic message or log the error for debugging
                                Toast.makeText(signupActivity.this, "Sign-up failed. Please try again.", Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseAuth", "Sign-up failed with error: " + e.getMessage());
                            }
                        }
                    });

        } else {
            Toast.makeText(signupActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImageLauncher.launch(intent);
    }

    private Bitmap compressImage(Bitmap image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        int targetWidth = originalWidth / 10;
        int targetHeight = originalHeight / 10;
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 20, outputStream); // Changed compression quality to 70
        byte[] imageBytes = outputStream.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
