package com.promeets.android.activity;

import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import android.os.Bundle;
import com.promeets.android.pojo.CustomerServicePost;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.Utility;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.pojo.ServiceResponse;

import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is a separated activity to report complaint or bugs
 *
 * Need to check no-null fields before submit
 *
 * @source: ExpertDetailActivity, BillManagementActivity
 *
 */

public class CustomerServiceActivity extends BaseActivity implements View.OnClickListener, IServiceResponseHandler {

    @BindView(R.id.activity_customer_service_name)
    EditText mEditServiceName;
    @BindView(R.id.activity_customer_service_cellphone)
    EditText mEditPhoneNumber;
    @BindView(R.id.activity_customer_service_email)
    EditText mEditEmailId;
    @BindView(R.id.activity_customer_service_content)
    EditText mEditContentService;
    @BindView(R.id.activity_customer_service_cancel)
    TextView mBtnCancel;
    @BindView(R.id.activity_customer_service_submit)
    TextView mEditSubmit;
    @BindView(R.id.root_layout)
    LinearLayout ly_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mEditSubmit.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        ly_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        mEditContentService.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditContentService.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mBtnCancel.getId())
            finish();
        else {
            if (isValid()) {
                submitIssue();
            }
        }
    }

    private void submitIssue() {
        HashMap<String, String> header = new HashMap<>();
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.CREATE_CUSTOMER_ISSUE));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());
        Bundle args = new Bundle();

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(this);
            /* Create the pojo for POST Request*/
            CustomerServicePost customerServicePost = new CustomerServicePost();
            customerServicePost.setContent(mEditContentService.getText().toString());
            customerServicePost.setEmailAddress(mEditEmailId.getText().toString());
            customerServicePost.setFullName(mEditServiceName.getText().toString());
            customerServicePost.setPhoneNumber(mEditPhoneNumber.getText().toString());

            //Call the service
            new GenericServiceHandler(null, this, Constant.BASE_URL + Constant.CREATE_CUSTOMER_ISSUE, "", customerServicePost, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    public boolean isValid() {
        String serviceName = mEditServiceName.getText().toString();
        String phoneNumber = mEditPhoneNumber.getText().toString();
        String emailId = mEditEmailId.getText().toString();
        String contentService = mEditContentService.getText().toString();

        String errorMesg = null;
        if (StringUtils.isEmpty(serviceName))
            errorMesg = "Please enter a valid name";
        else if (!Utility.isValidPassword(phoneNumber))
            errorMesg = "Please enter a valid phone number.";
        else if (!Utility.isValidEmail(emailId))
            errorMesg = "Please enter a valid email address.";
        else if (StringUtils.isEmpty(contentService))
            errorMesg = "Message content cannot be blank.";

        if (StringUtils.isEmpty(errorMesg))
            return true;
        else {
            PromeetsDialog.show(this, errorMesg);
        }
        return false;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        LoginResp loginResp = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if (isSuccess(loginResp.info.code)) {
            PromeetsDialog.show(this, "Your request submitted successfully! We will get back to you soon.", new PromeetsDialog.OnOKListener() {
                @Override
                public void onOKListener() {
                    finish();
                }
            });
        } else if (loginResp.info.code.equals(Constant.RELOGIN_ERROR_CODE) || loginResp.info.code.equals(Constant.UPDATE_TIME_STAMP) || loginResp.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
            Utility.onServerHeaderIssue(this, loginResp.info.code);
        } else {
            PromeetsDialog.show(this, loginResp.info.description);
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
