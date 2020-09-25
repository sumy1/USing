package com.usingstudioo.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class NLCheckBox extends CheckBox {
    public NLCheckBox(Context context) {
        super(context);
        createFont();
    }

    public NLCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }



    public NLCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/FontsFree-Net-Proxima-Nova-Light.otf");
        setTypeface(font);
    }
}
