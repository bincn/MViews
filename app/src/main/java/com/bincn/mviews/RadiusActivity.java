package com.bincn.mviews;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bincn.views.radius.CoverConner;
import com.bincn.views.radius.CoverConnerDrawable;
import com.bincn.views.radius.RoundRectDrawable;

/**
 * @author bin
 * @date 2019-09-12
 */
public class RadiusActivity extends BaseActivity {

  @Override public int getLayoutId() {
    return R.layout.activity_radius;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FrameLayout frameLayout = findViewById(R.id.fl_radius);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      RoundRectDrawable background = new RoundRectDrawable(null,
          (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
              getResources().getDisplayMetrics()));
      frameLayout.setBackground(background);
      frameLayout.setClipToOutline(true);
    } else {
      ColorStateList colorStateList = new ColorStateList(
          new int[][] {
              new int[] { android.R.attr.state_pressed },
              new int[] { android.R.attr.state_selected },
              new int[] {},
          },
          new int[] {
              getResources().getColor(R.color.black_e0),
              getResources().getColor(R.color.black_e0),
              getResources().getColor(R.color.colorWhite)
          });
      CoverConnerDrawable coverConnerDrawable =
          new CoverConnerDrawable(new CoverConner(colorStateList, 8, this));
      frameLayout.setForeground(coverConnerDrawable);
    }
  }
}
