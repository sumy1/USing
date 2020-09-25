package com.usingstudioo.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;

public class NLMaterialEditText extends TextInputEditText {
    public NLMaterialEditText(Context context) {
        super(context);
        createFont();
    }

    public NLMaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public NLMaterialEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/FontsFree-Net-Proxima-Nova-Light.otf");
        setTypeface(font);
    }
}
