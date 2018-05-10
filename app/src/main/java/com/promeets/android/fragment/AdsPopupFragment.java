package com.promeets.android.fragment;


import com.promeets.android.activity.ExpertDetailActivity;
import com.promeets.android.activity.ReferralActivity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.promeets.android.object.Advertisement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.EventDetailActivity;

import com.bumptech.glide.Glide;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is for showing image popup for promo ad clicking
 *
 * @source: HomePageFragment
 *
 */
public class AdsPopupFragment extends DialogFragment implements View.OnClickListener{

    private final String INVIVE_CODE = "promeets://invitecode";
    private final String EXPERT_PAGE = "promeets://expert/expertId/";
    private final String EVENT_DETAIL = "promeets://event/eventId/";

    @BindView(R.id.ads_close)
    ImageView mImgClose;
    @BindView(R.id.ads_popup)
    ImageView mImgAds;

    BaseActivity mBaseActivity;
    Advertisement mAds;
    public static AdsPopupFragment newInstance(Advertisement ads) {
        AdsPopupFragment f = new AdsPopupFragment();
        f.mAds = ads;
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final RelativeLayout root = new RelativeLayout(mBaseActivity);

        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(mBaseActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Setup the UI and return the view
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ads_popup, container, false);
        ButterKnife.bind(this, view);

        Glide.with(mBaseActivity).load(mAds.getPopupUrl()).into(mImgAds);
        mImgClose.setOnClickListener(this);
        mImgAds.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ads_popup:
                if (!StringUtils.isEmpty(mAds.getLinkUrl()) && mAds.getLinkUrl().startsWith(EXPERT_PAGE)) {
                    Intent intent = new Intent(mBaseActivity, ExpertDetailActivity.class);
                    String linkString = mAds.getLinkUrl().split("://")[1];
                    String expId = linkString.split("/")[2];
                    intent.putExtra("expId", String.valueOf(expId));
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (!StringUtils.isEmpty(mAds.getLinkUrl()) && mAds.getLinkUrl().equalsIgnoreCase(INVIVE_CODE)) {
                    Intent intent = new Intent(mBaseActivity, ReferralActivity.class);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (!StringUtils.isEmpty(mAds.getLinkUrl()) && mAds.getLinkUrl().startsWith(EVENT_DETAIL)) {
                    Intent intent = new Intent(mBaseActivity, EventDetailActivity.class);
                    String linkString = mAds.getLinkUrl().split("://")[1];
                    String eventId = linkString.split("/")[2];
                    intent.putExtra("eventId", eventId);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            case R.id.ads_close:
                mBaseActivity.getFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
