package com.promeets.android.activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Paint;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.internals.BuildConfig;
import com.linkedin.platform.listeners.ApiListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import com.promeets.android.services.GenericServiceHandler;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import com.promeets.android.util.MixpanelUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.TokenHelper;
import com.promeets.android.util.UserInfoHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.promeets.android.api.InfoApi;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;
import com.sendbird.android.SendBird;

import com.promeets.android.util.ChatHelper;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.promeets.android.Constant.ServiceType.NORMAL_USER_LOGIN;

/**
 * IMPORTANT ACTIVITY!!!
 *
 * This is for deal with user sign up and login
 *
 * VideoView for background video
 *
 * Integrated with Facebook & LinkedIn Authentication
 *
 * @source: When pages or actions are required user profile info
 *
 */

public class MainActivity extends BaseActivity implements IServiceResponseHandler, View.OnClickListener {

    private static final int REQUEST_SIGNUP = 0;

    private static final int LINKEDIN_WEB_REQUEST = 200;

    private static final String OAUTH_ACCESS_TOKEN_PARAM = "oauth2_access_token";

    private static final String QUESTION_MARK = "?";

    private static final String EQUALS = "=";

    @BindView(R.id.skip_button)
    TextView mBtnSkip;

    @BindView(R.id.slogan)
    TextView mTxtSlogan;

    @BindView(R.id.fb_login_button)
    LinearLayout mBtnFacebook;

    @BindView(R.id.li_login_button)
    LinearLayout mBtnLinkedIn;

    @BindView(R.id.sign_up_button)
    TextView mBtnSignUp;

    @BindView(R.id.log_in_button)
    LinearLayout mBtnLogin;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    @BindView(R.id.video_view)
    VideoView mVideoView;

