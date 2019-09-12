package com.bincn.views.expandableTextView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bincn.views.R;

/**
 * Created by bin on 2019/1/22.
 */
public class ExpandableTextView extends FrameLayout implements View.OnClickListener {

  private static final String TAG = "MExpandableTextView";

  //默认收缩行数
  private static final int MAX_COLLAPSED_LINES = 2;
  //默认动画时间
  private static final int DEFAULT_ANIM_DURATION = 200;
  //默认透明度变化开始值
  private static final float DEFAULT_ANIM_ALPHA_START = 0.7f;
  //默认字体大小
  private static final int DEFAULT_TEXT_SIZE = 16;
  //默认不开启渐变动画
  private boolean mAnimAlphaOpen;

  protected InnerTextView mInnerTextView;
  private TextView mClickableView;
  //是否重新layout
  private boolean mRelayout;
  //默认收缩
  private boolean mCollapsed = true;
  //收缩行数
  private int mMaxCollapsedLines;
  //动画时长
  private int mAnimationDuration;
  //透明度
  private float mAnimAlphaStart;
  //文本大小
  private int mTextSize;
  //收缩的高度、展开的高度、收缩和展开之间的高度
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
    TypedArray typedArray =
        getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
    mMaxCollapsedLines =
        typedArray.getInt(R.styleable.ExpandableTextView_expandable_maxCollapsedLines,
            MAX_COLLAPSED_LINES);
    mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_expandable_animDuration,
        DEFAULT_ANIM_DURATION);
    mAnimAlphaStart = typedArray.getFloat(R.styleable.ExpandableTextView_expandable_animAlphaStart,
        DEFAULT_ANIM_ALPHA_START);
    mTextSize =
        typedArray.getInt(R.styleable.ExpandableTextView_expandable_textSize, DEFAULT_TEXT_SIZE);
    mAnimAlphaOpen =
        typedArray.getBoolean(R.styleable.ExpandableTextView_expandable_animAlphaOpen, true);
    typedArray.recycle();
    //默认隐藏
    setVisibility(GONE);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    //当动画正在进行时，拦截所有触摸事件，防止动画期间的额外点击
    return mAnimating;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // 没有变化，默认测量绘制
    if (!mRelayout || getVisibility() == View.GONE) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      return;
    }
    mRelayout = false;
    // 设置最大行数
    mInnerTextView.setMaxLines(Integer.MAX_VALUE);
    // 重新测量绘制
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    // 如果适应收缩模式，直接结束
    if (mInnerTextView.getLineCount() <= mMaxCollapsedLines) {
      return;
    }
    // 获取 TextView 实际的高度
    mExpandedHeight = getRealTextViewHeight(mInnerTextView);
    // 如果不适应收缩模式，收缩并设置最大可见行数
    if (mCollapsed) {
      mInnerTextView.setMaxLines(mMaxCollapsedLines);
    }
    // 重新测量绘制，使用最新设置
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (mCollapsed) {
      // 获取收缩时容器高度
      mCollapsedHeight = getMeasuredHeight();
    }
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    //内部 TextView
    mInnerTextView = findViewById(R.id.expandable_inner_text_view);
    mInnerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
    mInnerTextView.setMovementMethod(LinkMovementMethod.getInstance());
    mInnerTextView.setHighlightColor(getResources().getColor(android.R.color.transparent));
    //内部可点击 view（i.e. 查看更多）
    mClickableView = findViewById(R.id.expandable_clickable_view);
    mClickableView.setVisibility(GONE);
    mInnerTextView.setClickableView(mClickableView);
  }

  /**
   * 设置文本，默认可点击
   */
  public void setText(@Nullable CharSequence text) {
    setText(text, true);
  }

  /**
   * 设置文本
   *
   * @param clickable 设置 InnerTextView 是否可点击
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
   * 当 InnerTextView 可点击时，收缩和展开动画
   */
  private void startCollapsedOrExpanded() {
    mCollapsed = !mCollapsed;
    mAnimating = true;
    Log.i(TAG, "startCollapsedOrExpanded: " + mCollapsed);
    ValueAnimator valueAnimator = null;
    if (mCollapsed) {
      valueAnimator = getAnimator(mExpandedHeight, mCollapsedHeight);
    } else {
      valueAnimator = getAnimator(mCollapsedHeight, mExpandedHeight);
    }
    valueAnimator.start();
  }

  /**
   * 动画
   *
   * @param startHeight 初始高度
   * @param endHeight 最大高度
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
        if (mAnimAlphaOpen) {
          mInnerTextView.setAlpha(1.0f);
        }
        if (mCollapsed) {
          mInnerTextView.setTextCollapsed();
        }
        if (mListener != null) {
          mListener.onExpandStateChanged(mInnerTextView, !mCollapsed);
        }
      }

      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        if (mAnimAlphaOpen) {
          mInnerTextView.setAlpha(mAnimAlphaStart);
        }
        if (!mCollapsed) {
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
     * 收缩、展开动画结束时调用
     *
     * @param textView TextView
     * @param isExpanded true-TextView 展开
     */
    void onExpandStateChanged(TextView textView, boolean isExpanded);

    /**
     * 点击可点击控件时调用
     */
    void onClickTargetView();
  }

  public abstract static class OnExpandStateChangeListenerImp
      implements OnExpandStateChangeListener {

    @Override
    public void onExpandStateChanged(TextView textView, boolean isExpanded) {

    }

    @Override
    public void onClickTargetView() {

    }
  }
}
