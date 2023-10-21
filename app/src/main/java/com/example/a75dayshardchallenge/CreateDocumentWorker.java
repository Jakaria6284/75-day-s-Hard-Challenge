package com.example.a75dayshardchallenge;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateDocumentWorker extends Worker {
    public CreateDocumentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
            final String currentDate = dateFormat.format(new Date());

            // Calculate the previous date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date previousDate = calendar.getTime();
            String previousDateStr = dateFormat.format(previousDate);

            // Query the Firestore to check if any field is false in the previous document.
            DocumentReference prevDocRef = FirebaseFirestore.getInstance()
                    .collection("USER")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("days")
                    .document(previousDateStr);

            prevDocRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot prevDocumentSnapshot = task.getResult();
                                try {
                                    processPreviousDocument(prevDocumentSnapshot, currentDate);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                // Handle the error here.
                                Log.e("CreateDocumentWorker", "Error fetching previous document: " + task.getException());
                                // Return a failure result or take appropriate action.
                                Result.failure();
                            }
                        }
                    });

            return Result.success();
        } catch (Exception e) {
            Log.e("CreateDocumentWorker", "Error in doWork: " + e.getMessage());
            return Result.failure();
        }
    }

    private void processPreviousDocument(DocumentSnapshot prevDocumentSnapshot, String currentDate) throws ParseException {
        if (prevDocumentSnapshot.exists()) {
            boolean field1 = prevDocumentSnapshot.getBoolean("field1");
            boolean field2 = prevDocumentSnapshot.getBoolean("field2");
            boolean field3 = prevDocumentSnapshot.getBoolean("field3");
            boolean field4 = prevDocumentSnapshot.getBoolean("field4");
            boolean field5 = prevDocumentSnapshot.getBoolean("field5");
            boolean field6 = prevDocumentSnapshot.getBoolean("field6");

            if (field1 && field2 && field3 && field4 && field5 && field6) {
                // All boolean fields are true in the previous document; create the next document.

                // Get the next date.
                Date currentDatee = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
                String formattedDate = sdf.format(currentDatee);

                // Create the Firestore document with all boolean fields set to false
                // and an incremented daycount field.
                Map<String, Object> data = new HashMap<>();
                data.put("field1", false);
                data.put("field2", false);
                data.put("field3", false);
                data.put("field4", false);
                data.put("field5", false);
                data.put("field6", false);

                // Increment the daycount field.
                long daycount = prevDocumentSnapshot.getLong("daycount") + 1;
                data.put("daycount", daycount);

                DocumentReference nextDocRef = FirebaseFirestore.getInstance()
                        .collection("USER")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("days")
                        .document(formattedDate);

                nextDocRef.set(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("CreateDocumentWorker", "Next document created successfully");
                                } else {
                                    Log.e("CreateDocumentWorker", "Error creating next document: " + task.getException());
                                }
                            }
                        });
            } else {
                // Delete all documents and create a new one with the current date.
                FirebaseFirestore.getInstance()
                        .collection("USER")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("days")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                    // Create a new document with the current date and daycount 1.
                                    createNewDocument(currentDate);
                                }
                            }
                        });
            }
        }
    }

    private void createNewDocument(String currentDate) {
        // Create a new document with the current date and daycount 1.

        Date currentDatee = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        String formattedDate = sdf.format(currentDatee);
        Map<String, Object> newData = new HashMap<>();
        newData.put("field1", false);
        newData.put("field2", false);
        newData.put("field3", false);
        newData.put("field4", false);
        newData.put("field5", false);
        newData.put("field6", false);
        newData.put("daycount", 1);


        FirebaseFirestore.getInstance()
                .collection("USER")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("days")
                .document(formattedDate)
                .set(newData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("CreateDocumentWorker", "New document created successfully");
                        } else {
                            Log.e("CreateDocumentWorker", "Error creating new document: " + task.getException());
                        }
                    }
                });
    }
}
