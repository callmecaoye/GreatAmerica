package com.promeets.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.R;

import java.util.List;

/**
 * Created by sosasang on 9/8/17.
 */

public class EmailAdapter extends BaseAdapter {
    private BaseActivity mBaseActivity;
    private List<String> emails;
    private LayoutInflater mInflater;

    public EmailAdapter(BaseActivity mBaseActivity, List<String> emails) {
        this.mBaseActivity = mBaseActivity;
        this.emails = emails;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public int getCount() {
        return emails.size();
    }

    @Override
    public Object getItem(int position) {
        return emails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder.mContent = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String email = emails.get(position);
        holder.mContent.setText(email);
        return convertView;
    }

    final class ViewHolder {
        TextView mContent;
    }
}
