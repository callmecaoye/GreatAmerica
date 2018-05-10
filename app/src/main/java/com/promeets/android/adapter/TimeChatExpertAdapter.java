package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.Gson;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sosasang on 11/29/17.
 */

public class TimeChatExpertAdapter extends BaseAdapter {

    private ArrayList<EventTimePOJO> listService;

    private LayoutInflater mInflater;

    private BaseActivity mBaseActivity;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    Gson gson = new Gson();

    public TimeChatExpertAdapter(BaseActivity baseActivity, ArrayList<EventTimePOJO> results) {
        mBaseActivity = baseActivity;
        listService = results;
        mInflater = LayoutInflater.from(mBaseActivity);
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public int getCount() {
        return listService.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listService.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        view = mInflater.inflate(R.layout.appoint_chat_expert, null);
        final SwipeRevealLayout mLayout = (SwipeRevealLayout) view.findViewById(R.id.swipe);
        TextView mTxtTime = (TextView) view.findViewById(R.id.content);
        TextView mTxtDelete = (TextView) view.findViewById(R.id.delete);

        final EventTimePOJO pojo = listService.get(position);
        viewBinderHelper.bind(mLayout, String.valueOf(pojo.id));

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

            String timeStr = dayStr + "   " + dateStr + "   " + begin + " - " + end;
            Spannable spannable = new SpannableString(timeStr);
            spannable.setSpan(new ForegroundColorSpan(mBaseActivity.getResources().getColor(R.color.primary)), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mTxtTime.setText(spannable, TextView.BufferType.SPANNABLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EventTimePOJO e : listService) {
                    if (e.id == pojo.id) {
                        listService.remove(e);
                        notifyDataSetChanged();
                        viewBinderHelper.closeLayout(pojo.id+"");
                        break;
                    }
                }
            }
        });

        return view;
    }


    public ArrayList<EventTimePOJO> getResult() {
        return listService;
    }

    public ArrayList<EventTimePOJO> getAll() {
        return listService;
    }
}
