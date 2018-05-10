package com.promeets.android.activity;

import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.object.ExpertProfilePOJO;
import android.os.Bundle;
import com.promeets.android.pojo.ProfileResp;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.util.FirebaseUtil;
import com.promeets.android.util.Utility;

import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.UserProfilePOJO;
import com.promeets.android.pojo.DashboardResp;
import com.promeets.android.R;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.ServiceResponseHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.promeets.android.Constant.ServiceType.EXPERT_DASHBOARD;
import static com.promeets.android.Constant.ServiceType.USER_PROFILE_DETAIL;

/**
 * This is a panel to link pages related to expert itself
 *
 * @source: AccountFragment
 *
 * @destination: BillManagementActivity, ExpertProfileActivity,
 * ExpertServiceListActivity, ExpertAvailActivity, ExpertDetailActivity
 *
 */
public class ExpertDashboardActivity extends BaseActivity implements IServiceResponseHandler, View.OnClickListener {
    @BindView(R.id.photo)
    CircleImageView mImgPhoto;
    @BindView(R.id.balance)
    TextView mTxtBalance;
    @BindView(R.id.balance_layout)
    LinearLayout mLayBalance;
    @BindView(R.id.likes)
    TextView mTxtLikes;
    @BindView(R.id.expert_profile)
    TextView mTxtExpProfile;
    @BindView(R.id.topic)
    TextView mTxtTopic;
    @BindView(R.id.meeting_preference)
    TextView mTxtMeetPref;
    @BindView(R.id.expert_page)
    TextView mTxtExpPage;

    private ServiceType serviceType;
    //private DialogFragment mFragmentProgress;
    private ExpertProfilePOJO expertProfile;

    public static ExpertDashboardActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_dashboard);
        ButterKnife.bind(this);
        expertProfile = (ExpertProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY, ExpertProfilePOJO.class);
        if (expertProfile == null) finish();
        instance = this;
    }

    @Override
    public void initElement() {
        final HashMap<String, String> mHeader = new HashMap<>();
        mHeader.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        mHeader.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        mHeader.put("API_VERSION", Utility.getVersionCode());
        mHeader.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);

        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(this);
        mHeader.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.EXPERT_DASHBOARD_DISPLAY));
        String[] key = {Constant.VIEW_ID};
        String[] value = {expertProfile.expId + ""};
        new GenericServiceHandler(ServiceType.EXPERT_DASHBOARD, this, PromeetsUtils.buildURL(Constant.EXPERT_DASHBOARD_DISPLAY, key, value), null, mHeader, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();

        HashMap<String, String> nHeader = new HashMap<>(mHeader);
        nHeader.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_MY_USER_PROFILE));
        key = new String[]{Constant.MYID};
        value = new String[]{expertProfile.expId + ""};
        new GenericServiceHandler(ServiceType.USER_PROFILE_DETAIL, this, PromeetsUtils.buildURL(Constant.FETCH_MY_USER_PROFILE, key, value), null, nHeader, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }

    @Override
    public void registerListeners() {
        mLayBalance.setOnClickListener(this);
        mTxtExpProfile.setOnClickListener(this);
        mTxtTopic.setOnClickListener(this);
        mTxtMeetPref.setOnClickListener(this);
        mTxtExpPage.setOnClickListener(this);
        mTxtMeetPref.setOnClickListener(this);
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        if (serviceType == EXPERT_DASHBOARD) {
            DashboardResp dashboardResp = (DashboardResp) serviceResponse.getServiceResponse(DashboardResp.class);
            if (isSuccess(dashboardResp.getInfo().getCode())) {
                mTxtBalance.setText(NumberFormatUtil.getInstance().getCurrencyTest(dashboardResp.getDisplayAmount()));
                mTxtLikes.setText(dashboardResp.getLikeCount());
                if (!StringUtils.isEmpty(dashboardResp.getPhotoUrl()))
                    Glide.with(this).load(dashboardResp.getPhotoUrl()).into(mImgPhoto);
                else if (!StringUtils.isEmpty(dashboardResp.getSmallphotoUrl()))
                    Glide.with(this).load(dashboardResp.getSmallphotoUrl()).into(mImgPhoto);
            } else if (dashboardResp.getInfo().getCode().equals(Constant.RELOGIN_ERROR_CODE) || dashboardResp.getInfo().getCode().equals(Constant.UPDATE_TIME_STAMP) || dashboardResp.getInfo().getCode().equals(Constant.UPDATE_THE_APPLICATION)) {
                Utility.onServerHeaderIssue(this, dashboardResp.getInfo().getCode());
            } else {
                onErrorResponse(dashboardResp.getInfo().getDescription());
            }
        } else if (serviceType == USER_PROFILE_DETAIL) {
            ProfileResp loginRep = (ProfileResp) serviceResponse.getServiceResponse(ProfileResp.class);
            if (isSuccess(loginRep.getInfo().getCode())) {
                ServiceResponseHolder.getInstance().setUserProfile(loginRep.getUserProfile());
                UserProfilePOJO userProfile = loginRep.getUserProfile();
                if (userProfile.expertStatus.equals("2")
                        && userProfile.viewExpProfile == 1)
                    mTxtExpProfile.setText("My Bio (Pending)");
                else if (userProfile.expertStatus.equals("3")) {
                    mTxtExpProfile.setText("My Bio (Update it!)");
                    mTxtExpProfile.setTextColor(getResources().getColor(R.color.material_red));
                }
            }  else {
                onErrorResponse(loginRep.getInfo().getDescription());
            }
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.balance_layout:
                intent = new Intent(this, BillManagementActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.expert_profile:
                intent = new Intent(this, ExpertProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.topic:
                intent = new Intent(this, ExpertServiceListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.meeting_preference:
                intent = new Intent(this, ExpertAvailActivity.class);
                intent.putExtra("expId", expertProfile.expId);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.expert_page:
                intent = new Intent(this, ExpertDetailActivity.class);
                intent.putExtra("expId", String.valueOf(expertProfile.expId));
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                String[] key = {Constant.EXPERTID, "prev_page"};
                String[] value = {intent.getStringExtra("expId"), "Account"};
                FirebaseUtil.getInstance(this).buttonClick(FirebaseUtil.EXPERT_SCREEN_LOAD, key, value);
                break;
        }
    }
}
