package com.promeets.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.object.SubCate;
import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by sosasang on 2/1/18.
 */

public class ExpSubcateAdapter extends BaseAdapter {
    private ArrayList<SubCate> results;
    private LayoutInflater mInflater;

    public ExpSubcateAdapter(BaseActivity activity, ArrayList<SubCate> results) {
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
            holder.mTxtCate = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SubCate subCate = results.get(i);
        holder.mTxtCate.setText(subCate.getTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView mTxtCate;
    }
}
