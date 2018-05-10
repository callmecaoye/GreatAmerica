package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.UserRequestSettingActivity;
import com.promeets.android.object.EventTimePOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.Gson;
import com.promeets.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xiaoyudong on 11/14/16.
 */

public class TimeSwipeAdapter extends BaseAdapter {

    private ArrayList<EventTimePOJO> listService;

    private LayoutInflater mInflater;

    private BaseActivity mBaseActivity;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    Gson gson = new Gson();

    public TimeSwipeAdapter(BaseActivity baseActivity, ArrayList<EventTimePOJO> results) {
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.time_swipe_item, null);
            holder.mLayout = (SwipeRevealLayout) convertView.findViewById(R.id.swipe);
            holder.mTxtDate = (TextView) convertView.findViewById(R.id.date);
            holder.mTxtTime = (TextView) convertView.findViewById(R.id.time);
            holder.mTxtDelete = (TextView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final EventTimePOJO pojo = listService.get(position);
        viewBinderHelper.bind(holder.mLayout, String.valueOf(pojo.id));

        /*mTxtDate.setText(pojo.detailDay);*/
        holder.mTxtTime.setText(pojo.beginHourOfDay + " - " + pojo.endHourOfDay);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(pojo.detailDay);
            sdf = new SimpleDateFormat("MMM dd, yyyy");
            holder.mTxtDate.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.mTxtDelete.setOnClickListener(new View.OnClickListener() {
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
                ArrayList<String> mEventList = ((UserRequestSettingActivity)mBaseActivity).getEventList();
                for (String mStrEvent : mEventList) {
                    WeekViewEvent mEvent = gson.fromJson(mStrEvent, WeekViewEvent.class);
                    if (mEvent.getId() == pojo.id) {
                        mEventList.remove(mStrEvent);
                        ((UserRequestSettingActivity)mBaseActivity).updateTimeUI();
                        break;
                    }
                }
            }
        });

        return convertView;
    }


    public ArrayList<EventTimePOJO> getResult() {
        return listService;
    }

    public ArrayList<EventTimePOJO> getAll() {
        return listService;
    }

    final class ViewHolder {
        SwipeRevealLayout mLayout;
        TextView mTxtDate;
        TextView mTxtTime;
        TextView mTxtDelete;
    }
}
