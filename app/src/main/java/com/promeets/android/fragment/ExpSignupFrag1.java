package com.promeets.android.fragment;

import com.promeets.android.activity.ExpSignUpActivity;
import android.content.Context;
import com.promeets.android.object.ExpertProfilePOJO;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.promeets.android.R;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sosasang on 1/26/18.
 */

public class ExpSignupFrag1 extends Fragment {
    @BindView(R.id.root_layout)
    View mLayRoot;
    @BindView(R.id.email_txt)
    EditText mTxtEmail;
    @BindView(R.id.phone_txt)
    EditText mTxtPhone;
    @BindView(R.id.next_txt)
    public TextView mTxtNext;

    private ExpSignUpActivity mActivity;
    private ExpertProfilePOJO draftExp;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ExpSignUpActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag1_exp_signup, container, false);
        ButterKnife.bind(this, view);

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideKeyboard();
            }
        });

        mTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) return;

                mActivity.mViewpager.setCurrentItem(1);
                mActivity.curPage = 1;
            }
        });

        draftExp = mActivity.getDraftExp();
        if (mActivity.user.accountNumber.contains("@"))
            mTxtEmail.setText(mActivity.user.accountNumber);
        else if (mActivity.user.accountNumber.matches(("[0-9]+")))
            mTxtPhone.setText(PhoneNumberUtils.formatNumber(mActivity.user.accountNumber));

        mActivity.setCallback(new ExpSignUpActivity.Callback() {
            @Override
            public void updateLI() {
                draftExp = mActivity.getDraftExp();
                if (!StringUtils.isEmpty(draftExp.contactEmail))
                    mTxtEmail.setText(draftExp.contactEmail);
                if (!StringUtils.isEmpty(draftExp.contactNumber))
                    mTxtPhone.setText(PhoneNumberUtils.formatNumber(draftExp.contactNumber));
            }
        });

        mTxtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        return view;
    }

    private boolean validate() {
        draftExp.contactEmail = mTxtEmail.getText().toString();
        if (StringUtils.isEmpty(draftExp.contactEmail) || !Utility.isValidEmail(draftExp.contactEmail)) {
            mTxtEmail.setError(getString(R.string.invalid_email_id));
            return false;
        } else {
            mTxtEmail.setError(null);
        }

        draftExp.contactNumber = mTxtPhone.getText().toString().replaceAll("[^0-9]", "");
        if (StringUtils.isEmpty(draftExp.contactNumber) || !Utility.isValidPhone(draftExp.contactNumber)) {
            mTxtPhone.setError(getString(R.string.invalid_phone_number));
            return false;
        } else {
            mTxtPhone.setError(null);
        }
        return true;
    }
}
