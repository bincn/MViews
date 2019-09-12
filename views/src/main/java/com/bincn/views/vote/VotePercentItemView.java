package com.bincn.views.vote;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.bincn.views.R;

/**
 * Created by bin on 2019/5/21.
 */
public class VotePercentItemView extends View {

    private int mWidth;
    private int mHeight;
    private boolean mIsLeft;
    private RectF mRectF;
    private Path mPath;
    private Paint paint;

    public VotePercentItemView(Context context) {
        this(context, null);
    }

    public VotePercentItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VotePercentItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VotePercentView);
            mIsLeft = ta.getBoolean(R.styleable.VotePercentView_vote_isLeft, true);
            ta.recycle();
        }

        mRectF = new RectF();
        mPath = new Path();
        paint = new Paint();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mPath.reset();
        paint.setAntiAlias(true);
        // 线性渐变色
        if (mIsLeft) {
            LinearGradient linearGradient = new LinearGradient(0, mHeight / 2, mWidth, mHeight / 2,
                    Color.parseColor("#79C0FF"), Color.parseColor("#589AFF"), Shader.TileMode.CLAMP);
            paint.setShader(linearGradient);
        } else {
            LinearGradient linearGradient = new LinearGradient(0, mHeight / 2, mWidth, mHeight / 2,
                    Color.parseColor("#FF73C9"), Color.parseColor("#FF86DF"), Shader.TileMode.CLAMP);
            paint.setShader(linearGradient);
        }

        // 左圆弧
        mRectF.set(0, 0, mHeight, mHeight);
        mPath.addArc(mRectF, -90, -180);
        // 矩形
        mPath.lineTo(mWidth - mHeight / 2, mHeight);

        // 右圆弧
        mRectF.set(mWidth - mHeight, 0, mWidth, mHeight);
        mPath.addArc(mRectF, 90, -180);
        mPath.lineTo(mHeight / 2, 0);
        canvas.drawPath(mPath, paint);
        canvas.restore();
    }

    public void isLeft(boolean isLeft) {
        mIsLeft = isLeft;
        invalidate();
    }
}
