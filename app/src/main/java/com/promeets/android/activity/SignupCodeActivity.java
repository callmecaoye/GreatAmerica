package com.promeets.android.activity;

import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import com.promeets.android.Constant;
import android.os.Bundle;
import android.os.CountDownTimer;
import com.promeets.android.services.GenericServiceHandler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.pojo.LoginPost;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialedittext.MaterialEditText;

/**
 * This is for sign up step 2: verify code
 *
 * @source: SignupEmailActivity
 *
 * @destination: SignupPasswordActivity
 *
 */

public class SignupCodeActivity extends BaseActivity implements IServiceResponseHandler, View.OnClickListener {

    @BindView(R.id.input_code)
    MaterialEditText mEditCode;

    @BindView(R.id.resend_code)
    TextView mTxtResend;

    @BindView(R.id.input_layout_code)
    LinearLayout inputLayoutCode;

    @BindView(R.id.btn_next)
    Button mTxtNext;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    private static final int REQUEST_SIGNUP = 100;

    //AnimationDrawable anim;

    CountDownTimer timer;

    String account;
    String name;

    String code;

    //private DialogFragment mFragmentProgress;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtResend.setOnClickListener(this);
        mTxtNext.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatus();
        setContentView(R.layout.activity_signup_code);
        ButterKnife.bind(this);

        account = getIntent().getStringExtra("account");
        name = getIntent().getStringExtra("fullName");

        mLayRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mLayRoot.requestFocus();
                return false;
            }
        });

        mTxtResend.setEnabled(false);
        startCountDownTime(10);

        onErrorResponse(getString(R.string.verification_code_mesg));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    /**
     * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
     * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
     * 有onTick，onFinsh、cancel和start方法
     */
    private void startCountDownTime(long time) {
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //每隔countDownInterval秒会回调一次onTick()方法
                mTxtResend.setText("Resend Code (" + millisUntilFinished / 1000 + ")");
            }

            @Override
            public void onFinish() {
                mTxtResend.setEnabled(true);
                mTxtResend.setText("Resend Code");
                mTxtResend.setTextColor(Color.parseColor("#C44A4A4A"));
                mTxtResend.setTypeface(null, Typeface.BOLD);
            }
        };
        timer.start();// 开始计时
        //timer.cancel(); // 取消
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_next:
                signUpCode();
                break;
            case R.id.resend_code:
                resendCode();
                startCountDownTime(10);
                mTxtResend.setEnabled(false);
                mTxtResend.setTextColor(getResources().getColor(R.color.em_black_38));
                mTxtResend.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    private void signUpCode() {
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
            loginPost.setVerifyCode(code);
            loginPost.setCodeType(LoginPost.REGISTER);
            //loginPost.setPassword(new EncryptUtil().getEncryptedText(password,EncryptUtil.SHA256));

            //Call the service
            new GenericServiceHandler(Constant.ServiceType.USER_VERIFICATION, this, Constant.BASE_URL+ Constant.USER_REGISTERATION_CHECK_CODE, "", loginPost,header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private void resendCode() {
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
            new GenericServiceHandler(Constant.ServiceType.NORMAL_USER_LOGIN, this, Constant.BASE_URL+ Constant.USER_REGISTERATION, "", loginPost,header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();

    }

    private boolean validate() {
        code = mEditCode.getText().toString();
        if (code.length() != 4) {
            mEditCode.setError(getString(R.string.invalid_code));
            return false;
        } else {
            mEditCode.setError(null);
        }
        return true;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();

        LoginResp loginRespPojo = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if (loginRespPojo.info.code.equals("200")) {
            if (serviceType == Constant.ServiceType.USER_VERIFICATION) {
                Intent intent = new Intent(this, SignupPasswordActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("fullName", name);
                intent.putExtra("code", code);
                startActivityForResult(intent, REQUEST_SIGNUP);
            } else if (serviceType == Constant.ServiceType.NORMAL_USER_LOGIN) {
                onErrorResponse(getString(R.string.verification_code_mesg));
            }
        } else {
            //Toast.makeText(SignUpActivity.this, loginRespPojo.info.description, Toast.LENGTH_SHORT).show();
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
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
