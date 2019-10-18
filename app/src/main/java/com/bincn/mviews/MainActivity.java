package com.bincn.mviews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author bin
 */
public class MainActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onExpandableTextClick(View v) {
        startActivity(new Intent(this, ExpandableTextActivity.class));
    }

    public void onVoteClick(View v) {
        startActivity(new Intent(this, VoteActivity.class));
    }

    public void onRadiusClick(View v) {
        startActivity(new Intent(this, RadiusActivity.class));
    }

    public void onTextSwitcher(View v) {
        startActivity(new Intent(this, TextSwitcherActivity.class));
    }

    public void onAutoVerticalScrollText(View v) {
        startActivity(new Intent(this, AutoVerticalScrollTextActivity.class));
    }
}
