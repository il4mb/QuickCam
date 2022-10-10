package com.ilhamb.quickcam.database;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.ilhamb.quickcam.utilities.jobObject;

import java.util.List;

@Dao
public interface jobDao {
    @Query("SELECT * FROM job_table")
    List<jobObject> getAll();

    @Query("SELECT * FROM job_table WHERE id IN (:id)")
    List<jobObject> loadAllByIds(int[] id);

    @Query("SELECT * FROM job_table WHERE id=:id")
    jobObject get(int id);

    @Query("UPDATE job_table SET job_prefix=:job_prefix, job_folder=:job_folder WHERE id=:id")
    void update(String job_prefix, String job_folder, int id);

    @Insert
    void insertAll(jobObject... job);

    @Delete
    void delete(jobObject job);
}
