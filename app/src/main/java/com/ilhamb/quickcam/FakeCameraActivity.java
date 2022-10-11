package com.ilhamb.quickcam;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ilhamb.quickcam.databinding.ActivityFakeCameraBinding;
import com.ilhamb.quickcam.utilities.ImageTools;
import com.ilhamb.quickcam.utilities.JobManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FakeCameraActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 2025;

    ActivityFakeCameraBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFakeCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (MainActivity.jobManager.jobList.size() > 0) {

            try {

                ConfigFileHandle();

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                this.finish();

            }

        } else {

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

                        try {

                            InputStream is = getContentResolver().openInputStream(data.getData());
                            Bitmap bitmap = BitmapFactory.decodeStream(is);

                            ImageTools imageTools = new ImageTools(getApplicationContext(), bitmap, data.getData());
                            imageTools.CropPresisi();
                            imageTools.stampDateGeo();
                            handleImage(imageTools.getBitmap());

                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                            this.finish();
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

    private void ConfigFileHandle() throws Exception {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                int cps = MainActivity.jobManager.prepos;
                int cds = MainActivity.jobManager.folpos;

                if (MainActivity.jobManager.prefixList.size() - 1 < cps)
                    cps = 0;

                if (MainActivity.jobManager.jobList.size() - 1 < cds)
                    cds = 0;

                String preFix = null;
                String directory = null;

                try {

                    preFix = MainActivity.jobManager.prefixList.get(cps).value;
                    directory = MainActivity.jobManager.jobList.get(cds).value;

                } catch (Exception e){  throw new Exception(e.getMessage()); }

                Log.d("FOLDER", directory);
                Log.d("PREFIX", preFix);

                File[] files = getChildFileList(Uri.parse(directory));

                Log.d("FILES", new Gson().toJson(files));

                if (files != null) {

                    final String pre = preFix;

                    files = Arrays.stream(files).filter(e -> e.getName().contains(pre)).toArray(File[]::new);
                    File file = files[new Random().nextInt(files.length)];

                    Uri fileUri = Uri.fromFile(file);
                    ImageTools imageTools = new ImageTools(getApplicationContext(), fileUri);
                    imageTools.CropPresisi();
                    imageTools.stampDateGeo();

                    MainActivity.jobManager.forwardPrefix();

                    handleImage(imageTools.getBitmap());

                } else throw new Exception("File yang cocok dengan prefix tidak di temukan !");
            }
    }

    public File[] getChildFileList(Uri uri) {

        File file = new File(uri.toString());
        return file.listFiles();
    }


}
