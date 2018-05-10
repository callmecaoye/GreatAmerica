package com.promeets.android.activity;

import com.promeets.android.adapter.RecycleReviewAdapter;
import com.promeets.android.api.ServiceApi;
import com.promeets.android.api.URL;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.EventOptionFragment;
import com.promeets.android.fragment.EventThxFragment;
import android.graphics.Color;
import android.net.Uri;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.ServiceEvent;
import com.promeets.android.object.UserPOJO;
import android.os.Bundle;
import com.promeets.android.pojo.ActiveEventResp;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.MixpanelUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for showing event details including upcoming and past events
 *
 * Recap layout is just for past events
 *
 * For leaving a review, user will show an input layout and guest will go to EventReviewActivity
 *
 * @source: EventFragment
 *
 */
public class EventDetailActivity extends BaseActivity
        implements View.OnClickListener {

    @BindView(R.id.book)
    TextView mBtnBook;

    @BindView(R.id.image_view)
    ImageView mImgView;

    @BindView(R.id.title)
    TextView mTxtTitle;

    @BindView(R.id.time)
    TextView mTxtTime;

    @BindView(R.id.location)
    TextView mTxtLocation;

    @BindView(R.id.share)
    ImageView mImgShare;

    // Description
    @BindView(R.id.description)
    WebView mWebDescript;
    @BindView(R.id.more_descript)
    TextView mTxtMoreDescript;

    // Recap
    @BindView(R.id.recap_title)
    TextView mTitleRecap;
    @BindView(R.id.more_recap)
    TextView mTxtMoreRecap;
    @BindView(R.id.recap)
    WebView mWebRecap;
    @BindView(R.id.recap_layer)
    CardView mLayRecap;

    // Going
    @BindView(R.id.going_lay)
    View mLayGoing;
    @BindView(R.id.going_txt)
    TextView mTxtGoing;
    @BindView(R.id.yes)
    ImageView mImgYes;
    @BindView(R.id.no)
    ImageView mImgNo;

    // Review
    @BindView(R.id.review_title)
    TextView mTitleReview;
    @BindView(R.id.review_list)
    RecyclerView mRVReview;
    @BindView(R.id.view_more)
    TextView mTxtMoreReview;
    @BindView(R.id.review_lay)
    CardView mLayReview;
    @BindView(R.id.start_review)
    TextView mTxtStartReview;
    @BindView(R.id.start_review_divider)
    View mDivStartReview;

    private ServiceEvent event;

    private ViewGroup.LayoutParams params;

    private boolean isDescriptExpand = false;

    private boolean isRecapExpand = false;

    private String photoPath;

    private UserPOJO mUserPOJO;

    private int userId;

    private RecycleReviewAdapter reviewAdapter;

    private int mWebDescriptHeight = -1;
    private int mWebRecapHeight = -1;

    String curFName, curLName;
    String eventId;
    String que1;



    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mBtnBook.setOnClickListener(this);
        mTxtMoreDescript.setOnClickListener(this);
        mTxtMoreRecap.setOnClickListener(this);
        mImgShare.setOnClickListener(this);
        mTxtLocation.setOnClickListener(this);
        mTxtMoreReview.setOnClickListener(this);
        mTxtStartReview.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        mUserPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (mUserPOJO == null) userId = 0;
        else userId = mUserPOJO.id;

        eventId = getIntent().getStringExtra("eventId");
        getEventDetails();

        mWebDescript.setScrollbarFadingEnabled(true);
        mWebRecap.setScrollbarFadingEnabled(true);
        params = mWebDescript.getLayoutParams();
        mImgShare.setColorFilter(Color.rgb(30, 30, 30));

        photoPath = getIntent().getStringExtra("photoPath");
        if (!StringUtils.isEmpty(photoPath)) {
            Glide.with(this).load(photoPath).into(mImgView);
        }
    }

    private void getEventDetails() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("activeEvent/displayDetail"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        Call<ActiveEventResp> call = service.displayEventDetail(eventId, userId, Utility.getDeviceId());
        call.enqueue(new Callback<ActiveEventResp>() {
            @Override
            public void onResponse(Call<ActiveEventResp> call, Response<ActiveEventResp> response) {
                final ActiveEventResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(EventDetailActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    event = result.eventData;

                    if (StringUtils.isEmpty(photoPath))
                        Glide.with(EventDetailActivity.this).load(event.photoUrl).into(mImgView);

                    mTxtTitle.setText(event.title);
                    mTxtTime.setText(ScreenUtil.convertUnitTime("MMM dd yyyy, hh:mm aa", event.beginTime));
                    mTxtLocation.setText(event.location);
                    String tmp = "<div class='content'> <style> *{font-family:'OpenSans' !important; src: url(\"file:///android_asset/fonts/\")}"
                            + "body {line-height:20px; color:#4A4A4A; font-family:'OpenSans'; font-size: medium;} </style>"
                            + "<body style='margin:0; padding:0'>" + event.description + "</body> </div>";
                    mWebDescript.loadDataWithBaseURL("file:///android_asset", tmp, "text/html", "UTF-8", null);

                    if (event.status.equals("pastEvent")) {
                        mBtnBook.setVisibility(View.GONE);
                        mTitleReview.setVisibility(View.VISIBLE);
                        mLayReview.setVisibility(View.VISIBLE);
                        if (!StringUtils.isEmpty(event.recap)) {
                            mTitleRecap.setVisibility(View.VISIBLE);
                            mLayRecap.setVisibility(View.VISIBLE);
                            mTxtMoreRecap.setVisibility(View.VISIBLE);
                            String tmp1 = "<div class='content'> <style> *{font-family:'OpenSans' !important; src: url(\"file:///android_asset/fonts/\")}"
                                    + "body {line-height:20px; color:#4A4A4A; font-family:'OpenSans'; font-size: medium;} </style>"
                                    + "<body style='margin:0; padding:0'>" + event.recap + "</body> </div>";
                            mWebRecap.loadDataWithBaseURL("file:///android_asset", tmp1, "text/html", "UTF-8", null);
                            mWebRecap.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_UP)
                                        toggleRecap();
                                    return false;
                                }
                            });
                        }

                        if (event.description.length() > 200) {
                            params.height = ScreenUtil.convertDpToPx(150, EventDetailActivity.this);
                            mWebDescript.setLayoutParams(params);
                            mTxtMoreDescript.setVisibility(View.VISIBLE);
                            mWebDescript.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_UP)
                                        toggleDescript();
                                    return false;
                                }
                            });
                        }

                        if (result.reviewListSize > 0) {
                            mRVReview.setLayoutManager(new LinearLayoutManager(EventDetailActivity.this));
                            mRVReview.setNestedScrollingEnabled(false);
                            reviewAdapter = new RecycleReviewAdapter(EventDetailActivity.this, result.reviewList, false);
                            mRVReview.setAdapter(reviewAdapter);
                            if (result.reviewListSize > 3) {
                                mTxtMoreReview.setVisibility(View.VISIBLE);
                                mTxtMoreReview.setText("Read all " + result.reviewListSize + " reviews");
                            }
                        } else {
                            mDivStartReview.setVisibility(View.GONE);
                        }

                        if (result.isReviewed == 1) {
                            mDivStartReview.setVisibility(View.GONE);
                            mTxtStartReview.setVisibility(View.GONE);
                        }
                        else {
                            if (userId == 0) {
                                if (result.firstName != null) curFName = result.firstName;
                                if (result.lastName != null) curLName = result.lastName;
                            }
                        }
                    } else if (event.status.equalsIgnoreCase("commingSoon")) {
                        mLayGoing.setVisibility(View.VISIBLE);
                        setGoingState(result.goingFlag);
                        que1 = result.question1;
                    }
                } else
                    PromeetsDialog.show(EventDetailActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<ActiveEventResp> call, Throwable t) {

            }
        });
    }

    public void submitNo() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("activeEvent/updateGoing"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        JSONObject json = new JSONObject();
        try {
            json.put("eventId", event.id);
            json.put("goingFlag", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Call<ActiveEventResp> call = service.updateGoing(requestBody);
        call.enqueue(new Callback<ActiveEventResp>() {
            @Override
            public void onResponse(Call<ActiveEventResp> call, Response<ActiveEventResp> response) {
                final ActiveEventResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(EventDetailActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    setGoingState(result.goingFlag);
                } else
                    PromeetsDialog.show(EventDetailActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<ActiveEventResp> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book:
                HashMap<String, String> map = new HashMap<>();
                map.put("eventId", event.id);
                MixpanelUtil.getInstance(this).trackEvent("Event page -> Ticket", map);
                if (!StringUtils.isEmpty(event.eventbriteUrl)) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri content_url = Uri.parse(event.eventbriteUrl);
                    intent.setData(content_url);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                break;
            case R.id.share:
                if (StringUtils.isEmpty(event.eventbriteUrl)) return;
                // get available share apps
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                share_intent.putExtra(Intent.EXTRA_TEXT, event.eventbriteUrl);
                startActivity(Intent.createChooser(share_intent, "Share event using"));
                break;
            case R.id.location:
                Intent placeIntent = new Intent(this, MapActivity.class);
                ArrayList<EventLocationPOJO> list = new ArrayList<>();
                EventLocationPOJO loc = new EventLocationPOJO();
                loc.latitude = event.latitude;
                loc.longitude = event.longitude;
                loc.location = event.location;
                loc.isSelected = true;
                list.add(loc);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                placeIntent.putExtras(bundle);

                startActivity(placeIntent);
                break;
            case R.id.more_descript:
                HashMap<String, String> aMap = new HashMap<>();
                aMap.put("eventId", event.id);
                MixpanelUtil.getInstance(this).trackEvent("Event page -> Description", aMap);
                toggleDescript();
                break;
            case R.id.more_recap:
                toggleRecap();
                break;
            case R.id.view_more:
                Intent intent = new Intent(EventDetailActivity.this, EventReviewListActivity.class);
                intent.putExtra("eventId", event.id);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.start_review:
                    intent = new Intent(this, EventReviewActivity.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("userId", userId);
                    if (userId == 0) { // GUEST
                        intent.putExtra("firstName", curFName);
                        intent.putExtra("lastName", curLName);
                    }
                    startActivityForResult(intent, 100);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100)
                getEventDetails();
            else if (requestCode == 200) {
                int flag = getIntent().getIntExtra("state", 1);
                setGoingState(flag);

                if (flag == 1) {
                    EventThxFragment dialogFragment = EventThxFragment.newInstance();
                    dialogFragment.show(getFragmentManager(), "event thank");
                    dialogFragment.setCancelable(true);
                }
            }
        }
    }

    private void toggleDescript() {
        if (isDescriptExpand) {
            PromeetsUtils.collapse(mWebDescript, 350, ScreenUtil.convertDpToPx(150, this));
            mTxtMoreDescript.setText("See More");
        } else {
            if (mWebDescriptHeight == -1) {
                mWebDescript.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mWebDescriptHeight = mWebDescript.getMeasuredHeight();
            }
            mTxtMoreDescript.setText("See Less");
            PromeetsUtils.expand(mWebDescript, 350, mWebDescriptHeight);
        }
        isDescriptExpand = !isDescriptExpand;
    }

    private void toggleRecap() {
        if (isRecapExpand) {
            PromeetsUtils.collapse(mWebRecap, 350, ScreenUtil.convertDpToPx(150, this));
            mTxtMoreRecap.setText("See More");
        } else {
            if (mWebRecapHeight == -1) {
                mWebRecap.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mWebRecapHeight = mWebRecap.getMeasuredHeight();
            }
            mTxtMoreRecap.setText("See Less");
            PromeetsUtils.expand(mWebRecap, 350, mWebRecapHeight);
        }
        isRecapExpand = !isRecapExpand;
    }

    /**
     * 0 not click the button
     * 1 going
     * 2 not going
     *
     * @param state
     */
    public void setGoingState(int state) {
        switch (state) {
            case 0:
                mTxtGoing.setVisibility(View.GONE);
                mImgYes.setVisibility(View.VISIBLE);
                mImgYes.setImageResource(R.drawable.yes_bolder);
                mImgNo.setVisibility(View.VISIBLE);
                mImgNo.setImageResource(R.drawable.no_bolder);

                mImgYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //setGoingState(1);
                        preSubmitYes();
                    }
                });
                mImgNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitNo();
                    }
                });
                break;
            case 1:
                mTxtGoing.setVisibility(View.VISIBLE);
                mTxtGoing.setText("I'm going");
                mTxtGoing.setTextColor(getResources().getColor(R.color.material_green));
                mImgYes.setVisibility(View.VISIBLE);
                mImgYes.setImageResource(R.drawable.yes_green);
                mImgNo.setVisibility(View.GONE);
                mTxtGoing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showOption(1);
                    }
                });
                mImgYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showOption(1);
                    }
                });


                break;
            case 2:
                mTxtGoing.setVisibility(View.VISIBLE);
                mTxtGoing.setText("Can't go");
                mTxtGoing.setTextColor(getResources().getColor(R.color.primary));
                mImgYes.setVisibility(View.GONE);
                mImgNo.setVisibility(View.VISIBLE);
                mImgNo.setImageResource(R.drawable.no_solid);
                mTxtGoing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showOption(2);
                    }
                });
                mImgNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showOption(2);
                    }
                });
                break;
        }
    }

    public void preSubmitYes() {
        Intent intent = new Intent(this, EventQuesActivity.class);
        intent.putExtra("eventId", event.id);
        intent.putExtra("q1", que1);
        startActivityForResult(intent, 200);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void showOption(int curState) {
        EventOptionFragment dialogFragment = EventOptionFragment.newInstance(curState);
        dialogFragment.show(getFragmentManager(), "event option");
        dialogFragment.setCancelable(true);
    }
}
