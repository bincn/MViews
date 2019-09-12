package com.bincn.mviews;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import android.widget.Toast;

import com.bincn.views.expandableTextView.ExpandableTextView;

/**
 * @author bin
 * @date 2019-09-09
 */
public class ExpandableTextActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_expandable_text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String text = "我们先一起来回顾一下实现沉浸式状态栏的一般套路。在 Android 上，关于对 StatusBar（状态栏）的操作，一直都在不断改善，并且表现越来越好，在 Android4.4 以下，我们可以对 StatusBar 和 NavigationBar 进行显示和隐藏操作。但是直到 Android4.4,我们才能真正意义上的实现沉浸式状态栏。从 Android4.4 到现在（Android 9），关于沉浸式大概可以分成三个阶段：\n" +
                "\n" +
                "Android4.4（API 19）- Android 5.0（API 21）：这个阶段可以实现沉浸式，但是表现得还不是很好，实现方式为: 通过 FLAGTRANSLUCENTSTATUS 设置状态栏为透明并且为全屏模式，然后通过添加一个与 StatusBar 一样大小的 View，将View 的 background 设置为我们想要的颜色，从而来实现沉浸式。\n" +
                "\n" +
                "Android 5.0（API 21）以上版本：在Android 5.0 的时候，加入了一个重要的属性和方法 android:statusBarColor （对应方法为 setStatusBarColor），通过这个方法我们就可以轻松实现沉浸式。也就是说，从 Android5.0 开始，系统才真正的支持沉浸式。\n" +
                "\n" +
                "Android 6.0（API 23）以上版本：其实 Android6.0 以上的实现方式和 Android 5.0+是一样，为什么要将它归为一个单独重要的阶段呢？是因为从 Android 6.0（API 23）开始，我们可以改状态栏的绘制模式，可以显示白色或浅黑色的内容和图标（除了魅族手机，魅族自家有做源码更改，6.0 以下就能实现）。\n" +
                "\n" +
                "总结：这三个阶段的 Android 上 API 版本混乱，各种 Flag 林立。再加上各大厂商的定制化可谓是火上浇油，让安卓开发者异常头疼。";

        ExpandableTextView expandableTextView = findViewById(R.id.expandable_text_view);
        expandableTextView.setText(text);
        expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListenerImp() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                super.onExpandStateChanged(textView, isExpanded);
                Log.i(TAG, "onExpandStateChanged: isExpanded : " + isExpanded);
                Toast.makeText(ExpandableTextActivity.this, "isExpand " + isExpanded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickTargetView() {
                super.onClickTargetView();
            }
        });
    }
}
