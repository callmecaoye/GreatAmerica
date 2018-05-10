package com.promeets.android.fragment;

import com.promeets.android.activity.ExpertDetailActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.promeets.android.activity.AboutUsActivity;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.BillManagementActivity;
import com.promeets.android.activity.ExpSignUpActivity;
import com.promeets.android.activity.ExpertDashboardActivity;
import com.promeets.android.activity.InviteActivity;
import com.promeets.android.activity.MainActivity;
import com.promeets.android.activity.MyAppointmentActivity;
import com.promeets.android.activity.NavigateActivity;
import com.promeets.android.activity.UserProfileActivity;
import com.promeets.android.activity.WishListActivity;
import com.bumptech.glide.Glide;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.object.UserProfilePOJO;
import com.promeets.android.pojo.ProfileResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.promeets.android.util.FirebaseUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import java.util.HashMap;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.promeets.android.Constant.RELOGIN_ERROR_CODE;
import static com.promeets.android.Constant.ServiceType.EXPERT_PROFILE_DETAIL;
import static com.promeets.android.Constant.ServiceType.USER_PROFILE_DETAIL;
import static com.promeets.android.Constant.UPDATE_TIME_STAMP;
import static com.promeets.android.util.PromeetsUtils.buildURL;
import static com.promeets.android.util.PromeetsUtils.getUserData;

/**
 * 5th page of HomeActivity
 *
 * @destination: UserProfileActivity, MyAppointmentActivity, NavigateActivity etc.
 *
 */

public class AccountFragment extends Fragment implements View.OnClickListener, IServiceResponseHandler {

    @BindView(R.id.profile_name)
    TextView mTxtProfileName;

    @BindView(R.id.profile_image)
    CircleImageView mImgProfileImage;

    @BindView(R.id.only_user_profile)
    LinearLayout mLinearUserProfile;

    @BindView(R.id.expert_profile)
    LinearLayout mLinearExpertProfile;

    @BindView(R.id.my_appointment_user_only)
    TextView mTxtMyAppointmentUserProfile;

    @BindView(R.id.become_an_expert_user_only)
    TextView mTxtBecomeAnExpertUserProfile;

    @BindView(R.id.become_an_expert_pending)
    TextView mTxtBecomeAnExpertPending;

    @BindView(R.id.balance_user_only)
    TextView mTxtBalanceUserProfile;

    @BindView(R.id.my_appointment_customer)
    TextView mTxtExpertCustomerAppointment;

    @BindView(R.id.my_appointment_expert)
    TextView mTxtExpertAppointments;

    @BindView(R.id.my_expert_dashboard)
    TextView mTxtMyExpertDashBoard;

    //@BindView(R.id.referral)
    //TextView mTxtReferral;
    @BindView(R.id.invite)
    TextView mTxtInvite;

    @BindView(R.id.about_us)
    TextView mTxtAbout;

    @BindView(R.id.my_wishlist_expert)
    TextView mTxtMyWishListExpert;

    @BindView(R.id.my_wishlist_user_only)
    TextView mTxtMyWishListUserOnly;

    @BindView(R.id.setting)
    TextView mTxtSetting;

    @BindView(R.id.photo_layout)
    RelativeLayout mLayPhoto;
    @BindView(R.id.profile_edit)
    TextView mTxtEdit;
    @BindView(R.id.help)
    TextView mTxtHelp;

    private UserPOJO userPOJO;

    private UserProfilePOJO userProfile;

    private BaseActivity mBaseActivity;

    private ExpertProfilePOJO expertProfile;

    private View mView;

    public AccountFragment() {
    }

