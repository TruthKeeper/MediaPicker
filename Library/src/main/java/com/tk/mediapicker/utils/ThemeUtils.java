package com.tk.mediapicker.utils;

import android.graphics.Color;

/**
 * Created by TK on 2016/9/28.
 */

public class ThemeUtils {
    public static int themeColor = 0xFF45C01A;
    /**
     * 生成新的颜色
     *
     * @param color
     * @return
     */
    public static final int[] initNewColor(int color) {
        int[] colors = new int[3];
        String colorStr = Integer.toHexString(color);
        if (colorStr.length() == 8) {
            colorStr = colorStr.substring(2, 8);
        }
        colors[0] = Color.parseColor("#ff" + colorStr);
        colors[1] = Color.parseColor("#80" + colorStr);
        colors[2] = Color.parseColor("#40" + colorStr);
        return colors;
    }
}
