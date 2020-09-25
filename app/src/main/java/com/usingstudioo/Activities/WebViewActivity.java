package com.usingstudioo.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.usingstudioo.Models.SongModel;
import com.usingstudioo.R;

import static com.usingstudioo.Constants.Constants.kData;

public class WebViewActivity extends AppCompatActivity {
    WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_view);
        SongModel song = (SongModel)getIntent().getSerializableExtra(kData);
        assert song != null;
        String videoId = song.getYoutubeLink();
        Log.e("song name",videoId);

        browser = (WebView) findViewById(R.id.webview);
        browser.setWebViewClient(new MyBrowser());
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.clearCache(true);
        browser.clearHistory();// Add this
        browser.setBackgroundColor(Color.TRANSPARENT);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        browser.loadUrl(videoId);

        findViewById(R.id.bt_back).setOnClickListener(v -> finish());
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        browser.onPause();
        browser.clearHistory();
    }
}
