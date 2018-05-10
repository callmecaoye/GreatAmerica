package com.promeets.android.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.promeets.android.Constant;
import com.promeets.android.R;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.UserProfileActivity;
import com.promeets.android.api.URL;
import com.promeets.android.api.UserActionApi;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.object.UserProfilePOJO;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmailInputFragment extends DialogFragment {

    private static EmailInputFragment instance;

    @BindView(R.id.email)
    EditText mTxtEmail;
    @BindView(R.id.submit)
    TextView mBtnSubmit;

    private BaseActivity mBaseActivity;
    private Gson gson = new Gson();

    public static EmailInputFragment newInstance() {
        if (instance != null)
            instance.dismiss();
        instance = new EmailInputFragment();
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBaseActivity = (BaseActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mBaseActivity, R.style.Base_AlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_email_input, null);
        mTxtEmail = view.findViewById(R.id.email);
        mBtnSubmit = view.findViewById(R.id.submit);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBtnSubmit.setOnClickListener(v -> {
            String emailAddr = mTxtEmail.getText().toString();
            if (!validate(emailAddr))
                return;
            else submitEmail(emailAddr);

        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private boolean validate(String email) {
        if (TextUtils.isEmpty(email) || !Utility.isValidEmail(email)) {
            mTxtEmail.setError(getString(R.string.invalid_email_id));
            return false;
        } else {
            mTxtEmail.setError(null);
            return true;
        }
    }

    private void submitEmail(String email) {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("userprofile/update"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserProfilePOJO userProfile = new UserProfilePOJO();
        userProfile.contactEmail = email;
        String json = gson.toJson(userProfile);

        UserActionApi service = retrofit.create(UserActionApi.class);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<SuperResp> call = service.updateUserProfile(requestBody);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.hideProgress();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if (mBaseActivity.isSuccess(result.info.code)) {
                    dismiss();
                    mCallback.onSubmitted();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(mBaseActivity, result.info.code);
                } else
                    PromeetsDialog.show(mBaseActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }

    private EmailCallback mCallback;
    public interface EmailCallback {
        void onSubmitted();
    }
    public void setCallback(EmailCallback callback) {
        this.mCallback = callback;
    }
}
