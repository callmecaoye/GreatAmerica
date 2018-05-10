package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.object.ServiceEvent;
import com.promeets.android.util.GlideUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.R;

import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by sosasang on 6/13/17.
 */

public class ListEventAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private BaseActivity mBaseActivity;
    private LayoutInflater mInflater;
    private ArrayList<ServiceEvent> mEvents = new ArrayList<>();

    int width;

    public ListEventAdapter(BaseActivity baseActivity, ArrayList<ServiceEvent> items) {
        this.mBaseActivity = baseActivity;
        this.mEvents = items;
        mInflater = LayoutInflater.from(mBaseActivity);

        width = mBaseActivity.getWidth() - ScreenUtil.convertDpToPx(32, mBaseActivity);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.listview_txt_header, parent, false);
            holder.mTxtType = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        ServiceEvent event = mEvents.get(position);
        if (event.status.equals("commingSoon")) {
            holder.mTxtType.setText("Upcoming Events");
            holder.mTxtType.setTextColor(mBaseActivity.getResources().getColor(R.color.primary));
        } else {
            holder.mTxtType.setText("Past Events");
            holder.mTxtType.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_dark));
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        ServiceEvent event = mEvents.get(position);
        String headerId = "0";
        if (event.status.equals("pastEvent"))
            headerId = "1";

        return Long.parseLong(headerId);
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_event_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mImgView.getLayoutParams().height = (int) (0.5 * width);
        holder.mImgView.requestLayout();

        ServiceEvent event = mEvents.get(position);

        //Glide.with(mContext).load(event.photoUrl).into(holder.mImgView);
        if (!GlideUtils.haveCache(mBaseActivity, event.photoUrl)) {
            GlideUtils.cacheImage(event.photoUrl, mBaseActivity);
            Log.d("cacheEvent", "Caching...");
        }

        if (!StringUtils.isEmpty(GlideUtils.getCache(mBaseActivity, event.photoUrl))) {
            Log.d("cacheEvent", "use cache");
            Glide.with(mBaseActivity).load(GlideUtils.getCache(mBaseActivity, event.photoUrl)).into(holder.mImgView);
        } else {
            Log.d("cacheEvent", "no cache");
            Glide.with(mBaseActivity).load(event.photoUrl).into(holder.mImgView);
        }

        holder.mTxtPrice.setText(event.price);
        holder.mTxtTime.setText(ScreenUtil.convertUnitTime("E. MMM dd. hh:mm aa", event.beginTime));
        holder.mTxtTitle.setText(event.title);
        holder.mTxtLoc.setText(event.city + ", " + event.state);

        return convertView;
    }

    static class HeaderViewHolder {
        TextView mTxtType;
    }

    static class ViewHolder {
        @BindView(R.id.image_view)
        ImageView mImgView;
        @BindView(R.id.price)
        TextView mTxtPrice;
        @BindView(R.id.time)
        TextView mTxtTime;
        @BindView(R.id.title)
        TextView mTxtTitle;
        @BindView(R.id.location)
        TextView mTxtLoc;
        //@BindView(R.id.card_root)
        //CardView mCardView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
