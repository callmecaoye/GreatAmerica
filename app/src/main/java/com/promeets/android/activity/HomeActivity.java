package com.promeets.android.activity;

import android.content.Context;
import com.promeets.android.custom.PromeetsBottomBar;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.CampaignFragment;
import com.promeets.android.listeners.OnScreenChangeListener;
import android.os.Bundle;
import com.promeets.android.services.GenericServiceHandler;
import android.util.DisplayMetrics;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.TokenHelper;
import com.promeets.android.util.UserInfoHelper;

import com.promeets.android.api.URL;
import com.promeets.android.api.UserActionApi;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.listeners.OnTabSelectedListner;
import com.promeets.android.pojo.AdsResp;
import com.promeets.android.pojo.NotificationResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.R;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;

import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This is the homepage activity including five fragments with a custom BottomBar
 *
 * HomePageFragment: categories and feature experts
 *
 * ServiceSearchListFragment: search topic via keyword or tags
 *
 * EventFragment: event list
 *
 * NotificationListFragment:notification list
 *
 * AccountFragment: navigate account related pages
 *
 */

public class HomeActivity extends BaseActivity implements IServiceResponseHandler, OnTabSelectedListner {

    private PromeetsBottomBar bottomBar;

    private UserPOJO userPOJO;

    private int totalNotification = 0;

    public static int currentTabIndex = 1;

    public static String mQuery;

    private int mIndex;

    private int scrollY;

    //private boolean fromNotify = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomBar = (PromeetsBottomBar) findViewById(R.id.bottomBar);
        //fromNotify = getIntent().getBooleanExtra("fromNotification", false);


            mQuery = getIntent().getStringExtra("search_key");
            if (!StringUtils.isEmpty(mQuery)) {
                currentTabIndex = 2;
            }

