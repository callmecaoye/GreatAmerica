package com.promeets.android.activity;

import android.content.Intent;
import android.os.Bundle;

import com.promeets.android.MyApplication;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.LogoutPost;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.sendbird.android.SendBird;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.UserInfoHelper;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.promeets.android.Constant.ServiceType.NORMAL_USER_LOGIN;

/**
 * This a navigate activity for user help and user settings
 *
 * @source: AccountFragment
 *
 */
public class NavigateActivity extends BaseActivity
        implements View.OnClickListener, IServiceResponseHandler {

    static final String HANDBOOK_URL = "https://www.promeets.us/handbook/exphandbook.htm";
    static final int REQUEST_RESET_PASSWORD = 100;

    // Setting
    @BindView(R.id.setting)
    LinearLayout mLaySetting;
    @BindView(R.id.reset_password)
    TextView mTxtResetPassword;
    @BindView(R.id.reset_password_divider)
    View mViewDivider;
    @BindView(R.id.my_profile)
    TextView mTxtProfile;
    @BindView(R.id.log_out)
    TextView mTxtLogOut;
    @BindView(R.id.clear_cache)
    TextView mTxtClearCache;

    // Help
    @BindView(R.id.help)
    LinearLayout mLayHelp;
    @BindView(R.id.expert_handbook)
    TextView mTxtHandbooks;

    // Handbooks
    @BindView(R.id.handbooks)
    LinearLayout mLayHandbooks;
    @BindView(R.id.exp_guideline)
    TextView mTxtGuideline;

    @Override
    public void initElement() {
        String source = getIntent().getStringExtra("from");
        if (!StringUtils.isEmpty(source)) {
            switch (source) {
                case "setting":
                    mLaySetting.setVisibility(View.VISIBLE);
                    if (GlobalVariable.isLILogin
                            || GlobalVariable.isFBLogin) {
                        mTxtResetPassword.setVisibility(View.GONE);
                        mViewDivider.setVisibility(View.GONE);
                    }
                    break;
                case "help":
                    mLayHelp.setVisibility(View.VISIBLE);
                    mLayHandbooks.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public void registerListeners() {
        // Setting
        mTxtResetPassword.setOnClickListener(this);
        mTxtProfile.setOnClickListener(this);
        mTxtClearCache.setOnClickListener(this);
        mTxtLogOut.setOnClickListener(this);

        // Help
        mTxtHandbooks.setOnClickListener(this);

        // Handbooks
        mTxtGuideline.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Setting
            case R.id.reset_password:
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                startActivityForResult(intent, REQUEST_RESET_PASSWORD);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.my_profile:
                startActivity(UserProfileActivity.class);
                break;
            case R.id.clear_cache:
                clearAppData();
                break;
            case R.id.log_out:
                PromeetsDialog.show(this, "Are you sure to logout?", "Cancel", "Yes", new PromeetsDialog.OnSubmitListener() {
                    @Override
                    public void onSubmitListener() {
                        callLogout();
                    }
                });
                break;

            // Help
            case R.id.expert_handbook:
                mLayHelp.setVisibility(View.GONE);
                mLayHandbooks.setVisibility(View.VISIBLE);
                break;

            // Handbooks
            case R.id.exp_guideline:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", HANDBOOK_URL);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayHandbooks.getVisibility() == View.VISIBLE) {
            mLayHandbooks.setVisibility(View.GONE);
            mLayHelp.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }

    private void clearAppData() {
        try {
            File internalDir = getCacheDir();
            File externalDir = getExternalCacheDir();
            if (deleteDir(internalDir) && deleteDir(externalDir)) {
                PromeetsDialog.show(this, "Success!");
            } else {
                PromeetsDialog.show(this, "Error! You can still clear cache at phone Settings");
            }
        } catch (Exception e) {
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void callLogout() {

        HashMap<String, String> header = new HashMap<>();
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.LOGOUT_OPERATION));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);
        header.put(Constant.DEVICE_ID, Utility.getDeviceId());

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
            PromeetsDialog.showProgress(this);
            /* Create the pojo for POST Request*/
            UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
            if (userPOJO == null) finish();
            LogoutPost logoutRequest = new LogoutPost();
            logoutRequest.setAccessToken(userPOJO.accessToken);
            logoutRequest.setUserId(userPOJO.id + "");
            //Call the service
            new GenericServiceHandler(NORMAL_USER_LOGIN, this, Constant.BASE_URL + Constant.LOGOUT_OPERATION, "", logoutRequest, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        LoginResp loginResp = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if (isSuccess(loginResp.info.code)) {
            UserInfoHelper userInfoHelper = new UserInfoHelper(this);
            userInfoHelper.cleanData();
            promeetsPreferenceUtil.clear();

            SendBird.disconnect(null);
            MyApplication.disconnectStomp();

            HomeActivity.currentTabIndex = 1;
            finish();
        } else {
            onErrorResponse(loginResp.info.description);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESET_PASSWORD && resultCode == RESULT_OK) {
            finish();
        }
    }
}
