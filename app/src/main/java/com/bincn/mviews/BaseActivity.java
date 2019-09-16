package com.bincn.mviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author bin
 * @date 2019-09-10
 */
public abstract class BaseActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    private RelativeLayout mLayoutBase;

    public abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initBaseView();
        addContentView();
    }

    private void initBaseView() {
        mLayoutBase = findViewById(R.id.layout_base);
        mLayoutBase.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private void addContentView() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(getLayoutId(), null);
        if (mLayoutBase != null) {
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.BELOW, R.id.layout_common_head);
            if (view != null) {
                mLayoutBase.addView(view, params);
            }
        }
    }
}
