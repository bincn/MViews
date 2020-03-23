package com.bincn.views.expandableTextView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bincn.views.R;
import com.bincn.views.utils.DeviceUtils;

/**
 * 内部 TextView 可以收缩展开
 *
 * @author mwb
 * @date 2020-03-16
 */
public class ExpandableTextLayout extends FrameLayout implements IExpandableLayout, View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "ExpandableTextLayout";

    protected InnerTextView mInnerTextView;
    private TextView mClickableView;

    /**
     * 默认收缩行数
     */
    private static final int MAX_COLLAPSED_LINES = 4;
    /**
     * 默认动画时间
     */
    private static final int DEFAULT_ANIM_DURATION = 100;
    /**
     * 默认字体大小
     */
    private static final int DEFAULT_TEXT_SIZE = 16;

    private static final String ELLIPSIS = "...";

    // 是否重新 layout，是否已经设置文本
    private boolean mRelayout, mHasSetText;
    // 默认收缩
    private boolean mCollapsed = true;
    // 默认开启收缩模式
    private boolean mCollapsedEnable = true;
    // 收缩行数
    private int mMaxCollapsedLines;
    // 动画时长
    private int mAnimationDuration;
    // 文本大小
    private int mTextSize;
    // 收缩文本的高度、展开文本的高度
    private int mCollapsedTextHeight, mExpandedTextHeight;
    // 正在动画
    private boolean mAnimating;
    // 原始文本（外部传入）、收缩时文本（如果文本需要收缩）
    private CharSequence mOriginText, mCollapsedText;
    private OnExpandableTextLayoutListener mExpandableLayoutListener;
    // 文本颜色，尾部文本颜色
    private int mTextColor, mTailTextColor;
    private ValueAnimator mValueAnimator;

    public ExpandableTextLayout(@NonNull Context context) {
        this(context, null);
    }

    public ExpandableTextLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandableTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextLayout);
            mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextLayout_etl_maxCollapsedLines, MAX_COLLAPSED_LINES);
            mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextLayout_etl_animDuration, DEFAULT_ANIM_DURATION);
            mTextSize = typedArray.getInt(R.styleable.ExpandableTextLayout_etl_textSize, DEFAULT_TEXT_SIZE);
            mTextColor = typedArray.getResourceId(R.styleable.ExpandableTextLayout_etl_textColor, R.color.config_color_50_pure_black);
            mTailTextColor = typedArray.getResourceId(R.styleable.ExpandableTextLayout_etl_tailTextColor, R.color.config_color_link);
            typedArray.recycle();
        }
        // 文本 view
        mInnerTextView = new InnerTextView(getContext());
        mInnerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        mInnerTextView.setTextColor(getContext().getResources().getColor(mTextColor));
        addView(mInnerTextView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // 可点击的 view
        mClickableView = new TextView(getContext());
        mClickableView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        mClickableView.setTextColor(getResources().getColor(mTailTextColor));
        mClickableView.setTextColor(getContext().getResources().getColor(mTailTextColor));
        mClickableView.setPadding(0, 0, DeviceUtils.dip2px(getContext(), 10), 0);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        addView(mClickableView, layoutParams);

        mClickableView.setOnClickListener(this);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

        setTailViewState();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 预先设置一次原始文本 ExpandableTextLayout.setText()
        // 判断实际文本行数是否大于需要的最大行数
        // 获取展开文本的高度（全部文本）
        // 设置实际需要显示的文本行数，获取收缩文本的高度
        if (!mCollapsedEnable || !mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mInnerTextView.getLineCount() <= mMaxCollapsedLines) {
            return;
        }
        mExpandedTextHeight = getRealTextViewHeight(mInnerTextView);
        mInnerTextView.setMaxLines(mMaxCollapsedLines);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCollapsedTextHeight = getMeasuredHeight();
    }

    @Override
    public void onGlobalLayout() {
        if (mHasSetText) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
            mClickableView.setVisibility(GONE);
            if (mCollapsedEnable && mInnerTextView.getLineCount() > mMaxCollapsedLines) {
                mClickableView.setVisibility(VISIBLE);
                setTextInner();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mAnimating) {
            // 动画正在进行，拦截所有触摸事件
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    /**
     * 外部调用，实际文本
     */
    public void setText(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            setVisibility(View.GONE);
            return;
        }
        if (!mCollapsedEnable) {
            mClickableView.setVisibility(GONE);
        }
        mHasSetText = true;
        mRelayout = true;
        mOriginText = text;
        mInnerTextView.setText(text);
    }

    /**
     * 内部调用，根据收缩或展开显示文本
     */
    private void setTextInner() {
        if (getVisibility() == VISIBLE) {
            mInnerTextView.setText(getDisplayText());
        }
    }

    /**
     * 文本实际高度
     */
    private int getRealTextViewHeight(@NonNull TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    /**
     * 显示的文本
     */
    private CharSequence getDisplayText() {
        if (TextUtils.isEmpty(mOriginText)) {
            return mOriginText;
        }
        if (mCollapsed) {
            if (!TextUtils.isEmpty(mCollapsedText)) {
                return mCollapsedText;
            }
            Paint textPaint = mInnerTextView.getPaint();
            Layout textLayout = mInnerTextView.getLayout();
            // 目标行开始和结束的位置，相对于文本起始位置
            int lineStart = textLayout.getLineStart(mMaxCollapsedLines - 1);
            int lineEnd = textLayout.getLineEnd(mMaxCollapsedLines - 1);
            // 目标行的文本
            CharSequence targetLine = mOriginText.subSequence(lineStart, lineEnd);
            // 目标行文本宽度
            int targetLineWidth = (int) measureTextWidth(textPaint, targetLine.toString());
            // 目标行剩余可显示宽度
            int remainWidth = textLayout.getWidth() - targetLineWidth;
            // 尾部需要拼接的宽度
            float tailWidth = getTailWidth(textPaint);
            while (remainWidth < tailWidth) {
                lineEnd--;
                targetLine = mOriginText.subSequence(lineStart, lineEnd);
                targetLineWidth = (int) measureTextWidth(textPaint, targetLine.toString());
                remainWidth = textLayout.getWidth() - targetLineWidth;
            }
            mCollapsedText = mOriginText.subSequence(0, lineEnd) + ELLIPSIS;
            return mCollapsedText;
        }
        return mOriginText;
    }

    /**
     * 尾部需要拼接的宽度
     */
    private float getTailWidth(Paint textPaint) {
        return measureTextWidth(textPaint, ELLIPSIS) + mClickableView.getMeasuredWidth() + DeviceUtils.dip2px(getContext(), 10);
    }

    /**
     * 文本的宽度
     */
    private float measureTextWidth(Paint textPaint, String text) {
        return textPaint.measureText(text);
    }

    /**
     * 无动画
     */
    private void startWithoutAnim() {
        mCollapsed = !mCollapsed;
        setTailViewState();
        mInnerTextView.setMaxHeight(mCollapsed ? getCollapsedHeight() : getExpandedHeight());
        getLayoutParams().height = mCollapsed ? getCollapsedHeight() : getExpandedHeight();
        requestLayout();
        setTextInner();
        if (mExpandableLayoutListener != null) {
            mExpandableLayoutListener.onExpandStateChanged(ExpandableTextLayout.this, !mCollapsed);
        }
    }

    /**
     * 收缩和展开的动画
     */
    private void startWithAnim() {
        if (mAnimating) {
            return;
        }
        mCollapsed = !mCollapsed;
        setTailViewState();
        mAnimating = true;
        if (mCollapsed) {
            mValueAnimator = getAnimator(getExpandedHeight(), getCollapsedHeight());
        } else {
            mValueAnimator = getAnimator(getCollapsedHeight(), getExpandedHeight());
        }
        mValueAnimator.start();
    }

    private void setTailViewState() {
        mClickableView.setText(mCollapsed ? "展开" : "收起");
        mClickableView.setCompoundDrawablePadding(DeviceUtils.dip2px(getContext(), 2));
        mClickableView.setCompoundDrawablesWithIntrinsicBounds(0, 0, mCollapsed ? R.drawable.video_part_unfold : R.drawable.video_part_fold, 0);
    }

    /**
     * 动画
     *
     * @param startHeight 初始高度
     * @param endHeight   最大高度
     */
    private ValueAnimator getAnimator(int startHeight, int endHeight) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        valueAnimator.setDuration(mAnimationDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mInnerTextView.setMaxHeight(value);
                getLayoutParams().height = value;
                requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimating = false;
                if (mCollapsed) {
                    setTextInner();
                }
                if (mExpandableLayoutListener != null) {
                    mExpandableLayoutListener.onExpandStateChanged(ExpandableTextLayout.this, !mCollapsed);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (!mCollapsed) {
                    setTextInner();
                }
            }
        });
        return valueAnimator;
    }

    @Override
    public void onClick(View v) {
        if (mAnimationDuration > 0) {
            startWithAnim();
        } else {
            startWithoutAnim();
        }
    }

    @Override
    public int getCollapsedHeight() {
        return mCollapsedEnable ? mCollapsedTextHeight : getHeight();
    }

    @Override
    public int getExpandedHeight() {
        return mCollapsedEnable ? mExpandedTextHeight + mClickableView.getMeasuredHeight() : getHeight();
    }

    @Override
    public int getOffsetHeight() {
        return getExpandedHeight() - getCollapsedHeight();
    }

    public void setOnExpandableTextLayoutListener(@Nullable OnExpandableTextLayoutListener listener) {
        mExpandableLayoutListener = listener;
    }

    public interface OnExpandableTextLayoutListener {
        /**
         * 收缩、展开动画结束时调用
         *
         * @param expandableTextLayout
         * @param isExpanded           true-展开, false-收缩
         */
        void onExpandStateChanged(ExpandableTextLayout expandableTextLayout, boolean isExpanded);
    }

    public class InnerTextView extends AppCompatTextView {

        public InnerTextView(Context context) {
            this(context, null);
        }

        public InnerTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public InnerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
    }
}
