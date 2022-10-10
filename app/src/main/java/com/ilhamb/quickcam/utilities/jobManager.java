package com.ilhamb.quickcam.utilities;

import java.util.ArrayList;
import java.util.List;

public class jobManager {

    public static List<String> listFolder = new ArrayList<>(), listPrefix = new ArrayList<>();
    public static int folpos = 0, prepos = 0;

    public static void forwardJob() {
        if (listFolder.size() -1 < folpos) {
            folpos += 1;
        } else folpos = 0;
    }
    public static void backwardJob() {
        if (folpos > 0) {
            folpos -= 1;
        } else folpos = listFolder.size()-1;
    }


    public static void forwardPrefix() {
        if(listPrefix.size() -1 < prepos) {
            prepos += 1;
        } else {
            prepos = 0;
            forwardJob();
        }
    }
    public static void backwardPrefix() {
        if(prepos > 0) {
            prepos -= 1;
        } else {
            prepos = listFolder.size()-1;
            backwardJob();
        }
    }
}
