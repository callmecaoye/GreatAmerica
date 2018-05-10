package com.promeets.android.adapter;

/**
 * Created by sosasang on 3/2/18.
 */

import com.promeets.android.activity.UserRequestSettingActivity;
import android.content.Context;
import com.promeets.android.object.EventLocationPOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.promeets.android.activity.AppointStatusActivity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import com.promeets.android.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationCheckAdapter2 extends BaseAdapter {

    private ArrayList<EventLocationPOJO> listService;

    private LayoutInflater mInflater;

    private Context context;

    public LocationCheckAdapter2(Context context, ArrayList<EventLocationPOJO> results) {
        this.context = context;
        listService = results;
        mInflater = LayoutInflater.from(context);
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
        convertView = mInflater.inflate(R.layout.check_txt_item2, null);
        final EventLocationPOJO pojo = listService.get(position);
        final CheckBox text = (CheckBox) convertView.findViewById(R.id.text);
        text.setText(pojo.location);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pojo.isSelected = !pojo.isSelected;
                notifyDataSetChanged();
            }
        });

        if (context instanceof UserRequestSettingActivity) {
            HashMap<EventLocationPOJO, Marker> map = ((UserRequestSettingActivity)context).getMap();
            if (pojo.isSelected) {
                text.setChecked(true);
                if (map.get(pojo) != null)
                    map.get(pojo).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary));
            } else {
                text.setChecked(false);
                if (map.get(pojo) != null)
                    map.get(pojo).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_gray));
            }
        } else if (context instanceof AppointStatusActivity) {
            HashMap<EventLocationPOJO, Marker> map = ((AppointStatusActivity)context).getMap();
            if (pojo.isSelected)
                text.setChecked(true);
            else
                text.setChecked(false);

            if (map.get(pojo) != null)
                map.get(pojo).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary));
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

