package com.usingstudioo;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private static float mHeight;
    private static float mWidth;
    private static float mDensity;
    private static int screenSize;
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMetrics();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void getMetrics() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mDensity = getResources().getDisplayMetrics().density;
        mHeight = outMetrics.heightPixels;
        mWidth = outMetrics.widthPixels;
        screenSize =
                getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

    }

    public static boolean isDialog() {
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return true;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return false;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return false;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return true;
            default:
                return false;
        }
    }

    public static int getSpan() {
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return 3;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 3;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 2;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return 1;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                return 0;
            case Configuration.SCREENLAYOUT_SIZE_MASK:
                return 0;
            default:
                return 3;
        }
    }

    public static int getGallarySpan() {
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return 4;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return 3;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return 2;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return 4;
            default:
                return 3;
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavigatyionBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public boolean hasNavBar() {
        int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && getResources().getBoolean(id);
    }

    public static float getDensity() {
        return mDensity;
    }

    public static float getHeight() {
        return mHeight;
    }

    public static float getWidth() {
        return mWidth;
    }
}
