package com.usingstudioo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.usingstudioo.R;

public class StudioSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_splash);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Do something after 1000ms
            Intent mainIntent = new Intent(StudioSplashActivity.this, StudioInfoActivity.class);
            startActivity(mainIntent);
            finish();
        }, 1800);

    }

}
