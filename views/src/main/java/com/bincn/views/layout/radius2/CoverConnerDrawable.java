package com.bincn.views.layout.radius2;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

public class CoverConnerDrawable extends Drawable {

    private CoverConner mCoverConner;
    private Rect mRect = new Rect();
    private Path mPath = new Path();
    private Paint mPaint;
    private int mRadius;

    public CoverConnerDrawable(CoverConner coverConner) {
        mCoverConner = coverConner;
        mRadius =
                (int) TypedValue.applyDimension(coverConner.getUnit(), coverConner.getSize(),
                        Resources
                                .getSystem().getDisplayMetrics());
        initTextPaint();
    }

    private void initTextPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mRect.set(left, top, right, bottom);
        mPath.reset();
        //左上
        mPath.addArc(new RectF(0, 0, mRadius * 2, mRadius * 2), 180, 90);
        mPath.lineTo(0, 0);
        mPath.lineTo(0, mRadius);
        //右上
        mPath.addArc(new RectF(right - mRadius * 2, 0, right, mRadius * 2), 270, 90);
        mPath.lineTo(right, 0);
        mPath.lineTo(right - mRadius, 0);
        //右下
        mPath.addArc(new RectF(right - mRadius * 2, bottom - mRadius * 2, right, bottom), 0, 90);
        mPath.lineTo(right, bottom);
        mPath.lineTo(right, bottom - mRadius);
        //左下
        mPath.addArc(new RectF(0, bottom - mRadius * 2, mRadius * 2, bottom), 90, 90);
        mPath.lineTo(0, bottom);
        mPath.lineTo(mRadius, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(mCoverConner.getColor()
                .getColorForState(getState(), mCoverConner.getColor().getDefaultColor()));
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public void clearColorFilter() {
        mPaint.setColorFilter(null);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public boolean isStateful() {
        return mCoverConner.getColor().isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        invalidateSelf();
        return true;
    }
}
