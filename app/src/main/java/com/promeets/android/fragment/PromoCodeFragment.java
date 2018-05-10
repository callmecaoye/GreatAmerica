package com.promeets.android.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;

import com.promeets.android.api.ServiceApi;
import com.promeets.android.api.URL;
import com.bumptech.glide.Glide;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.listeners.OnDismissPromoCodeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.R;
import com.promeets.android.util.ServiceResponseHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by sosasang on 8/11/17.
 */

public class PromoCodeFragment extends DialogFragment {
    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;
    BaseActivity mBaseActivity;
    @BindView(R.id.start)
    TextView mTxtStart;
    @BindView(R.id.code)
    EditText mTxtCode;
    @BindView(R.id.image)
    RoundedImageView mImg;

    private boolean mIsKeyboardVisible = false;
    private String url;
    private OnDismissPromoCodeListener listener;

    public static PromoCodeFragment newInstance(String url, OnDismissPromoCodeListener listener) {
        PromoCodeFragment f = new PromoCodeFragment();
        f.url = url;
        f.listener = listener;
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo_code, container);
        ButterKnife.bind(this, view);

        Glide.with(mBaseActivity).load(url).into(mImg);
        mLayRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect measureRect = new Rect(); //you should cache this, onGlobalLayout can get called often
                mLayRoot.getWindowVisibleDisplayFrame(measureRect);
                // measureRect.bottom is the position above soft keypad
                int keypadHeight = mLayRoot.getRootView().getHeight() - measureRect.bottom;

                if (keypadHeight > 0) {
                    // keyboard is opened
                    mIsKeyboardVisible = true;
                } else {
                    //store keyboard state to use in onBackPress if you need to
                    mIsKeyboardVisible = false;
                }
            }
        });
        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsKeyboardVisible) {
                    InputMethodManager imm = (InputMethodManager) mBaseActivity
                            .getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } else {
                    dismiss();
                }
            }
        });

        mTxtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPromoCode();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void submitPromoCode() {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("inviteCustomer/create"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceApi service = retrofit.create(ServiceApi.class);
        Call<BaseResp> call = service.inviteCustomer(mTxtCode.getText().toString());
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if (mBaseActivity.isSuccess(result.info.code)) {
                    dismiss();
                    PromeetsDialog.show(mBaseActivity, "Thank you!", new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            listener.onDismiss();
                        }
                    });
                    GlobalVariable.promoBgUrl = "";
                } else
                    PromeetsDialog.show(mBaseActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }
}

