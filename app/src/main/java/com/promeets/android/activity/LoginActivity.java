package com.promeets.android.activity;

import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.os.Bundle;
import com.promeets.android.services.GenericServiceHandler;
import android.support.design.widget.TabLayout;
import android.util.Base64;
import com.promeets.android.util.EncryptUtil;
import com.promeets.android.util.ServerTimeUtil;
import android.util.Log;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.TokenHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.LoginPost;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.util.ChatHelper;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialedittext.MaterialEditText;

import static com.promeets.android.Constant.ServiceType.NORMAL_USER_LOGIN;

/**
 * This is for normal user log in.
 *
 * Input EditText for phone is not visible at first
 *
 * Forget password option is available for careless user
 *
 * @source: MainActivity
 *
 */

public class LoginActivity extends BaseActivity implements IServiceResponseHandler, View.OnClickListener {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindView(R.id.input_layout_email)
    LinearLayout mLayEmail;

    @BindView(R.id.input_layout_phone)
    LinearLayout mLayPhone;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    @BindView(R.id.input_email)
    MaterialEditText mEditTextEmail;

    @BindView(R.id.input_phone)
    MaterialEditText mEditTextPhone;

    @BindView(R.id.input_password)
    MaterialEditText mEditTextPassword;

    @BindView(R.id.btn_login)
    Button mBtnLogin;

    //@BindView(R.id.link_signup)
    //TextView mTxtSignUp;

    @BindView(R.id.activity_login_country_picker)
    CountryCodePicker countryCodePicker;

    @BindView(R.id.activity_login_forgot_password)
    TextView mTxtForgetPassword;

    private ServiceType serviceType;

    private static final int REQUEST_SIGNUP = 100;

    private static final int PHONE_TYPE = 1;

    private static final int EMAIL_TYPE = 0;

    private int type = EMAIL_TYPE;

    private String account;
    private String password;

    //private CallbackManager mCallbackManager;

    //private String mPlatformToken;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatus();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mTabLayout.addTab(mTabLayout.newTab().setText("Email"), true);
        mTabLayout.addTab(mTabLayout.newTab().setText("Phone"));
        int dp20 = ScreenUtil.convertDpToPx(20, this);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup)mTabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            lp.setMargins(dp20, 0, dp20, 0);
            tab.requestLayout();
        }
    }

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtForgetPassword.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        //mTxtSignUp.setOnClickListener(this);

        mLayRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mLayRoot.requestFocus();
                return false;
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTabLayout.getWindowToken(), 0);

                if (tab.getPosition() == 0) {
                    mLayEmail.setVisibility(View.VISIBLE);
                    mLayPhone.setVisibility(View.GONE);
                    type = EMAIL_TYPE;
                } else if (tab.getPosition() == 1) {
                    mLayPhone.setVisibility(View.VISIBLE);
                    mLayEmail.setVisibility(View.GONE);
                    type = PHONE_TYPE;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void login() {

        Log.d(TAG, "Login");

        //Check whether all the fields are filled properly.
        if (!validate()) {
            return;
        }

        //Disable the button to disallow multiple clicks
        mBtnLogin.setEnabled(false);

        /*
        * Set the service type to handle the request
        * */
        serviceType = NORMAL_USER_LOGIN;
        HashMap<String, String> header = new HashMap<>();
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);
        header.put(Constant.DEVICE_ID, Utility.getDeviceId());
        header.put(Constant.TIMEZON, TimeZone.getDefault().getID());

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            mBtnLogin.setEnabled(true);
            return;
        }

        PromeetsDialog.showProgress(this);
            /* Create the pojo for POST Request*/
            LoginPost loginPost = new LoginPost();
            loginPost.setAccountNumber(account);
            loginPost.setPassword(new EncryptUtil().getEncryptedText(password, EncryptUtil.SHA256));

            new ServerTimeUtil(this).getServerTime();
            //Call the service
            new GenericServiceHandler(NORMAL_USER_LOGIN, this, Constant.BASE_URL + Constant.LOGIN_OPERATION, "", loginPost, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();

            // set isLILogin false
            GlobalVariable.isLILogin = false;
            GlobalVariable.isFBLogin = false;
    }

    public void onLoginSuccess(int userId) {
        mBtnLogin.setEnabled(true);
        ChatHelper chatUserInfoHelper = new ChatHelper(LoginActivity.this);
        chatUserInfoHelper.getChatAccountInfoFromServer(userId);
        TokenHelper tokenHelper = new TokenHelper(this);
        tokenHelper.initAppInfo(this);
    }

    public void onLoginFailed() {
        mBtnLogin.setEnabled(true);
    }

    public boolean validate() {
        if (type == EMAIL_TYPE) {
            account = mEditTextEmail.getText().toString();
            if (StringUtils.isEmpty(account) || !Utility.isValidEmail(account)) {
                mEditTextEmail.setError(getString(R.string.invalid_email_id));
                return false;
            } else {
                mEditTextEmail.setError(null);
            }
        }

        if (type == PHONE_TYPE) {
            account = mEditTextPhone.getText().toString();
            if (StringUtils.isEmpty(account) || !Utility.isValidPhone(account)) {
                mEditTextPhone.setError(getString(R.string.invalid_phone_number));
                return false;
            } else {
                mEditTextPhone.setError(null);
            }
        }

        password = mEditTextPassword.getText().toString();
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mEditTextPassword.setError(getString(R.string.password_error));
            return false;
        } else {
            mEditTextPassword.setError(null);
        }
        return true;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        if (serviceType == NORMAL_USER_LOGIN) {
            LoginResp loginResp = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);

            if (isSuccess(loginResp.info.code)) {
                PromeetsUtils.saveUserData(this, loginResp);
                onLoginSuccess(loginResp.user.id);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                hideSoftKeyboard();
                break;
            case R.id.activity_login_forgot_password:
                Intent intent = new Intent(LoginActivity.this, ForgetPswEmailActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (userPOJO != null) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }
}
