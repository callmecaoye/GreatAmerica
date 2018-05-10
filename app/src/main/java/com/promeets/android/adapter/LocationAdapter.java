package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.object.EventLocationPOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by sosasang on 8/23/17.
 */

public class LocationAdapter extends BaseAdapter {

    private ArrayList<EventLocationPOJO> listService;

    private LayoutInflater mInflater;

    private BaseActivity mBaseActivity;

    public LocationAdapter(BaseActivity baseActivity, ArrayList<EventLocationPOJO> results) {
        this.mBaseActivity = baseActivity;
        this.listService = results;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public int getCount() {
        return listService.size();
    }

    @Override
    public Object getItem(int position) {
        return listService.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.location_item, null);
            holder = new ViewHolder();
            holder.mTxtLoc = (TextView) convertView.findViewById(R.id.location);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EventLocationPOJO pojo = listService.get(position);
        holder.mTxtLoc.setText(pojo.location);

        return convertView;
    }

    static class ViewHolder {
        TextView mTxtLoc;
    }
}
