package com.bincn.views.vote;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.bincn.views.utils.DeviceUtils;

/**
 * Created by bin on 2019/5/21.
 */
public class PathView extends View {

    private Paint mPaint;
    private int mHeight;
    private int mWidth;
    private float mLeftAnimValue;
    private boolean mStartLeftAnim;
    private Paint mArcPaint;
    private int mGap;
    private float mLeftRate;
    private float mRightRate;
    private int mLeftWidth;
    private int mRightWidth;
    private int mRadius;
    private float mRightAnimValue;
    private boolean mStartRightAnim;
    private ValueAnimator mLeftValueAnimator;
    private ValueAnimator mRightValueAnimator;

    public PathView(Context context) {
        super(context);
        init(context);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        mGap = DeviceUtils.px2dip(getContext(), 20);
        mLeftRate = 0.5f;
        mRightRate = 0.5f;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = getHeight();
        mWidth = getWidth();
        if (mLeftWidth == 0) {
            mLeftWidth = Math.round((mWidth - mGap) * mLeftRate);
        }
        if (mRightWidth == 0) {
            mRightWidth = Math.round((mWidth - mGap) * mRightRate);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRadius = mHeight / 2;
        drawLeft(canvas);
        drawRight(canvas);
    }

    private void drawRight(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        if (!mStartRightAnim) {
            mStartRightAnim = true;
            mRightValueAnimator = ValueAnimator.ofFloat(0, mRightWidth - mRadius);
            mRightValueAnimator.setDuration(800);
            mRightValueAnimator.setInterpolator(new DecelerateInterpolator());
            mRightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRightAnimValue = (float) animation.getAnimatedValue();
                    invalidate();
                    Log.i("asd", "onAnimationUpdate: " + mRightAnimValue);
                }
            });
//            valueAnimator.start();
        }
        // 线性渐变色
        LinearGradient linearGradient = new LinearGradient(mWidth - mRadius, mRadius, mWidth - mRightWidth, mRadius,
                Color.parseColor("#FF86DF"), Color.parseColor("#FF73C9"), Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);

        // 画圆弧
        RectF rectF = new RectF(mWidth - mHeight, 0, mWidth, mHeight);
        Path path = new Path();
        path.addArc(rectF, -90, 180);

        // 画矩形
        path.lineTo(mWidth - mRadius - mRightAnimValue, mHeight);
        path.lineTo(mWidth - mHeight - mRightAnimValue, 0);
        path.moveTo(mWidth - mRadius, 0);
        canvas.drawPath(path, mPaint);

//        // 绘制圆弧
//        mPaint.setColor(Color.parseColor("#FF86DF"));
//        RectF rectF = new RectF(mWidth - mHeight, 0, mWidth, mHeight);
////        canvas.drawArc(rectF, -90, 180, false, mPaint);
////        canvas.drawCircle(mWidth - mRadius, mRadius, mRadius, mPaint);
//        Path arcPath = new Path();
//        arcPath.addArc(rectF, -135, 225);
//        canvas.drawPath(arcPath, mPaint);
//
//        // 画矩形
//        LinearGradient linearGradient = new LinearGradient(mWidth - mRadius, mRadius, mWidth - mRightWidth, mRadius,
//                Color.parseColor("#FF86DF"), Color.parseColor("#FF73C9"), Shader.TileMode.CLAMP);
//        mPaint.setShader(linearGradient);
//        Path path = new Path();
//        path.moveTo(mWidth - mRadius, 0);
//        path.lineTo(mWidth - mHeight - mRightAnimValue, 0);
//        path.lineTo(mWidth - mRadius - mRightAnimValue, mHeight);
//        path.lineTo(mWidth - mRadius, mHeight);
//        canvas.drawPath(path, mPaint);

        // 画文字
        drawRightText(canvas);
    }

    private void drawRightText(Canvas canvas) {
        Rect rect = new Rect(mLeftWidth + mGap - mRadius, 0, mWidth, mHeight);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);

        canvas.drawText("20%", rect.centerX(), baseLineY, textPaint);
    }

    private void drawLeft(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        if (!mStartLeftAnim) {
            mStartLeftAnim = true;
            mLeftValueAnimator = ValueAnimator.ofFloat(0, mLeftWidth - mHeight);
            mLeftValueAnimator.setDuration(800);
            mLeftValueAnimator.setInterpolator(new DecelerateInterpolator());
            mLeftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mLeftAnimValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
//            mLeftValueAnimator.start();
        }
        // 线性渐变色
        LinearGradient linearGradient = new LinearGradient(mRadius, mRadius, mLeftWidth, mRadius,
                Color.parseColor("#79C0FF"), Color.parseColor("#589AFF"), Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);

        // 画圆弧
        RectF rectF = new RectF(0, 0, mHeight, mHeight);
        Path path = new Path();
        path.addArc(rectF, -90, -180);

        // 画矩形
        path.lineTo(mLeftAnimValue + mHeight, mHeight);
        path.lineTo(mLeftAnimValue + mRadius, 0);
        path.lineTo(mRadius, 0);
        path.close();
        canvas.drawPath(path, mPaint);

        // 画文字
        drawLeftText(canvas);
    }

    private void drawLeftText(Canvas canvas) {
        Rect rect = new Rect(0, 0, mLeftWidth, mHeight);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);

        canvas.drawText("20%", rect.centerX(), baseLineY, textPaint);
    }

    public void startAnim() {
        if (mLeftValueAnimator != null) {
            mLeftValueAnimator.start();
        }
        if (mRightValueAnimator != null) {
            mRightValueAnimator.start();
        }
    }
}
