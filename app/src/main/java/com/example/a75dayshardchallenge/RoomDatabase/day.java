package com.example.a75dayshardchallenge.RoomDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity
public class day {


   @ColumnInfo

   String id;
    @ColumnInfo
    boolean field1;
    @ColumnInfo
    boolean field2;
    @ColumnInfo
    boolean field3;
    @ColumnInfo
    boolean field4;
    @ColumnInfo
    boolean field5;
    @ColumnInfo
    boolean field6;

    @ColumnInfo
    int  daycount;
    @ColumnInfo
    long poinCount;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    int idcard;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isField1() {
        return field1;
    }

    public void setField1(boolean field1) {
        this.field1 = field1;
    }

    public boolean isField2() {
        return field2;
    }

    public void setField2(boolean field2) {
        this.field2 = field2;
    }

    public boolean isField3() {
        return field3;
    }

    public void setField3(boolean field3) {
        this.field3 = field3;
    }

    public boolean isField4() {
        return field4;
    }

    public void setField4(boolean field4) {
        this.field4 = field4;
    }

    public boolean isField5() {
        return field5;
    }

    public void setField5(boolean field5) {
        this.field5 = field5;
    }

    public boolean isField6() {
        return field6;
    }

    public void setField6(boolean field6) {
        this.field6 = field6;
    }

    public int getDaycount() {
        return daycount;
    }

    public void setDaycount(int daycount) {
        this.daycount = daycount;
    }

    public long getPoinCount() {
        return poinCount;
    }

    public void setPoinCount(long poinCount) {
        this.poinCount = poinCount;
    }

    public int getIdcard() {
        return idcard;
    }

    public void setIdcard(int idcard) {
        this.idcard = idcard;
    }
}
