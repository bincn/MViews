package com.bincn.mviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.bincn.views.views.AutoPollRecyclerView;
import com.bincn.views.views.AutoVerticalScrollLayout;
import com.bincn.views.views.ScrollSpeedLinearLayoutManger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bin
 * @date 2019-10-17
 */
public class AutoVerticalScrollTextActivity extends BaseActivity {

    @Override public int getLayoutId() {
        return R.layout.activity_auto_vertical_scroll_text;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewFlipper();

        initAutoVerticalScrollLayout();

        initAutoPollRecyclerView();
    }

    private void initAutoPollRecyclerView() {
        AutoPollRecyclerView autoPollRecyclerView = findViewById(R.id.auto_poll_recycler_view);
        ScrollSpeedLinearLayoutManger linearLayoutManger = new ScrollSpeedLinearLayoutManger(this);
        linearLayoutManger.setMillisecondsPerInch(2.0f);
        autoPollRecyclerView.setLayoutManager(linearLayoutManger);
        AutoPollRecyclerViewAdapter autoPollRecyclerViewAdapter = new AutoPollRecyclerViewAdapter(this);
        autoPollRecyclerView.setHandleDispatchTouchEvent(false);
        autoPollRecyclerView.setAdapter(autoPollRecyclerViewAdapter);
        autoPollRecyclerView.scrollToPosition(0);
        autoPollRecyclerView.start();
    }

    private void initAutoVerticalScrollLayout() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("测试数据 " + (i + 1));
        }
        list.add(0, "");
        list.add(1, "");
        list.add(2, "");
        AutoVerticalScrollLayout scrollLayout = findViewById(R.id.auto_vertical_scroll_layout);
        scrollLayout.setData(list);
    }

    private void initViewFlipper() {
        List<TextView> list = new ArrayList<TextView>();
        String[] strings = new String[] { "盈盈一水间，脉脉不得语。", "我渴望和你打架，也渴望抱抱你，也渴望抱抱你。", "醒来觉得甚是爱你。" };
        for (int i = 0; i < strings.length; i++) {
            TextView tv = new TextView(this);
            tv.setTextSize(16);
            tv.setText(strings[i]);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //这里只是Toast了一下，实际开发中可以跳转到指定的页面
                    Toast.makeText(AutoVerticalScrollTextActivity.this, "do Something",
                            Toast.LENGTH_SHORT).show();
                }
            });
            list.add(tv);
        }

        ViewFlipper viewFlipper = findViewById(R.id.view_flipper);
        for (TextView textView : list) {
            viewFlipper.addView(textView);
        }
        viewFlipper.setInAnimation(this, R.anim.push_up_in);
        viewFlipper.setOutAnimation(this, R.anim.push_up_out);
        viewFlipper.setFlipInterval(2 * 1000);
        viewFlipper.startFlipping();
    }
}
