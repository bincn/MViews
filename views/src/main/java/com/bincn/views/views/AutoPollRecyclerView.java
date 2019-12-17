package com.bincn.views.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import java.lang.ref.WeakReference;

/**
 * @author bin
 * @date 2019-11-20
 */
public class AutoPollRecyclerView extends RecyclerView {

    private static final String TAG = "AutoPollRecyclerView";

    private static final long TIME_AUTO_POLL = 16;
    private static final long TIME_AUTO_POLL_1 = 5000;
    AutoPollTask autoPollTask;
    AutoPollTask1 autoPollTask1;
    private int index = 0;

    //标示是否正在自动轮询
    private boolean running;

    //标示是否可以自动轮询,可在不需要的是否置false
    private boolean canRun;
    private final int mTouchSlop;
    // 外部有调用 start()
    private boolean startByExternal;
    // 是否要处理 dispatchTouchEvent()，默认需要处理
    private boolean handleDispatchTouchEvent = true;

    public AutoPollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoPollTask1 = new AutoPollTask1(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 持续滑动（走马灯）
     */
    static class AutoPollTask implements Runnable {

        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null
                    && recyclerView.running
                    && recyclerView.canRun
                    && recyclerView.isAttachedToWindow()) {
                recyclerView.scrollBy(2, 2);
                recyclerView.postDelayed(recyclerView.autoPollTask, TIME_AUTO_POLL);
            }
        }
    }

    /***
     * 一次只能滑一个item（轮播图）
     */
    static class AutoPollTask1 implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask1(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null
                    && recyclerView.running
                    && recyclerView.canRun
                    && recyclerView.isAttachedToWindow()) {
                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.smoothScrollToPosition(
                        layoutManager.findLastCompletelyVisibleItemPosition() + 1);
                recyclerView.postDelayed(recyclerView.autoPollTask1, TIME_AUTO_POLL_1);
            }
        }
    }

    //开启:如果正在运行,先停止->再开启
    public void start() {
        if (running) {
            return;
        }
        canRun = true;
        running = true;
        startByExternal = true;
        postDelayed(autoPollTask1, TIME_AUTO_POLL_1);
    }

    public void stop() {
        running = false;
        //        index=0;
        removeCallbacks(autoPollTask1);
    }

    //取消RecyclerView的惯性，使每次手动只能滑一个
    int lastY = 0;

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!handleDispatchTouchEvent) {
            return false;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) ev.getRawY();
                if (running) {
                    stop();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                if (!running) {
                    start();
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                //                int nowY = (int) ev.getRawY();
                //                //向下滑动
                //                if (nowY - lastY > mTouchSlop) {
                ////                    smoothScrollToPosition(index == 0 ? 0 : --index);
                //                    if (canRun) {
                //                        start();
                //                    }
                //                    return true;
                //                    //向上滑动
                //                } else if (lastY - nowY > mTouchSlop) {
                ////                    smoothScrollToPosition(++index);
                //                    if (canRun) {
                //                        start();
                //                    }
                //                    return true;
                //                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否要处理 dispatchTouchEvent()，默认需要处理
     */
    public void setHandleDispatchTouchEvent(boolean handleDispatchTouchEvent) {
        this.handleDispatchTouchEvent = handleDispatchTouchEvent;
    }

    public boolean isStartByExternal() {
        return startByExternal;
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (startByExternal && !running && canRun) {
            start();
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (running) {
            stop();
        }
    }

    // 实现渐变效果

    //    Paint mPaint;
    //
    //    private int layerId;
    //
    //    private LinearGradient linearGradient;
    //    private int preWidth = 0;// Recyclerview宽度动态变化时，监听每一次的宽度

    //    public void doTopGradualEffect(final int itemViewWidth) {
    //        mPaint = new Paint();
    //        // dst_in 模式，实现底层透明度随上层透明度进行同步显示（即上层为透明时，下层就透明，并不是上层覆盖下层)
    //        final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    //        mPaint.setXfermode(xfermode);
    //        addItemDecoration(new RecyclerView.ItemDecoration() {
    //            @Override
    //            public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    //                super.onDrawOver(canvas, parent, state);
    //
    //                // 当linearGradient为空即第一次绘制 或 Recyclerview宽度发生改变时，重新计算透明位置
    //                if (linearGradient == null || preWidth != parent.getWidth()) {
    //                   // 透明位置从最后一个 itemView 的一半处到 Recyclerview 的最右边
    //                    linearGradient = new LinearGradient(parent.getWidth() - (itemViewWidth / 2), 0.0f, parent.getWidth(), 0.0f, new int[]{Color.BLACK, 0}, null, Shader.TileMode.CLAMP);
    //                    preWidth = parent.getWidth();
    //
    //                }
    //                mPaint.setXfermode(xfermode);
    //                mPaint.setShader(linearGradient);
    //                canvas.drawRect(0.0f, 0.0f, parent.getRight(), parent.getBottom(), mPaint);
    //                mPaint.setXfermode(null);
    //                canvas.restoreToCount(layerId);
    //
    //            }
    //
    //            @Override
    //            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    //
    //                super.onDraw(c, parent, state);
    //                // 此处 Paint的参数这里传的null， 在传入 mPaint 时会出现第一次打开黑屏闪现的问题
    //                // 注意 saveLayer 不能省也不能移动到onDrawOver方法里
    //                layerId = c.saveLayer(0.0f, 0.0f, (float) parent.getWidth(), (float) parent.getHeight(), null, Canvas.ALL_SAVE_FLAG);
    //
    //            }
    //
    //            @Override
    //            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    //
    //                // 该方法作用自行百度
    //                super.getItemOffsets(outRect, view, parent, state);
    //
    //            }
    //        });
    //
    //    }
}