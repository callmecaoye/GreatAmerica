package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.MainActivity;
import com.promeets.android.activity.UserRequestSettingActivity;
import android.content.Intent;
import com.promeets.android.fragment.ServiceDetailPopupFragment;
import android.graphics.Paint;
import com.promeets.android.object.ExpertProfile;
import com.promeets.android.object.ExpertService;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.MixpanelUtil;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.activity.AppointStatusActivity;

import com.bumptech.glide.Glide;

import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.List;

public class RecycleServiceAdapter extends RecyclerView.Adapter<RecycleServiceAdapter.ViewHolder> {

    private List<ExpertService> mListService;
    private BaseActivity mBaseActivity;
    private LayoutInflater mInflater;
    private ExpertProfile mExpProfile;
    private DialogFragment fragment;

    private int dp5AsPixels;
    private int dp13AsPixels;

    public RecycleServiceAdapter(BaseActivity mBaseActivity, List<ExpertService> items, ExpertProfile expProfile) {
        this.mListService = items;
        this.mBaseActivity = mBaseActivity;
        this.mExpProfile = expProfile;
        mInflater = LayoutInflater.from(mBaseActivity);

        dp5AsPixels = ScreenUtil.convertDpToPx(5, mBaseActivity);
        dp13AsPixels = ScreenUtil.convertDpToPx(13, mBaseActivity);
    }

