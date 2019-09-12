package com.bincn.views.middle_refresh_recyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;

/**
 * Created by bin on 2018/11/30.
 */
public class HeaderContentView extends LinearLayout implements IRefreshHeaderContentView {

    private static final String TAG = "HeaderContentView";

    private static final float PULL_OFFSET_RATIO = 1f / 3;
    private int mOriginalPaddingTop;
    private ValueAnimator mValueAnimator;

    public HeaderContentView(Context context) {
        super(context);
    }

    public HeaderContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mOriginalPaddingTop = getPaddingTop();
    }

    @Override
    public void onPullRelease() {
        Log.i(TAG, "onPullRelease: ");
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            return;
        }
        mValueAnimator = ValueAnimator.ofInt(getPaddingTop(), mOriginalPaddingTop);
        mValueAnimator.setDuration(200);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setPadding(getPaddingLeft(), (int) valueAnimator.getAnimatedValue(), getPaddingRight(), getPaddingBottom());
            }
        });
        mValueAnimator.start();
    }

    @Override
    public void onPull(float offset) {
        Log.i(TAG, "onPull: ");
        int realOffset = (int) (offset * PULL_OFFSET_RATIO);
        int maxIncreaseHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics());
        if (realOffset > maxIncreaseHeight) {
            realOffset = maxIncreaseHeight;
        } else if (realOffset < 0) {
            realOffset = 0;
        }
        int targetHeight = realOffset + mOriginalPaddingTop;
        setPadding(getPaddingLeft(), targetHeight, getPaddingRight(), getPaddingBottom());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
    }
}
