package com.promeets.android.adapter;

import android.content.Context;
import com.promeets.android.custom.CategoryView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.promeets.android.activity.CategoryListActivity;
import com.promeets.android.activity.CategorySearchActivity;

import com.promeets.android.R;

import java.util.ArrayList;


/**
 * Created by sosasang on 7/19/17.
 */

public class RecycleCategoryTitleAdapter extends RecyclerView.Adapter<RecycleCategoryTitleAdapter.ViewHolder> {

    private ArrayList<String> listService;

    private Context mContext;

    private SparseArray<View> map;

    private int mSelect;

    public RecycleCategoryTitleAdapter(Context context, ArrayList<String> results){
        this.listService = results;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_tab, parent, false);

        return new RecycleCategoryTitleAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = listService.get(position);
        holder.mTxtTitle.setText(title);

        if (position == mSelect)
            holder.mTxtTitle.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        else
            holder.mTxtTitle.setTextColor(ContextCompat.getColor(mContext, R.color.pm_dark));
    }

    @Override
    public int getItemCount() {
        return listService.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTxtTitle;

        public ViewHolder(View view){
            super(view);
            mTxtTitle = (TextView) view.findViewById(R.id.list_tab_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelect = getAdapterPosition();
            notifyDataSetChanged();
            if (mContext instanceof CategoryListActivity) {
                ((CategoryListActivity)mContext).getRecyclerView().smoothScrollToPosition(mSelect);
                map = ((CategoryListActivity)mContext).getMap();
                final ScrollView mScrollView = ((CategoryListActivity)mContext).getScrollView();
                final CategoryView view = (CategoryView) map.get(mSelect);
                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.smoothScrollTo(0, view.getTop());
                    }
                });
            } else if (mContext instanceof CategorySearchActivity) {
                ((CategorySearchActivity)mContext).getRecyclerView().smoothScrollToPosition(mSelect);
                map = ((CategorySearchActivity)mContext).getMap();
                final ScrollView mScrollView = ((CategorySearchActivity)mContext).getScrollView();
                final View view = map.get(mSelect);
                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.smoothScrollTo(0, view.getTop());
                    }
                });
            }
        }
    }

    public void reset() {
        mSelect = 0;
        notifyDataSetChanged();
        if (mContext instanceof CategoryListActivity) {
            ((CategoryListActivity)mContext).getRecyclerView().scrollToPosition(0);
            ScrollView mScrollView = ((CategoryListActivity)mContext).getScrollView();
            mScrollView.scrollTo(0, 0);
        } else if (mContext instanceof CategorySearchActivity) {
            ((CategorySearchActivity)mContext).getRecyclerView().scrollToPosition(0);
            ScrollView mScrollView = ((CategorySearchActivity)mContext).getScrollView();
            mScrollView.scrollTo(0, 0);
        }
    }
}
