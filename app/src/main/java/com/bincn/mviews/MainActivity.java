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
        Intent intent = new Intent(this, ExpandableTextActivity.class);
        startActivity(intent);
    }

    public void onVoteClick(View v) {
        Intent intent = new Intent(this, VoteActivity.class);
        startActivity(intent);
    }
}
