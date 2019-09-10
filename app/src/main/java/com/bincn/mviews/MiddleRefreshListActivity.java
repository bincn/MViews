package com.bincn.mviews;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bincn.views.middle_refresh_recyclerview.MiddleRefreshRecyclerView;

/**
 *
 * @author bin
 * @date 2019-09-09
 */
public class MiddleRefreshListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_refresh_recycler_view);

        MiddleRefreshRecyclerView recyclerView = findViewById(R.id.recycler_view);
    }
}
