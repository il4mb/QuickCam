package com.ilhamb.quickcam;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ilhamb.quickcam.databinding.ActivityFakeCameraBinding;
import com.ilhamb.quickcam.utilities.ImageTools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FakeCameraActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 2025;

    ActivityFakeCameraBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFakeCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        this.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
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

                       // imageTools.CropPresisi();
                        imageTools.stampDateGeo(binding.frameStamp.getRoot());

                        handleImage(imageTools.getBitmap());

                        Log.d("GALLERY RESULT", new Gson().toJson(img));

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
}
