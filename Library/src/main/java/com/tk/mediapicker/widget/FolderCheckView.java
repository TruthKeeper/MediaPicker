package com.tk.mediapicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tk.mediapicker.R;
import com.tk.mediapicker.utils.ThemeUtils;


/**
 * Created by TK on 2016/9/27.
 * Circle And Ring
 */

public class FolderCheckView extends View {
    public static final float RADIUS = 0.3f;
    public static final float STROKE = 0.1f;
    private float stroke;
    private float radius;
    private Paint paint = new Paint();
    private boolean isChecked;

    public FolderCheckView(Context context) {
        super(context);
        init();
    }

    public FolderCheckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FolderCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FolderCheckView);
            isChecked = array.getBoolean(R.styleable.FolderCheckView_checked, false);
            array.recycle();
        }
    }

    private void init() {
        //硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(ThemeUtils.themeColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, (getWidth() - stroke) / 2, paint);
        if (isChecked) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, radius, paint);
        }
    }

    public void setChecked(boolean checked) {
        if (isChecked == checked) {
            return;
        }
        isChecked = checked;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        stroke = w * STROKE;
        radius = w * RADIUS;
        paint.setStrokeWidth(stroke);
    }
}
