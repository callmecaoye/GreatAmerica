package com.promeets.android.adapter;

import android.content.Context;
import com.promeets.android.object.HistoryPOJO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by sosasang on 1/6/17.
 */

public class ListviewHistoryAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    final private Map<String, List<HistoryPOJO>> map = new HashMap<>();
    private List<HistoryPOJO> mOrders;
    private LayoutInflater mInflater;
    private Context mContext;

    public ListviewHistoryAdapter(Context context, List<HistoryPOJO> orders) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mOrders = orders;
    }

    @Override
    public int getCount() {
        return mOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_bill_history, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HistoryPOJO order = mOrders.get(position);
        if (!StringUtils.isEmpty(order.photoURL)) {
            Glide.with(mContext).load(order.photoURL).into(holder.iv_photo);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(order.eventDate);
            sdf = new SimpleDateFormat("MMM dd, yyyy");
            holder.tv_date.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_fullname.setText(order.fullName);
        holder.tv_displayInfo.setText(order.displayInfo);
        if (!StringUtils.isEmpty(order.amount)) {
            String amount = new StringBuilder(order.amount).insert(1, "$").toString();
            holder.tv_amount.setText(amount);
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.listview_txt_header, parent, false);
            //holder.mImgViewIcon = (ImageView) convertView.findViewById(R.id.type_icon);
            holder.mTxtViewType = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        HistoryPOJO order = mOrders.get(position);
        if (order.displayType.equalsIgnoreCase("pending")) {
            //holder.mImgViewIcon.setImageResource(R.drawable.ic_pending);
            holder.mTxtViewType.setText("Pending");
        } else {
            //holder.mImgViewIcon.setImageResource(R.drawable.ic_complete);
            holder.mTxtViewType.setText("Completed");
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        HistoryPOJO order = mOrders.get(position);
        //String headerId = order.month.toString() + order.year.toString();
        String headerId = "0";
        if (order.displayType.equalsIgnoreCase("pending"))
            headerId = "1";

        return Long.parseLong(headerId);
    }

    static class HeaderViewHolder {
        TextView mTxtViewType;
    }

    static class ViewHolder {
        @BindView(R.id.activity_bill_management_history_list_photo)
        CircleImageView iv_photo;
        @BindView(R.id.activity_bill_management_history_list_date)
        TextView tv_date;
        @BindView(R.id.activity_bill_management_history_list_fullname)
        TextView tv_fullname;
        @BindView(R.id.activity_bill_management_history_list_displayInfo)
        TextView tv_displayInfo;
        @BindView(R.id.activity_bill_management_history_list_amount)
        TextView tv_amount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
