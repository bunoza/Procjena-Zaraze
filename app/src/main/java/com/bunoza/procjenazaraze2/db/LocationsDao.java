package com.bunoza.procjenazaraze2.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.bunoza.procjenazaraze2.model.LocationsModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface LocationsDao {

    @Query("SELECT * FROM loc")
    LiveData<List<LocationsModel>> getAllLive();

    @Query("SELECT * FROM loc")
    List<LocationsModel> getAll();

    @Insert
    void insert(LocationsModel locationsModel);

    @Query("DELETE FROM loc")
    void eraseTableData();

    @Delete
    void delete(LocationsModel locationsModel);
}
