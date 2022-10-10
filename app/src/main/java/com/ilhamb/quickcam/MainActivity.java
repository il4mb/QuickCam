package com.ilhamb.quickcam;

import static android.content.ContentValues.TAG;
import static com.ilhamb.quickcam.utilities.jobManager.listFolder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.ilhamb.quickcam.adapter.ListViewAdapter;
import com.ilhamb.quickcam.database.DataBase;
import com.ilhamb.quickcam.databinding.ActivityMainBinding;
import com.ilhamb.quickcam.utilities.RealPathUtil;
import com.ilhamb.quickcam.utilities.jobManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DataBase _DB;

    public final boolean TEST = false;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        _DB = DataBase.getDbInstance(getApplicationContext());

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 9999:

                Uri dataUri = data.getData();
                String realPath = RealPathUtil.getRealPathFromURI( dataUri );
                Log.d("REAL PATH", realPath);

                listFolder.add(data.getData().toString());

                ListViewAdapter listAdapter = new ListViewAdapter(this, listFolder);
                binding.listView.setAdapter(listAdapter);

                List<File> files = getChildFileList(data.getData());

                Log.d("folder", new Gson().toJson(data.getData().toString()));
                Log.d("array LISt", new Gson().toJson(files));
                break;
        }
    }

    public List<File> getChildFileList(Uri uri){

        File file = new File(RealPathUtil.getRealPathFromURI(uri));
        File[] directories = file.listFiles();

        Log.d("FILE FOL", new Gson().toJson(directories));

        return null;
    }

    private void updateListView() {
        if(listFolder.size() > 0) {
            ListViewAdapter listAdapter = new ListViewAdapter(this, listFolder);
            binding.listView.setAdapter(listAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
    }

    private void testMode() {
        jobManager.listPrefix.add("images");

        Intent intent = new Intent(MainActivity.this, TestActivity.class);
        startActivity(intent);
    }
}