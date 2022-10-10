package com.ilhamb.quickcam.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PrefixDao {
    @Query("SELECT * FROM prefix")
    List<Prefix> getAll();

    @Query("SELECT * FROM prefix WHERE id=:id")
    Prefix get(int id);

    @Query("UPDATE prefix SET value=:value WHERE id=:id")
    void update(String value, int id);

    @Insert
    void insertAll(Prefix... job);

    @Delete
    void delete(Prefix job);
}
