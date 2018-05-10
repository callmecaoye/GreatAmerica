package com.promeets.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.TermsFragment;
import com.hbb20.CountryCodePicker;
import com.promeets.android.pojo.LoginPost;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.TokenHelper;
import com.promeets.android.util.Utility;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialedittext.MaterialEditText;

/**
 * This is for sign up step 1: email
 *
 * Input EditText for phone is not visible at first
 *
 * @source: MainActivity
 *
 * @destination: SignupCodeActivity
 *
 */

public class SignupEmailActivity extends BaseActivity implements IServiceResponseHandler, View.OnClickListener {

    @BindView(R.id.input_layout_email)
    LinearLayout mLayEmail;

    @BindView(R.id.input_layout_phone)
    LinearLayout mLayPhone;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    @BindView(R.id.input_name)
    MaterialEditText mEditTextName;

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

    private static final int REQUEST_SIGNUP = 100;

    //private AnimationDrawable anim;

    private int type = EMAIL_TYPE;

    private static final int PHONE_TYPE = 1;

    private static final int EMAIL_TYPE = 0;

    private String account;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatus();
        setContentView(R.layout.activity_signup_email);
        ButterKnife.bind(this);

        setSpannableString();

        TokenHelper tokenHelper = new TokenHelper(this);
        tokenHelper.initAppInfo(this);
    }

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
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_next:
                signUpEmail();
                break;
        }
    }

    private void signUpEmail() {
        if (!validate()) return;

        HashMap<String, String> header = new HashMap<>();
        header.put(Constant.CONTENT_TYPE,Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(this);
            /* Create the pojo for POST Request*/
            LoginPost loginPost = new LoginPost();
            loginPost.setAccountNumber(account);
            loginPost.setFullName(name);
            //loginPost.setPassword(new EncryptUtil().getEncryptedText(password,EncryptUtil.SHA256));

            //Call the service
            new GenericServiceHandler(null, this, Constant.BASE_URL+USER_REGISTERATION, "", loginPost,header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private boolean validate() {
        name = mEditTextName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mEditTextName.setError("Please enter your name.");
            return false;
        } else {
            mEditTextName.setError(null);
        }

        // Check if email id is selected
        if (type == EMAIL_TYPE) {
            account = mEditTextEmail.getText().toString();

            //Check if entered email address is valid
            if (!Utility.isValidEmail(account) ) {
                mEditTextEmail.setError(getString(R.string.invalid_email_id));
                return false;
            } else{
                mEditTextEmail.setError(null);
            }
        }
        // Check if phone number is selected
        if (type == PHONE_TYPE) {
            account = countryCodePicker.getFullNumber() + mEditTextPhone.getText().toString();


            // Check if entered phone number is vali

            if (!Utility.isValidPhone(account)) {
                mEditTextPhone.setError(getString(R.string.invalid_phone_number));
                return false;
            } else {
                mEditTextPhone.setError(null);
            }
        }

        return true;
    }

    private void setSpannableString() {
        String termStr = getResources().getString(R.string.promeets_policy);
        SpannableString spannableString = new SpannableString(termStr);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TermsFragment termsFragment = new TermsFragment();
                termsFragment.show(getFragmentManager(), "Terms of Use");
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.primary));
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan, 20, termStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mTxtPromeetsPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        mTxtPromeetsPolicy.setText(spannableString);
        mTxtPromeetsPolicy.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        LoginResp loginRespPojo = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if (loginRespPojo.info.code.equals("200")) {
            Intent intent = new Intent(this, SignupCodeActivity.class);
            intent.putExtra("account", account);
            intent.putExtra("fullName", name);
            startActivityForResult(intent, REQUEST_SIGNUP);
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
        if (requestCode == REQUEST_SIGNUP && resultCode == RESULT_OK) {
            this.finish();
        }
    }
}
