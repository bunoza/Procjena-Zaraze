package com.bunoza.procjenazaraze2.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bunoza.procjenazaraze2.model.Approximation;

import java.util.List;

@Dao
public interface ApproxDao {

    @Query("SELECT * FROM approx")
    LiveData<List<Approximation>> getAllLive();

    @Query("SELECT * FROM approx")
    List<Approximation> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Approximation approximation);

    @Delete
    void delete(Approximation approximation);
}
