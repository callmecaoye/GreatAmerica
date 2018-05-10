package com.promeets.android.activity;

import com.promeets.android.api.ServiceApi;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.api.URL;
import com.bumptech.glide.Glide;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.R;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartnerActivity extends BaseActivity {

    @BindView(R.id.image)
    ImageView mImg;
    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;
    @BindView(R.id.name)
    EditText mTxtName;
    @BindView(R.id.email)
    EditText mTxtEmail;
    @BindView(R.id.company)
    EditText mTxtCompany;
    @BindView(R.id.content)
    EditText mTxtContent;
    @BindView(R.id.submit)
    TextView mTxtSubmit;

    private String photoUrl;
    private boolean isNameNull = true, isEmailNull = true, isCompanyNull = true, isContentNull = true;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
            }
        });
        mTxtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && !StringUtils.isEmpty(charSequence.toString().trim()))
                    isNameNull = false;
                else isNameNull = true;
                checkNotNull();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mTxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && !StringUtils.isEmpty(charSequence.toString().trim()))
                    isEmailNull = false;
                else isEmailNull = true;
                checkNotNull();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mTxtCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && !StringUtils.isEmpty(charSequence.toString().trim()))
                    isCompanyNull = false;
                else isCompanyNull = true;
                checkNotNull();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mTxtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && !StringUtils.isEmpty(charSequence.toString().trim()))
                    isContentNull = false;
                else isContentNull = true;
                checkNotNull();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTxtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTxtContent.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mTxtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.isValidEmail(mTxtEmail.getText().toString()))
                    PromeetsDialog.show(PartnerActivity.this, getString(R.string.invalid_email_id));
                else
                    submit();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);

        photoUrl = getIntent().getStringExtra("photoUrl");
        if (!StringUtils.isEmpty(photoUrl))
            Glide.with(this).load(photoUrl).into(mImg);
    }

    private void checkNotNull() {
        if (!isNameNull && !isEmailNull && !isCompanyNull && !isContentNull) {
            mTxtSubmit.setBackground(getResources().getDrawable(R.drawable.btn_solid_primary));
            mTxtSubmit.setEnabled(true);
        } else {
            mTxtSubmit.setBackground(getResources().getDrawable(R.drawable.btn_solid_gray));
            mTxtSubmit.setEnabled(false);
        }
    }

    private void submit() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("personinfo/update"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);

        JSONObject json = new JSONObject();
        try {
            json.put("emailAddress", mTxtEmail.getText().toString());
            json.put("companyName", mTxtCompany.getText().toString());
            json.put("fullName", mTxtName.getText().toString());
            json.put("description", mTxtContent.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Call<BaseResp> call = service.updateInfo(requestBody);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(PartnerActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    finish();
                } else
                    PromeetsDialog.show(PartnerActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(PartnerActivity.this, t.getLocalizedMessage());
            }
        });
    }
}
