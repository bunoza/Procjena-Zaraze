package com.bunoza.procjenazaraze2.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.bunoza.procjenazaraze2.model.CovidDB;
import com.bunoza.procjenazaraze2.model.LocationsModel;

@Dao
public interface CovidDBDao {

    @Query("SELECT * FROM covid")
    LiveData<CovidDB> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CovidDB covidDB);

    @Delete
    void delete(CovidDB covidDB);
}
