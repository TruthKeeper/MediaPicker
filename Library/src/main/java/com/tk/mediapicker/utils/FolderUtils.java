package com.tk.mediapicker.utils;

import android.support.v7.widget.RecyclerView;

import com.tk.mediapicker.R;


/**
 * Created by TK on 2016/9/28.
 */

public class FolderUtils {
    /**
     * 动态设置高度
     *
     * @param recyclerView
     * @param limit
     */
    public static final void setFolderHeight(final RecyclerView recyclerView, final int limit) {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            return;
        }
        int count = recyclerView.getAdapter().getItemCount();
        int realCount = Math.min(limit, count);
        int height = recyclerView.getResources().getDimensionPixelOffset(R.dimen.floder_height);
        recyclerView.getLayoutParams().height = height * realCount + recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();
    }
}
