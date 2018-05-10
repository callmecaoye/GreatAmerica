package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.GroupChatActivity;
import com.promeets.android.activity.UserRequestSettingActivity;
import com.promeets.android.fragment.GroupChatExpertFragment;
import com.promeets.android.object.EventLocationPOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.promeets.android.activity.AppointStatusActivity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import com.promeets.android.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sosasang on 8/25/17.
 */

public class LocationSingleCheckAdapter extends BaseAdapter {
    private ArrayList<EventLocationPOJO> listService;
    private LayoutInflater mInflater;
    private BaseActivity mBaseActivity;
    private GroupChatExpertFragment mFragment;

    public LocationSingleCheckAdapter(BaseActivity baseActivity, ArrayList<EventLocationPOJO> results) {
        this.mBaseActivity = baseActivity;
        listService = results;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    public LocationSingleCheckAdapter(BaseActivity baseActivity, GroupChatExpertFragment fragment, ArrayList<EventLocationPOJO> results) {
        this.mBaseActivity = baseActivity;
        this.mFragment = fragment;
        listService = results;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listService.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return listService.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = mInflater.inflate(R.layout.check_txt_item, null);
        final EventLocationPOJO pojo = listService.get(position);
        final CheckedTextView text = (CheckedTextView) convertView.findViewById(R.id.text);
        text.setText(pojo.location);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < listService.size(); i++) {
                    if (i == position)
                        listService.get(i).isSelected = true;
                    else
                        listService.get(i).isSelected = false;
                }
                notifyDataSetChanged();

                if (mBaseActivity instanceof AppointStatusActivity) {
                    if (pojo.isSelected)
                        ((AppointStatusActivity)mBaseActivity).locationSelected = true;
                    ((AppointStatusActivity)mBaseActivity).updatePayment();
                    ((AppointStatusActivity)mBaseActivity).getChkCall().setChecked(false);
                } else if (mBaseActivity instanceof GroupChatActivity
                        && mFragment != null)
                    mFragment.getChkCall().setChecked(false);
            }
        });

        if (mBaseActivity instanceof UserRequestSettingActivity) {
            HashMap<EventLocationPOJO, Marker> map = ((UserRequestSettingActivity)mBaseActivity).getMap();
            if (pojo.isSelected) {
                text.setChecked(true);
                if (map.get(pojo) != null)
                    map.get(pojo).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary));
            } else {
                text.setChecked(false);
                if (map.get(pojo) != null)
                    map.get(pojo).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_gray));
            }
        } else if (mBaseActivity instanceof AppointStatusActivity) {
            HashMap<EventLocationPOJO, Marker> map = ((AppointStatusActivity)mBaseActivity).getMap();
            if (pojo.isSelected) {
                text.setChecked(true);
                if (map.get(pojo) != null)
                    map.get(pojo).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary));
            } else {
                text.setChecked(false);
                if (map.get(pojo) != null)
                    map.get(pojo).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_gray));
            }
        } else {
            if (pojo.isSelected)
                text.setChecked(true);
            else
                text.setChecked(false);

        }


        return convertView;
    }

    public void add(EventLocationPOJO item) {
        this.listService.add(item);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.listService.remove(position);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<EventLocationPOJO> items) {
        this.listService.clear();
        this.listService = items;
        notifyDataSetChanged();
    }

    public EventLocationPOJO getSingleSelected() {
        for (EventLocationPOJO pojo : listService)
            if (pojo.isSelected)
                return pojo;
        return null;
    }

    public ArrayList<EventLocationPOJO> getResult() {

        ArrayList<EventLocationPOJO> eventLocationPOJOs = new ArrayList<>();
        for (EventLocationPOJO eventLocationPOJO : listService) {
            if (eventLocationPOJO.isSelected)
                eventLocationPOJOs.add(eventLocationPOJO);
        }
        return eventLocationPOJOs;


    }

    public ArrayList<EventLocationPOJO> getAll() {
        return listService;
    }

    public void clearSelection() {
        for (EventLocationPOJO pojo : listService)
            if (pojo.isSelected) {
                pojo.isSelected = false;
                notifyDataSetChanged();
                return;
        }
    }

    static class ViewHolder {
        @BindView(R.id.text)
        TextView tv_location;
        //@BindView(R.id.list_service_location_distance)
        //TextView tv_range;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
