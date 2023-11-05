package com.example.a75dayshardchallenge;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.DayDao;
import com.example.a75dayshardchallenge.RoomDatabase.day;
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
import java.util.concurrent.TimeUnit;

public class CreateDocumentWorker extends Worker {
    AppDatabase appDatabase;
    public CreateDocumentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        appDatabase=AppDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            DayDao dayDao=appDatabase.days75Dao();



            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
            final String currentDate = dateFormat.format(new Date());




            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date previousDate = calendar.getTime();
            String previousDateStr = dateFormat.format(previousDate);




            day previousDay= dayDao.getDayById(previousDateStr);










            if (previousDay != null) {
                // Check if any of the boolean fields are false
                if (!previousDay.isField1()|| !previousDay.isField2() || !previousDay.isField3() ||
                        !previousDay.isField4() || !previousDay.isField5() || !previousDay.isField6()) {
                    // Delete all data in the table
                    dayDao.deleteDay();

                    day newDay = new day();
                    newDay.setId(currentDate);
                    newDay.setField1(false);
                    newDay.setField2(false);
                    newDay.setField3(false);
                    newDay.setField4(false);
                    newDay.setField5(false);
                    newDay.setField6(false);
                    newDay.setDaycount(1); // Increment daycount
                    dayDao.insertDay(newDay);




                }else
                {
                   // long currentDateee = System.currentTimeMillis();
                    day initialEntry = new day();
                    initialEntry.setId(currentDate);
                    initialEntry.setField1(false);
                    initialEntry.setField2(false);
                    initialEntry.setField3(false);
                    initialEntry.setField4(false);
                    initialEntry.setField5(false);
                    initialEntry.setField6(false);
                    initialEntry.setDaycount(previousDay.getDaycount()+1);

                    appDatabase.days75Dao().insertDay(initialEntry);
                }
            }


            return Result.success();
        } catch (Exception e) {
            Log.e("CreateDocumentWorker", "Error in doWork: " + e.getMessage());
            return Result.failure();
        }
    }


}

