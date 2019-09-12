package com.bincn.mviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author bin
 * @date 2019-09-10
 */
public abstract class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    public abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
    }
}
