package com.promeets.android.activity;

import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.UserPOJO;
import android.os.Bundle;
import android.os.Handler;
import com.promeets.android.pojo.ProfileResp;
import com.promeets.android.pojo.SyncTimeResp;
import com.promeets.android.services.GenericServiceHandler;
import android.support.v4.app.DialogFragment;
import com.promeets.android.util.ChatHelper;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.UserInfoHelper;

import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.pojo.VersionResp;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import com.promeets.android.R;

import java.util.HashMap;
import java.util.TimeZone;

/**
 * This is start activity of the whole app
 *
 * Declared intent-filter in AndroidManifest.xml
 *
 * Showing a ProgressBar to do initialization:
 * check updates, sync server time, access current user profile (if have)
 *
 * @Destination: HomeActivity
 *
 */

public class PromeetsSplashActivity extends BaseActivity implements IServiceResponseHandler {
    /**
     * flag to ensure singleton HomeActivity is instanced
     */
    private boolean isHomeStarted = false;

    /**
     * start HomeActivity
     */
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isHomeStarted) {
                Intent intent = new Intent(PromeetsSplashActivity.this,HomeActivity.class);
                intent.putExtra("from_splash",true);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
            isHomeStarted = true;
        }
    };

    private DialogFragment mFragmentProgress;

    @Override
    public void initElement() {
        ServiceResponseHolder.setInstance(null);
    }

    @Override
    public void registerListeners() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTransparentStatus();
        setContentView(R.layout.splash);

        CheckforUpdate();
    }

    /**
     * Check version updates of apps
     */
    private void CheckforUpdate() {
        try {
            HashMap<String, String> header = new HashMap<>();

            Bundle args = new Bundle();

            //Check for internet Connection
            if (!hasInternetConnection()) {
                PromeetsDialog.show(this, getString(R.string.no_internet));
            } else {
                args.putString("info", getString(R.string.please_wait)); //set message to diplay in no intenet connection
                args.putBoolean("isSuccessful", false); // set icon type

                String[] key = {Constant.VIEW_ID};
                String[] value = {Utility.getVersionCode()};

                new GenericServiceHandler(Constant.ServiceType.CHECK_FOR_UPDATE, this, PromeetsUtils.buildURL(Constant.CHECK_FOR_UPDATE, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Synchronize time between local device and server
     */
    private void syncTime() {
        try {
            HashMap<String, String> header = new HashMap<>();
            header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
            header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_SERVER_TIME));
            header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
            header.put("API_VERSION",Utility.getVersionCode());
            header.put(Constant.CONTENT_TYPE, Constant.FETCH_SERVER_TIME);

            //Check for internet Connection
            if (!hasInternetConnection()) {
                PromeetsDialog.show(this, getString(R.string.no_internet));
            } else {
                //args.putString("info", getString(R.string.please_wait)); //set message to diplay in no intenet connection
                //args.putBoolean("isSuccessful", false); // set icon type

                new GenericServiceHandler(Constant.ServiceType.SYNC_TIME, this, Constant.BASE_URL + Constant.FETCH_SERVER_TIME, null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.postDelayed(mRunnable, 2000);
        }
    }

    /**
     * If there is a logged in user, get its profile
     */
    private void requestForUserData() {
        UserPOJO userPOJO = new UserInfoHelper(this).getUserObject();
        try {
            //Check for internet Connection
            if (!hasInternetConnection()) {
                PromeetsDialog.show(this, getString(R.string.no_internet));
            } else  {
                //args.putString("info", getString(R.string.please_wait)); //set message to diplay in no intenet connection
                //args.putBoolean("isSuccessful", false); // set icon type
                if (userPOJO != null && userPOJO.id != null) {
                    HashMap<String, String> header = new HashMap<>();
                    String[] key = {Constant.EXPERTID, Constant.TIMEZON};
                    String[] value = {userPOJO.id + "", TimeZone.getDefault().getID()};

                    header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
                    header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_MY_EXPERT_PROFILE));
                    header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
                    header.put("API_VERSION",Utility.getVersionCode());
                    header.put(Constant.CONTENT_TYPE, Constant.FETCH_SERVER_TIME);
                    new GenericServiceHandler(Constant.ServiceType.EXPERT_PROFILE_DETAIL, this, PromeetsUtils.buildURL(Constant.FETCH_MY_EXPERT_PROFILE, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();

                    key = new String[]{Constant.MYID};
                    value = new String[]{userPOJO.id + ""};

                    header = new HashMap<>();
                    header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
                    header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_MY_USER_PROFILE));
                    header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
                    header.put("API_VERSION",Utility.getVersionCode());
                    new GenericServiceHandler(Constant.ServiceType.USER_PROFILE_DETAIL, this, PromeetsUtils.buildURL(Constant.FETCH_MY_USER_PROFILE, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();

                    ChatHelper chatUserInfoHelper = new ChatHelper(PromeetsSplashActivity.this);
                    chatUserInfoHelper.getChatAccountInfoFromServer(userPOJO.id);
                } else {
                    isExpertCallBack = true;
                    isUserProfileCallBack = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.postDelayed(mRunnable, 2000);
        }
    }

    private boolean isTimeCallBack;
    private boolean isExpertCallBack;
    private boolean isUserProfileCallBack;

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        if(serviceType == Constant.ServiceType.CHECK_FOR_UPDATE){
            VersionResp versionResp = (VersionResp) serviceResponse.getServiceResponse(VersionResp.class);
            if(versionResp.getData()==null)
                syncTime();
            else
                Utility.onServerHeaderIssue(this,"2002");
        }
        if (serviceType == Constant.ServiceType.SYNC_TIME) {
            SyncTimeResp syncTimeResp = (SyncTimeResp) serviceResponse.getServiceResponse(SyncTimeResp.class);
            if (isSuccess(syncTimeResp.getInfo().getCode())) {
                ServiceHeaderGeneratorUtil.getInstance().setmSyncTime(syncTimeResp.getSyncTime());
                requestForUserData();
            }
            isTimeCallBack = true;
        }
        if (serviceType == Constant.ServiceType.EXPERT_PROFILE_DETAIL) {
            ProfileResp loginRep = (ProfileResp) serviceResponse.getServiceResponse(ProfileResp.class);
            if (isSuccess(loginRep.getInfo().getCode())) {
                ServiceResponseHolder.getInstance().setExpertProfile(loginRep.getExpertProfile());
            }
            isExpertCallBack = true;
        }
        if (serviceType == Constant.ServiceType.USER_PROFILE_DETAIL) {
            ProfileResp loginRep = (ProfileResp) serviceResponse.getServiceResponse(ProfileResp.class);
            if (isSuccess(loginRep.getInfo().getCode())) {
                ServiceResponseHolder.getInstance().setUserProfile(loginRep.getUserProfile());
            }
            isUserProfileCallBack = true;
        }
        if (isUserProfileCallBack && isExpertCallBack && isTimeCallBack)
            mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        mHandler.postDelayed(mRunnable, 1000);

    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        mHandler.postDelayed(mRunnable, 1000);
    }
}
