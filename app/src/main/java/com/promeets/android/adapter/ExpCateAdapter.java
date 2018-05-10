package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promeets.android.object.Category;
import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by sosasang on 2/1/18.
 */

public class ExpCateAdapter extends BaseAdapter {

    private ArrayList<Category> results;
    private LayoutInflater mInflater;

    public ExpCateAdapter(BaseActivity activity, ArrayList<Category> results) {
        this.results = results;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int i) {
        return results.get(i);
    }

    @Override
    public long getItemId(int i) {
        return results.get(i).getId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_cate_item, null);
            holder = new ViewHolder();
            holder.mTxtCate = convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Category cate = results.get(i);
        holder.mTxtCate.setText(cate.getTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView mTxtCate;
    }
}
