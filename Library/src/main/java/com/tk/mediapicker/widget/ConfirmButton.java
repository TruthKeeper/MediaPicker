package com.tk.mediapicker.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.tk.mediapicker.utils.DensityUtil;
import com.tk.mediapicker.utils.ThemeUtils;

/**
 * Created by TK on 2016/9/28.
 * Like Complete Button
 */

public class ConfirmButton extends TextView {

    public static final int[][] STATES = new int[][]{
            {android.R.attr.state_pressed, android.R.attr.state_enabled},
            {android.R.attr.state_enabled},
            {0}};
    public static final int[] TEXT_COLOR = new int[]{
            0xFFBBBBBB,
            0xFFFFFFFF,
            0xFF999999};
    public static final int RADIUS = 2;
    private Paint paint = new Paint();
    private int[] colors;

    private ShapeDrawable enableDrawable;
    private ShapeDrawable pressDrawable;
    private ShapeDrawable nullDrawable;

    public ConfirmButton(Context context) {
        this(context, null);
    }

    public ConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setClickable(true);
        setEnabled(false);
        setTextSize(14);
        paint.setDither(true);
        paint.setAntiAlias(true);
        colors = ThemeUtils.initNewColor(ThemeUtils.themeColor);
        int r = DensityUtil.dp2px(getContext(), RADIUS);
        float[] outR = new float[]{r, r, r, r, r, r, r, r};
        RoundRectShape enableShape = new RoundRectShape(outR, null, null);
        RoundRectShape pressShape = new RoundRectShape(outR, null, null);
        RoundRectShape nullShape = new RoundRectShape(outR, null, null);


        enableDrawable = new ShapeDrawable(enableShape);
        enableDrawable.getPaint().setAntiAlias(true);
        enableDrawable.getPaint().setDither(true);
        enableDrawable.getPaint().setColor(colors[0]);

        pressDrawable = new ShapeDrawable(pressShape);
        pressDrawable.getPaint().setAntiAlias(true);
        pressDrawable.getPaint().setDither(true);
        pressDrawable.getPaint().setColor(colors[1]);

        nullDrawable = new ShapeDrawable(nullShape);
        nullDrawable.getPaint().setAntiAlias(true);
        nullDrawable.getPaint().setDither(true);
        nullDrawable.getPaint().setColor(colors[2]);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(STATES[0], pressDrawable);
        stateListDrawable.addState(STATES[1], enableDrawable);
        stateListDrawable.addState(STATES[2], nullDrawable);
        setBackgroundDrawable(stateListDrawable);
        setTextColor(new ColorStateList(STATES, TEXT_COLOR));
    }

}
