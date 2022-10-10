package com.ilhamb.quickcam.utilities;

import android.util.Log;

import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.database.Prefix;

import java.util.ArrayList;
import java.util.List;

public class jobManager {

    public static List<Job> jobList = new ArrayList<>();
    public static List<Prefix> prefixList = new ArrayList<>();
    public static int folpos = 0, prepos = 0;

    public static void forwardJob() {
        if (jobList.size() -1 > folpos) {
            folpos += 1;
        } else folpos = 0;
    }
    public static void backwardJob() {
        if (folpos > 0) {
            folpos -= 1;
        } else folpos = jobList.size()-1;
    }


    public static void forwardPrefix() {
        if(prefixList.size() -1 < prepos) {
            prepos += 1;
        } else {
            prepos = 0;
            forwardJob();
        }

        Log.d("JOB", String.valueOf(prepos));
    }
    public static void backwardPrefix() {
        if(prepos > 0) {
            prepos -= 1;
        } else {
            prepos = jobList.size()-1;
            backwardJob();
        }
    }

    public static void setJobPos(int position) {
        folpos = position;
    }
}
