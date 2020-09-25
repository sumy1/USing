package com.usingstudioo.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.usingstudioo.R;

public class StudioHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_help);
        findViewById(R.id.bt_back).setOnClickListener(v -> finish());

        findViewById(R.id.card_studio).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","info@usingstudio.com", null));
            //emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "U Sing! Studio App Support");
            //emailIntent.putExtra(Intent.EXTRA_TEXT, "write your query");
            startActivity(Intent.createChooser(emailIntent, "Send Email..."));
        });
    }
}
