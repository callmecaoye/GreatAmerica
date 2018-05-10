package com.promeets.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.pojo.LoginPost;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.EncryptUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.Utility;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialedittext.MaterialEditText;

/**
 * This is for forget password step 3: password
 *
 * Similar to reset password
 *
 * @source: ForgetPswCodeActivity
 *
 * @destination: ForgetPswCodeActivity
 *
 */

public class ForgetPswPasswordActivity extends BaseActivity implements IServiceResponseHandler, View.OnClickListener {

    @BindView(R.id.input_password)
    MaterialEditText mEditPassword;

    @BindView(R.id.input_repeat_password)
    MaterialEditText mEditRepeatPassword;

    @BindView(R.id.btn_submit)
    Button mTxtSubmit;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    String accountNumber = "";
    String code = "";
    String password;

    @Override
    public void initElement() {
        accountNumber = getIntent().getStringExtra("account");
        code = getIntent().getStringExtra("code");
    }

    @Override
    public void registerListeners() {
        mTxtSubmit.setOnClickListener(this);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatus();
        setContentView(R.layout.activity_signup_password);
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_submit:
                submit();
                hideSoftKeyboard();
                break;
        }
    }

    private void submit() {
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
            loginPost.setAccountNumber(accountNumber);
            loginPost.setPassword(new EncryptUtil().getEncryptedText(password, EncryptUtil.SHA256));
            loginPost.setVerifyCode(code);

            //Call the service
            new GenericServiceHandler(null ,this, Constant.BASE_URL+Constant.PASSWORD_REGISTER_NEW, "", loginPost,header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private boolean validate() {
        password = mEditPassword.getText().toString();
        //A Password must contain minimum 6 character, check if password is valid
        if (!Utility.isValidPassword(password)) {
            mEditPassword.setError(getString(R.string.invalid_password));
            return false;
        } else {
            mEditPassword.setError(null);
        }

        String repeatPassword = mEditRepeatPassword.getText().toString();
        if (!password.equals(repeatPassword)) {
            mEditRepeatPassword.setError("Two passwords don't match");
            return false;
        } else {
            mEditRepeatPassword.setError(null);
        }

        return true;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        LoginResp loginResp = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if (isSuccess(loginResp.info.code)) {
            PromeetsUtils.saveUserData(this, loginResp);

            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);

            finish();

            /*GlobalVariable.showPolling = false;
            if (loginResp.urls != null && loginResp.urls.size() > 0) {
                for (String url : loginResp.urls) {
                    if (url.startsWith("promeets://polling")) {
                        GlobalVariable.showPolling = true;
                    }
                }
            }*/
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
}
