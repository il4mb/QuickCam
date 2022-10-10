package com.ilhamb.quickcam;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ilhamb.quickcam.databinding.ActivityFakeCameraBinding;
import com.ilhamb.quickcam.utilities.ImageTools;
import com.ilhamb.quickcam.utilities.RealPathUtil;
import com.ilhamb.quickcam.utilities.jobManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeCameraActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 2025;

    ActivityFakeCameraBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (jobManager.jobList.size() > 0) {

            ConfigFileHandle();

        } else {

            binding = ActivityFakeCameraBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            this.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_IMAGE_CAPTURE:
                    Bitmap img = null;

                    try {

                        ImageTools imageTools = new ImageTools(getApplicationContext(), data.getData());

                        imageTools.CropPresisi();
                        imageTools.stampDateGeo();

                        handleImage(imageTools.getBitmap());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    this.finish();
                    break;

            }

        }

        if (resultCode == RESULT_CANCELED) {

            this.finish();

        }

    }

    private void handleImage(Bitmap img) throws IOException {
        // CLIP DATA FROM CALLER ACT
            ClipData clipData = getIntent().getClipData();

            if (clipData != null) {

                for (int i = 0; i < clipData.getItemCount(); i++) {

                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();

                    OutputStream inputStream = null;

                    inputStream = this.getContentResolver().openOutputStream(uri);
                    img.compress(Bitmap.CompressFormat.JPEG, 50, inputStream);

                    inputStream.flush();
                    inputStream.close();
                }

            } else {

                getIntent().putExtra("data", img);

            }

            setResult(RESULT_OK, getIntent());
            FakeCameraActivity.this.finish();

    }

    private void ConfigFileHandle(){
        try {

            int currentPrefixPos = jobManager.prepos;
            int currentDirectoryPos = jobManager.folpos;

            if(jobManager.prefixList.size()-1 < currentPrefixPos)
                currentPrefixPos = 0;

            if(jobManager.jobList.size()-1 < currentDirectoryPos)
                currentDirectoryPos = 0;

            String preFix = jobManager.prefixList.get(currentPrefixPos).value;
            String directory = jobManager.jobList.get(currentDirectoryPos).value;

            //Log.d("FOLDER", directory);
            //Log.d("PREFIX", preFix);

            File[] files = getChildFileList(Uri.parse(directory));
           // Log.d("LIST FILE", new Gson().toJson(files));

            List<File> containsFile = new ArrayList<>();

            for (File file : files) {
                if (file.getName().contains(preFix)) {
                    containsFile.add(file);
                }
            }
            File file = containsFile.size() > 0 ? getRandSingleFile(containsFile) : null;
            if (file != null) {
                try {

                    Uri fileUri = Uri.fromFile(file);
                    ImageTools imageTools = new ImageTools(getApplicationContext(), fileUri);
                    imageTools.CropPresisi();
                    imageTools.stampDateGeo();

                    jobManager.forwardPrefix();

                    handleImage(imageTools.getBitmap());

                    //Log.d("OUTPUT", fileUri.toString());

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Sayang sekali : " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }

            } else {
                this.finish();
                Toast.makeText(getApplicationContext(),
                        "File yang cocok dengan prefix tidak di temukan !",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {

            Log.d("ERROR MESSAGE", e.getMessage());
        }
    }

    public File getRandSingleFile(List<File> files){

        final int max = files.size();
        final int random = new Random().nextInt(max - 1);
        return files.get(random);

    }

    public File[] getChildFileList(Uri uri){

        File file = new File(uri.toString());
        return file.listFiles();
    }


}
