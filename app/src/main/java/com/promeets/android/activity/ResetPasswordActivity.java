package com.promeets.android.activity;

import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.UserPOJO;
import android.os.Bundle;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.EncryptUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.Utility;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.promeets.android.pojo.LoginPost;
import com.promeets.android.pojo.ServiceResponse;

import com.promeets.android.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialedittext.MaterialEditText;

/**
 * This is for reset password when user is logged in
 *
 * Dd validation before submit
 *
 * @source: NavigateActivity
 *
 */

public class ResetPasswordActivity extends BaseActivity
        implements IServiceResponseHandler, View.OnClickListener {

    @BindView(R.id.input_old_password)
    MaterialEditText mEditOldPassword;

    @BindView(R.id.input_new_password)
    MaterialEditText mEditNewPassword;

    @BindView(R.id.input_repeat_password)
    MaterialEditText mEditRepeatPassword;

    @BindView(R.id.btn_submit)
    Button mTxtSubmit;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    String oldPassword;
    String newPassword;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtSubmit.setOnClickListener(this);
        mLayRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mLayRoot.requestFocus();
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTransparentStatus();
        setContentView(R.layout.activity_reset_password);
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
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.PASSWORD_RESET));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.CONTENT_TYPE,Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(this);
            /* Create the pojo for POST Request*/
            UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
            if (userPOJO == null) return;
            LoginPost loginPost = new LoginPost();
            loginPost.setUserId(userPOJO.id.toString());
            loginPost.setOldPassword(new EncryptUtil().getEncryptedText(oldPassword, EncryptUtil.SHA256));
            loginPost.setNewPassword(new EncryptUtil().getEncryptedText(newPassword, EncryptUtil.SHA256));

            //Call the service
            new GenericServiceHandler(null ,this, Constant.BASE_URL+Constant.PASSWORD_RESET, "", loginPost,header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private boolean validate() {
        oldPassword = mEditOldPassword.getText().toString();
        //A Password must contain minimum 6 character, check if password is valid
        if (!Utility.isValidPassword(oldPassword)) {
            mEditOldPassword.setError(getString(R.string.invalid_password));
            return false;
        } else {
            mEditOldPassword.setError(null);
        }

        newPassword = mEditNewPassword.getText().toString();
        //A Password must contain minimum 6 character, check if password is valid
        if (!Utility.isValidPassword(newPassword)) {
            mEditNewPassword.setError(getString(R.string.invalid_password));
            return false;
        } else {
            mEditNewPassword.setError(null);
        }

        String repeatPassword = mEditRepeatPassword.getText().toString();
        if (!newPassword.equals(repeatPassword)) {
            mEditRepeatPassword.setError("Two passwords don't match");
            return false;
        } else {
            mEditRepeatPassword.setError(null);
        }

        return true;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        LoginResp loginResp = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if (isSuccess(loginResp.info.code)) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
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
}
