package com.bincn.views.expandableTextView;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.bincn.views.utils.DeviceUtils;

/**
 * Created by bin on 2019/1/22.
 */
public class InnerTextView extends AppCompatTextView {

    private static final String TAG = "MInnerTextView";

    private CharSequence mTextExpanded;     // 展开的文字内容
    private CharSequence mTextCollapsed;    // 收缩的文字内容
    private String mTagCollapsed = "...";   // 收缩时显示
    private String mTagExpanded = "";       // 展开时显示

    private int mMeasureWidth;
    private int mMaxCollapsedLines;
    private View mClickableView;
    private boolean mClickable;

    public InnerTextView(Context context) {
        super(context);
    }

    public InnerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mMeasureWidth == 0) {
            mMeasureWidth = getMeasuredWidth();
            calculator();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 当 MInnerTextView 不可点击时，重新计算文本宽度
     * mMaxCollapsedLines 显示的行数
     */
    public void calculator() {
        // 右下角可点击 view 的宽度和左右间距
        int clickableViewWidth = mClickableView.getMeasuredWidth();
        int clickableViewPaddingLeft = mClickableView.getPaddingLeft();
        int clickableViewPaddingRight = mClickableView.getPaddingRight();
        Log.i(TAG, "calculator: clickableViewWidth = " + clickableViewWidth + ", clickableViewPaddingLeft = " + clickableViewPaddingLeft);
        // 原始文本
        String originText = mTextExpanded.toString();
        Paint paint = getPaint();
        // 原始文本的宽度
        float textWidth = paint.measureText(originText);
        // 拼在末尾的文本宽度
        float tagUnExpandedWidth = paint.measureText(mTagCollapsed);

        // 除了文本其他元素需要的宽度
        float elementWidth = 0;
        if (mClickable) {
            elementWidth = tagUnExpandedWidth + DeviceUtils.dip2px(getContext(), 20);
        } else {
            elementWidth = clickableViewWidth + clickableViewPaddingLeft + clickableViewPaddingRight + tagUnExpandedWidth;
        }

        // 原始文本的宽度大于目标宽度
        if (textWidth + elementWidth >= mMeasureWidth * mMaxCollapsedLines) {
            int currentWidth = 0;
            int lastIndex = 0;
            int len = originText.length();
            float[] widths = new float[len];
            paint.getTextWidths(originText, widths);
            for (int i = 0; i < len; i++) {
                if (currentWidth + elementWidth < mMeasureWidth * mMaxCollapsedLines) {
                    currentWidth += (int) Math.ceil(widths[i]);
                } else {
                    lastIndex = i - 1;
                    break;
                }
            }
            mTextCollapsed = originText.substring(0, lastIndex) + mTagCollapsed;
            setText(mTextCollapsed);
        }
    }

    public void setMaxCollapsedLines(int maxCollapsedLines) {
        mMaxCollapsedLines = maxCollapsedLines;
    }

    public void setClickableView(View clickableView) {
        mClickableView = clickableView;
    }

    public void setOriginText(CharSequence text) {
        mTextExpanded = text;
        setText(text);
    }

    public void setClickableViewVisible(boolean collapsed) {
        if (collapsed) {
            if (!mClickable) {
                mClickableView.setVisibility(VISIBLE);
            }
        } else {
            if (!mClickable) {
                mClickableView.setVisibility(GONE);
            }
        }
    }

    public void setTextCollapsed() {
        setText(mTextCollapsed);
    }

    public void setTextExpanded() {
        setText(mTextExpanded);
    }

    public void setCanClick(boolean clickable) {
        mClickable = clickable;
    }

//    private SpannableString getSpannableStringCollapsed() {
//        if (TextUtils.isEmpty(mTextCollapsed)) {
//            return new SpannableString("");
//        }
//        SpannableString spannableString = new SpannableString(mTextCollapsed);
//        spannableString.setSpan(new ExpandableClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                startCollapsedOrExpanded();
//                Log.i(TAG, "onClick: spannable click");
//                Toast.makeText(getContext(), "clickable span", Toast.LENGTH_SHORT).show();
//            }
//        }, mTextCollapsed.length() - mTagCollapsed.length(), mTextCollapsed.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spannableString;
//    }
//
//    private SpannableString getSpannableStringExpanded() {
//        if (TextUtils.isEmpty(mTextExpanded)) {
//            return new SpannableString("");
//        }
//        SpannableString spannableString = new SpannableString(mTextExpanded);
//        spannableString.setSpan(new ExpandableClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                startCollapsedOrExpanded();
//                Log.i(TAG, "onClick: spannable click");
//                Toast.makeText(getContext(), "clickable span", Toast.LENGTH_SHORT).show();
//            }
//        }, mTextExpanded.length() - mTagExpanded.length(), mTextExpanded.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spannableString;
//    }
}