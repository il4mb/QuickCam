package com.ilhamb.quickcam.fragment;

import static com.ilhamb.quickcam.MainActivity._DB;
import static com.ilhamb.quickcam.MainActivity.jobManager;
import static com.ilhamb.quickcam.database.Job.updateJob;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ilhamb.quickcam.R;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.databinding.FragmentJobSettingBinding;
import com.ilhamb.quickcam.utilities.TODO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSettingFragment extends DialogFragment {

    public static JobSettingFragment newInstance() {
        JobSettingFragment fragment = new JobSettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentJobSettingBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJobSettingBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    Dialog dialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Job job = jobManager.jobList.get(jobManager.folpos);

        dialog = this.getDialog();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        binding.jobPath.setText(job.value);
        binding.jobStamp.setText(job.text);

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChange();
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

    }

    private void saveChange() {

        jobManager.jobList.get(jobManager.folpos).value = binding.jobPath.getText().toString();
        jobManager.jobList.get(jobManager.folpos).text = binding.jobStamp.getText().toString();

        Job job = jobManager.jobList.get(jobManager.folpos);

        updateJob(_DB.jobDao(), job, new TODO() {
            @Override
            public void onSuccess() {

                dialog.dismiss();
                Toast.makeText(getContext(), "Perubahan disimpan !!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCallBack(int key, String data) {

            }
        });
    }
}