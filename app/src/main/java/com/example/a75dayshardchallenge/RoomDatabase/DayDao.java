package com.example.a75dayshardchallenge.RoomDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface DayDao {

    @Query("SELECT * FROM day")
    List<day> getAllDays();

    @Query("SELECT * FROM day WHERE id= :id")
    day getDayById(String  id);

    @Insert
    void insertDay(day da);

    @Update
    void updateDay(day da);


    @Query("DELETE FROM day")
    void deleteDay();
}
