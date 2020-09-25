package com.usingstudioo.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class NBButton extends Button {
    public NBButton(Context context) {
        super(context);
        createFont();
    }

    public NBButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }



    public NBButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
