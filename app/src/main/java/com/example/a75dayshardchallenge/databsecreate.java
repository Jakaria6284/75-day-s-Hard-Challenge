package com.example.a75dayshardchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.TextView;

import com.example.a75dayshardchallenge.RoomDatabase.AppDatabase;
import com.example.a75dayshardchallenge.RoomDatabase.day;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class databsecreate extends AppCompatActivity {

    TextView txt;
    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_databsecreate);
        txt=findViewById(R.id.startt);
        database=AppDatabase.getInstance(this);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        final String currentDate = dateFormat.format(new Date());



        database= Room.databaseBuilder(getApplicationContext()
                        ,AppDatabase.class,"app_database").allowMainThreadQueries()
                .build();


        day initialEntry=new day();
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
}