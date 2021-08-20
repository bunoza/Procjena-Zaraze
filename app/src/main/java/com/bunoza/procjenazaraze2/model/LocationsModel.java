package com.bunoza.procjenazaraze2.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "loc")
public class LocationsModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "timestamp")
    public String timestamp;


    public LocationsModel(String timestamp, String address) {
        this.address = address;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return address + " " + timestamp;
    }
}