package com.bincn.views.expandableTextView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bincn.views.R;

/**
 * Created by bin on 2019/1/22.
 */
public class ExpandableTextView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "MExpandableTextView";

    /* The default number of lines */
    private static final int MAX_COLLAPSED_LINES = 2;
    /* The default animation duration */
    private static final int DEFAULT_ANIM_DURATION = 200;
    /* The default alpha value when the animation starts */
    private static final float DEFAULT_ANIM_ALPHA_START = 0.7f;
    /* The default text size */
    private static final int DEFAULT_TEXT_SIZE = 16;
    /* The default alpha animation open */
    private boolean mAnimAlphaOpen;

    protected InnerTextView mInnerTextView;
    private TextView mClickableView;
    private boolean mRelayout;
    private boolean mCollapsed = true;      // 默认收缩
    private int mMaxCollapsedLines;         // 收缩行数
    private int mAnimationDuration;         // 动画时长
    private float mAnimAlphaStart;          // 透明度
    private int mTextSize;                  // 文本大小
    // 收缩的高度、展开的高度、收缩和展开之间的高度
    private int mCollapsedHeight, mExpandedHeight, mMarginBetweenTxtAndBottom;
    private boolean mAnimating;
    private OnExpandStateChangeListener mListener;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_expandable_maxCollapsedLines, MAX_COLLAPSED_LINES);
        mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_expandable_animDuration, DEFAULT_ANIM_DURATION);
        mAnimAlphaStart = typedArray.getFloat(R.styleable.ExpandableTextView_expandable_animAlphaStart, DEFAULT_ANIM_ALPHA_START);
        mTextSize = typedArray.getInt(R.styleable.ExpandableTextView_expandable_textSize, DEFAULT_TEXT_SIZE);
        mAnimAlphaOpen = typedArray.getBoolean(R.styleable.ExpandableTextView_expandable_animAlphaOpen, true);
        typedArray.recycle();

        // add inner textview
//        mInnerTextView = new MInnerTextView(getContext());
//        mInnerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
//        mInnerTextView.setMovementMethod(LinkMovementMethod.getInstance());
//        mInnerTextView.setHighlightColor(getResources().getColor(android.R.color.transparent));
//        addView(mInnerTextView);

        // default visibility is gone
        setVisibility(GONE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // while an animation is in progress, intercept all the touch events to children to
        // prevent extra clicks during the animation
        return mAnimating;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If no change, measure and return
        if (!mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;
        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mInnerTextView.setMaxLines(Integer.MAX_VALUE);
        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // If the text fits in collapsed mode, we are done.
        if (mInnerTextView.getLineCount() <= mMaxCollapsedLines) {
            return;
        }
        // Saves the text height w/ max lines
        mExpandedHeight = getRealTextViewHeight(mInnerTextView);
        // Doesn't fit in collapsed mode. Collapse text view as needed. Show
        // button.
        if (mCollapsed) {
            mInnerTextView.setMaxLines(mMaxCollapsedLines);
        }
        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mInnerTextView.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTxtAndBottom = getHeight() - mInnerTextView.getHeight();
                }
            });
            // Saves the collapsed height of this ViewGroup
            mCollapsedHeight = getMeasuredHeight();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mInnerTextView = findViewById(R.id.expandable_inner_text_view);
        mInnerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        mInnerTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mInnerTextView.setHighlightColor(getResources().getColor(android.R.color.transparent));

        mClickableView = findViewById(R.id.expandable_clickable_view);
        mClickableView.setVisibility(GONE);
        mInnerTextView.setClickableView(mClickableView);
    }

    /**
     * 设置文本，默认可点击
     *
     * @param text
     */
    public void setText(@Nullable CharSequence text) {
        setText(text, true);
    }

    /**
     * 设置文本
     *
     * @param text
     * @param clickable 设置 MInnerTextView 是否可点击
     */
    public void setText(@Nullable CharSequence text, boolean clickable) {
        if (clickable) {
            mInnerTextView.setOnClickListener(this);
        } else {
            mClickableView.setVisibility(VISIBLE);
            mClickableView.setOnClickListener(this);
        }
        mRelayout = true;
        mInnerTextView.setCanClick(clickable);
        mInnerTextView.setMaxCollapsedLines(mMaxCollapsedLines);
        mInnerTextView.setOriginText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    @Nullable
    public CharSequence getText() {
        if (mInnerTextView == null) {
            return "";
        }
        return mInnerTextView.getText();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.expandable_inner_text_view) {
            startCollapsedOrExpanded();
        } else if (view.getId() == R.id.expandable_clickable_view) {
            if (mListener != null) {
                mListener.onClickTargetView();
            }
        }
    }

    /**
     * 当 MInnerTextView 可点击时，收缩和展开动画
     */
    private void startCollapsedOrExpanded() {
        mCollapsed = !mCollapsed;
        // mark that the animation is in progress
        mAnimating = true;
        Log.i(TAG, "startCollapsedOrExpanded: " + mCollapsed);
        ValueAnimator valueAnimator = null;
        if (mCollapsed) {
            valueAnimator = getAnimator(mCollapsed, mExpandedHeight, mCollapsedHeight);
        } else {
            valueAnimator = getAnimator(mCollapsed, mCollapsedHeight, mExpandedHeight);
        }
        valueAnimator.start();
    }

    /**
     * 动画
     *
     * @param collapsed   当前状态：true-收缩，false-展开
     * @param startHeight 初始高度
     * @param endHeight   最大高度
     * @return
     */
    private ValueAnimator getAnimator(final boolean collapsed, int startHeight, int endHeight) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        valueAnimator.setDuration(mAnimationDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (collapsed) {
                    mInnerTextView.setMaxHeight(mCollapsedHeight + value);
                    getLayoutParams().height = value;
                } else {
                    mInnerTextView.setMaxHeight(value);
                    getLayoutParams().height = value;
                }
                requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimating = false;
                if (mAnimAlphaOpen) {
                    mInnerTextView.setAlpha(1.0f);
                }
                if (collapsed) {
                    mInnerTextView.setTextCollapsed();
                }
                if (mListener != null) {
                    mListener.onExpandStateChanged(mInnerTextView, !collapsed);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mAnimAlphaOpen) {
                    mInnerTextView.setAlpha(mAnimAlphaStart);
                }
                if (!collapsed) {
                    mInnerTextView.setTextExpanded();
                }
            }
        });
        return valueAnimator;
    }

    private static int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    public void setOnExpandStateChangeListener(@Nullable OnExpandStateChangeListener listener) {
        mListener = listener;
    }

    private interface OnExpandStateChangeListener {
        /**
         * Called when the expand/collapse animation has been finished
         *
         * @param textView   - TextView being expanded/collapsed
         * @param isExpanded - true if the TextView has been expanded
         */
        void onExpandStateChanged(TextView textView, boolean isExpanded);

        /**
         * Called when click clickableview
         */
        void onClickTargetView();
    }

    public abstract static class OnExpandStateChangeListenerImp implements OnExpandStateChangeListener {

        @Override
        public void onExpandStateChanged(TextView textView, boolean isExpanded) {

        }

        @Override
        public void onClickTargetView() {

        }
    }
}
