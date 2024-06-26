package com.example.a75dayshardchallenge.RoomDatabase;

import android.provider.ContactsContract;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface ImgDao {

    @Insert
    long insertImage(Image imageEntity);

    @Query("SELECT * FROM Image")
    List<Image> getAllImages();

    @Query("DELETE FROM Image WHERE id= :id")
    void deletebyid(int id);



    @Query("SELECT * FROM Image WHERE id= :id")
    Image getDayByIdd(int  id);


   @Query("DELETE FROM Image")
    void delete();

}
