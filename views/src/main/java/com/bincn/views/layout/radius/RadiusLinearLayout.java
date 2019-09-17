package com.bincn.views.layout.radius;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author bin
 * @date 2019-09-16
 */
public class RadiusLinearLayout extends LinearLayout implements IRadiusLayout {

    private RadiusLayoutHelper mLayoutHelper;

    public RadiusLinearLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RadiusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RadiusLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mLayoutHelper = new RadiusLayoutHelper(context, attrs, defStyleAttr, this);
    }

    @Override public void setRadius(int radius) {
        mLayoutHelper.setRadius(radius);
    }

    @Override public int getRadius() {
        return mLayoutHelper.getRadius();
    }

    @Override public void setOuterNormalColor(int color) {
        mLayoutHelper.setOuterNormalColor(color);
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mLayoutHelper.dispatchDraw(canvas);
    }
}
