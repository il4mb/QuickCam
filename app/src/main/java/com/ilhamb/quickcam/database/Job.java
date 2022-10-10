package com.ilhamb.quickcam.database;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.ilhamb.quickcam.MainActivity;
import com.ilhamb.quickcam.utilities.RealPathUtil;
import com.ilhamb.quickcam.utilities.TODO;
import com.ilhamb.quickcam.utilities.JobManager;

@Entity(tableName= "job")
public class Job {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "value")
    public String value;

    public static void addJob(JobDao jobDao, Uri uri, TODO todo) {

        Job job = new Job();
        job.value = RealPathUtil.getRealPathFromURI(uri);
        jobDao.insertAll(job);
        MainActivity.jobManager.setJobList(jobDao.getAll());

        todo.onSuccess();
    }

    public static void deleteJob(JobDao jobDao, Job job, TODO todo) {

        jobDao.delete(job);
        MainActivity.jobManager.setJobList(jobDao.getAll());

        todo.onSuccess();
    }

}
