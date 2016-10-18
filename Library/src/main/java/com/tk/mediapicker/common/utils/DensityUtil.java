package com.tk.mediapicker.common.utils;

import android.content.Context;

public final class DensityUtil {

    public static final int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}