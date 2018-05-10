package com.promeets.android.fragment;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.promeets.android.object.Advertisement;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.MainActivity;
import com.promeets.android.adapter.CampPagerAdapter;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sosasang on 11/17/17.
 */
public class CampaignFragment extends DialogFragment {

    @BindView(R.id.skip)
    Button mBtnSkip;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private BaseActivity mBaseActivity;
    private Advertisement[] mDataList;

    public static CampaignFragment newInstance(Advertisement[] dataList) {
        CampaignFragment f = new CampaignFragment();
        f.mDataList = dataList;
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(mBaseActivity);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        final Dialog dialog = new Dialog(mBaseActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campaign, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        CampPagerAdapter campPagerAdapter = new CampPagerAdapter(mBaseActivity, mDataList);

        mViewPager.setAdapter(campPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mDataList.length - 1) {
                    mBtnSkip.setText("Done");
                } else {
                    mBtnSkip.setText("Skip");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);


        UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if(userPOJO==null || userPOJO.id==null)
            mBaseActivity.startActivity(MainActivity.class);
        else {
            mBaseActivity.finish();
            startActivity(mBaseActivity.getIntent());
        }
    }
}
