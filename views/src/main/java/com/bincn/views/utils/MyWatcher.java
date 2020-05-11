package com.bincn.views.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity 生命周期监听
 *
 * @author mwb
 * @date 2020-05-09
 */
public class MyWatcher {

    private static final String TAG = "MyWatcher";

    private boolean mIsInited = false;
    private List<Application.ActivityLifecycleCallbacks> mWatchers = Collections.synchronizedList(new ArrayList<Application.ActivityLifecycleCallbacks>());

    private MyWatcher() {
    }

    private static class Holder {
        private static MyWatcher instance = new MyWatcher();
    }

    public static MyWatcher getInstance() {
        return Holder.instance;
    }

    /**
     * 新增监听器
     */
    public boolean addWatcher(Application.ActivityLifecycleCallbacks watcher) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Log.e(TAG, "禁止在线程调用addWatcher");
            return false;
        }
        if (watcher == null || !mIsInited)
            return false;
        if (!mWatchers.contains(watcher)) {
            mWatchers.add(watcher);
            return true;
        }
        return false;
    }

    /**
     * 移除监听器
     */
    public boolean removeWatcher(Application.ActivityLifecycleCallbacks watcher) {
        if (watcher == null || !mIsInited)
            return false;
        if (mWatchers.contains(watcher)) {
            mWatchers.remove(watcher);
            return true;
        }
        return false;
    }

    /**
     * 初始化
     */
    public void init(Application application) {
        if (mIsInited) {
            return;
        }
        mIsInited = true;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                onActivityCreatedThis(activity, savedInstanceState);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                onActivityStartedThis(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                onActivityResumedThis(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                onActivityPausedThis(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                onActivityStoppedThis(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                onActivitySaveInstanceStateThis(activity, outState);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                onActivityDestroyedThis(activity);
            }
        });
    }

    private void onActivityDestroyedThis(Activity activity) {
        for (Application.ActivityLifecycleCallbacks lifecycleCallbacks : mWatchers) {
            lifecycleCallbacks.onActivityDestroyed(activity);
        }
    }

    private void onActivitySaveInstanceStateThis(Activity activity, Bundle outState) {
        for (Application.ActivityLifecycleCallbacks lifecycleCallbacks : mWatchers) {
            lifecycleCallbacks.onActivitySaveInstanceState(activity, outState);
        }
    }

    private void onActivityStoppedThis(Activity activity) {
        for (Application.ActivityLifecycleCallbacks lifecycleCallbacks : mWatchers) {
            lifecycleCallbacks.onActivityStopped(activity);
        }
    }

    private void onActivityPausedThis(Activity activity) {
        for (Application.ActivityLifecycleCallbacks lifecycleCallbacks : mWatchers) {
            lifecycleCallbacks.onActivityPaused(activity);
        }
    }

    private void onActivityResumedThis(Activity activity) {
        for (Application.ActivityLifecycleCallbacks lifecycleCallbacks : mWatchers) {
            lifecycleCallbacks.onActivityResumed(activity);
        }
    }

    private void onActivityStartedThis(Activity activity) {
        for (Application.ActivityLifecycleCallbacks lifecycleCallbacks : mWatchers) {
            lifecycleCallbacks.onActivityStarted(activity);
        }
    }

    private void onActivityCreatedThis(Activity activity, Bundle bundle) {
        for (Application.ActivityLifecycleCallbacks lifecycleCallbacks : mWatchers) {
            lifecycleCallbacks.onActivityCreated(activity, bundle);
        }
    }
}
