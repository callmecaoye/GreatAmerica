package com.promeets.android.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.object.ExpertService;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListviewExpertServiceAdapter extends BaseAdapter {

    private ArrayList<ExpertService> listService;
    private LayoutInflater mInflater;
    private BaseActivity mBaseActivity;

    private int dp5;
    private int dp13;
    private int dp40;

    public ListviewExpertServiceAdapter(Context context, ArrayList<ExpertService> results){
        this.mBaseActivity = (BaseActivity) context;
        listService = results;
        mInflater = LayoutInflater.from(mBaseActivity);

        dp5 = ScreenUtil.convertDpToPx(5, mBaseActivity);
        dp13 = ScreenUtil.convertDpToPx(13, mBaseActivity);
        dp40 = ScreenUtil.convertDpToPx(40, mBaseActivity);
    }

    @Override
    public int getCount() {
        return listService.size();
    }

    @Override
    public ExpertService getItem(int arg0) {
        return listService.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void refresh(ArrayList<ExpertService> items)
    {
        this.listService = items;
        notifyDataSetChanged();
    }
    public void add(ExpertService item){
        this.listService.add(item);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_expert_profile_service, parent,false);
            holder = new ViewHolder();
            holder.mTxtTitle = (TextView) convertView.findViewById(R.id.service_title);
            holder.mTxtPrice = (TextView) convertView.findViewById(R.id.service_price);
            holder.mTxtDuration = (TextView) convertView.findViewById(R.id.service_duration);
            holder.mTxtStatus = (TextView) convertView.findViewById(R.id.service_status);
            holder.mCardView = (CardView) convertView.findViewById(R.id.card_root);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams();
        if (position == 0)
            lp.setMargins(dp13, dp40, dp13, dp5);
        //else if (position == listService.size() - 1)
        //    lp.setMargins(dp13, dp5, dp13, dp40);
        else
            lp.setMargins(dp13, dp5, dp13, dp5);
        holder.mCardView.requestLayout();

        ExpertService item = listService.get(position);
        holder.mTxtTitle.setText(item.title);
        holder.mTxtPrice.setText("$ " + item.price);

        double hour = Integer.valueOf(item.duratingTime) / 60.0;
        DecimalFormat df = new DecimalFormat("#.#");
        if (hour <= 1)
            holder.mTxtDuration.setText("Duration: " + df.format(hour) + " hour");
        else
            holder.mTxtDuration.setText("Duration: " + df.format(hour) + " hours");

        switch (listService.get(position).serviceStatus) {
            case 0:
                holder.mTxtStatus.setText("Verified");
                holder.mTxtStatus.setTextColor(mBaseActivity.getResources().getColor(R.color.primary));
                break;
            case 1:
                holder.mTxtStatus.setText("Pending");
                holder.mTxtStatus.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_gray));
                break;
            default: holder.mTxtStatus.setVisibility(View.GONE);
        }

            /*// become expert and expert
            if (!mCurExpertStatus.equals("2")) {
                holder.cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ExpertServicePOJO service = listService.get(position);
                        Intent intent = new Intent(mContext, BecomeExpertServiceActivity.class);
                        Gson gson = new Gson();
                        String json = gson.toJson(service);
                        intent.putExtra("expertServicePOJO", json);
                        ((Activity)mContext).startActivityForResult(intent, UPDATED_SERVICE);
                        BecomeExpertServiceListActivity.mPositionSel = position;
                    }
                });
            }
            // become expert
            if (mCurExpertStatus.equals("0") || mCurExpertStatus.equals("3")) {
                holder.cell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PromeetsDialog.show((BaseActivity) mContext, "Do you want to delete this service?", new PromeetsDialog.OnSubmitListener() {
                            @Override
                            public void onSubmitListener() {
                                listService.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        return false;
                    }
                });
            }*/
        return convertView;
    }

    static class ViewHolder{
        TextView mTxtTitle;
        TextView mTxtPrice;
        TextView mTxtDuration;
        TextView mTxtStatus;
        CardView mCardView;
    }
}
