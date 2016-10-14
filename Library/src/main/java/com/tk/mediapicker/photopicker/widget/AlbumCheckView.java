package com.tk.mediapicker.photopicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

import com.tk.mediapicker.photopicker.PhotoPicker;
import com.tk.mediapicker.photopicker.utils.DensityUtil;


/**
 * Created by TK on 2016/9/27.
 */

public class AlbumCheckView extends View {
    public static final PorterDuffXfermode pd = new PorterDuffXfermode(PorterDuff.Mode.XOR);
    public static final float PADDING = 1.5f;
    public static final int RADIUS = 2;
    public static final float STROKE = 2f;
    private Paint paint = new Paint();
    private int padding;
    private int radius;

    private float out[];
    private RectF innerR;
    private ShapeDrawable initDrawable;
    private ShapeDrawable checkDrawable;
    private boolean isCheck;
    private Path path = new Path();
    private int themeColor;

    public AlbumCheckView(Context context) {
        super(context);
        init();
    }


    public AlbumCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        themeColor = PhotoPicker.themeColor;

        padding = DensityUtil.dp2px(getContext(), PADDING);
        radius = DensityUtil.dp2px(getContext(), RADIUS);

        out = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        //内部矩形与外部的距离
        innerR = new RectF(padding, padding, padding, padding);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DensityUtil.dp2px(getContext(), STROKE));
        paint.setXfermode(pd);

        initDrawable = new ShapeDrawable(new RoundRectShape(out, innerR, null));
        initDrawable.getPaint().setStyle(Paint.Style.FILL);
        initDrawable.getPaint().setColor(Color.WHITE);
        checkDrawable = new ShapeDrawable(new RoundRectShape(out, null, null));
        checkDrawable.getPaint().setStyle(Paint.Style.FILL);
        checkDrawable.getPaint().setColor(themeColor);
    }

    public boolean isChecked() {
        return isCheck;
    }

    public void setChecked(boolean check) {
        if (this.isCheck == check) {
            return;
        }
        this.isCheck = !this.isCheck;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isCheck) {
            checkDrawable.draw(canvas);
            canvas.drawPath(path, paint);
        } else {
            initDrawable.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int realW = w - getPaddingLeft() - getPaddingRight();
        int realH = h - getPaddingTop() - getPaddingBottom();
        initDrawable.setBounds(getPaddingLeft(), getPaddingTop(),
                w - getPaddingRight(),
                h - getPaddingBottom());
        checkDrawable.setBounds(getPaddingLeft(), getPaddingTop(),
                w - getPaddingRight(),
                h - getPaddingBottom());
        path.reset();
        path.moveTo(getPaddingLeft() + realW * 0.18f, getPaddingTop() + realH * 0.5f);
        path.lineTo(getPaddingLeft() + realW * 0.4f, getPaddingTop() + realH * 0.72f);
        path.lineTo(getPaddingLeft() + realW * 0.82f, getPaddingTop() + realH * 0.32f);
    }
}
