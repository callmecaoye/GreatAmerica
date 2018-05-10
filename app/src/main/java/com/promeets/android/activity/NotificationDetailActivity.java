package com.promeets.android.activity;

import com.promeets.android.object.NotificationPOJO;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import com.promeets.android.R;


import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is for showing default notification details
 *
 * If no msgUrl matches specific schemes, pass NotificationPOJO here
 *
 * @source: NotificationListFragment
 */
public class NotificationDetailActivity extends BaseActivity {

    @BindView(R.id.notification_detail_icon)
    CircleImageView mImgIcon;
    @BindView(R.id.notification_detail_title)
    TextView mTxtTitle;
    @BindView(R.id.notification_detail_date)
    TextView mTxtDate;
    @BindView(R.id.notification_detail_content)
    TextView mTxtContent;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        ButterKnife.bind(this);

        Gson gson = new Gson();
        String json = getIntent().getStringExtra("notificationPOJO");
        NotificationPOJO notificationPOJO = gson.fromJson(json, NotificationPOJO.class);

        if (!StringUtils.isEmpty(notificationPOJO.iconUrl))
            Glide.with(this).load(notificationPOJO.iconUrl).into(mImgIcon);
        if (!StringUtils.isEmpty(notificationPOJO.msgTitle))
            mTxtTitle.setText(notificationPOJO.msgTitle);
        if (!StringUtils.isEmpty(notificationPOJO.lastSendTime))
            mTxtDate.setText(notificationPOJO.lastSendTime);
        if (!StringUtils.isEmpty(notificationPOJO.msgContent))
            mTxtContent.setText(notificationPOJO.msgContent);
    }

    @Override
    public void onBackPressed() {
        HomeActivity.currentTabIndex = 3;
        startActivity(HomeActivity.class);
    }
}
