package com.bincn.views.vote;

import android.animation.AnimatorSet;
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
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import com.bincn.views.utils.DeviceUtils;

/**
 * 左右百分比控件
 * Created by bin on 2019/5/21.
 */
public class VotePercentResultView extends View implements ViewTreeObserver.OnGlobalLayoutListener {

    private final String TAG = getClass().getSimpleName();

    private Paint mLeftPaint, mRightPaint, mTextPaint;
    /**
     * 控件的宽高
     */
    private int mHeight, mWidth;
    /**
     * 中间间隔
     */
    private int mMiddleGap;
    private int mMiddleDefaultGap = 20;
    /**
     * 左右的百分比
     */
    private int mLeftOriginPercent, mRightOriginPercent;
    private int mLeftFinalPercent, mRightFinalPercent;
    /**
     * 最大最小占比
     */
    private int maxPercent = 88, minPercent = 12;
    /**
     * 左右实际的宽度
     */
    private int mLeftWidth, mRightWidth;
    private int mRadius;
    private float mLeftAnimValue, mRightAnimValue;
    private ValueAnimator mLeftValueAnimator, mRightValueAnimator;
    private int ANIM_DURATION = 800;
    private boolean mIsShowAnim = true;
    /**
     * 水平便宜宽度
     */
    private int mOffsetWidth;
    private ViewTreeObserver mViewTreeObserver;

    public VotePercentResultView(Context context) {
        super(context);
        init(context);
    }

    public VotePercentResultView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VotePercentResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mMiddleGap = DeviceUtils.px2dip(getContext(), mMiddleDefaultGap);

        mLeftPaint = new Paint();
        mLeftPaint.setAntiAlias(true);
        mLeftPaint.setColor(Color.parseColor("#5C9EFF"));

