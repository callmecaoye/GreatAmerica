package com.promeets.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.promeets.android.activity.ExpSignUpActivity;
import com.promeets.android.custom.EditTextWithPrefix;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sosasang on 1/26/18.
 */

public class ExpSignupFrag2 extends Fragment {
    @BindView(R.id.title_txt)
    EditText mTxtTitle;
    @BindView(R.id.txt_about)
    EditText mTxtAbout;
    @BindView(R.id.rate_txt)
    EditTextWithPrefix mTxtRate;
    @BindView(R.id.next_txt)
    public TextView mTxtNext;
    @BindView(R.id.root_layout)
    View mLayRoot;

    @BindView(R.id.title_img)
    ImageView mImgTitle;
    @BindView(R.id.bio_img)
    ImageView mImgBio;

    @BindView(R.id.demo_bio_lay)
    View mLayDemoBio;
    @BindView(R.id.demo_title_lay)
    View mLayDemoTitle;

    private ExpSignUpActivity mActivity;
    private ExpertProfilePOJO draftExp;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ExpSignUpActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag2_exp_signup, container, false);
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

                mActivity.mViewpager.setCurrentItem(2);
                mActivity.curPage = 2;
            }
        });

        mTxtAbout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTxtAbout.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mImgTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideKeyboard();
                mLayDemoTitle.setVisibility(View.VISIBLE);
            }
        });
        mImgBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideKeyboard();
                mLayDemoBio.setVisibility(View.VISIBLE);
            }
        });
        mLayDemoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayDemoTitle.setVisibility(View.GONE);
            }
        });
        mLayDemoBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayDemoBio.setVisibility(View.GONE);
            }
        });

        return view;
    }

    /**
     * previous fill-in info
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            draftExp = mActivity.getDraftExp();
            if (!StringUtils.isEmpty(draftExp.positon))
                mTxtTitle.setText(draftExp.positon);
            if (!StringUtils.isEmpty(draftExp.description))
                mTxtAbout.setText(draftExp.description);
            if (!StringUtils.isEmpty(draftExp.hourlyRate))
                mTxtRate.setText(draftExp.hourlyRate);
        }
    }

    private boolean validate() {
        draftExp.positon = mTxtTitle.getText().toString();
        draftExp.description = mTxtAbout.getText().toString();
        draftExp.hourlyRate = mTxtRate.getText().toString();

        if (StringUtils.isEmpty(draftExp.positon)) {
            mTxtTitle.setError("This field is required");
            return false;
        } else {
            mTxtTitle.setError(null);
        }
        return true;
    }
}
