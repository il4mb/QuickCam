package com.ilhamb.quickcam.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ilhamb.quickcam.MainActivity;
import com.ilhamb.quickcam.R;
import com.ilhamb.quickcam.adapter.ModalListViewAdapter;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.database.Prefix;
import com.ilhamb.quickcam.databinding.FragmentDialogBinding;
import com.ilhamb.quickcam.utilities.TODO;

import java.util.List;
import java.util.stream.Stream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListModalDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListModalDialog extends DialogFragment {
    private static int viewPosition;

    public ListModalDialog() {
        // Required empty public constructor
    }


    public static ListModalDialog newInstance(int position) {

        ListModalDialog fragment = new ListModalDialog();
        viewPosition = position;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentDialogBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDialogBinding.inflate(inflater);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    TODO todo;
    Dialog dialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = getDialog();

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setLayout(width, height);

        List<Prefix> preList = MainActivity.jobManager.prefixList;

        if(preList != null) {

            ModalListViewAdapter adapter = new ModalListViewAdapter(getContext(), preList);
            binding.PreList.setAdapter(adapter);
        }

        binding.PreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MainActivity.jobManager.prepos = i;

                if(preList != null) {

                    ModalListViewAdapter adapter = new ModalListViewAdapter(getContext(), preList);
                    binding.PreList.setAdapter(adapter);

                    ListModalDialog.this.dialog.dismiss();

                    if(todo != null) {

                        todo.onCallBack(viewPosition, preList.get(i).value);
                    }
                }

            }
        });

    }

    public void onSave(TODO todo) {
        this.todo = todo;
    }
}