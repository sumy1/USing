package com.usingstudioo.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public VerticalSpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        /*if(parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }*/

    }
}
