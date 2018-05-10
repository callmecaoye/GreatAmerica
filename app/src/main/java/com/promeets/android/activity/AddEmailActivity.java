package com.promeets.android.activity;

import com.promeets.android.api.PaymentApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import android.os.Bundle;
import com.promeets.android.util.UserInfoHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.promeets.android.R;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for add email account with Stripe for withdraw
 *
 * Check not-null fields and same values
 *
 * @source BillManagementActivity -> WithdrawFragment
 */

public class AddEmailActivity extends BaseActivity {

    @BindView(R.id.activity_add_email_emailId)
    EditText et_email;
    @BindView(R.id.activity_add_email_confirm_emailId)
    EditText et_confirm_email;
    @BindView(R.id.activity_add_email_save)
    TextView btn_save;

    private int userId;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = et_email.getText().toString();
                if (validate()) {
                    addEmail(emailId);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_email);
        ButterKnife.bind(this);

        UserInfoHelper helper = new UserInfoHelper(this);
        userId = helper.getUserObject().id;
    }

    private void addEmail(final String emailId) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("expertwithdraw/createExpAccount"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PaymentApi payment = retrofit.create(PaymentApi.class);
        Call<JsonObject> call = payment.addEmail(userId, emailId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                PromeetsDialog.hideProgress();
                JsonObject json = response.body();
                if (json == null) return;

                JsonObject info = json.get("info").getAsJsonObject();
                String code = info.get("code").getAsString();

                if(code.equals(Constant.RELOGIN_ERROR_CODE) || code.equals(Constant.UPDATE_TIME_STAMP) || code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(AddEmailActivity.this,code);
                } else if (code.equals("200")) {
                    PromeetsDialog.show(AddEmailActivity.this, getResources().getDrawable(R.drawable.ic_email_round), "Check your email!", new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            finish();
                        }
                    });
                } else {
                    PromeetsDialog.show(AddEmailActivity.this, info.get("description").getAsString(), new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(AddEmailActivity.this, t.getLocalizedMessage());
            }
        });
    }

    public boolean validate() {
        boolean valid = true;
        String emailId = et_email.getText().toString();
        String confirmEmailId = et_confirm_email.getText().toString();

        if (emailId.isEmpty()) {
            et_email.setError("contents cannot be blank");
            valid = false;
        } else if (!Utility.isValidEmail(emailId)) {
            et_email.setError("invalid email address");
            valid = false;
        } else {
            et_email.setError(null);
        }

        if (confirmEmailId.isEmpty()) {
            et_confirm_email.setError("contents cannot be blank");
            valid = false;
        } else if (!Utility.isValidEmail(confirmEmailId)) {
            et_confirm_email.setError("invalid email address");
            valid = false;
        } else {
            et_confirm_email.setError(null);
        }

        if (!emailId.isEmpty() && !confirmEmailId.isEmpty()
                && Utility.isValidEmail(emailId) && Utility.isValidEmail(confirmEmailId)
                && !emailId.equals(confirmEmailId)) {
            et_email.setError("contents are not identical");
            et_confirm_email.setError("contents are not identical");
            valid = false;
        }

        return valid;
    }
}
