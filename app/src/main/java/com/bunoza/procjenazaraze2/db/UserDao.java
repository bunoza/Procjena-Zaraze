package com.bunoza.procjenazaraze2.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bunoza.procjenazaraze2.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAllDead();

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAll();

//    @Query("SELECT * FROM user WHERE id IN (:userIds)")
//    List<User> loadAllByIds(int[] userIds);

//    @Query("SELECT * FROM user WHERE Ime LIKE :first AND " +
//            "Prezime LIKE :last LIMIT 1")
//    User findByName(String first, String last);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Delete
    void delete(User user);

    @Query("SELECT COUNT(*) FROM user")
    int getElementCount();
}
