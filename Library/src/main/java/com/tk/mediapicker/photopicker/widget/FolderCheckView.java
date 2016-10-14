package com.tk.mediapicker.photopicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tk.mediapicker.photopicker.PhotoPicker;


/**
 * Created by TK on 2016/9/27.
 */

public class FolderCheckView extends View {
    public static final float RADIUS = 0.3f;
    public static final float STROKE = 0.1f;
    private int stroke;
    private int radius;
    private Paint paint = new Paint();

    public FolderCheckView(Context context) {
        super(context);
        init();
    }


    public FolderCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(PhotoPicker.themeColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, (getWidth() - stroke) >> 1, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, radius, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        stroke = (int) (w * STROKE);
        radius = (int) (w * RADIUS);
        paint.setStrokeWidth(stroke);
    }
}
