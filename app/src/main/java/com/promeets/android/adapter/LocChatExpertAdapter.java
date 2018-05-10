package com.promeets.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.promeets.android.object.EventLocationPOJO;

import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by sosasang on 11/29/17.
 */

public class LocChatExpertAdapter extends BaseAdapter {

    private ArrayList<EventLocationPOJO> listService;

    private LayoutInflater mInflater;

    private BaseActivity mBaseActivity;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public LocChatExpertAdapter(BaseActivity baseActivity, ArrayList<EventLocationPOJO> results) {
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
        TextView mTxtLoc = (TextView) view.findViewById(R.id.content);
        TextView mTxtDelete = (TextView) view.findViewById(R.id.delete);

        final EventLocationPOJO pojo = listService.get(position);
        viewBinderHelper.bind(mLayout, String.valueOf(pojo.id));
        mTxtLoc.setText(pojo.location);

        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EventLocationPOJO e : listService) {
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

    public ArrayList<EventLocationPOJO> getResult() {
        return listService;
    }

    public ArrayList<EventLocationPOJO> getAll() {
        return listService;
    }
}
