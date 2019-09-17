package com.bincn.mviews;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bincn.views.layout.radius.RadiusLinearLayout;
import com.bincn.views.layout.radius2.CoverConner;
import com.bincn.views.layout.radius2.CoverConnerDrawable;
import com.bincn.views.layout.radius2.RoundRectDrawable;
import com.bincn.views.utils.DeviceUtils;

/**
 * @author bin
 * @date 2019-09-12
 */
public class RadiusActivity extends BaseActivity {

    @BindView(R.id.fl_radius) FrameLayout mFrameLayout;
    @BindView(R.id.layout_radius) RadiusLinearLayout mRadiusLinearLayout;

    @Override public int getLayoutId() {
        return R.layout.activity_radius;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initLayout();
    }

    private void initLayout() {
        setRadius2();
        setRadius();
    }

    private void setRadius() {
        mRadiusLinearLayout.setOuterNormalColor(getResources().getColor(R.color.config_color_background));
        mRadiusLinearLayout.setRadius(DeviceUtils.dip2px(this, 10));
    }

    private void setRadius2() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] {},
                    },
                    new int[] {
                            getResources().getColor(R.color.config_color_white)
                    });
            RoundRectDrawable background = new RoundRectDrawable(colorStateList,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                            getResources().getDisplayMetrics()));
            mFrameLayout.setBackground(background);
            mFrameLayout.setClipToOutline(true);
        } else {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { android.R.attr.state_pressed },
                            new int[] { android.R.attr.state_selected },
                            new int[] {},
                    },
                    new int[] {
                            getResources().getColor(R.color.config_color_gray_9),
                            getResources().getColor(R.color.config_color_gray_9),
                            getResources().getColor(R.color.config_color_background)
                    });
            CoverConnerDrawable coverConnerDrawable =
                    new CoverConnerDrawable(new CoverConner(colorStateList, 8, this));
            mFrameLayout.setForeground(coverConnerDrawable);
        }
    }
}
