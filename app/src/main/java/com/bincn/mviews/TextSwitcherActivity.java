package com.bincn.mviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * @author bin
 * @date 2019-10-17
 */
public class TextSwitcherActivity extends BaseActivity {

    private int count;
    private TextView mTextView;
    private TextSwitcher mTextSwitcher;

    @Override public int getLayoutId() {
        return R.layout.activity_text_switcher;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextSwitcher = findViewById(R.id.text_switcher);
        // 设置转换动画，这里引用系统自带动画
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mTextSwitcher.setInAnimation(in);
        mTextSwitcher.setOutAnimation(out);
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override public View makeView() {
                mTextView = new TextView(TextSwitcherActivity.this);
                return mTextView;
            }
        });
        setText();
    }

    public void changeText(View v) {
        count++;
        setText();
    }

    private void setText() {
        mTextSwitcher.setText(String.valueOf(count));
    }
}
