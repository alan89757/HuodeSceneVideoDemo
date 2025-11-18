package com.bokecc.vod.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.bokecc.vod.R;

@SuppressLint("AppCompatCustomView")
public class TriangleView extends View {

    private static final int DEFAULT_COLOR = 0xff757575;

    private Paint mPaint;
    private Path mTrianglePath;
    private int mColor;
    private float corners;
    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TriangleView);
            mColor = a.getColor(R.styleable.TriangleView_tr_color, DEFAULT_COLOR);
            corners = a.getDimension(R.styleable.TriangleView_tr_corners,20);
            a.recycle();
        } else {
            mColor = DEFAULT_COLOR;
        }

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
    }

    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            if (mPaint != null) {
                mPaint.setColor(color);
            }
            mTrianglePath = null;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(getTrianglePath(), mPaint);
    }

    private Path getTrianglePath() {
        mTrianglePath = new Path();
        int width = getWidth();
        int height = getHeight();
        Point p1, p2, p3,p4;
        RectF rectF = new RectF(0,0,corners*2,corners*2);
        p1 = new Point((int) corners, 0);
        p2 = new Point(width, 0);
        p3 = new Point(0, height);
        p4 = new Point(0, (int) corners);
        mTrianglePath.moveTo(p1.x, p1.y);
        mTrianglePath.lineTo(p2.x, p2.y);
        mTrianglePath.lineTo(p3.x, p3.y);
        mTrianglePath.lineTo(p4.x, p4.y);
        mTrianglePath.arcTo(rectF,180F,270F);
        return mTrianglePath;
    }
}
