package com.bincn.views.layout.radius;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import java.lang.ref.WeakReference;

/**
 * @author bin
 * @date 2019-09-16
 */
public class RadiusLayoutHelper implements IRadiusLayout {

    private Context mContext;
    private WeakReference<View> mReference;

    // round
    private Paint mClipPaint;
    private PorterDuffXfermode mMode;
    private int mRadius;
    private RectF mBorderRect;
    private int mOuterNormalColor = 0;

    public RadiusLayoutHelper(Context context, AttributeSet attrs, int defStyleAttr,
            View view) {
        mContext = context;
        mReference = new WeakReference<>(view);
        mMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        mClipPaint = new Paint();
        mClipPaint.setAntiAlias(true);
        mBorderRect = new RectF();
    }

    @Override public void setRadius(int radius) {
        setupRadius(radius);
        invalidate();
    }

    @Override public int getRadius() {
        return mRadius;
    }

    @Override public void setOuterNormalColor(int color) {
        mOuterNormalColor = color;
        invalidate();
    }

    private void invalidate() {
        View view = mReference.get();
        if (view != null) {
            view.invalidate();
        }
    }

    private void setupRadius(int radius) {
        View view = mReference.get();
        if (view == null) {
            return;
        }
        mRadius = radius;
        if (isAfterSDK21()) {
            view.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                @Override public void getOutline(View view, Outline outline) {
                    int w = view.getWidth(), h = view.getHeight();
                    if (w == 0 || h == 0) {
                        return;
                    }
                    int left = 0, top = 0, right = w, bottom = h;
                    if (mRadius <= 0) {
                        outline.setRect(left, top, right, bottom);
                    } else {
                        outline.setRoundRect(left, top, right, bottom, mRadius);
                    }
                }
            });
            view.setClipToOutline(mRadius > 0);
        }
    }

    /**
     * 4.4 之前显示圆角
     */
    public void dispatchDraw(Canvas canvas) {
        View view = mReference.get();
        if (view == null) {
            return;
        }
        if (isAfterSDK21() || mRadius == 0) {
            return;
        }
        int width = canvas.getWidth(), height = canvas.getHeight();

        // react
        mBorderRect.set(1, 1, width - 1, height - 1);

        // 圆角矩形
        if (!isAfterSDK21()) {
            int layerId = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
            canvas.drawColor(mOuterNormalColor);
            mClipPaint.setColor(mOuterNormalColor);
            mClipPaint.setStyle(Paint.Style.FILL);
            mClipPaint.setXfermode(mMode);
            canvas.drawRoundRect(mBorderRect, mRadius, mRadius, mClipPaint);
            mClipPaint.setXfermode(null);
            canvas.restoreToCount(layerId);
        }
    }

    public static boolean isAfterSDK21() {
        return Build.VERSION.SDK_INT >= 21;
    }
}
