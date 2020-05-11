package com.bincn.views.utils;

import android.app.Activity;
import android.app.Application;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * App 灰白化处理
 *
 * @author mwb
 * @date 2020-05-08
 */
public class GrayColorFilter {

    /**
     * 是否添加 activity watcher
     */
    private boolean mIsAddActivityWatcher;
    /**
     * 是否灰白化
     */
    private boolean mIsGray;

    private static class Holder {
        private static final GrayColorFilter instance = new GrayColorFilter();
    }

    private GrayColorFilter() {
    }

    public static GrayColorFilter getInstance() {
        return Holder.instance;
    }

    public boolean init() {
        try {
            if (isGray() && !mIsAddActivityWatcher) {
                MyWatcher.getInstance().removeWatcher(lifecycleCallbacks);
                mIsAddActivityWatcher = MyWatcher.getInstance().addWatcher(lifecycleCallbacks);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mIsAddActivityWatcher;
    }

    @MainThread
    public void grayForView(View view) {
        if (view == null) {
            return;
        }
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(isGray() ? 0 : 1);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    public void setGray(boolean isGray) {
        mIsGray = isGray;
        init();
    }

    public boolean isGray() {
        return mIsGray;
    }

    private Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            grayForView(activity.getWindow().getDecorView());
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };
}