        CheckIfCampaignsAvailable();
    }

    public void handleLogin() {
            boolean isFromSplash = getIntent().getBooleanExtra("from_splash", false);
            if (isFromSplash) {
                userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
                if (userPOJO == null || userPOJO.id == null) {
                    startActivity(MainActivity.class);
                    return;
                }
            }

        homeScreenSetup();
    }

    public void homeScreenSetup(){


        //bottomBar.setCurrentTab();



        /*try {
            LocationHandlerUtil.getInstance(this).getLastKnownCity();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //UserPOJO userPOJO = (UserPOJO) getUserData(this, PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        //if (userPOJO != null && userPOJO.id != null) {
            getNotificationCount();

        //}
        TokenHelper tokenHelper = new TokenHelper(this);

        tokenHelper.initAppInfo(this);

    }
    @Override
    public void initElement() {
    }

    @Override
    public void registerListeners() {
        bottomBar.setOnTabSelectedListner(this,currentTabIndex);
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        if (serviceType == ServiceType.CHECK_FOR_CAMPAIGN) {
            AdsResp adsResp = (AdsResp) serviceResponse.getServiceResponse(AdsResp.class);
            if (adsResp.getDataList() != null) {
                CampaignFragment campaignFragment = CampaignFragment.newInstance(adsResp.getDataList());
                campaignFragment.show(getSupportFragmentManager(), "send");
            } else {
                handleLogin();
            }
        } else if (serviceType == ServiceType.UNREAD_MSG_COUNT) {
            NotificationResp notificationRep = (NotificationResp) serviceResponse.getServiceResponse(NotificationResp.class);

            if (notificationRep.info.code.equals(Constant.RELOGIN_ERROR_CODE) || notificationRep.info.code.equals(Constant.UPDATE_TIME_STAMP) || notificationRep.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                Utility.onServerHeaderIssue(HomeActivity.this, notificationRep.info.code);
                return;
            }

            totalNotification = notificationRep.msgCount;
            GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
                @Override
                public void onResult(int count, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    totalNotification += count;
                    totalNotification += Utility.unreadOneSignal();
                    bottomBar.setUnreadNumber(totalNotification);
                }
            });

        }
    }

    private void CheckIfCampaignsAvailable() {
        HashMap<String, String> header = new HashMap<>();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels / 2;
        int width = displaymetrics.widthPixels / 2;
        header.put(Constant.PROMEETS_SCREEN_HEIGHT, height + "");
        header.put(Constant.PROMEETS_SCREEN_WIDTH, width + "");

        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        String[] key = {Constant.UUID};
        String[] value = {Utility.getDeviceId()};
        new GenericServiceHandler(Constant.ServiceType.CHECK_FOR_CAMPAIGN, this, PromeetsUtils.buildURL(Constant.CHECK_FOR_CAMPAIGN, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }


    @Override
    public void onErrorResponse(String errorMessage) {
        handleLogin();
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    public void getNotificationCount() {

        UserInfoHelper userInfoHelper = new UserInfoHelper(this);

        UserPOJO userPOJO = userInfoHelper.getUserObject();

        if (userPOJO != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
            header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.NOTIFICATION_OPERATION));
            header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
            header.put("API_VERSION", Utility.getVersionCode());
            //Check for internet Connection
            if (!hasInternetConnection()) {
                PromeetsDialog.show(this, getString(R.string.no_internet));
                return;
            }
            String valueString = "";
            if (userInfoHelper.getUserObject() != null && userInfoHelper.getUserObject().id != null)
                valueString = userInfoHelper.getUserObject().id.toString();
            String[] key = {Constant.USERID};
            String[] value = {valueString};
            new GenericServiceHandler(Constant.ServiceType.UNREAD_MSG_COUNT, this, PromeetsUtils.buildURL(Constant.NOTIFICATION_OPERATION, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
        } else {
            totalNotification = Utility.unreadOneSignal();
            bottomBar.setUnreadNumber(totalNotification);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        homeScreenSetup();
        //bottomBar.setCurrentTab(currentTabIndex);
    }

    /**
     * This method played a role as a navigation for five fragments in HomeActivity
     *
     * Cooperate with PromeetsBottomBar
     *
     * @param tabIndex depends which fragment to be loaded
     */
    @Override
    public void onTabSelected(int tabIndex) {
        switch (tabIndex) {
            case 1:
                hideSoftKeyboard();
                currentTabIndex = 1;
                onScreenChange(R.id.frame_container, OnScreenChangeListener.SCREEN.HOME_SCREEN_FRAGMENT, "HOME_SCREEN_FRAGMENT", false, null);
                break;
            case 2:
                currentTabIndex = 2;
                onScreenChange(R.id.frame_container, OnScreenChangeListener.SCREEN.SERVICE_SEARCH_FRAGMENT, "SERVICE_SEARCH_FRAGMENT", false, null);
                break;
            case 3:
                /*hideSoftKeyboard();
                userPOJO = (UserPOJO) getUserData(this, PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
                if (userPOJO == null || userPOJO.id == null) {
                    currentTabIndex = 1;
                    startActivity(MainActivity.class);
                } else {*/
                    currentTabIndex = 3;
                    onScreenChange(R.id.frame_container, OnScreenChangeListener.SCREEN.NOTIFICATION_LIST_FRAGMENT, "NOTIFICATION_LIST_FRAGMENT", false, null);
                    bottomBar.setUnreadNumber(totalNotification);
                //}
                break;
            case 4:
                hideSoftKeyboard();
                userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
                if (userPOJO == null || userPOJO.id == null) {
                    currentTabIndex = 1;
                    startActivity(MainActivity.class);
                } else {
                    currentTabIndex = 4;
                    onScreenChange(R.id.frame_container, OnScreenChangeListener.SCREEN.ACCOUNT_SCREEN_FRAGMENT, "ACCOUNT_SCREEN_FRAGMENT", false, null);

                    /*if (fromNotify) {
                        fromNotify = false;
                        requestUserData();
                    }*/
                }
                break;
            case 6:
                hideSoftKeyboard();
                currentTabIndex = 6;
                onScreenChange(R.id.frame_container, OnScreenChangeListener.SCREEN.EVENT_FRAGMENT, "EVENT_FRAGMENT", false, null);
                break;
        }
    }

    private void requestUserData(){
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("userprofile/fetch"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserActionApi service = retrofit.create(UserActionApi.class);
        Call<SuperResp> call = service.fechUserProfile(userPOJO.id);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(HomeActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code) && result.userProfile != null) {
                    ServiceResponseHolder.getInstance().setUserProfile(result.userProfile);
                    if (result.userProfile.expertStatus.equals("0")) {
                        startActivity(ExpSignUpActivity.class);
                    } else if (result.userProfile.expertStatus.equals("3")) {
                        startActivity(ExpertProfileActivity.class);
                    }
                } else
                    PromeetsDialog.show(HomeActivity.this, result.info.description);
            }
            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(HomeActivity.this, t.getLocalizedMessage());
            }
        });
    }

    public void setQuery(String query) {
        this.mQuery = query;
    }
    public String getQuery() {
        return this.mQuery;
    }
    public void setTabIndex(int index) {
        this.mIndex = index;
    }
    public int getTabIndex() {
        return this.mIndex;
    }
}