package com.ilhamb.quickcam.utilities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.database.Prefix;

import java.util.ArrayList;
import java.util.List;

public class DataViewModel extends ViewModel {

    private MutableLiveData<List<Job>> liveDataJob;
    private MutableLiveData<List<Prefix>> liveDataPrefix;

    public DataViewModel() {

        this.liveDataJob = new MutableLiveData<>();
        this.liveDataPrefix = new MutableLiveData<>();

        this.liveDataJob.setValue(new ArrayList<>());
        this.liveDataPrefix.setValue(new ArrayList<>());
    }

    public LiveData<List<Job>> getJobData() { return this.liveDataJob; }
    public LiveData<List<Prefix>> getPrefixData() { return this.liveDataPrefix; }


    public void setLiveDataJob(List<Job> listJob) {

        this.liveDataJob.setValue(listJob);
    }
    public void setLiveDataPrefix(List<Prefix> listPrefix) {

        this.liveDataPrefix.setValue(listPrefix);
    }
}
