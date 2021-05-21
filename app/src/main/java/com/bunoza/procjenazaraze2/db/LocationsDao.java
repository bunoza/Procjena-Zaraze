package com.bunoza.procjenazaraze2.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.bunoza.procjenazaraze2.model.LocationsModel;

@Dao
public interface LocationsDao {

    @Query("SELECT * FROM loc")
    LiveData<LocationsModel> getAllLive();

    @Query("SELECT * FROM loc")
    LocationsModel getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocationsModel locationsModel);

    @Delete
    void delete(LocationsModel locationsModel);
}
