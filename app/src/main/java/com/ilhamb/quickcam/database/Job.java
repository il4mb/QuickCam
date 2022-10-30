package com.ilhamb.quickcam.database;

import static com.ilhamb.quickcam.MainActivity.viewModel;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.ilhamb.quickcam.MainActivity;
import com.ilhamb.quickcam.utilities.RealPathUtil;
import com.ilhamb.quickcam.utilities.TODO;
import com.ilhamb.quickcam.utilities.JobManager;

import java.util.List;

@Entity(tableName= "job")
public class Job {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "value")
    public String value;

    @ColumnInfo(name = "text")
    public String text;


    public static void addJob(JobDao jobDao, Uri uri, TODO todo) {

        Job job = new Job();
        job.value = RealPathUtil.getRealPathFromURI(uri);
        jobDao.insertAll(job);
        MainActivity.jobManager.setJobList(jobDao.getAll());

        todo.onSuccess();
    }

    public static void updateJob(JobDao jobDao, Job job, TODO todo) {

        jobDao.update(job.value, job.text, job.id);

        List<Job> jobs = jobDao.getAll();
        MainActivity.jobManager.setJobList(jobs);
        todo.onSuccess();

        viewModel.setLiveDataJob(jobs);
    }

    public static void deleteJob(JobDao jobDao, Job job, TODO todo) {

        jobDao.delete(job);
        MainActivity.jobManager.setJobList(jobDao.getAll());

        todo.onSuccess();
    }

}
