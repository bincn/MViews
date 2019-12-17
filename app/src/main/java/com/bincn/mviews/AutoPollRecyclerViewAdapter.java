package com.bincn.mviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mwb
 * @date 2019-12-17
 */
public class AutoPollRecyclerViewAdapter
        extends RecyclerView.Adapter<AutoPollRecyclerViewAdapter.BaseViewHolder> {

    private List<String> mList = new ArrayList<>();
    private Context mContext;

    public AutoPollRecyclerViewAdapter(Context context) {
        this.mContext = context;
        for (int i = 1; i < 4; i++) {
            mList.add("单列竖向滚动列表 " + i);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_auto_poll_recycler_view, parent, false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (mList == null || mList.size() == 0) {
            return;
        }
        holder.mTvContent.setText(mList.get(position % mList.size()));
    }

    @Override
    public int getItemCount() {
        return mList.size() > 0 ? Integer.MAX_VALUE : 0;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        TextView mTvContent;

        public BaseViewHolder(View itemView) {
            super(itemView);
            mTvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
