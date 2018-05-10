package com.promeets.android.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.PaymentActivity;
import com.promeets.android.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Show payment details and confirm charge
 */

public class PaymentConfirmationPopupFragment extends DialogFragment {

    /**
     * Interface for confirm callback with credit card index
     */
    OnConfirmClickListener mListener;
    /*@BindView(R.id.dialog_payment_confirmation_chargeAmount)
    TextView tv_amount;
    @BindView(R.id.dialog_payment_confirmation_chargeTip)
    TextView tv_tip;
    @BindView(R.id.dialog_payment_confirm)
    Button btn_confirm;*/

    //EventDetailResp result;
    @BindView(R.id.root_layout)
    FrameLayout mLayRoot;
    @BindView(R.id.dialog_layout)
    LinearLayout mLayDialog;
    @BindView(R.id.total_txt)
    TextView mTxtTotal;
    @BindView(R.id.total_amount)
    TextView mAmountTotal;
    @BindView(R.id.balance_amount)
    TextView mAmountBalance;
    @BindView(R.id.balance_amount_lay)
    FrameLayout mLayBalance;
    @BindView(R.id.deduct_amount)
    TextView mAmountDeduct;
    @BindView(R.id.deduct_amount_lay)
    FrameLayout mLayDedect;
    @BindView(R.id.final_amount)
    TextView mAmountFinal;
    @BindView(R.id.chargeTip)
    TextView mChargeTip;
    @BindView(R.id.payment_tip)
    TextView mPaymentTip;
    @BindView(R.id.confirm)
    TextView mTxtConfrim;
    /*@BindView(R.id.dialog_payment_confirmation_chargeAmount_withCode)
    TextView mTxtAmountWithCode;
    @BindView(R.id.layout_with_code)
    LinearLayout mLayWithCode;*/

    private BaseActivity mBaseActivity;
    private boolean useBalance, useValidCode;
    private int position;

    public static PaymentConfirmationPopupFragment newInstance(boolean useBalance, boolean useCode, int position) {
        PaymentConfirmationPopupFragment fragment = new PaymentConfirmationPopupFragment();
        fragment.useBalance = useBalance;
        fragment.useValidCode = useCode;
        fragment.position = position;
        return fragment;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
        try {
            mListener = (OnConfirmClickListener) mBaseActivity;
        } catch (Exception e) {

        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_payment_confirmation, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (useBalance) mLayBalance.setVisibility(View.VISIBLE);
        if (useValidCode) mLayDedect.setVisibility(View.VISIBLE);

        mChargeTip.setText(mBaseActivity.getIntent().getExtras().getString("chargeTip"));
        new DownloadFileTask().execute();

        mTxtTotal.setText(((PaymentActivity) mBaseActivity).mTxtTotal.getText().toString());
        mAmountTotal.setText(((PaymentActivity) mBaseActivity).mTxtTotalAmount.getText().toString());
        mAmountBalance.setText(((PaymentActivity) mBaseActivity).mTxtBalanceAmount.getText().toString());
        mAmountDeduct.setText(((PaymentActivity) mBaseActivity).mTxtDedAmount.getText().toString());
        mAmountFinal.setText(((PaymentActivity) mBaseActivity).mTxtFinalAmount.getText().toString());

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mLayDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mTxtConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                mListener.OnConfirmClick(position);
            }
        });
    }

    public interface OnConfirmClickListener {
        void OnConfirmClick(int position);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    /**
     * Get payment tips from url
     */
    private class DownloadFileTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            final String txtSource = "https://www.promeets.us/security/payment_tip";
            String content = "";
            URL txtUrl;
            try {
                txtUrl = new URL(txtSource);
                BufferedReader reader = new BufferedReader(new InputStreamReader(txtUrl.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    content += line + "\n";
                }
                reader.close();
            } catch (IOException e) {

            }
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                mPaymentTip.setText(result);
            }
        }
    }
}
