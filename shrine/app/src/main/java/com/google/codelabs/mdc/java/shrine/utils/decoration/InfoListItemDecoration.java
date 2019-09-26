package com.google.codelabs.mdc.java.shrine.utils.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class InfoListItemDecoration extends RecyclerView.ItemDecoration {
    private int verticalPadding;
    private int horisontalPadding;

    public InfoListItemDecoration(int verticalPadding, int horisontalPadding) {
        this.verticalPadding = verticalPadding;
        this.horisontalPadding = horisontalPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = horisontalPadding;
        outRect.right = horisontalPadding;
        outRect.top = verticalPadding;
        outRect.bottom = verticalPadding;
    }
}
