package com.ilhamb.quickcam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.ilhamb.quickcam.adapter.ListViewAdapter;
import com.ilhamb.quickcam.database.DataBase;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.database.Prefix;
import com.ilhamb.quickcam.databinding.ActivityMainBinding;
import com.ilhamb.quickcam.fragment.JobSettingFragment;
import com.ilhamb.quickcam.utilities.RealPathUtil;
import com.ilhamb.quickcam.utilities.TODO;
import com.ilhamb.quickcam.utilities.DataViewModel;
import com.ilhamb.quickcam.utilities.JobManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_CODE = 6652;
    public static final int STORAGE_PERMISSION_CODE = 5562;

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
            createListView();

            if(jobManager.jobList.size() <= 0) {

                binding.jobSet.setClickable(false);

            } else binding.jobSet.setClickable(true);
        });
        viewModel.getPrefixData().observe(this, val -> {

            jobManager.setPrefixList(val);
            createListView();
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

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
                }
            }
        });
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                updateListView(i, view);
            }
        });
        binding.jobSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JobSettingFragment jobSettingFragment = JobSettingFragment.newInstance();
                jobSettingFragment.show(getSupportFragmentManager(), "setjob");
            }
        });

        // CREATE DEFAULT VALUE FOR PREFIX DATA
        defaultPrex();

        // CHECK STORAGE PERMISSION
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
    }

    private void updateListView(int i, View v) {

        for (int x = 0; x < binding.listView.getChildCount(); x++) {

            View vg = binding.listView.getChildAt(x);
            TextView tv = vg.findViewById(R.id._job),
                    pr = vg.findViewById(R.id._prefix);

            pr.setVisibility(View.GONE);
            tv.setTextColor(getResources().getColor(R.color.light_gray));
        }

        jobManager.setJobPos(i);

        TextView tv = v.findViewById(R.id._job),
                pr = v.findViewById(R.id._prefix);
        tv.setTextColor(getResources().getColor(R.color.light_blue));
        pr.setVisibility(View.VISIBLE);
        pr.setText(jobManager.prefixList.get(jobManager.prepos).value);

    }

    private void createListView() {

        binding.listView.setVisibility(View.GONE);

        if (jobManager.jobList.size() > 0) {

            ListViewAdapter listAdapter = new ListViewAdapter(this, getSupportFragmentManager(), jobManager.jobList);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setVisibility(View.VISIBLE);

        }
    }

    private void defaultPrex() {
        Prefix prefix = new Prefix();
        prefix.value = "images";
        // jobManager.prefixList.add(prefix);

        Prefix depan = new Prefix();
        depan.value = "Depan";
        Prefix ruangan = new Prefix();
        ruangan.value = "Ruangan";
        Prefix booth = new Prefix();
        booth.value = "Booth";
        Prefix belakang = new Prefix();
        belakang.value = "Belakang";

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

        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case 9999:

                    Uri dataUri = data.getData();
                    String realPath = RealPathUtil.getRealPathFromURI(dataUri);

                    if (realPath != null) {

                        Job.addJob(_DB.jobDao(), dataUri, new TODO() {
                            @Override
                            public void onSuccess() {

                                if (MainActivity.viewModel != null) {

                                    MainActivity.viewModel.setLiveDataJob(jobManager.jobList);
                                }
                            }

                            @Override
                            public void onCallBack(int key, String data) {

                            }
                        });
                    }
                    break;
            }
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

            @Override
            public void onCallBack(int key, String data) {

            }
        });
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();

                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            }
        }
    }

}