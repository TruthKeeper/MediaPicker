package com.tk.mediapicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tk.mediapicker.R;
import com.tk.mediapicker.utils.DensityUtil;


/**
 * Created by TK on 2016/9/27.
 */

public class FolderLayout extends RelativeLayout {
    private Paint paint = new Paint();
    private Path path_1 = new Path();
    private Path path_2 = new Path();
    private int stroke;
    private int dark_v0;
    private int dark_v1;

    public FolderLayout(Context context) {
        this(context, null);
    }

    public FolderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        stroke = DensityUtil.dp2px(context, 1);
        paint.setStrokeWidth(stroke);
        paint.setColor(0xFFCECECE);
        dark_v0 = getResources().getColor(R.color.dark_v0);
        dark_v1 = getResources().getColor(R.color.dark_v1);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        paint.setColor(dark_v1);
        canvas.drawPath(path_1, paint);
        paint.setColor(dark_v0);
        canvas.drawPath(path_2, paint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        path_1.reset();
        path_1.moveTo(1.5f * stroke, h - 2.5f * stroke);
        path_1.lineTo(w - 2.5f * stroke, h - 2.5f * stroke);
        path_1.lineTo(w - 2.5f * stroke, 1.5f * stroke);

        path_2.reset();
        path_2.moveTo(3f * stroke, h - 0.5f * stroke);
        path_2.lineTo(w - 0.5f * stroke, h - 0.5f * stroke);
        path_2.lineTo(w - 0.5f * stroke, 3f * stroke);

    }
}
