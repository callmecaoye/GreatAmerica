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
 * Created by sosasang on 2/2/18.
 */

public class TagAdapter extends BaseAdapter {

    private BaseActivity mBaseActivity;
    private List<String> tags;
    private LayoutInflater mInflater;

    public TagAdapter(BaseActivity mBaseActivity, List<String> tags) {
        this.mBaseActivity = mBaseActivity;
        this.tags = tags;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int i) {
        return tags.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_tag_item, null);
            holder = new ViewHolder();
            holder.mTxtTag = convertView.findViewById(R.id.content);
            holder.mTxtDel = convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String tag = tags.get(i);
        holder.mTxtTag.setText(tag);

        holder.mTxtDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tags.remove(i);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView mTxtTag;
        TextView mTxtDel;
    }
}
