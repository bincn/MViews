package com.bincn.mviews;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.bincn.views.vote.VotePercentView;

/**
 * @author bin
 * @date 2019-09-10
 */
public class VoteActivity extends BaseActivity {

  private VotePercentView mVotePercentView;
  private boolean mVoted;

  @Override public int getLayoutId() {
    return R.layout.activity_vote;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mVotePercentView = findViewById(R.id.vote_percent_view);
    mVotePercentView.setOnVotePercentItemClickListener(
        new VotePercentView.OnVotePercentItemClickListener() {
          @Override public void onItemClick(String str) {
            if (!mVoted) {
              mVoted = true;
              Toast.makeText(VoteActivity.this, "click " + str, Toast.LENGTH_SHORT).show();
              mVotePercentView.showResult();
            }
          }
        });
  }

  public void onVoteClick(View v) {
    if (mVoted) {
      mVoted = false;
      mVotePercentView.showOptions();
    }
  }
}
