package com.bunoza.procjenazaraze2.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bunoza.procjenazaraze2.model.Approximation;
import com.bunoza.procjenazaraze2.model.CovidDB;
import com.bunoza.procjenazaraze2.model.LocationsModel;
import com.bunoza.procjenazaraze2.model.User;

import java.lang.reflect.Array;

@Database(entities = {LocationsModel.class, CovidDB.class, User.class, Approximation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CovidDBDao covidDBDao();
    public abstract LocationsDao locationsDao();
    public abstract UserDao userDao();
    public abstract ApproxDao approxDao();
}


