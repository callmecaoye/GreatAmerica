package com.promeets.android.adapter;

import android.content.Context;
import com.promeets.android.object.EventTimePOJO;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.promeets.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sosasang on 8/23/17.
 */

public class TimeCheckAdapter extends BaseAdapter {

    private ArrayList<EventTimePOJO> listService;

    private LayoutInflater mInflater;

    private Context context;

    public TimeCheckAdapter(Context context, ArrayList<EventTimePOJO> results) {
        this.context = context;
        listService = results;
        mInflater = LayoutInflater.from(context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.check_txt_item, null);
        final EventTimePOJO pojo = listService.get(position);
        final CheckedTextView text = (CheckedTextView) convertView.findViewById(R.id.text);




        String dayStr = "";
        String dateStr = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(pojo.detailDay);
            sdf = new SimpleDateFormat("EEE");
            dayStr = sdf.format(date);
            sdf = new SimpleDateFormat("MMM dd, yyyy");
            dateStr = sdf.format(date);
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

            String timeStr = dayStr + " " + dateStr + " " + begin + " - " + end;
            Spannable spannable = new SpannableString(timeStr);
            spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.primary)), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            text.setText(spannable, TextView.BufferType.SPANNABLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pojo.isSelected)
            text.setChecked(true);
        else
            text.setChecked(false);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pojo.isSelected = !pojo.isSelected;
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
