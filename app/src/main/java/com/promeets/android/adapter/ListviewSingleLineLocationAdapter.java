package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.ExpertAvailActivity;
import android.content.Context;
import com.promeets.android.object.EventLocationPOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.gms.maps.SupportMapFragment;
import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by sosasang on 2/9/17.
 */

public class ListviewSingleLineLocationAdapter extends BaseAdapter {

    private static ArrayList<EventLocationPOJO> mLocationList = new ArrayList<>();
    private LayoutInflater mInflater;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private BaseActivity mBaseActivity;

    public ListviewSingleLineLocationAdapter(Context aContext, ArrayList<EventLocationPOJO> aLocationList) {
        mLocationList = aLocationList;
        mInflater = LayoutInflater.from(aContext);
        viewBinderHelper.setOpenOnlyOne(true);
        this.mBaseActivity = (BaseActivity) aContext;
    }

    @Override
    public int getCount() {
        return mLocationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLocationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mLocationList.get(position).id;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        view = mInflater.inflate(R.layout.list_location_single_line, parent, false);

        final SwipeRevealLayout mLayout = (SwipeRevealLayout) view.findViewById(R.id.swipe);
        TextView mLocation = (TextView) view.findViewById(R.id.location);
        TextView mTxtDelete = (TextView) view.findViewById(R.id.delete);

        final EventLocationPOJO mLoc = mLocationList.get(position);
        viewBinderHelper.bind(mLayout, String.valueOf(mLoc.location));
        mLocation.setText(mLoc.location);

        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationList.remove(position);
                notifyDataSetChanged();
                viewBinderHelper.closeLayout(mLoc.location);
                if (mBaseActivity instanceof ExpertAvailActivity) {
                    SupportMapFragment mapFragment = ((ExpertAvailActivity)mBaseActivity).getMapFragment();
                    if (mapFragment != null)
                        mapFragment.getMapAsync(((ExpertAvailActivity)mBaseActivity).getMapCallback());
                }
            }
        });

        return view;
    }

    public void add(EventLocationPOJO item) {
        mLocationList.add(item);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mLocationList.remove(position);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<EventLocationPOJO> items) {
        mLocationList.clear();
        mLocationList = items;
        notifyDataSetChanged();
    }

    public ArrayList<EventLocationPOJO> getAll() {
        return mLocationList;
    }
}
