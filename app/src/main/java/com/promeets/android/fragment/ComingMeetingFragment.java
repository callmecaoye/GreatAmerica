package com.promeets.android.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.R;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.VideoChatActivity;
import com.promeets.android.object.VideoData;
import com.promeets.android.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ComingMeetingFragment extends BaseDialogFragment {

    @BindView(R.id.join)
    TextView mTxtJoin;
    @BindView(R.id.photo)
    CircleImageView mImgPhoto;
    @BindView(R.id.name)
    TextView mTxtName;
    @BindView(R.id.tips)
    TextView mTxtTips;

    private VideoData videoData;

    private static ComingMeetingFragment instance;

    public static ComingMeetingFragment newInstance(VideoData videoData) {
        if (instance != null)
            instance.dismiss();
        instance = new ComingMeetingFragment();
        instance.videoData = videoData;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, null);
        View view = inflater.inflate(R.layout.fragment_coming_meeting, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(videoData.fullName)) {
            mTxtName.setText(videoData.fullName);
            mTxtTips.setText("You have an online meeting with \n" + videoData.fullName);
        }
        if (!TextUtils.isEmpty(videoData.smallPhotoUrl))
            Glide.with(this).load(videoData.smallPhotoUrl).into(mImgPhoto);
    }

    @OnClick({R.id.join})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.join:
                dismiss();
                Intent intent = new Intent(mBaseActivity, VideoChatActivity.class);
                intent.putExtra("name", videoData.fullName);
                intent.putExtra("photoUrl", videoData.smallPhotoUrl);
                intent.putExtra("appId", videoData.appId);
                intent.putExtra("uid", videoData.uid);
                intent.putExtra("channelName", videoData.channelName);
                startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
