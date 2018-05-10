package com.promeets.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.activity.ExpSignUpActivity;
import com.promeets.android.activity.WeekViewActivity;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sosasang on 1/26/18.
 */

public class ExpSignupFrag4 extends Fragment {

    static final int WEEKVIEW_REQUEST_CODE = 101;

    @BindView(R.id.next_txt)
    public TextView mTxtNext;
    @BindView(R.id.book_lay)
    LinearLayout mLayBook;
    @BindView(R.id.call_txt)
    TextView mTxtCall;
    @BindView(R.id.call_lay)
    LinearLayout mLayCall;

    private ExpSignUpActivity mActivity;
    private ExpertProfilePOJO draftExp;
    private String mStrEvent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ExpSignUpActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag4_exp_signup, container, false);
        ButterKnife.bind(this, view);

        mTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.mViewpager.setCurrentItem(4);
                mActivity.curPage = 4;
            }
        });

        mLayBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, WeekViewActivity.class);
                intent.putExtra("what", 1);
                startActivityForResult(intent, WEEKVIEW_REQUEST_CODE);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        mTxtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, WeekViewActivity.class);
                intent.putExtra("eventToCalendar", mStrEvent);
                intent.putExtra("what", 1);
                startActivityForResult(intent, WEEKVIEW_REQUEST_CODE);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WEEKVIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            Gson gson = new Gson();
            mStrEvent = data.getStringExtra("eventFromCalendar");
            if (StringUtils.isEmpty(mStrEvent)) {
                mLayBook.setVisibility(View.VISIBLE);
                mLayCall.setVisibility(View.GONE);
                return;
            }

            WeekViewEvent mEvent = gson.fromJson(mStrEvent, WeekViewEvent.class);
            draftExp.callTime = mEvent.getStartTime().getTimeInMillis() / 1000;

            SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy hh:mm aa", Locale.US);
            String fromTime = timeFormat.format(mEvent.getStartTime().getTime());
            mLayBook.setVisibility(View.GONE);
            mLayCall.setVisibility(View.VISIBLE);
            mTxtCall.setText(fromTime);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            draftExp = mActivity.getDraftExp();

            if (draftExp.callTime > 0) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy hh:mm aa", Locale.US);
                String fromTime = timeFormat.format(draftExp.callTime * 1000);
                mLayBook.setVisibility(View.GONE);
                mLayCall.setVisibility(View.VISIBLE);
                mTxtCall.setText(fromTime);
            }
        }
    }
}
