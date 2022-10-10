package com.ilhamb.quickcam.utilities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "job_table")
public class jobObject {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "job_prefix")
    public String prefix;

    @ColumnInfo(name = "job_folder")
    public String folder;
}
