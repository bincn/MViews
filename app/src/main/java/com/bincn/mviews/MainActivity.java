package com.bincn.mviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bincn.views.utils.GrayColorFilter;

import butterknife.BindView;

/**
 * @author bin
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.checkboxGrayMode)
    CheckBox mCheckBoxGrayMode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 灰白化
        mCheckBoxGrayMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GrayColorFilter.getInstance().setGray(isChecked);
            }
        });
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
