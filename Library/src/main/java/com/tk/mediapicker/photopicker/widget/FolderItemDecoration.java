package com.tk.mediapicker.photopicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tk.mediapicker.common.DensityUtil;


/**
 * Created by TK on 2016/9/26.
 */
public class FolderItemDecoration extends RecyclerView.ItemDecoration {
    //网格线粗细
    public static final int DIVIDER = 1;
    private static final ColorDrawable DRAWABLE = new ColorDrawable(0xFFECECEC);
    private Context mContext;
    private int divider;

    public FolderItemDecoration(Context mContext) {
        this.mContext = mContext;
        divider = DensityUtil.dp2px(mContext, DIVIDER);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, divider);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + divider;
            DRAWABLE.setBounds(left, top, right, bottom);
            DRAWABLE.draw(c);
        }
    }

}
