package com.usingstudioo.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NBTextView extends TextView {
    public NBTextView(Context context) {
        super(context);
        createFont();
    }

    public NBTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }



    public NBTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }
    //FontsFree-Net-Proxima-Nova-Light.otf
    //FontsFree-Net-Proxima-Nova-Sbold.otf
    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/FontsFree-Net-Proxima-Nova-Sbold.otf");
        setTypeface(font);
    }
}