        mRightPaint = new Paint();
        mRightPaint.setAntiAlias(true);
        mRightPaint.setColor(Color.parseColor("#FF74B9"));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(DeviceUtils.dip2px(getContext(), 16));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        drawLeft(canvas);
        drawRight(canvas);
        canvas.restore();
    }

    /**
     * 右半部分
     */
    private void drawRight(Canvas canvas) {
        // 画圆弧
        RectF rectF = new RectF(mWidth - mHeight, 0, mWidth, mHeight);
        Path path = new Path();
        path.addArc(rectF, -90, 180);

        // 画矩形
        path.lineTo(mWidth - mRightAnimValue + mOffsetWidth - mHeight, mHeight);
        path.lineTo(mWidth - mRightAnimValue - mOffsetWidth - mHeight, 0);
        path.lineTo(mWidth - mRadius, 0);
        path.close();
        canvas.drawPath(path, mRightPaint);

        // 画文字
        drawRightText(canvas);
    }

    private void drawRightText(Canvas canvas) {
        Rect rect = new Rect(mLeftWidth + mMiddleGap, 0, mWidth, mHeight);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);

        canvas.drawText(mRightOriginPercent + "%", rect.centerX(), baseLineY, mTextPaint);
    }

    /**
     * 左半部分
     */
    private void drawLeft(Canvas canvas) {
        // 画圆弧
        RectF rectF = new RectF(0, 0, mHeight, mHeight);
        Path path = new Path();
        path.addArc(rectF, -90, -180);

        // 画矩形
        path.lineTo(mLeftAnimValue + mOffsetWidth + mHeight, mHeight);
        path.lineTo(mLeftAnimValue - mOffsetWidth + mHeight, 0);
        path.lineTo(mRadius, 0);
        path.close();
        canvas.drawPath(path, mLeftPaint);

        // 画文字
        drawLeftText(canvas);
    }

    private void drawLeftText(Canvas canvas) {
        Rect rect = new Rect(0, 0, mLeftWidth - mMiddleGap, mHeight);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, paint);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);

        canvas.drawText(mLeftOriginPercent + "%", rect.centerX(), baseLineY, mTextPaint);
    }

    public void startAnim() {
        if (mLeftValueAnimator == null) {
            mLeftValueAnimator = ValueAnimator.ofFloat(0, mLeftWidth - mHeight);
            mLeftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mLeftAnimValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
        }
        if (mRightValueAnimator == null) {
            mRightValueAnimator = ValueAnimator.ofFloat(0, mRightWidth - mHeight);
            mRightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRightAnimValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(mLeftValueAnimator, mRightValueAnimator);
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        if (!animatorSet.isRunning()) {
            animatorSet.start();
        }
    }

    /**
     * 设置百分比
     *
     * @param leftPercent  左
     * @param rightPercent 右
     */
    public void setPercent(int leftPercent, int rightPercent) {
        mLeftOriginPercent = leftPercent;
        mRightOriginPercent = rightPercent;
        mLeftFinalPercent = leftPercent;
        mRightFinalPercent = rightPercent;
        // 如果有百分比为 0，给个默认值
        if (leftPercent == 0 && rightPercent == 0) {
            mLeftFinalPercent = 50;
            mRightFinalPercent = 50;
        } else if (leftPercent < minPercent) {
            mLeftFinalPercent = minPercent;
            mRightFinalPercent = maxPercent;
        } else if (rightPercent < minPercent) {
            mLeftFinalPercent = maxPercent;
            mRightFinalPercent = minPercent;
        }
        mViewTreeObserver = getViewTreeObserver();
        if (mViewTreeObserver.isAlive()) {
            mViewTreeObserver.addOnGlobalLayoutListener(this);
        }
    }

    /**
     * 设置画笔颜色
     */
    private void setPaintColor() {
        // 右边渐变色
        LinearGradient rightLinearGradient = new LinearGradient(mWidth - mRadius, mRadius, mWidth - mRightWidth, mRadius,
                Color.parseColor("#FF86DF"), Color.parseColor("#FF73C9"), Shader.TileMode.CLAMP);
        mRightPaint.setShader(rightLinearGradient);

        // 左边渐变色
        LinearGradient leftLinearGradient = new LinearGradient(mRadius, mRadius, mLeftWidth, mRadius,
                Color.parseColor("#79C0FF"), Color.parseColor("#589AFF"), Shader.TileMode.CLAMP);
        mLeftPaint.setShader(leftLinearGradient);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLeftValueAnimator != null) {
            mLeftValueAnimator.cancel();
            mLeftValueAnimator.removeAllUpdateListeners();
            mLeftValueAnimator = null;
        }
        if (mRightValueAnimator != null) {
            mRightValueAnimator.cancel();
            mRightValueAnimator.removeAllUpdateListeners();
            mRightValueAnimator = null;
        }
    }

    public void reset() {
        mLeftAnimValue = 0;
        mRightAnimValue = 0;
        mLeftOriginPercent = 0;
        mRightOriginPercent = 0;
        mLeftFinalPercent = 0;
        mRightFinalPercent = 0;
    }

    public void setShowAnim(boolean showAnim) {
        mIsShowAnim = showAnim;
    }

    @Override public void onGlobalLayout() {
        Log.i(TAG, "onGlobalLayout: height : " + getHeight() + ", width : " + getWidth());

        mWidth = getWidth();
        mHeight = getHeight();
        mRadius = mHeight / 2;
        mOffsetWidth = mRadius / 3;

        mLeftWidth = (mWidth - mMiddleGap) * mLeftFinalPercent / 100;
        mRightWidth = (mWidth - mMiddleGap) * mRightFinalPercent / 100;

        setPaintColor();
        if (!mIsShowAnim) {
            mLeftAnimValue = mLeftWidth - mHeight;
            mRightAnimValue = mRightWidth - mHeight;
            invalidate();
        } else {
            startAnim();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mViewTreeObserver.removeOnGlobalLayoutListener(this);
        } else {
            mViewTreeObserver.removeGlobalOnLayoutListener(this);
        }
    }
}
