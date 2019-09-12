package com.bincn.views.middle_refresh_recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by bin on 2018/11/30.
 */
public class MiddleRefreshRecyclerView extends RecyclerView {

    private final String TAG = "RefreshRecyclerView";

    private HeaderContentView mHeaderContentView;
    private RefreshView mRefreshView;
    // 最小有效滑动距离
    private int mTouchSlop;
    // 手指第一次触摸点
    private float mStartY = -1;
    private float mLastY = -1;
    // 释放刷新
    private boolean mIsDragToRefresh;

    public MiddleRefreshRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public MiddleRefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MiddleRefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() / 4;
    }

    public void setHeaderContentView(@NonNull HeaderContentView headerContentView) {
        if (headerContentView == null) {
            throw new RuntimeException("HeaderContentView can't null");
        }
        mHeaderContentView = headerContentView;
    }

    public void setRefreshView(@NonNull RefreshView refreshView) {
        if (refreshView == null) {
            throw new RuntimeException("RefreshView can't null");
        }
        mRefreshView = refreshView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.i(TAG, "onTouchEvent: ");
        Log.i(TAG, "onTouchEvent: isTop() = " + isTop() + ", isRefreshing = " + isRefreshing());
        // 列表不在顶部或者正在刷新
        if (!isTop() || isRefreshing()) {
            mIsDragToRefresh = false;
            mLastY = e.getY();
            mStartY = mLastY;
            return super.onTouchEvent(e);
        }
        // 仅处理列表刚好在顶部的情况
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: action_down");
                mIsDragToRefresh = false;
                mLastY = e.getY();
                mStartY = mLastY;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: action_move");
                float currentY = e.getY();
                // 修复 adapter item 设置点击监听，DOWN 事件被吃掉问题
                if (mStartY == -1) {
                    mStartY = currentY;
                }
                if (mLastY == -1) {
                    mLastY = currentY;
                }
                // 是否往下拉
                boolean isOverTouchSlop = Math.abs(currentY - mStartY) > mTouchSlop;
                boolean isDragDown = currentY - mStartY > 0;
                if ((isOverTouchSlop && isDragDown)) {
                    mIsDragToRefresh = true;
                    float offset = currentY - mStartY;
                    mHeaderContentView.onPull(offset);
                    mRefreshView.onPull(offset);
                }
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: action_up");
                mStartY = -1;
                mLastY = -1;
                if (mIsDragToRefresh) {
                    if (mRefreshView.isCanReleaseToRefresh()) {
                        setRefreshing();
                    } else {
                        mRefreshView.animateToInitialState();
                    }
                    mHeaderContentView.onPullRelease();
                    mIsDragToRefresh = false;
                    e.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onTouchEvent(e);
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    private void setRefreshing() {
        mRefreshView.setRefreshing();
    }

    private boolean isRefreshing() {
        return mRefreshView.isRefreshing();
    }

    /**
     * 列表在顶部状态
     */
    private boolean isTop() {
        if (getFirstVisiblePosition() == 0 && getChildCount() > 0) {
            View firstChildView = getChildAt(0);
            if (firstChildView != null) {
                return firstChildView.getTop() >= 0;
            }
        }
        return false;
    }

    public int getFirstVisiblePosition() {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && getChildCount() > 0) {
            return layoutManager.getPosition(getChildAt(0));
        } else {
            return 0;
        }
    }

    public void setRefreshComplete(String tips) {
        mRefreshView.setRefreshComplete(tips);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
        if (mRefreshView != null) {
            mRefreshView.stopAllAnimation();
        }
    }
}
