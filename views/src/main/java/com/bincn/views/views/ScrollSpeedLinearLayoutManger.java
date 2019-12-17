package com.bincn.views.views;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * @author mwb
 * @date 2019-12-17
 */
public class ScrollSpeedLinearLayoutManger extends LinearLayoutManager {

    private float MILLISECONDS_PER_INCH = 0.03f;

    private Context mContext;

    public ScrollSpeedLinearLayoutManger(Context context) {
        super(context);
        this.mContext = context;
        //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
        //0.3f是自己估摸的一个值，可以根据不同需求自己修改
        MILLISECONDS_PER_INCH = mContext.getResources().getDisplayMetrics().density * 3f;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
            int position) {

        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return ScrollSpeedLinearLayoutManger.this.computeScrollVectorForPosition(
                                targetPosition);
                    }

                    //This returns the milliseconds it takes to
                    //scroll one pixel.
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.density;
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    /**
     * 控制滚动速度 值越大滚动速度越慢
     */
    public void setMillisecondsPerInch(float millisecondsPerInch) {
        MILLISECONDS_PER_INCH =
                mContext.getResources().getDisplayMetrics().density * millisecondsPerInch;
    }
}
