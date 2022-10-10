package com.ilhamb.quickcam.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ilhamb.quickcam.utilities.jobObject;

import java.util.List;

@Dao
public interface JobDao {
    @Query("SELECT * FROM job")
    List<Job> getAll();

    @Query("SELECT * FROM job WHERE id=:id")
    Job get(int id);

    @Query("UPDATE job SET value=:value WHERE id=:id")
    void update(String value, int id);

    @Insert
    void insertAll(Job... job);

    @Delete
    void delete(Job job);
}