    public RecycleServiceAdapter(BaseActivity mBaseActivity, List<ExpertService> items, ExpertProfile expProfile, DialogFragment fragment) {
        this.mListService = items;
        this.mBaseActivity = mBaseActivity;
        this.mExpProfile = expProfile;
        this.fragment = fragment;
        mInflater = LayoutInflater.from(mBaseActivity);

        dp5AsPixels = ScreenUtil.convertDpToPx(5, mBaseActivity);
        dp13AsPixels = ScreenUtil.convertDpToPx(13, mBaseActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.
                inflate(R.layout.recycle_service_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListService.size() == 1) {
            ViewGroup.LayoutParams params = holder.mRootView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.mRootView.setLayoutParams(params);
            holder.mRootView.setPadding(dp13AsPixels, 0, dp13AsPixels, 0);
        } else {
            if (position == 0) {
                holder.mRootView.setPadding(dp13AsPixels, 0, dp5AsPixels, 0);
            } else if (position == mListService.size() - 1) {
                holder.mRootView.setPadding(dp5AsPixels, 0, dp13AsPixels, 0);
            } else
                holder.mRootView.setPadding(dp5AsPixels, 0, dp5AsPixels, 0);
        }

        ExpertService service = mListService.get(position);
        if (!StringUtils.isEmpty(service.randomPhotoUrl))
            Glide.with(mBaseActivity).load(service.randomPhotoUrl).into(holder.mImgView);

        if(!StringUtils.isEmpty(service.reviewCount))
            holder.mTxtMeet.setText(service.reviewCount+ " Meets");

        if(!StringUtils.isEmpty(service.price)) {
            if (service.price.equals("0")
                    || service.price.equals("0.0")
                    || service.price.equals("0.00"))
                service.price = "Free";
            else if (service.price.endsWith(".00"))
                service.price = service.price.substring(0, service.price.length()-3);
            holder.mTxtPrice.setText("$"+service.price);
        }

        if(!StringUtils.isEmpty(service.originalPrice)) {
            holder.mTxtOrigPrice.setVisibility(View.VISIBLE);
            if (service.originalPrice.endsWith(".00"))
                service.originalPrice = service.originalPrice.substring(0, service.originalPrice.length()-3);
            holder.mTxtOrigPrice.setText("$"+service.originalPrice);
            holder.mTxtOrigPrice.setPaintFlags(holder.mTxtOrigPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.mTxtTitle.setText(service.title);
        if (!StringUtils.isEmpty(service.descriptionText))
            holder.mTxtDescription.setText(service.descriptionText);
        else {
            holder.mTxtDescription.setText(service.description);
        }
        holder.mTxtRating.setText(String.format("%.1f", service.rating));
    }

    @Override
    public int getItemCount() {
        return mListService.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout mRootView;
        ImageView mImgView;
        TextView mTxtTitle;
        TextView mTxtDescription;
        TextView mTxtPrice;
        TextView mTxtOrigPrice;
        TextView mTxtMeet;
        TextView mTxtRating;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = (LinearLayout) itemView.findViewById(R.id.root_layout);
            mImgView = (ImageView) itemView.findViewById(R.id.image);
            mTxtTitle = (TextView) itemView.findViewById(R.id.title);
            mTxtDescription = (TextView) itemView.findViewById(R.id.description);
            mTxtPrice = (TextView) itemView.findViewById(R.id.price);
            mTxtOrigPrice = (TextView) itemView.findViewById(R.id.orig_price);
            mTxtMeet = (TextView) itemView.findViewById(R.id.meet);
            mTxtRating = (TextView) itemView.findViewById(R.id.rating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (fragment != null) fragment.dismiss();
            UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY,UserPOJO.class);
            if (userPOJO == null) {
                Intent intent = new Intent(mBaseActivity, MainActivity.class);
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            int position = getAdapterPosition();
            HashMap<String, String> map = new HashMap<>();
            map.put("serviceId", mListService.get(position).id.toString());
            MixpanelUtil.getInstance(mBaseActivity).trackEvent("Expert page -> Click one of topics", map);

            if (fragment == null) {
                ServiceDetailPopupFragment dialogFragment = ServiceDetailPopupFragment.newInstance(mListService.get(position), mExpProfile);
                dialogFragment.show(mBaseActivity.getSupportFragmentManager(), "detail");
                dialogFragment.setCancelable(true);
            } else {
                ExpertService servicePOJO = mListService.get(position);
                if (servicePOJO.ifExistRequest.equals("1")) {
                    // View existing appointment
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("serviceId", servicePOJO.id.toString());
                        MixpanelUtil.getInstance(mBaseActivity).trackEvent("Expert page -> topic -> Make an appointment", hashMap);
                        Intent intent = new Intent(mBaseActivity, AppointStatusActivity.class);
                        int id = 0;
                        try{
                            id=Integer.parseInt(servicePOJO.eventDataId);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        intent.putExtra("eventRequestId", id);
                        intent.putExtra("parentPage",0 );
                        mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (servicePOJO.ifExistRequest.equals("0")) {
                    // Make new request
                    Intent intent = new Intent(mBaseActivity, UserRequestSettingActivity.class);
                    intent.putExtra("serviceId", servicePOJO.id+"");
                    intent.putExtra("expId", mExpProfile.getExpId()+"");
                    intent.putExtra("price", servicePOJO.price);
                    intent.putExtra("discount", servicePOJO.originalPrice);
                    intent.putExtra("topic", servicePOJO.title);
                    intent.putExtra("author", mExpProfile.getFullName());
                    intent.putExtra("position", mExpProfile.getPosition());
                    if (!StringUtils.isEmpty(mExpProfile.getPhotoUrl()))
                        intent.putExtra("photo", mExpProfile.getPhotoUrl());
                    else if (!StringUtils.isEmpty(mExpProfile.getSmallphotoUrl()))
                        intent.putExtra("photo", mExpProfile.getSmallphotoUrl());
                    if (!StringUtils.isEmpty(servicePOJO.duratingTime))
                        intent.putExtra("duratingTime", NumberFormatUtil.getInstance().getFloat(servicePOJO.duratingTime));
                    if (!StringUtils.isEmpty(mExpProfile.getDefaultDate()))
                        intent.putExtra("defaultDate", new Integer(mExpProfile.getDefaultDate()).intValue());
                    intent.putExtra("defaultLocationList", mExpProfile.getExpertDefaultLocationList());
                    intent.putExtra("availabilityType", mExpProfile.getAvailabilityType());

                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        }
    }
}
