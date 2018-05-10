package com.promeets.android.adapter;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.bumptech.glide.Glide;
import com.promeets.android.object.ExpertProfile;
import com.promeets.android.object.ExpertService;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class WishListAdapter extends BaseAdapter {
    private ArrayList<ExpertProfile> expertProfiles;
    private BaseActivity mBaseActivity;
    private LayoutInflater mInflater;

    private int dp4;
    private int dp16;
    private int dp32;
    private int dp40;

    public WishListAdapter(BaseActivity baseActivity, ArrayList<ExpertProfile> expertProfiles) {
        this.mBaseActivity = baseActivity;
        this.expertProfiles = expertProfiles;
        mInflater = LayoutInflater.from(mBaseActivity);
        dp4 = ScreenUtil.convertDpToPx(4, mBaseActivity);
        dp16 = ScreenUtil.convertDpToPx(16, mBaseActivity);
        dp32 = ScreenUtil.convertDpToPx(32, mBaseActivity);
        dp40 = ScreenUtil.convertDpToPx(40, mBaseActivity);
    }

    @Override
    public int getCount() {
        return expertProfiles.size();
    }

    @Override
    public ExpertProfile getItem(int i) {
        return expertProfiles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_wishlist_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams();
        if (i == 0)
            lp.setMargins(dp16, dp40, dp16, dp4);
        else if (i == expertProfiles.size() - 1)
            lp.setMargins(dp16, dp4, dp16, dp32);
        else
            lp.setMargins(dp16, dp4, dp16, dp4);
        holder.mCardView.requestLayout();

        ExpertProfile expertProfile = getItem(i);
        holder.mTxtName.setText(expertProfile.getFullName());
        holder.mTxtPosition.setText(expertProfile.getPosition());
        if (!StringUtils.isEmpty(expertProfile.getSmallphotoUrl()))
            Glide.with(mBaseActivity).load(expertProfile.getSmallphotoUrl()).into(holder.mImg);

        holder.mLayService.removeAllViews();
        if (expertProfile.getServiceList().size() > 0) {
            for (int index = 0; index < expertProfile.getServiceList().size(); index++) {
                ExpertService serviceList = expertProfile.getServiceList().get(index);
                View serviceListView = mBaseActivity.getLayoutInflater().inflate(R.layout.wish_list_service_layout, null);
                TextView txtServiceName = (TextView) serviceListView.findViewById(R.id.service_name);
                TextView txtServicePrice = (TextView) serviceListView.findViewById(R.id.price);
                txtServiceName.setText(serviceList.title);

                if (!StringUtils.isEmpty(serviceList.price)) {
                    Double num;
                    DecimalFormat format = new DecimalFormat(("0.##"));
                    num = Double.valueOf(serviceList.price);

                    if (num == 0)
                        txtServicePrice.setText("Free");
                    else
                        txtServicePrice.setText("$" + format.format(num));
                }

                holder.mLayService.addView(serviceListView);
            }
        } else
            holder.mLayService.setVisibility(View.GONE);

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.card_view)
        CardView mCardView;
        @BindView(R.id.profile_image)
        CircleImageView mImg;
        @BindView(R.id.name)
        TextView mTxtName;
        @BindView(R.id.position)
        TextView mTxtPosition;
        @BindView(R.id.service_layout)
        LinearLayout mLayService;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
