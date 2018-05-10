package com.promeets.android.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.promeets.android.api.EventApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.Gson;

import com.promeets.android.Constant;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.SurveyPost;
import com.promeets.android.pojo.SurveyResp;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SurveyActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.step1)
    View mStep1;
    @BindView(R.id.step2)
    View mStep2;
    @BindView(R.id.step3)
    View mStep3;
    @BindView(R.id.step4)
    View mStep4;
    @BindView(R.id.step5)
    View mStep5;
    @BindView(R.id.step6)
    View mStep6;
    @BindView(R.id.step7)
    View mStep7;

    @BindView(R.id.level)
    TextView mTxtLevel;
    @BindView(R.id.next)
    TextView mTxtNext;

    @BindView(R.id.seek_bar_layer)
    LinearLayout mLaySeekBar;
    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;

    @BindView(R.id.yes_no_layer)
    LinearLayout mLayYesNo;
    @BindView(R.id.yes)
    FloatingActionButton mFABYes;
    @BindView(R.id.no)
    FloatingActionButton mFABNo;
    @BindView(R.id.yes_txt)
    TextView mTxtYes;
    @BindView(R.id.no_txt)
    TextView mTxtNo;

    @BindView(R.id.step1_layout)
    LinearLayout mLayStep1;
    @BindView(R.id.step2_layout)
    LinearLayout mLayStep2;
    @BindView(R.id.step3_layout)
    LinearLayout mLayStep3;
    @BindView(R.id.step4_layout)
    LinearLayout mLayStep4;
    @BindView(R.id.step5_layout)
    LinearLayout mLayStep5;
    @BindView(R.id.step6_layout)
    LinearLayout mLayStep6;
    @BindView(R.id.step7_layout)
    LinearLayout mLayStep7;

    @BindView(R.id.txt_q1)
    TextView mTxtQ1;
    @BindView(R.id.txt_q2)
    TextView mTxtQ2;
    @BindView(R.id.txt_q3)
    TextView mTxtQ3;
    @BindView(R.id.txt_q4)
    TextView mTxtQ4;
    @BindView(R.id.txt_q5)
    TextView mTxtQ5;
    @BindView(R.id.txt_q6)
    TextView mTxtQ6;
    @BindView(R.id.q6_title)
    TextView mTitleQ6;
    @BindView(R.id.q7_title)
    TextView mTitleQ7;

    @BindView(R.id.welcome_page)
    View mLayWelcome;
    @BindView(R.id.background)
    ImageView mImgView;
    @BindView(R.id.start)
    TextView mTxtStart;
    @BindView(R.id.steps)
    View mLaySteps;
    @BindView(R.id.content_page)
    View mLayContent;
    @BindView(R.id.thank_page)
    LinearLayout mLayThank;






    /**
     * FAB value
     *
     * 0 : no selection
     * 2 : NO
     * 1 : YES
     */
    private int mSelect = 0;

    /**
     * SeekBar value: 0 - 4
     */
    private int mValue = 3;

    /**
     * role
     *
     * 0: user
     * 1: expert
     */
    private int role = 1;

    /**
     * page number
     *
     * step 0: welcome page
     * step 100: thank page
     */
    private int curStep = 0;

    private Animation leftIn;
    private Animation leftOut;
    private Animation rightIn;
    private Animation rightOut;

    private SparseArray<Integer> mResult;

    private String name;
    private int orderId;
    private String msgId;

    @Override
    public void initElement() {
        leftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        leftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
        rightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        rightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out);

        mResult = new SparseArray<>();

        orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) {
            finish();
            HomeActivity.currentTabIndex = 3;
            startActivity(HomeActivity.class);
        }
        else
            fetchSurvey();

        int width = getWidth();
        mImgView.getLayoutParams().height = width;
        mImgView.requestLayout();
    }

    @Override
    public void registerListeners() {
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setOnTouchListener(new SeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle SeekBar touch events.
                view.onTouchEvent(motionEvent);
                return true;
            }
        });

        mFABYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtNext.setTextColor(getResources().getColor(R.color.primary));
                mTxtNext.setEnabled(true);

                if (mSelect != 1) {
                    mFABYes.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
                    mTxtYes.setTextColor(getResources().getColor(R.color.white));
                    mFABYes.setCompatElevation(0);

                    if (mSelect == 2) {
                        mFABNo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        mTxtNo.setTextColor(getResources().getColor(R.color.primary));
                        mFABNo.setCompatElevation(ScreenUtil.convertDpToPx(5, SurveyActivity.this));
                    }

                    mSelect = 1;
                }
            }
        });

        mFABNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtNext.setTextColor(getResources().getColor(R.color.primary));
                mTxtNext.setEnabled(true);

                if (mSelect != 2) {
                    mFABNo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
                    mTxtNo.setTextColor(getResources().getColor(R.color.white));
                    mFABNo.setCompatElevation(0);

                    if (mSelect == 1) {
                        mFABYes.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        mTxtYes.setTextColor(getResources().getColor(R.color.primary));
                        mFABYes.setCompatElevation(ScreenUtil.convertDpToPx(5, SurveyActivity.this));
                    }

                    mSelect = 2;
                }
            }
        });

        mTxtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curStep = 1;
                mLayWelcome.setVisibility(View.GONE);
                mLayWelcome.startAnimation(leftOut);
                mLaySteps.setVisibility(View.VISIBLE);
                mLaySteps.startAnimation(leftIn);
                mTxtNext.setVisibility(View.VISIBLE);
                mTxtNext.startAnimation(leftIn);
                mLayContent.setVisibility(View.VISIBLE);
                mLayContent.startAnimation(leftIn);

                if (mResult.indexOfKey(1) < 0)
                    initSeekBar();
                else
                    mSeekBar.setProgress(mResult.get(1));
            }
        });

        mTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (curStep) {
                    case 1:
                        mResult.put(1, mValue);
                        curStep = 2;
                        mStep2.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        mTxtQ2.setText("Did " + name + " arrive on time?");

                        mLayStep1.setVisibility(View.GONE);
                        mLayStep1.startAnimation(leftOut);
                        mLaySeekBar.setVisibility(View.GONE);
                        mLaySeekBar.startAnimation(leftOut);
                        mLayStep2.setVisibility(View.VISIBLE);
                        mLayStep2.startAnimation(leftIn);
                        mLayYesNo.setVisibility(View.VISIBLE);
                        mLayYesNo.startAnimation(leftIn);

                        if (mResult.indexOfKey(2) < 0) {
                            mTxtNext.setTextColor(getResources().getColor(R.color.pm_gray));
                            mTxtNext.setEnabled(false);
                        }
                        break;
                    case 2:
                        mResult.put(2, mSelect);
                        curStep = 3;
                        mStep3.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        mTxtQ3.setText("Did " + name + " come prepared?");

                        mLayStep2.setVisibility(View.GONE);
                        mLayStep2.startAnimation(leftOut);
                        mLayYesNo.setVisibility(View.GONE);
                        mLayYesNo.startAnimation(leftOut);
                        mLayStep3.setVisibility(View.VISIBLE);
                        mLayStep3.startAnimation(leftIn);
                        mLaySeekBar.setVisibility(View.VISIBLE);
                        mLaySeekBar.startAnimation(leftIn);

                        if (mResult.indexOfKey(3) < 0)
                            initSeekBar();
                        else
                            mSeekBar.setProgress(mResult.get(3));
                        break;
                    case 3:
                        mResult.put(3, mValue);
                        curStep = 4;
                        mStep4.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        mTxtQ4.setText("Was " + name + " easy to communicate with?");

                        mLayStep3.setVisibility(View.GONE);
                        mLayStep3.startAnimation(leftOut);
                        mLayStep4.setVisibility(View.VISIBLE);
                        mLayStep4.startAnimation(leftIn);

                        if (mResult.indexOfKey(4) < 0)
                            initSeekBar();
                        else
                            mSeekBar.setProgress(mResult.get(4));
                        break;
                    case 4:
                        mResult.put(4, mValue);
                        mLayStep4.setVisibility(View.GONE);
                        mLayStep4.startAnimation(leftOut);

                        /*if (role == 0) {
                            curStep = 6;
                            mStep6.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                            mLayStep6.setVisibility(View.VISIBLE);
                            mLayStep6.startAnimation(leftIn);
                            mTitleQ6.setText("Question 5");
                            mTxtQ6.setText("Did " + name + " give helpful feedback during the meeting?");

                            if (mResult.indexOfKey(6) < 0)
                                initSeekBar();
                            else
                                mSeekBar.setProgress(mResult.get(6));
                        } else {*/
                            curStep = 5;
                            mStep5.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                            mLayStep5.setVisibility(View.VISIBLE);
                            mLayStep5.startAnimation(leftIn);
                            mTxtQ5.setText("How likely would you refer " + name +" to an investor?");

                            if (mResult.indexOfKey(5) < 0)
                                initSeekBar();
                            else
                                mSeekBar.setProgress(mResult.get(5));
                        //}
                        break;
                    case 5:
                        mResult.put(5, mValue);
                        curStep = 6;
                        mStep6.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        mLayStep5.setVisibility(View.GONE);
                        mLayStep5.startAnimation(leftOut);
                        mLayStep6.setVisibility(View.VISIBLE);
                        mLayStep6.startAnimation(leftIn);
                        mTxtQ6.setText("Did " + name + " give helpful feedback during the meeting?");

                        if (mResult.indexOfKey(6) < 0)
                            initSeekBar();
                        else
                            mSeekBar.setProgress(mResult.get(6));
                        break;
                    case 6:
                        mResult.put(6, mValue);
                        curStep = 7;
                        mStep7.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        mLayStep6.setVisibility(View.GONE);
                        mLayStep6.startAnimation(leftOut);
                        mLayStep7.setVisibility(View.VISIBLE);
                        mLayStep7.startAnimation(leftIn);
                        mTxtNext.setText("Submit");

                        //if (role == 0)
                        //    mTitleQ7.setText("Question 6");

                        initSeekBar();
                        break;
                    case 7:
                        mResult.put(7, mValue);
                        curStep = 100;
                        submitSurvey();
                        break;
                    case 100:
                        finish();
                        HomeActivity.currentTabIndex = 3;
                        startActivity(HomeActivity.class);
                        break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        /**
         * i: index, value 0 - 4
         */
        switch (i) {
            case 0:
                mTxtLevel.setText("Poor");
                mValue = i;
                break;
            case 1:
                mTxtLevel.setText("Fair");
                mValue = i;
                break;
            case 2:
                mTxtLevel.setText("Good");
                mValue = i;
                break;
            case 3:
                mTxtLevel.setText("Very Good");
                mValue = i;
                break;
            case 4:
                mTxtLevel.setText("Excellent");
                mValue = i;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        switch (curStep) {
            case 0:
                finish();
                HomeActivity.currentTabIndex = 3;
                startActivity(HomeActivity.class);
                break;
            case 1:
                curStep = 0;
                mLayWelcome.setVisibility(View.VISIBLE);
                mLayWelcome.startAnimation(rightIn);
                mLaySteps.setVisibility(View.GONE);
                mLaySteps.startAnimation(rightOut);
                mTxtNext.setVisibility(View.GONE);
                mTxtNext.startAnimation(rightOut);
                mLayContent.setVisibility(View.GONE);
                mLayContent.startAnimation(rightOut);
                break;
            case 2:
                curStep = 1;
                mStep2.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));

                mLayStep1.setVisibility(View.VISIBLE);
                mLayStep1.startAnimation(rightIn);
                mLaySeekBar.setVisibility(View.VISIBLE);
                mLaySeekBar.startAnimation(rightIn);
                mLayStep2.setVisibility(View.GONE);
                mLayStep2.startAnimation(rightOut);
                mLayYesNo.setVisibility(View.GONE);
                mLayYesNo.startAnimation(rightOut);

                mSeekBar.setProgress(mResult.get(1));
                mTxtNext.setTextColor(getResources().getColor(R.color.primary));
                mTxtNext.setEnabled(true);
                break;
            case 3:
                curStep = 2;
                mStep3.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));

                mLayStep3.setVisibility(View.GONE);
                mLayStep3.startAnimation(rightOut);
                mLaySeekBar.setVisibility(View.GONE);
                mLaySeekBar.startAnimation(rightOut);
                mLayStep2.setVisibility(View.VISIBLE);
                mLayStep2.startAnimation(rightIn);
                mLayYesNo.setVisibility(View.VISIBLE);
                mLayYesNo.startAnimation(rightIn);

                if (mSelect == 1)
                    mFABYes.callOnClick();
                else if (mSelect == 2)
                    mFABNo.callOnClick();
                break;
            case 4:
                curStep = 3;
                mStep4.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                mLayStep4.setVisibility(View.GONE);
                mLayStep4.startAnimation(rightOut);
                mLayStep3.setVisibility(View.VISIBLE);
                mLayStep3.startAnimation(rightIn);

                mSeekBar.setProgress(mResult.get(3));
                break;
            case 5:
                curStep = 4;
                mStep5.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                mLayStep5.setVisibility(View.GONE);
                mLayStep5.startAnimation(rightOut);
                mLayStep4.setVisibility(View.VISIBLE);
                mLayStep4.startAnimation(rightIn);

                mSeekBar.setProgress(mResult.get(4));
                break;
            case 6:
                mLayStep6.setVisibility(View.GONE);
                mLayStep6.startAnimation(rightOut);
                mStep6.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                /*if (role == 0) {
                    curStep = 4;
                    mLayStep4.setVisibility(View.VISIBLE);
                    mLayStep4.startAnimation(rightIn);

                    mSeekBar.setProgress(mResult.get(4));
                } else {*/
                    curStep = 5;
                    mLayStep5.setVisibility(View.VISIBLE);
                    mLayStep5.startAnimation(rightIn);

                    mSeekBar.setProgress(mResult.get(5));
                //}
                break;
            case 7:
                curStep = 6;
                mStep7.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                mLayStep7.setVisibility(View.GONE);
                mLayStep7.startAnimation(rightOut);
                mLayStep6.setVisibility(View.VISIBLE);
                mLayStep6.startAnimation(rightIn);
                mTxtNext.setText("Continue");

                mSeekBar.setProgress(mResult.get(6));
                break;
            case 100:
                finish();
                HomeActivity.currentTabIndex = 3;
                startActivity(HomeActivity.class);
                break;
        }
    }

    private void initSeekBar() {
        mValue = 3;
        mSeekBar.setProgress(3);
    }

    private void fetchSurvey() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/doSurvey"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventApi service = retrofit.create(EventApi.class);
        Call<SurveyResp> call = service.fetchSurvey(orderId);

        call.enqueue(new Callback<SurveyResp>() {
            @Override
            public void onResponse(Call<SurveyResp> call, Response<SurveyResp> response) {
                PromeetsDialog.hideProgress();
                final SurveyResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(SurveyActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    name = result.fullName;
                    mTxtQ1.setText("How would you rate the meeting with "+ name +"?");

                    /*if (result.isExpert == 0) {
                        role = 0;
                        mStep5.setVisibility(View.GONE);
                    } else role = 1;*/
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP)|| result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(SurveyActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(SurveyActivity.this, result.info.description, new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            finish();
                            HomeActivity.currentTabIndex = 3;
                            startActivity(HomeActivity.class);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SurveyResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(SurveyActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void submitSurvey() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        ArrayList<Integer> scores = new ArrayList<>();
        for (int i = 0; i < mResult.size(); i++) {
            int key = mResult.keyAt(i);
            scores.add(mResult.get(key) + 1);
        }
        SurveyPost post = new SurveyPost();
        post.orderId = orderId;
        post.scores = scores;

        Gson gson = new Gson();
        String json = gson.toJson(post);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/saveSurvey"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        EventApi service = retrofit.create(EventApi.class);
        Call<BaseResp> call = service.submitSurvey(requestBody);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                final BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(SurveyActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    mLayContent.setVisibility(View.GONE);
                    mLayContent.startAnimation(leftOut);
                    mLayThank.setVisibility(View.VISIBLE);
                    mLayThank.startAnimation(leftIn);
                    mTxtNext.setText("Finish");
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP)|| result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(SurveyActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(SurveyActivity.this, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(SurveyActivity.this, t.getLocalizedMessage());
            }
        });
    }
}
