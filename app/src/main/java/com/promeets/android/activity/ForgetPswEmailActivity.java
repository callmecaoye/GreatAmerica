package com.promeets.android.activity;

import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.promeets.android.pojo.LoginPost;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.Utility;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialedittext.MaterialEditText;

/**
 * This is for forget password step 1: email
 *
 * Input EditText for phone is not visible at first
 *
 * @source: LoginActivity
 *
 * @destination: ForgetPswCodeActivity
 *
 */

public class ForgetPswEmailActivity extends BaseActivity implements IServiceResponseHandler, View.OnClickListener {

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

    @BindView(R.id.btn_next)
    Button mTxtNext;

    //@BindView(R.id.login_link)
    //TextView mTxtLogin;

    @BindView(R.id.activity_sign_up_country_picker)
    CountryCodePicker countryCodePicker;

    @BindView(R.id.promeets_policy_txt)
    TextView mTxtPromeetsPolicy;

    private static final int REQUEST_PASSWORD = 100;
    @BindView(R.id.name_lay)
    LinearLayout mLayName;

    //private AnimationDrawable anim;

    private int type;

    private static final int PHONE_TYPE = 1;

    private static final int EMAIL_TYPE = 0;

    private String account;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtNext.setOnClickListener(this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatus();
        setContentView(R.layout.activity_signup_email);
        ButterKnife.bind(this);
        mTxtPromeetsPolicy.setVisibility(View.GONE);
        mLayName.setVisibility(View.GONE);

        mTabLayout.addTab(mTabLayout.newTab().setText("Email"), true);
        mTabLayout.addTab(mTabLayout.newTab().setText("Phone"));
        int dp20 = ScreenUtil.convertDpToPx(20, this);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            lp.setMargins(dp20, 0, dp20, 0);
            tab.requestLayout();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                forgetPasswordEmail();
                break;
        }
    }

    private void forgetPasswordEmail() {
        if (!validate()) return;

        HashMap<String, String> header = new HashMap<>();
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(this);
            /* Create the pojo for POST Request*/
            LoginPost loginPost = new LoginPost();
            loginPost.setAccountNumber(account);
            //loginPost.setPassword(new EncryptUtil().getEncryptedText(password,EncryptUtil.SHA256));

            //Call the service
            new GenericServiceHandler(null, this, Constant.BASE_URL + PASSWORD_VERIFY_CODE, "", loginPost, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private boolean validate() {
        /**
         * Check if email id is selected
         * **/
        if (type == EMAIL_TYPE) {
            account = countryCodePicker.getFullNumber() + mEditTextEmail.getText().toString();

            //Check if entered email address is valid
            if (!Utility.isValidEmail(account)) {
                mEditTextEmail.setError(getString(R.string.invalid_email_id));
                return false;
            } else {
                mEditTextEmail.setError(null);
            }
        }

        // Check if phone number is selected
        if (type == PHONE_TYPE) {
            account = countryCodePicker.getFullNumber() + mEditTextPhone.getText().toString();
            // Check if entered phone number is valid
            if (!Utility.isValidPhone(account)) {
                mEditTextPhone.setError(getString(R.string.invalid_phone_number));
                return false;
            } else {
                mEditTextPhone.setError(null);
            }
        }

        return true;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        LoginResp loginRespPojo = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if (loginRespPojo.info.code.equals("200")) {
            Intent intent = new Intent(this, ForgetPswCodeActivity.class);
            intent.putExtra("account", account);
            startActivityForResult(intent, REQUEST_PASSWORD);
        } else {
            onErrorResponse(loginRespPojo.info.description);
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
        if (requestCode == REQUEST_PASSWORD && resultCode == RESULT_OK) {
            this.finish();
        }
    }
}
