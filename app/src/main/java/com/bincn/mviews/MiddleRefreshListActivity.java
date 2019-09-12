package com.bincn.mviews;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author bin
 * @date 2019-09-09
 */
public class MiddleRefreshListActivity extends BaseActivity {
  @Override public int getLayoutId() {
    return R.layout.activity_middle_refresh_recycler_view;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_middle_refresh_recycler_view);
  }
}
