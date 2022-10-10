package com.ilhamb.quickcam.utilities;

import android.util.Log;

import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.database.Prefix;

import java.util.List;

public class JobManager {

    public List<Job> jobList;
    public List<Prefix> prefixList;
    public int folpos = 0, prepos = 0;

    public JobManager(List<Job> listJob, List<Prefix> listPrefix) {

        this.jobList = listJob;
        this.prefixList = listPrefix;
    }

    public void forwardJob() {
        if (jobList.size() -1 > folpos) {
            folpos += 1;
        } else folpos = 0;
    }
    public void backwardJob() {
        if (folpos > 0) {
            folpos -= 1;
        } else folpos = jobList.size()-1;
    }


    public void forwardPrefix() {
        if(prefixList.size() -1 > prepos) {
            prepos += 1;
        } else {
            prepos = 0;
            forwardJob();
        }

        Log.d("PRE SIZ", String.valueOf(prefixList.size()));
        Log.d("PRE POS", String.valueOf(prepos));
    }
    public void backwardPrefix() {
        if(prepos > 0) {
            prepos -= 1;
        } else {
            prepos = jobList.size()-1;
            backwardJob();
        }
    }

    public void setJobPos(int position) {
        folpos = position;
    }

    public void setJobList(List<Job> _jobList) {

        jobList = _jobList;
    }
    public void setPrefixList(List<Prefix> _prefixList) {

        prefixList = _prefixList;
    }
}
