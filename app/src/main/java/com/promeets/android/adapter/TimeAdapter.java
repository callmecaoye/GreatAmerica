package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sosasang on 8/21/17.
 */

public class TimeAdapter extends BaseAdapter {

    private ArrayList<EventTimePOJO> listService;

    private LayoutInflater mInflater;

    private BaseActivity mBaseActivity;

    public TimeAdapter(BaseActivity baseActivity, ArrayList<EventTimePOJO> results) {
        mBaseActivity = baseActivity;
        listService = results;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public int getCount() {
        return listService.size();
    }

    @Override
    public EventTimePOJO getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.time_item, null);
            holder = new ViewHolder();
            holder.txtDay = (TextView) convertView.findViewById(R.id.day);
            holder.txtDate = (TextView) convertView.findViewById(R.id.date);
            holder.txtTimeRange = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EventTimePOJO pojo = listService.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(pojo.detailDay);
            sdf = new SimpleDateFormat("EEE");
            holder.txtDay.setText(sdf.format(date));
            sdf = new SimpleDateFormat("MMM dd, yyyy");
            holder.txtDate.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            int hour = Integer.valueOf(pojo.beginHourOfDay.split(":")[0]);
            String begin;
            if (hour < 12)
                begin = pojo.beginHourOfDay + " AM";
            else if (hour == 12)
                begin = pojo.beginHourOfDay + " PM";
            else
                begin = String.format("%02d", hour - 12) + pojo.beginHourOfDay.substring(2, pojo.beginHourOfDay.length()) + " PM";

            hour = Integer.valueOf(pojo.endHourOfDay.split(":")[0]);
            String end;
            if (hour < 12)
                end = pojo.endHourOfDay + " AM";
            else if (hour == 12)
                end = pojo.endHourOfDay + " PM";
            else
                end = String.format("%02d", hour - 12) + pojo.endHourOfDay.substring(2, pojo.endHourOfDay.length()) + " PM";

            holder.txtTimeRange.setText(begin + " - " + end);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView txtDay, txtDate, txtTimeRange;
    }
}
