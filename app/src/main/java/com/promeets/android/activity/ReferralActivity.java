package com.promeets.android.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.object.UserProfilePOJO;
import com.promeets.android.R;
import com.promeets.android.util.PromeetsPreferenceUtil;

import com.promeets.android.util.PromeetsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Currently not in use
 */
public class ReferralActivity extends BaseActivity implements View.OnClickListener {

    UserProfilePOJO userProfile;

    @BindView(R.id.txt_code)
    TextView mTxtCode;
    @BindView(R.id.txt_link)
    TextView mTxtLink;
    @BindView(R.id.txt_copy)
    TextView mTxtCopy;
    @BindView(R.id.btn_send)
    TextView mBtnSend;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtLink.setOnClickListener(this);
        mTxtCopy.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        ButterKnife.bind(this);

        userProfile = (UserProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY, UserProfilePOJO.class);
        if (userProfile != null) {
            if (userProfile.inviteCode != null)
                mTxtCode.setText(userProfile.inviteCode);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_link:
                Intent intent = new Intent(this, ReferralDetailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.txt_copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", mTxtCode.getText());
                clipboard.setPrimaryClip(clip);

                PromeetsDialog.show(ReferralActivity.this, "Invite code is copied to clipboard.");
                break;
            case R.id.btn_send:
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                share_intent.putExtra(Intent.EXTRA_SUBJECT, "ProMeets Invite Code");
                share_intent.putExtra(Intent.EXTRA_TEXT, "The referral code is: " + mTxtCode.getText() + "\nhttps://promeets.us");
                share_intent = Intent.createChooser(share_intent, null);
                startActivity(share_intent);
                break;
        }
    }
}
