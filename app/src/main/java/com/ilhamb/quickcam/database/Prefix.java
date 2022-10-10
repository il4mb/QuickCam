package com.ilhamb.quickcam.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prefix")
public class Prefix {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "value")
    public String value;
}
