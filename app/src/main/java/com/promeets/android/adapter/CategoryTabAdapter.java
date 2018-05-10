package com.promeets.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.promeets.android.object.Tab;
import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by xiaoyudong on 11/14/16.
 */

public class CategoryTabAdapter extends RecyclerView.Adapter<CategoryTabAdapter.ViewHolder> {

    private ArrayList<Tab> listService;

    private int mSelect = 0;

    private Context mContext;

    public CategoryTabAdapter(Context context, ArrayList<Tab> results){
        listService = results;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_tab, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tab tab = listService.get(position);
        holder.txttitle.setText(tab.tab);

        if (position == mSelect)
            holder.txttitle.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        else
            holder.txttitle.setTextColor(ContextCompat.getColor(mContext, R.color.pm_dark));
    }

    @Override
    public int getItemCount() {
        return listService.size();
    }



    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public void setSelect(int position) {
        this.mSelect = position;
        notifyDataSetChanged();
    }

    public int getSelect() {
        return mSelect;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txttitle;

        public ViewHolder(View view){
            super(view);
            txttitle = (TextView) view.findViewById(R.id.list_tab_item);
        }
    }
}
