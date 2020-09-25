package com.usingstudioo.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NLTextView extends TextView {
    public NLTextView(Context context) {
        super(context);
        createFont();
    }

    public NLTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }



    public NLTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/FontsFree-Net-Proxima-Nova-Light.otf");
        setTypeface(font);
    }
}
