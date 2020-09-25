package com.usingstudioo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.skyfishjy.library.RippleBackground;
import com.usingstudioo.R;

public class StudioInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_info);

        RippleBackground ripple = findViewById(R.id.ripple);
        ripple.startRippleAnimation();
        ripple.setVisibility(View.VISIBLE);

        findViewById(R.id.card_studio).setOnClickListener(v -> {
            Intent mainIntent = new Intent(StudioInfoActivity.this, StudioPlayerActivity.class);
            startActivity(mainIntent);
            finish();
        });

        findViewById(R.id.bt_back).setOnClickListener(v -> {
            Intent mainIntent = new Intent(StudioInfoActivity.this, WelcomeActivity.class);
            startActivity(mainIntent);
            finish();
        });

    }

}
