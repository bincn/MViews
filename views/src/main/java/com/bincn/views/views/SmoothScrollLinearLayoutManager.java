package com.bincn.views.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * @author bin
 * @date 2019-10-18
 */
public class SmoothScrollLinearLayoutManager extends LinearLayoutManager {

    private float mMillisecondsPerInch = 5f;
    private LinearSmoothScroller mLinearSmoothScroller;

    public SmoothScrollLinearLayoutManager(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mLinearSmoothScroller = new LinearSmoothScroller(context) {

            /**
             * 将移动的置顶显示
             */
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            /**
             * 控制速度
             */
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return mMillisecondsPerInch / displayMetrics.density;
            }
        };
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        mLinearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(mLinearSmoothScroller);
    }

    /**
     * 控制滚动速度 值越大滚动速度越慢
     */
    public void setMillisecondsPerInch(float millisecondsPerInch) {
        mMillisecondsPerInch = millisecondsPerInch;
    }
}