    public static AccountFragment newInstance() {

        AccountFragment sampleFragment = new AccountFragment();

        return sampleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);
        mBaseActivity = (BaseActivity) getActivity();
        ButterKnife.bind(this, mView);

        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();
        userPOJO = (UserPOJO) getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        userProfile = (UserProfilePOJO) getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY, UserProfilePOJO.class);
        expertProfile = (ExpertProfilePOJO) getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY, ExpertProfilePOJO.class);

        if (userPOJO == null ) {
            mBaseActivity.startActivity(MainActivity.class);
            return;
        }

        //mImgProfileImage.setOnClickListener(this);
        //mTxtProfileName.setOnClickListener(this);
        mLayPhoto.setOnClickListener(this);
        //mTxtReferral.setOnClickListener(this);
        mTxtInvite.setOnClickListener(this);
        mTxtAbout.setOnClickListener(this);
        mTxtSetting.setOnClickListener(this);
        mTxtHelp.setOnClickListener(this);

        updateUi();
        requestForUserData();
    }

    void updateUi() {
        userPOJO = (UserPOJO) getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        userProfile = (UserProfilePOJO) getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY, UserProfilePOJO.class);
        expertProfile = (ExpertProfilePOJO) getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY, ExpertProfilePOJO.class);

        mLinearUserProfile.setVisibility(View.GONE);
        mLinearExpertProfile.setVisibility(View.GONE);

        if (userProfile != null) {
            Glide.with(mBaseActivity).load(userProfile.photoURL).into(mImgProfileImage);
            mTxtProfileName.setText(userProfile.fullName);

            switch (userProfile.expertStatus) {
                case "0":
                    mLinearUserProfile.setVisibility(View.VISIBLE);
                    mTxtBalanceUserProfile.setOnClickListener(this);
                    mTxtBecomeAnExpertUserProfile.setOnClickListener(this);
                    mTxtMyAppointmentUserProfile.setOnClickListener(this);
                    mTxtMyWishListUserOnly.setOnClickListener(this);
                    break;
                case "1":
                case "3":
                    mLinearExpertProfile.setVisibility(View.VISIBLE);
                    mTxtExpertAppointments.setOnClickListener(this);
                    mTxtExpertCustomerAppointment.setOnClickListener(this);
                    mTxtMyWishListExpert.setOnClickListener(this);
                    mTxtMyExpertDashBoard.setOnClickListener(this);
                    break;
                case "2":
                    if (userProfile.viewExpProfile == 0) {
                        mLinearUserProfile.setVisibility(View.VISIBLE);
                        mTxtBecomeAnExpertUserProfile.setVisibility(View.GONE);
                        mTxtBecomeAnExpertPending.setVisibility(View.VISIBLE);
                        mTxtBalanceUserProfile.setOnClickListener(this);
                        mTxtMyAppointmentUserProfile.setOnClickListener(this);
                        mTxtMyWishListUserOnly.setOnClickListener(this);
                        mTxtBecomeAnExpertPending.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PromeetsDialog.show(mBaseActivity, "Hiya! Our specialist is still reviewing your profile!");
                            }
                        });
                    } else if (userProfile.viewExpProfile == 1) {
                        mLinearExpertProfile.setVisibility(View.VISIBLE);
                        mTxtExpertAppointments.setOnClickListener(this);
                        mTxtExpertCustomerAppointment.setOnClickListener(this);
                        mTxtMyWishListExpert.setOnClickListener(this);
                        mTxtMyExpertDashBoard.setOnClickListener(this);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_layout:
                mBaseActivity.startActivity(UserProfileActivity.class);
                break;
            case R.id.balance_user_only:
                mBaseActivity.startActivity(BillManagementActivity.class);
                break;
            case R.id.invite:
                mBaseActivity.startActivity(InviteActivity.class);
                break;
            case R.id.my_expert_dashboard:
                mBaseActivity.startActivity(ExpertDashboardActivity.class);
                break;
            case R.id.become_an_expert_user_only:
                mBaseActivity.startActivity(ExpSignUpActivity.class);
                break;
            case R.id.my_appointment_user_only:
                Intent intent = new Intent(mBaseActivity, MyAppointmentActivity.class);
                intent.putExtra("ifExpert", false);
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.my_appointment_customer:
                intent = new Intent(mBaseActivity, MyAppointmentActivity.class);
                intent.putExtra("ifExpert", false);
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.my_appointment_expert:
                intent = new Intent(mBaseActivity, MyAppointmentActivity.class);
                intent.putExtra("ifExpert", true);
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.about_us:
                mBaseActivity.startActivity(AboutUsActivity.class);
                //mBaseActivity.startActivity(ExpSignUpActivity.class);
                break;
            case R.id.expert_page:
                intent = new Intent(mBaseActivity, ExpertDetailActivity.class);
                intent.putExtra("expId", String.valueOf(expertProfile.expId));
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                String[] key = {Constant.EXPERTID, "prev_page"};
                String[] value = {intent.getStringExtra("expId"), "Account"};
                FirebaseUtil.getInstance(mBaseActivity).buttonClick(FirebaseUtil.EXPERT_SCREEN_LOAD, key, value);
                break;
            case R.id.my_wishlist_expert:
            case R.id.my_wishlist_user_only:
                mBaseActivity.startActivity(WishListActivity.class);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.setting:
                intent = new Intent(mBaseActivity, NavigateActivity.class);
                intent.putExtra("from", "setting");
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.help:
                intent = new Intent(mBaseActivity, NavigateActivity.class);
                intent.putExtra("from", "help");
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }


    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        if (serviceType == EXPERT_PROFILE_DETAIL) {
            ProfileResp loginRep = (ProfileResp) serviceResponse.getServiceResponse(ProfileResp.class);
            if (mBaseActivity.isSuccess(loginRep.getInfo().getCode())) {
                ServiceResponseHolder.getInstance().setExpertProfile(loginRep.getExpertProfile());
            }
            updateUi();
        } else if (serviceType == USER_PROFILE_DETAIL) {
            ProfileResp loginRep = (ProfileResp) serviceResponse.getServiceResponse(ProfileResp.class);
            if (mBaseActivity.isSuccess(loginRep.getInfo().getCode())) {
                ServiceResponseHolder.getInstance().setUserProfile(loginRep.getUserProfile());
            } else if (loginRep.getInfo().getCode().equals(RELOGIN_ERROR_CODE) || loginRep.getInfo().getCode().equals(UPDATE_TIME_STAMP) || loginRep.getInfo().getCode().equals(Constant.UPDATE_THE_APPLICATION)) {
                Utility.onServerHeaderIssue(mBaseActivity, loginRep.getInfo().getCode());
            }
            updateUi();
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.show(mBaseActivity, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    private void requestForUserData() {
        if (userPOJO == null ) return;

        HashMap<String, String> header = new HashMap<>();
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());

            //Check for internet Connection
            if (!mBaseActivity.hasInternetConnection()) {
                PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
                return;
            }

        String[] key = {Constant.EXPERTID, Constant.TIMEZON};
        String[] value = {userPOJO.id + "", TimeZone.getDefault().getID()};
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_MY_EXPERT_PROFILE));
        new GenericServiceHandler(Constant.ServiceType.EXPERT_PROFILE_DETAIL, this, buildURL(Constant.FETCH_MY_EXPERT_PROFILE, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();


        key = new String[]{Constant.MYID};
        value = new String[]{userPOJO.id + ""};
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_MY_USER_PROFILE));
        new GenericServiceHandler(Constant.ServiceType.USER_PROFILE_DETAIL, this, buildURL(Constant.FETCH_MY_USER_PROFILE, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }
}