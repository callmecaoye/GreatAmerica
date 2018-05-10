package com.promeets.android.activity;

import com.promeets.android.api.URL;
import com.promeets.android.api.UserActionApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import android.net.Uri;
import com.promeets.android.object.UserProfilePOJO;
import android.os.Bundle;
import com.promeets.android.pojo.InviteResp;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for sharing invite code
 *
 * Use Intent filter to start other application
 *
 * @source: AccountFragment
 *
 */

public class InviteActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.info_lay)
    LinearLayout mLayInfo;
    @BindView(R.id.share)
    TextView mTxtShare;

    @BindView(R.id.share_lay)
    LinearLayout mLayShare;
    @BindView(R.id.email)
    TextView mTxtEmail;
    @BindView(R.id.sms)
    TextView mTxtSMS;
    @BindView(R.id.copy)
    TextView mTxtCopy;
    @BindView(R.id.facebook)
    TextView mTxtFacebook;
    @BindView(R.id.linkedin)
    TextView mTxtLinkedIn;
    @BindView(R.id.more)
    TextView mTxtMore;
    @BindView(R.id.fb_divider)
    View mFBDivider;
    @BindView(R.id.li_divider)
    View mLIDivder;

    private String title;
    private String html;
    private String link;
    private String txt;

    private Intent linkedinIntent;
    private Intent facebookIntent;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtShare.setOnClickListener(this);
        mTxtEmail.setOnClickListener(this);
        mTxtSMS.setOnClickListener(this);
        mTxtCopy.setOnClickListener(this);
        mTxtFacebook.setOnClickListener(this);
        mTxtLinkedIn.setOnClickListener(this);
        mTxtMore.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);

        UserProfilePOJO userProfile = (UserProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY, UserProfilePOJO.class);
        if (userProfile == null) finish();
        fetchLink();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share:
                Animation out = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
                mLayInfo.setVisibility(View.GONE);
                mTxtShare.setVisibility(View.GONE);
                mLayInfo.startAnimation(out);
                mTxtShare.startAnimation(out);
                Animation in = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
                mLayShare.setVisibility(View.VISIBLE);
                mLayShare.startAnimation(in);
                break;
            case R.id.email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                //send.putExtra(Intent.EXTRA_EMAIL, addresses);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                if (!StringUtils.isEmpty(html))
                    //emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(html));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, txt + "\n" + link);
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else
                    PromeetsDialog.show(this, "No Email app to share");
                break;
            case R.id.sms:
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.putExtra("sms_body", txt + "\n" + link);
                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                } else
                    PromeetsDialog.show(this, "No SMS app to share");
                break;
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", "copy");
                clipboard.setPrimaryClip(clip);
                PromeetsDialog.show(this, "Copied to clipboard.");
                break;
            case R.id.facebook:
                try {
                    startActivity(facebookIntent);
                } catch (Exception ex) {
                    PromeetsDialog.show(this, "Fail to share using Facebook app.");
                }
                break;
            case R.id.linkedin:
                try {
                    startActivity(linkedinIntent);
                } catch (Exception ex) {
                    PromeetsDialog.show(this, "Fail to share using LinkedIn app.");
                }
                break;
            case R.id.more:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, title);
                intent.putExtra(Intent.EXTRA_TITLE, title);
                intent.putExtra(Intent.EXTRA_TEXT, txt + "\n" + link);
                intent.setType("text/plain");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, "Share via..."));
                } else
                    PromeetsDialog.show(this, "No app to share");
                break;
        }
    }

    private void fetchLink() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("userprofile/shareInviteCodeLink"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserActionApi service = retrofit.create(UserActionApi.class);
        Call<InviteResp> call = service.getInviteCodeLink();

        call.enqueue(new Callback<InviteResp>() {
            @Override
            public void onResponse(Call<InviteResp> call, Response<InviteResp> response) {
                PromeetsDialog.hideProgress();
                InviteResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(InviteActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    title = result.shareTitle;
                    html = result.shareHtml;
                    link = result.shareLink;
                    txt = result.shareTxt;
                    checkApp();
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP)|| result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(InviteActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(InviteActivity.this, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<InviteResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(InviteActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void checkApp() {
        facebookIntent = new Intent(Intent.ACTION_SEND);
        facebookIntent.setType("text/plain");
        facebookIntent.putExtra(Intent.EXTRA_TEXT, txt + "\n" + link);

        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(facebookIntent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(
                    "com.facebook.katana")) {
                facebookIntent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        if (!facebookAppFound) {
            mTxtFacebook.setVisibility(View.GONE);
            mFBDivider.setVisibility(View.GONE);
        }

        // Check LinkedIn
        linkedinIntent = new Intent(Intent.ACTION_SEND);
        linkedinIntent.setType("text/plain");
        linkedinIntent.putExtra(Intent.EXTRA_TEXT, txt + "\n" + link);

        boolean linkedinAppFound = false;
        matches = getPackageManager().queryIntentActivities(linkedinIntent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(
                    "com.linkedin")) {
                linkedinIntent.setPackage(info.activityInfo.packageName);
                linkedinAppFound = true;
                break;
            }
        }

        if (!linkedinAppFound) {
            mTxtLinkedIn.setVisibility(View.GONE);
            mLIDivder.setVisibility(View.GONE);
        }
    }
}
