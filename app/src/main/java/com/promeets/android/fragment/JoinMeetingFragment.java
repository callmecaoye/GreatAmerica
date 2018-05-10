package com.promeets.android.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promeets.android.R;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.VideoChatActivity;
import com.promeets.android.object.VideoData;
import com.promeets.android.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinMeetingFragment extends BaseDialogFragment {

    @BindView(R.id.dismiss)
    TextView mBtnDismiss;
    @BindView(R.id.join)
    TextView mBtnJoin;

    private VideoData data;

    private static JoinMeetingFragment instance;

    public static JoinMeetingFragment newInstance(VideoData videoData) {
        if (instance != null)
            instance.dismiss();
        instance = new JoinMeetingFragment();
        instance.data = videoData;

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, null);
        View view = inflater.inflate(R.layout.fragment_join_meeting, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.dismiss, R.id.join})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.join:
                Intent intent = new Intent(mBaseActivity, VideoChatActivity.class);
                intent.putExtra("name", data.fullName);
                intent.putExtra("photoUrl", data.smallPhotoUrl);
                intent.putExtra("appId", data.appId);
                intent.putExtra("uid", data.uid);
                intent.putExtra("channelName", data.channelName);
                startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            case R.id.dismiss:
                dismiss();
                break;
        }
    }
}
