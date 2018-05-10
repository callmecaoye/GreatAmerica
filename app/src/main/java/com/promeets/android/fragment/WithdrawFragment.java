package com.promeets.android.fragment;

import com.promeets.android.activity.AddEmailActivity;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.BillManagementActivity;
import com.promeets.android.adapter.EmailAdapter;
import com.promeets.android.api.PaymentApi;
import com.promeets.android.api.URL;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.promeets.android.Constant;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.promeets.android.R;
import com.promeets.android.util.Utility;

import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for showing existing email accounts or start AddEmailActivity
 *
 * When withdraw, send emailId to our server
 *
 */

public class WithdrawFragment extends DialogFragment {

    @BindView(R.id.email_list)
    ListView mLVEmail;

    @BindView(R.id.add_account)
    TextView mTxtAdd;

    @BindView(R.id.cancel)
    TextView mTxtCancel;

    private BaseActivity mBaseActivity;
    private List<String> emails;
    private EmailAdapter emailAdapter;
    private int userId;

    public static WithdrawFragment newInstance(int userId, List<String> emails) {
        WithdrawFragment f = new WithdrawFragment();
        f.emails = emails;
        f.userId = userId;
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(mBaseActivity);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(mBaseActivity);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw, container);
        ButterKnife.bind(this, view);

        emailAdapter = new EmailAdapter(mBaseActivity, emails);
        mLVEmail.setAdapter(emailAdapter);
        if (emailAdapter.getCount() > 3) {
            ViewGroup.LayoutParams params = mLVEmail.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ScreenUtil.convertDpToPx(140, mBaseActivity);
            mLVEmail.setLayoutParams(params);
        }
        mLVEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedEmail = emails.get(position);  //Selected item in listview
                transferToExpAccount(selectedEmail);
                dismiss();
            }
        });

        mTxtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mBaseActivity.startActivity(AddEmailActivity.class);
            }
        });
        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void transferToExpAccount(final String emailId) {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("expertwithdraw/transfer"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PaymentApi payment = retrofit.create(PaymentApi.class);
        Call<JsonObject> call = payment.transfer(userId, emailId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                PromeetsDialog.hideProgress();
                JsonObject json = response.body();
                if (json == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                JsonObject info = json.get("info").getAsJsonObject();
                String code = info.get("code").getAsString();
                if (code.equals("200")) {
                    PromeetsDialog.show(mBaseActivity, "Successfully transferred to " + emailId, new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            if (mBaseActivity instanceof BillManagementActivity) {
                                Intent intent = mBaseActivity.getIntent();
                                mBaseActivity.finish();
                                startActivity(intent);
                            }
                        }
                    });
                } else if(code.equals(Constant.RELOGIN_ERROR_CODE) || code.equals(Constant.UPDATE_TIME_STAMP) || code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(mBaseActivity,code);
                } else {
                    PromeetsDialog.show(mBaseActivity, info.get("description").getAsString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }
}
