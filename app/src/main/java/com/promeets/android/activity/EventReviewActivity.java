package com.promeets.android.activity;

import com.promeets.android.api.ServiceApi;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.api.URL;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.R;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import com.willy.ratingbar.ScaleRatingBar;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for leaving review for event when no user logged in
 *
 * Pass device Id to server to record
 *
 * @source: EventDetailActivity
 *
 */
public class EventReviewActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.guest_layer)
    NestedScrollView mLayGuestInput;
    @BindView(R.id.title_fname)
    TextView mTitleFName;
    @BindView(R.id.title_lname)
    TextView mTitleLName;
    @BindView(R.id.first_name)
    EditText mGuestFirstName;
    @BindView(R.id.last_name)
    EditText mGuestLastName;
    @BindView(R.id.rating_bar)
    ScaleRatingBar mRatingBar;
    @BindView(R.id.guest_input)
    EditText mGuestInput;
    @BindView(R.id.guest_send)
    TextView mGuestSend;
    @BindView(R.id.guest_root)
    LinearLayout mRootGuest;

    private String curFName, curLName;
    private String eventId;

    private int userId;
    private int mStatus;
    private final int GUEST = 0;
    private final int USER = 1;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mGuestSend.setOnClickListener(this);
        mRootGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });
        mGuestInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGuestInput.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_review);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);

        eventId = getIntent().getStringExtra("eventId");
        userId = getIntent().getIntExtra("userId", 0);
        if (userId == 0) {
            mStatus = GUEST;
            curFName = getIntent().getStringExtra("firstName");
            curLName = getIntent().getStringExtra("lastName");
            if (!StringUtils.isEmpty(curFName)) mGuestFirstName.setText(curFName);
            if (!StringUtils.isEmpty(curLName)) mGuestLastName.setText(curLName);
        } else {
            mStatus = USER;
            mTitleFName.setVisibility(View.GONE);
            mGuestFirstName.setVisibility(View.GONE);
            mTitleLName.setVisibility(View.GONE);
            mGuestLastName.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guest_send:
                if (mStatus == GUEST) {
                    if (mGuestFirstName.getText().toString().trim().length() > 0
                            && mGuestLastName.getText().toString().trim().length() > 0
                            && mGuestInput.getText().toString().trim().length() > 0) {
                        submitEventReview(mGuestFirstName.getText().toString(), mGuestLastName.getText().toString(), mRatingBar.getRating(), mGuestInput.getText().toString());
                    } else
                        PromeetsDialog.show(this, "Please fill in all fields");
                } else if (mStatus == USER) {
                    if (mGuestInput.getText().toString().trim().length() > 0) {
                        submitEventReview(null, null, mRatingBar.getRating(), mGuestInput.getText().toString());
                    } else
                        PromeetsDialog.show(this, "Please fill in Review Text");
                }
                break;
        }
    }

    private void submitEventReview(String firstName, String lastName,float rating, String content) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("review/createReviewEvent"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        Call<BaseResp> call = service.createReviewEvent(firstName, lastName, Utility.getDeviceId(), eventId, rating, content);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(EventReviewActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    /*mLayUserInput.setVisibility(View.GONE);
                    //mLayGuestInput.setVisibility(View.GONE);
                    imm.hideSoftInputFromWindow(mUserInput.getWindowToken(), 0);
                    getEventDetails(event.id);*/
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else
                    PromeetsDialog.show(EventReviewActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(EventReviewActivity.this, t.getLocalizedMessage());
            }
        });
    }
}
