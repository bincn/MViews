package com.bincn.mviews;

import android.app.Application;

import com.bincn.views.utils.MyWatcher;
import com.bincn.views.utils.GrayColorFilter;

/**
 * @author mwb
 * @date 2020-05-08
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyWatcher.getInstance().init(this);
        GrayColorFilter.getInstance().init();
    }
}
