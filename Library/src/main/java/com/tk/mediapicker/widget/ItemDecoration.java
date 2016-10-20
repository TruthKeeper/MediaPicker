package com.tk.mediapicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tk.mediapicker.utils.DensityUtil;


/**
 * Created by TK on 2016/9/26.
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {
    public static final int DIVIDER = 2;
    private static final ColorDrawable DRAWABLE = new ColorDrawable(0xFF191919);
    private int divider;
    private int span;

    public ItemDecoration(Context mContext, int span) {
        this.span = span;
        divider = DensityUtil.dp2px(mContext, DIVIDER);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int poition = parent.getChildAdapterPosition(view);
        if (isLastCount(parent, poition)) {
            //最后一个都不需要
            outRect.set(0, 0, 0, 0);
        } else if (isLastColum(poition)) {
            //最后一列不需要右边
            outRect.set(0, 0, 0, divider);
        } else if (isLastRaw(parent, poition)) {
            //最后一行不需要底部
            outRect.set(0, 0, divider, 0);
        } else {
            outRect.set(0, 0, divider, divider);
        }

    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + divider;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + divider;
            DRAWABLE.setBounds(left, top, right, bottom);
            DRAWABLE.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + divider;

            DRAWABLE.setBounds(left, top, right, bottom);
            DRAWABLE.draw(c);
        }
    }


    /**
     * 是否最后一列
     *
     * @param position
     * @return
     */
    private boolean isLastColum(int position) {
        return (position + 1) % span == 0;
    }

    /**
     * 是否最后一行
     *
     * @param parent
     * @param position
     * @return
     */
    private boolean isLastRaw(RecyclerView parent, int position) {
        int count = parent.getAdapter().getItemCount();
        return position >= ((count - 1) / span) * span;
    }

    /**
     * 是否最后一个
     *
     * @param parent
     * @param position
     * @return
     */
    private boolean isLastCount(RecyclerView parent, int position) {
        int count = parent.getAdapter().getItemCount();
        return position == count - 1;
    }
}
