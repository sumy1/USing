package com.usingstudioo.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.SwitchCompat;

public class NBSwitch extends SwitchCompat {
    public NBSwitch(Context context) {
        super(context);
        createFont();
    }

    public NBSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }



    public NBSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/FontsFree-Net-Proxima-Nova-Sbold.otf");
        setTypeface(font);
    }
}
