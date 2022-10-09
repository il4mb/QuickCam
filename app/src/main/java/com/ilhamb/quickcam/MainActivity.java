package com.ilhamb.quickcam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ilhamb.quickcam.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public final boolean TEST = true;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(TEST == true) {

            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            startActivity(intent);

            this.finish();
        }

    }
}