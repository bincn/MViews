package com.bincn.views.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bincn.views.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bin
 * @date 2019-10-18
 */
public class AutoVerticalScrollLayout extends FrameLayout {

    private ScrollHandler mHandler;
    private MyRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private int position = 0;
    private Context mContext;

    public AutoVerticalScrollLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AutoVerticalScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoVerticalScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_auto_vertical_scroll, this);
        mHandler = new ScrollHandler(this);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        mAdapter = new MyRecyclerAdapter(mContext);
        recyclerView = findViewById(R.id.rvNews);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                return false;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setData(List<String> data) {
        mAdapter.setList(data);
        if (data != null && data.size() > 0) {
            mHandler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    private void smoothScroll() {
        position++;
        recyclerView.smoothScrollToPosition(position);
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 弱引用防止内存泄露
     */
    private static class ScrollHandler extends Handler {

        private WeakReference<AutoVerticalScrollLayout> mView;

        public ScrollHandler(AutoVerticalScrollLayout view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mView.get() != null) {
                mView.get().smoothScroll();
            }
        }
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private Context mContext;
        private List<String> mList;

        public MyRecyclerAdapter(Context context) {
            mContext = context;
            mList = new ArrayList<>();
        }

        public void setList(List<String> list) {
            this.mList.clear();
            this.mList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_auto_vertical_scroll, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.bindData(mList.get(position % mList.size()));
            holder.mTextView.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    Toast.makeText(mContext,
                            "text : " + mList.get(position % mList.size()) + " , position : " +
                                    (position % mList.size()), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size() > 0 ? Integer.MAX_VALUE : 0;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_content);
        }

        public void bindData(String content) {
            mTextView.setText(content);
        }
    }
}
