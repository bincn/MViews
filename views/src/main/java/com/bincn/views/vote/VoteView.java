package com.bincn.views.vote;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * Created by bin on 2019/5/21.
 */
public class VoteView extends View {

    private Paint mPaint;
    private int mHeight;
    private int mWidth;
    private float mValue;
    private boolean mStartAnim;
    private Paint mPaint1;

    public VoteView(Context context) {
        super(context);
        init(context);
    }

    public VoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = getHeight();
        mWidth = getWidth();
        Log.i("asd", "onLayout: " + mHeight + ", " + mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        if (!mStartAnim) {
            mStartAnim = true;
            Log.i("asd", "onFinishInflate: " + mHeight);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mHeight * 2);
            valueAnimator.setDuration(800);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    Log.i("asd", "onAnimationUpdate: " + value);
                    setTargetPercent((float) animation.getAnimatedValue());
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                }
            });
            valueAnimator.start();
        }

//        mPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
////        RectF rectF = new RectF(mHeight * 2 - mHeight / 2, 0, mHeight * 2 + mHeight / 2, mHeight);
////        canvas.drawRect(rectF , mPaint);
//
////        mPaint.setColor(Color.WHITE);
//        Path path1 = new Path();
////        path1.addRect(mHeight + mHeight / 2, 0, mHeight * 2 + mHeight / 2, mHeight, Path.Direction.CW);
//        path1.moveTo(mHeight * 2 - mHeight / 2, 0);
//        path1.lineTo(mHeight * 2 + mHeight / 2, 0);
//        path1.lineTo(mHeight * 2 + mHeight / 2, mHeight);
//        path1.lineTo(mHeight * 2, 0);
//        canvas.drawPath(path1, mPaint);

//        RectF rectF = new RectF(mHeight / 2, 0, mWidth / 2, mHeight);
//        canvas.drawRoundRect(rectF, mHeight / 2, mHeight / 2, mPaint);



        // 绘制圆弧
        mPaint.setColor(Color.parseColor("#79C0FF"));
        RectF rectF = new RectF(0, 0, mHeight, mHeight);
        canvas.drawArc(rectF, -90, -180, false, mPaint);

        // 画三角形
//        canvas.translate(0, mHeight / 2);
//        Path path = new Path();
//        path.lineTo(mHeight / 2, mHeight / 2);
//        path.lineTo(mHeight / 2, 0);
//        canvas.drawPath(path, mPaint);

//        canvas.translate(0, mHeight / 2);
        LinearGradient linearGradient = new LinearGradient(mHeight / 2, mHeight / 2, mHeight * 2 + mHeight / 2, mHeight / 2, Color.parseColor("#79C0FF"), Color.parseColor("#589AFF"), Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        Path path = new Path();
        path.moveTo(mHeight / 2, 0);
        path.lineTo(mValue + mHeight / 2, 0);
        path.lineTo(mValue + mHeight / 2, mHeight);
        path.lineTo(mHeight / 2, mHeight);

//        path.op(path1, Path.Op.DIFFERENCE);
        canvas.drawPath(path, mPaint);



//        mPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
//        RectF rectF = new RectF(mHeight * 2 - mHeight / 2, 0, mHeight * 2 + mHeight / 2, mHeight);
//        canvas.drawRect(rectF , mPaint);

        // 画三角形
        mPaint1.setColor(Color.WHITE);
        Path path1 = new Path();
//        path1.addRect(mHeight + mHeight / 2, 0, mHeight * 2 + mHeight / 2, mHeight, Path.Direction.CW);
        path1.moveTo(mHeight * 2 - mHeight / 2, 0);
        path1.lineTo(mHeight * 2 + mHeight / 2, 0);
        path1.lineTo(mHeight * 2 + mHeight / 2, mHeight);
        path1.lineTo(mHeight * 2, 0);
        canvas.drawPath(path1, mPaint1);


//        mPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
//        canvas.drawCircle(mHeight / 2, mHeight / 2, mHeight /2, mPaint);
//        RectF rectF = new RectF();
//        rectF.set(mHeight / 2, - mWidth / 2, mWidth / 2, mHeight);
//        canvas.drawRect(rectF, mPaint);
//        canvas.skew(0.5f, 0);
//        canvas.drawRect(rectF, mPaint);

//        canvas.restore();
//        mPaint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
//        canvas.drawCircle(mWidth - mHeight / 2, mHeight / 2, mHeight /2, mPaint);
//        RectF rectFR = new RectF();
//        rectFR.set(mWidth / 2 + 200, - mWidth / 2, mWidth - mHeight / 2, mHeight);
//        canvas.drawRect(rectFR, mPaint);
//        canvas.skew(0.5f, 0.5f);
//        canvas.drawRect(rectFR, mPaint);
//        canvas.rotate(45, mWidth / 4, mHeight / 2);
    }

    public void setTargetPercent(float animatedValue) {
        mValue = animatedValue;
    }
}
