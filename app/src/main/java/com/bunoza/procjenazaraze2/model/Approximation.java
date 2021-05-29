package com.bunoza.procjenazaraze2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "approx")
public class Approximation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "value")
    public Double value;

    @ColumnInfo(name = "date")
    public String date;


    public Approximation(Double value, String date) {
        this.value = value;
        this.date = date;
    }
}