    @BindView(R.id.img_logo)
    ImageView mImgLogo;

    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.middle_lay)
    LinearLayout mLayMiddle;
    @BindView(R.id.bottom_lay)
    LinearLayout mLayBottom;
    @BindView(R.id.txt_fb)
    TextView mTxtFB;
    @BindView(R.id.txt_li)
    TextView mTxtLI;


    private CallbackManager mCallbackManager;

    private ServiceType serviceType;

    private String mPlatformToken;

    private static final String TAG = "MainActivity";

    private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host
            + "/v1/people/~:" +
            "(email-address,formatted-name,location,positions,picture-urls::(original))";

    //boolean isDemoBg = true;

    private String slogan = "";

    private boolean isServerDenied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatus();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        UserInfoHelper userInfoHelper = new UserInfoHelper(this);
        userInfoHelper.cleanData();
        promeetsPreferenceUtil.clear();

        isServerDenied = getIntent().getBooleanExtra("isServerDenied", false);
        if (isServerDenied) {
            PromeetsDialog.show(this, "You are logged in somewhere else, For your security, please re-login");
        }

        text2.setAutoLinkMask(Linkify.ALL);
        text2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        initVideoView();
        getSlogan();

        SendBird.disconnect(null);
    }

    private void initVideoView() {
        mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro));
        mVideoView.start();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        });
    }

    private void getSlogan() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        } //else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(com.promeets.android.api.URL.HOST)
                    .client(ServiceResponseHolder.getInstance().getRetrofitHeader("version/info"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            InfoApi service = retrofit.create(InfoApi.class);
            Call<BaseResp> call = service.getInfo("loginpageTitle");
            call.enqueue(new Callback<BaseResp>() {
                @Override
                public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                    BaseResp result = response.body();
                    if (result == null) {
                        PromeetsDialog.show(MainActivity.this, response.errorBody().toString());
                        return;
                    }

                    if (isSuccess(result.info.code)) {
                        slogan = result.data.loginpageTitle;
                        mTxtSlogan.setText(slogan);
                    } else {
                        PromeetsDialog.show(MainActivity.this, result.info.description);
                    }
                }

                @Override
                public void onFailure(Call<BaseResp> call, Throwable t) {

                }
            });
        //}
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initVideoView();

        UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (userPOJO != null) {
            finish();

            if (isServerDenied)
                startActivity(HomeActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MixpanelUtil.getInstance(this).trackEvent("Login page view");
    }

    @Override
    protected void onStop() {
        mVideoView.stopPlayback();
        super.onStop();
    }

    @Override
    public void initElement() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                //info.setText("User ID:  " +
                //        loginResult.getAccessToken().getUserId() + "\n" +
                //        "Auth Token: " + loginResult.getAccessToken().getToken());
                loginResult.getAccessToken();
                mPlatformToken = loginResult.getAccessToken().getToken();
                //System.out.println("Token:"+loginResult.getAccessToken().getToken());
                //System.out.println("AccessToken:"+loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, response.toString());
                        Bundle bFacebookData = getFacebookData(object);
                        if (bFacebookData == null) return;
                        /**
                         * loginOther Parameters
                         * String account, String platformToken,String platform, String userName,
                         * String city, String employer, String photoURL, String releaseEmail,
                         * String position, String releasePhoneNumber
                         */
                        if (StringUtils.isEmpty(bFacebookData.getString("email")))
                            loginOther(bFacebookData.getString("idFacebook"), mPlatformToken, "F", bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name"),
                                    "", "", bFacebookData.getString("profile_pic"), "", "", "");
                        else
                            loginOther(bFacebookData.getString("email"), mPlatformToken, "F", bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name"),
                                    "", "", bFacebookData.getString("profile_pic"), "", "", "");
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
                GlobalVariable.isFBLogin = true;
                GlobalVariable.isLILogin = false;
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });

    }

    @Override
    public void registerListeners() { //registering the listeners
        mBtnSignUp.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mBtnFacebook.setOnClickListener(this);
        mBtnLinkedIn.setOnClickListener(this);
        mBtnSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) { //handle the clicks
            case R.id.sign_up_button:
                Intent intent = new Intent(MainActivity.this, SignupEmailActivity.class); // starting signup activity
                intent.putExtra("isFromLogin", false);
                startActivity(intent);
                break;
            case R.id.log_in_button:
                startActivity(LoginActivity.class);
                break;
            case R.id.li_login_button:
                PackageManager packageManager = getApplicationContext().getPackageManager();
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo("com.linkedin.android", PackageManager.GET_ACTIVITIES);
                    if (packageInfo.versionCode >= BuildConfig.LI_APP_SUPPORTED_VER_CODE) {
                        // LinkedIn APK installed
                        LISessionManager.getInstance(getApplicationContext()).init(MainActivity.this, Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS), new AuthListener() {
                            @Override
                            public void onAuthSuccess() {

                            }

                            @Override
                            public void onAuthError(LIAuthError error) {
                                LISessionManager.getInstance(getApplicationContext()).clearSession();
                            }
                        }, false);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // LinkedIn APK not installed
                    Intent intentLI = new Intent(MainActivity.this, AuthActivity.class);
                    startActivityForResult(intentLI, LINKEDIN_WEB_REQUEST);
                }
                break;
            case R.id.fb_login_button:
                try {
                    LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
                } catch (Exception e) {

                }
                break;
            case R.id.skip_button:
                MixpanelUtil.getInstance(this).trackEvent("Login page -> Skip");
                finish();

                if (isServerDenied)
                    startActivity(HomeActivity.class);
                break;
        }
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        if (serviceType == NORMAL_USER_LOGIN) {
            LoginResp loginResp = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);

            if (isSuccess(loginResp.info.code)) {
                PromeetsUtils.saveUserData(this, loginResp);
                onLoginSuccess(loginResp.user.id);
                startActivity(HomeActivity.class);
                finish();


                if (loginResp.urls != null && loginResp.urls.size() > 0) {
                    for (String url : loginResp.urls) {
                        if (url.startsWith("promeets://invitecustomer/url/")) {
                            String[] arrUrl = loginResp.url.split("/");
                            byte[] data = Base64.decode(arrUrl[arrUrl.length - 1], Base64.DEFAULT);
                            try {
                                GlobalVariable.promoBgUrl = new String(data, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                PromeetsDialog.show(this, loginResp.info.description);
                onLoginFailed();
            }
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(this, errorMessage);

        if (serviceType == NORMAL_USER_LOGIN)
            onLoginFailed();
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                startActivity(HomeActivity.class);
                finish();
            }
        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            if (resultCode == RESULT_OK) {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == LINKEDIN_WEB_REQUEST) { //LinkedIn Web Auth
            if (data != null) {
                String mLIToken = data.getStringExtra("accessToken");
                String profileUrl = url + QUESTION_MARK + OAUTH_ACCESS_TOKEN_PARAM + EQUALS + mLIToken;
                fetchLinkedInProfileWithToken(profileUrl, mLIToken);
            }
        } else {
            if (resultCode == RESULT_OK) {
                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
                mPlatformToken = LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().getValue();

                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(MainActivity.this, url, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse result) {
                        JSONObject response = result.getResponseDataAsJson();
                        try {
                            // email-address,formatted-name,location,positions,picture-urls::(original)
                            String name = response.has("formattedName") ? response.get("formattedName").toString() : "";
                            String emailAddr = response.has("emailAddress") ? response.get("emailAddress").toString() : "";

                            String picUrl = "";
                            if (response.has("pictureUrls")) {
                                JSONObject url = response.getJSONObject("pictureUrls");
                                if (url.has("values"))
                                    picUrl = url.getJSONArray("values").get(0).toString();
                            }

                            String city = "";
                            if (response.has("location")) {
                                JSONObject loc = response.getJSONObject("location");
                                if (loc.has("name"))
                                    city = loc.get("name").toString();
                            }

                            String companyName = "";
                            String position = "";
                            JSONObject posValue;
                            JSONObject comp;
                            if (response.has("positions")) {
                                JSONObject pos = response.getJSONObject("positions");
                                if (pos.has("values")) {
                                    posValue = pos.getJSONArray("values").getJSONObject(0);
                                    if (posValue.has("company")) {
                                        comp = posValue.getJSONObject("company");
                                        if (comp.has("name"))
                                            companyName = comp.get("name").toString();
                                    }

                                    if (posValue.has("title"))
                                        position = posValue.get("title").toString();
                                }
                            }

                            /**
                             * loginOther Parameters
                             * String account, String platformToken,String platform, String userName,
                             * String city, String employer, String photoURL, String releaseEmail,
                             * String position, String releasePhoneNumber
                             */
                            loginOther(emailAddr, mPlatformToken, "L", name, city, companyName, picUrl, emailAddr, position, "");
                            // LinkedIn Login for expert profile from LinkedIn import
                            GlobalVariable.isLILogin = true;
                            GlobalVariable.isFBLogin = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onApiError(LIApiError error) {

                    }
                });
            }
        }
    }

    private void onLoginSuccess(int userId) {
        ChatHelper chatUserInfoHelper = new ChatHelper(MainActivity.this);
        chatUserInfoHelper.getChatAccountInfoFromServer(userId);
        TokenHelper tokenHelper = new TokenHelper(this);
        tokenHelper.initAppInfo(this);
    }

    public void onLoginFailed() {

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
            return null;
        }
    }

    public void loginOther(String accountNumber, String platformToken, String platform, String fullName,
                           String city, String employer, String photoURL, String releaseEmail,
                           String position, String releasePhoneNumber) {
        Log.i("token", platformToken);
        /*
        * Set the service type to handle the request
        * */
        serviceType = NORMAL_USER_LOGIN;

        /*
        * Hashmap in case we want to pass any parameterized headers
        * */
        HashMap<String, String> header = new HashMap<>();

        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);
        header.put(Constant.DEVICE_ID, Utility.getDeviceId());
        header.put(Constant.TIMEZON, TimeZone.getDefault().getID());

        Bundle args = new Bundle();

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
        } else {
            args.putString("info", getString(R.string.please_wait)); //set message to diplay in no intenet connection
            args.putBoolean("isSuccessful", false); // set icon type

            String[] key = {Constant.ACCOUNT_NUMBER, Constant.PLATFORM, Constant.PLATFORM_TOKEN, Constant.FULLNAME,
                    Constant.CITY, Constant.EMPLOYER, Constant.PHOTO_URL, Constant.RELEASE_EMAIL,
                    Constant.POSITION, Constant.RELEASE_PHONE_NUMBER};
            String[] value = {accountNumber, platform, platformToken, fullName,
                    city, employer, photoURL, releaseEmail,
                    position, releasePhoneNumber};

            new GenericServiceHandler(ServiceType.NORMAL_USER_LOGIN, this, PromeetsUtils.buildURL(Constant.LOGIN_OTHER, key, value), "", header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
        }
        //collect infomation in backend, to be implement in the future;
    }

    private void fetchLinkedInProfileWithToken(String url, final String token) {
        PromeetsDialog.showProgress(this);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("x-li-format", "json")
                .url(url)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                PromeetsDialog.hideProgress();
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response result) throws IOException {
                PromeetsDialog.hideProgress();
                if (!result.isSuccessful()) {
                    return;
                }
                // Read data on the worker thread
                final String responseStr = result.body().string();
                // Run view-related code back on the main thread
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject response = null;
                        try {
                            response = new JSONObject(responseStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (response != null) {
                            try {
                                // email-address,formatted-name,location,positions,picture-urls::(original)
                                String name = response.has("formattedName") ? response.get("formattedName").toString() : "";
                                String emailAddr = response.has("emailAddress") ? response.get("emailAddress").toString() : "";

                                String picUrl = "";
                                if (response.has("pictureUrls")) {
                                    JSONObject url = response.getJSONObject("pictureUrls");
                                    if (url.has("values"))
                                        picUrl = url.getJSONArray("values").get(0).toString();
                                }

                                String city = "";
                                if (response.has("location")) {
                                    JSONObject loc = response.getJSONObject("location");
                                    if (loc.has("name"))
                                        city = loc.get("name").toString();
                                }

                                String companyName = "";
                                String position = "";
                                JSONObject posValue;
                                JSONObject comp;
                                if (response.has("positions")) {
                                    JSONObject pos = response.getJSONObject("positions");
                                    if (pos.has("values")) {
                                        posValue = pos.getJSONArray("values").getJSONObject(0);
                                        if (posValue.has("company")) {
                                            comp = posValue.getJSONObject("company");
                                            if (comp.has("name"))
                                                companyName = comp.get("name").toString();
                                        }

                                        if (posValue.has("title"))
                                            position = posValue.get("title").toString();
                                    }
                                }

                                /**
                                 * loginOther Parameters
                                 * String account, String platformToken,String platform, String userName,
                                 * String city, String employer, String photoURL, String releaseEmail,
                                 * String position, String releasePhoneNumber
                                 */
                                loginOther(emailAddr, token, "L", name, city, companyName, picUrl, emailAddr, position, "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();

        if (isServerDenied)
            startActivity(HomeActivity.class);
    }
}
