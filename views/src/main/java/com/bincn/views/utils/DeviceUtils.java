package com.bincn.views.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Window;

import java.lang.reflect.Field;

/**
 * Created by bin on 2018/11/30.
 */
public class DeviceUtils {

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        try {
            if (context != null) {
                return context.getResources().getDisplayMetrics().widthPixels;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 480;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        try {
            if (context != null) {
                return context.getResources().getDisplayMetrics().heightPixels;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 800;
    }

    /**
     * dip 到 px 的转换
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将 px 值转换为 sp 值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp 到 px 的转换
     *
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * px 到 dip 的转换
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static int mStatusBarHeight;

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        if (mStatusBarHeight != 0) {
            return mStatusBarHeight;
        }
        int statusBarHeight = 0;
        //尝试第一种获取方式
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            if (statusBarHeight > 0) {
                mStatusBarHeight = statusBarHeight;
                return statusBarHeight;
            }
        }
        if (statusBarHeight <= 0) {
            //第一种失败时, 尝试第二种获取方式
            Rect rectangle = new Rect();
            Window window = activity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
            statusBarHeight = rectangle.top;
            if (statusBarHeight > 0) {
                mStatusBarHeight = statusBarHeight;
                return statusBarHeight;
            }
        }
        if (statusBarHeight <= 0) {
            try {
                Class<?> c = null;
                Object obj = null;
                Field field = null;
                int x = 0;
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = activity.getResources().getDimensionPixelSize(x);
                mStatusBarHeight = statusBarHeight;
                return statusBarHeight;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        mStatusBarHeight = DeviceUtils.dip2px(activity, 20);
        return mStatusBarHeight;
    }
}
