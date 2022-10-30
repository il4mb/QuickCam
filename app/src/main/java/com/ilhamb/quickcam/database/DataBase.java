package com.ilhamb.quickcam.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ilhamb.quickcam.utilities.jobObject;

@Database(entities ={Job.class, Prefix.class}, version = 3, exportSchema = false)
public abstract class DataBase extends RoomDatabase {

    public abstract JobDao jobDao();
    public abstract PrefixDao prefixDao();

    private static DataBase INSTANCE;

    public static DataBase getDbInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataBase.class, "quick_cam_db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
