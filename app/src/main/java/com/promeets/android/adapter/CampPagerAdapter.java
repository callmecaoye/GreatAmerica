package com.promeets.android.adapter;

import android.content.Context;
import com.promeets.android.object.Advertisement;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.promeets.android.activity.BaseActivity;
import com.bumptech.glide.Glide;
import com.promeets.android.R;
import com.promeets.android.fragment.AdsPopupFragment;

/**
 * Created by sosasang on 11/17/17.
 */

public class CampPagerAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    private Advertisement[] ads;
    private BaseActivity mBaseActivity;
    public CampPagerAdapter(BaseActivity baseActivity, Advertisement[] ads){
        mLayoutInflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ads = ads;
        this.mBaseActivity = baseActivity;
    }
    @Override
    public int getCount() {
        return ads.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.recommended_pager_layout, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        //imageView.setBackgroundResource(R.drawable.bg_recommend);
        container.addView(itemView);

        Glide.with(mBaseActivity).load(ads[position].getPhotoUrl()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsPopupFragment fragment = AdsPopupFragment.newInstance(ads[position]);
                fragment.show(mBaseActivity.getFragmentManager(), "Popup Ads");
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
