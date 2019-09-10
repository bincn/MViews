package com.bincn.views.middle_refresh_recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bincn.views.R;

import java.util.Random;

/**
 * Created by bin on 2018/11/30.
 */
public class RefreshView extends RelativeLayout implements IRefreshView {

    private static final String TAG = "RefreshView";

    private String[] Tips = new String[]{"暂无更新", "网络出错", "更新了10条数据"};

    private View mLayoutLoadingTips, mLayoutCompleteTips;
    private TextView mTvLoadingTips, mTvCompleteTips;
    private ImageView mIvCompleteBg;

    // 刷新时的高度
    private int mRefreshingHeight;
    // 大于这个高度松手，触发刷新的高度
    private int mReleaseToRefreshHeight;
    // 刷新完成后，背景展开的初始宽度
    private int mCompleteBgStartWidth;
    // 是否正在刷新
    private boolean mIsRefreshing;
    // 高度变化动画
    private ValueAnimator mHeightValueAnimator;
    // 完成布局背景扩展动画
    private ValueAnimator mCompleteBgWidthValueAnimator;
    // 起始高度
    private int mOriginalHeight = 0;
    // 下拉位移比例
    private float mPullRatio = 2f / 3;

    public RefreshView(Context context) {
        super(context);
        init();
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initLayout();
        initValue();
    }

    private void initLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.refresh_view, this);
        mLayoutLoadingTips = findViewById(R.id.layout_loading_tips);
        mTvLoadingTips = findViewById(R.id.tv_loading_tips);
        mLayoutCompleteTips = findViewById(R.id.layout_complete_tips);
        mTvCompleteTips = findViewById(R.id.tv_complete_tips);
        mIvCompleteBg = findViewById(R.id.iv_complete_bg);
    }

    private void initValue() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mRefreshingHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics);
        mReleaseToRefreshHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, displayMetrics);
        mCompleteBgStartWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int height = MeasureSpec.makeMeasureSpec(layoutParams.height >= 0 ? layoutParams.height : mOriginalHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, height);
    }

    @Override
    public boolean isEyeVisible() {
        return getHeight() > mOriginalHeight && getVisibility() == VISIBLE;
    }

    @Override
    public boolean isCanReleaseToRefresh() {
        return getHeight() >= mReleaseToRefreshHeight;
    }

    @Override
    public void setRefreshing() {
        if (mIsRefreshing) {
            return;
        }
        mIsRefreshing = true;
        mLayoutCompleteTips.setAlpha(0);
        mLayoutLoadingTips.setAlpha(1);
        stopAllAnimation();
        mHeightValueAnimator = getRefreshingHeightValueAnimator();
        mHeightValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mHeightValueAnimator.start();

        mTvLoadingTips.setText("正在刷新");
        mTvLoadingTips.setAlpha(1);
    }

    private ValueAnimator getRefreshingHeightValueAnimator() {
        int duration = getHeight() == mRefreshingHeight ? 0 : 200;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(getHeight(), mRefreshingHeight);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float height = (int) valueAnimator.getAnimatedValue();
                setViewHeight(height);
            }
        });
        return valueAnimator;
    }

    @Override
    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    public void setRefreshComplete(final String tips) {
        if (!mIsRefreshing) {
            return;
        }
        stopAllAnimation();
        mHeightValueAnimator = getRefreshingHeightValueAnimator();
        mHeightValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLayoutCompleteTips.setAlpha(1);
                mTvCompleteTips.setText(tips);

                // 背景扩展动画
                mCompleteBgWidthValueAnimator = ValueAnimator.ofInt(mCompleteBgStartWidth, getWidth());
                mCompleteBgWidthValueAnimator.setDuration(150);
                mCompleteBgWidthValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mLayoutCompleteTips.setAlpha(animation.getAnimatedFraction());
                        int width = (int) animation.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = mIvCompleteBg.getLayoutParams();
                        layoutParams.width = width;
                        mIvCompleteBg.requestLayout();
                    }
                });
                mCompleteBgWidthValueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animateToInitialState();
                            }
                        }, 800);
                    }
                });
                mCompleteBgWidthValueAnimator.start();
            }
        });
        mHeightValueAnimator.start();
    }

    @Override
    public void animateToInitialState() {
        stopAllAnimation();
        int currentHeight = getHeight();
        int duration = 400;
        if (currentHeight < mRefreshingHeight) {
            duration = (int) ((currentHeight * 1.0f / mRefreshingHeight) * duration);
        }
        mHeightValueAnimator = ValueAnimator.ofFloat(currentHeight, mOriginalHeight);
        mHeightValueAnimator.setDuration(duration);
        mHeightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float height = (float) valueAnimator.getAnimatedValue();
                setViewHeight(height);
                handleLoadingView(height);
            }
        });
        mHeightValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsRefreshing = false;
            }
        });
        mHeightValueAnimator.start();
    }

    @Override
    public void stopAllAnimation() {
        if (mHeightValueAnimator != null && mHeightValueAnimator.isRunning()) {
            mHeightValueAnimator.cancel();
        }
        if (mCompleteBgWidthValueAnimator != null && mCompleteBgWidthValueAnimator.isRunning()) {
            mCompleteBgWidthValueAnimator.cancel();
        }
    }

    @Override
    public void onPull(float offset) {
        stopAllAnimation();
        mLayoutCompleteTips.setAlpha(0);
        float height = offset * mPullRatio + mOriginalHeight;
        // 设置当前View高度
        setViewHeight(height);
        // 处理加载状态
        handleLoadingView(height);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                setRefreshComplete(Tips[random.nextInt(Tips.length)]);
            }
        }, 1500);
    }

    private void handleLoadingView(float height) {
        if (height < mReleaseToRefreshHeight) {
            mTvLoadingTips.setText("下拉刷新");
            mTvLoadingTips.setAlpha((height - mOriginalHeight) / (mReleaseToRefreshHeight - mOriginalHeight));
        } else {
            mTvLoadingTips.setText("松开刷新");
            mTvLoadingTips.setAlpha(1);
        }
    }

    private void setViewHeight(float height) {
        if (getLayoutParams() != null) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams.height != (int) height) {
                layoutParams.height = (int) height;
                requestLayout();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
    }
}
