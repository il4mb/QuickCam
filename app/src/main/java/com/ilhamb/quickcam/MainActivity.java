package com.ilhamb.quickcam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.ilhamb.quickcam.adapter.ListViewAdapter;
import com.ilhamb.quickcam.database.DataBase;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.database.Prefix;
import com.ilhamb.quickcam.databinding.ActivityMainBinding;
import com.ilhamb.quickcam.utilities.RealPathUtil;
import com.ilhamb.quickcam.utilities.TODO;
import com.ilhamb.quickcam.utilities.DataViewModel;
import com.ilhamb.quickcam.utilities.JobManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DataBase _DB;

    public final boolean TEST = false;
    ActivityMainBinding binding;
    public static DataViewModel viewModel;
    public static JobManager jobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        _DB = DataBase.getDbInstance(getApplicationContext());

        List<Job> jobList = _DB.jobDao().getAll();
        List<Prefix> prefixList = _DB.prefixDao().getAll();
        jobManager = new JobManager(jobList, prefixList);

        viewModel = new ViewModelProvider(this).get(DataViewModel.class);
        viewModel.setLiveDataJob(jobList);
        viewModel.setLiveDataPrefix(prefixList);

        viewModel.getJobData().observe(this, val -> {

            jobManager.setJobList(val);
            updateListView();
        });
        viewModel.getPrefixData().observe(this, val -> {

            jobManager.setPrefixList(val);
            updateListView();
        });


        if (TEST == true) {

            testMode();
            this.finish();
        }

        binding.testmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                testMode();
            }
        });
        binding.addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
                }
            }
        });
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                jobManager.setJobPos(i);
                updateListView();
            }
        });

        Prefix prefix = new Prefix();
        prefix.value = "images";
       // jobManager.prefixList.add(prefix);

        Prefix depan = new Prefix();
        depan.value = "Depan ";
        Prefix ruangan = new Prefix();
        ruangan.value = "Ruangan ";
        Prefix booth = new Prefix();
        booth.value = "Booth ";
        Prefix belakang = new Prefix();
        belakang.value = "Belakang ";

        jobManager.prefixList.add(depan);
        jobManager.prefixList.add(ruangan);
        jobManager.prefixList.add(booth);
        jobManager.prefixList.add(belakang);
        jobManager.prefixList.add(depan);
        jobManager.prefixList.add(ruangan);
        jobManager.prefixList.add(booth);
        jobManager.prefixList.add(belakang);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 9999:

                Uri dataUri = data.getData();
                String realPath = RealPathUtil.getRealPathFromURI( dataUri );

                if(realPath != null) {

                    Job.addJob(_DB.jobDao(), dataUri, new TODO() {
                        @Override
                        public void onSuccess() {

                            updateListView();
                        }
                    });
                }
                break;
        }
    }

    private void updateListView() {

        binding.listView.setVisibility(View.GONE);

        if(jobManager.jobList.size() > 0) {

            ListViewAdapter listAdapter = new ListViewAdapter(this, jobManager.jobList);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
    }

    private void testMode() {

        Intent intent = new Intent(MainActivity.this, TestActivity.class);
        startActivity(intent);
    }

    public static void deleteJob(Job job) {

        Job.deleteJob(_DB.jobDao(), job, new TODO() {
            @Override
            public void onSuccess() {

                viewModel.setLiveDataJob(jobManager.jobList);
            }
        });
    }

}