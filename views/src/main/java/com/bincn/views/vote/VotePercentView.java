package com.bincn.views.vote;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bincn.views.R;
import com.bincn.views.utils.DeviceUtils;
import java.util.List;

/**
 * Created by bin on 2019/5/21.
 */
public class VotePercentView extends LinearLayout {

    private OnVotePercentItemClickListener mItemClickListener;
    private VotePercentResultView mPercentResultView;
    private View mLeftOptionView, mRightOptionView;
    private ImageView mIvMiddle;
    private TextView mTvLeftResult, mTvRightResult;
    private TextView mTvLeftOption, mTvRightOption;
    private LinearLayout mLlOptions;

    public VotePercentView(Context context) {
        super(context);
        init(context);
    }

    public VotePercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VotePercentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private TextView voteTitle;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_vote_percent, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTvLeftResult = findViewById(R.id.tv_left_result);
        mTvRightResult = findViewById(R.id.tv_right_result);
        mPercentResultView = findViewById(R.id.vote_percent_result_view);
        mLlOptions = findViewById(R.id.ll_options);
        mLeftOptionView = findViewById(R.id.fl_left_option);
        mRightOptionView = findViewById(R.id.fl_right_option);
        mTvLeftOption = findViewById(R.id.tv_left_option);
        mTvRightOption = findViewById(R.id.tv_right_option);
        mIvMiddle = findViewById(R.id.iv_middle);
        mLeftOptionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick("left");
                }
            }
        });
        mRightOptionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick("right");
                }
            }
        });
        mPercentResultView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 吃事件
            }
        });
    }

    /**
     * 显示选项
     */
    public void showOptions() {
        mLlOptions.setVisibility(VISIBLE);
        mTvLeftResult.setVisibility(GONE);
        mTvRightResult.setVisibility(GONE);
        mPercentResultView.setVisibility(GONE);
        mPercentResultView.reset();
    }

    /**
     * 显示结果
     */
    public void showResult() {
        mLlOptions.setVisibility(GONE);
        mTvLeftResult.setVisibility(VISIBLE);
        mTvRightResult.setVisibility(VISIBLE);
        mPercentResultView.setVisibility(VISIBLE);
        mPercentResultView.setPercent(50, 50);
    }

    public interface OnVotePercentItemClickListener {
        void onItemClick(String str);
    }

    public void setOnVotePercentItemClickListener(OnVotePercentItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
