package com.promeets.android.activity;

import android.content.Intent;
import com.promeets.android.custom.NoScrollListView;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import com.promeets.android.object.EventLocationPOJO;
import android.os.Bundle;
import com.promeets.android.pojo.EventPost;
import com.promeets.android.pojo.EventUpdatePost;
import android.text.Editable;
import android.text.TextWatcher;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.promeets.android.adapter.LocationCheckAdapter2;
import com.promeets.android.adapter.TimeSwipeAdapter;
import com.alamkanak.weekview.WeekViewEvent;
import com.promeets.android.api.EventApi;
import com.promeets.android.api.URL;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.Constant;
import com.promeets.android.object.UserProfilePOJO;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.R;
import com.promeets.android.util.DateUtils;
import com.promeets.android.util.FirebaseUtil;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.UserInfoHelper;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for user requesting expert's topic
 *
 * Including Question & About me, time & location
 *
 * @source: ServiceDetailPopupFragment
 *
 */
public class UserRequestSettingActivity extends BaseActivity
        implements View.OnClickListener, OnMapReadyCallback {

    final int PLACE_PICKER_REQUEST = 1;

    @BindView(R.id.primary_line)
    View primaryLine;
    @BindView(R.id.gray_line)
    View grayLine;
    @BindView(R.id.text1)
    TextView step_text_1;
    @BindView(R.id.circle1)
    View step_circle_1;

    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.root_layout)
    LinearLayout mRootLayout;
    @BindView(R.id.activity_user_request_setting_original_service_price)
    TextView mTxtMainPrice;
    @BindView(R.id.activit_user_request_setting_why)
    EditText why;
    @BindView(R.id.activity_user_request_setting_about_me)
    EditText aboutMe;
    @BindView(R.id.submit_btn)
    Button submit;
    @BindView(R.id.map_front)
    View mapFront;

    @BindView(R.id.user_location_list)
    NoScrollListView locationList;
    @BindView(R.id.expert_location_list)
    NoScrollListView expLocationList;
    @BindView(R.id.add_location0)
    ImageView addLocation0;

    @BindView(R.id.meeting)
    CheckedTextView mChkMeeting;
    @BindView(R.id.call)
    CheckedTextView mChkCall;
    @BindView(R.id.meeting_layer)
    View mLayMeeting;
    @BindView(R.id.call_layer)
    View mLayCall;
    @BindView(R.id.timezone_txt)
    TextView mTxtTimeZone;

    @BindView(R.id.time_list)
    NoScrollListView timeList;
    @BindView(R.id.add_time0)
    ImageView addTime0;
    @BindView(R.id.add_time)
    ImageView addTime;
    @BindView(R.id.time_list_lay)
    LinearLayout mLayTimeList;

    @BindView(R.id.activity_user_request_setting_service_name)
    TextView expertName;
    @BindView(R.id.activity_user_request_setting_service_price)
    TextView servicePrice;
    @BindView(R.id.activity_user_request_setting_service_topic)
    TextView serviceTopic;
    @BindView(R.id.activity_user_request_setting_service_image)
    CircleImageView serviceImage;

    @BindView(R.id.position)
    TextView mTxtPosition;


    boolean isLocationEnabled = false;


    // input
    @BindView(R.id.input_layer)
    RelativeLayout mLayInput;
    @BindView(R.id.input_title)
    TextView mTxtInputTitle;
    @BindView(R.id.input_content)
    EditText mTxtInputContent;
    @BindView(R.id.input_save)
    TextView mBtnInputSave;


    private int inputType = 0; // 0: why  1: about me
    private TimeSwipeAdapter timeListAdapter;
    private LocationCheckAdapter2 locationListAdapter;
    private LocationCheckAdapter2 expLocationListAdapter;
    ArrayList<EventTimePOJO> arrayListTime;
    ArrayList<EventLocationPOJO> userLocationList;

    private String userId;
    private String serviceId;
    private String expId;
    private String ServiceTopic;
    private String ServicePrice;
    private String ServiceMainPrice;
    private String author;
    private String servicePhotoUrl;
    private String position;
    private float duratingTime;
    private int defaultDate;
    private int availabilityType;
    private ArrayList<EventLocationPOJO> defaultLocationList;
    private ArrayList<String> mWVEventStrList;

    private List<Marker> markerList = new ArrayList<>();
    private GoogleMap mMap;
    private HashMap<EventLocationPOJO, Marker> map = new HashMap<>();

    private UserProfilePOJO userProfile;

    private Typeface tf_semi;
    private Typeface tf_regular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request_setting);
        ButterKnife.bind(this);
        tf_semi = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        tf_regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        //AndroidBug5497Workaround.assistActivity(this);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 1.5f);
        primaryLine.setLayoutParams(param);
        param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 4.5f);
        grayLine.setLayoutParams(param);
        step_text_1.setTextColor(getResources().getColor(R.color.primary));
        step_circle_1.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            serviceId = extras.getString("serviceId");
            expId = extras.getString("expId");
            ServiceTopic = extras.getString("topic");
            ServicePrice = extras.getString("price");
            ServiceMainPrice = extras.getString("discount");
            author = extras.getString("author");
            servicePhotoUrl = extras.getString("photo");
            duratingTime = extras.getFloat("duratingTime");
            defaultDate = extras.getInt("defaultDate");
            if (defaultDate < 0) defaultDate *= -1;
            availabilityType = extras.getInt("availabilityType");
            position = extras.getString("position");
            defaultLocationList = (ArrayList<EventLocationPOJO>) getIntent().getSerializableExtra("defaultLocationList");
        }
        if (!StringUtils.isEmpty(ServiceMainPrice)) {
            mTxtMainPrice.setText("$" + NumberFormatUtil.getInstance().getCurrency(ServiceMainPrice));
            mTxtMainPrice.setPaintFlags(mTxtMainPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        servicePrice.setText("$" + ServicePrice);
        serviceTopic.setText(ServiceTopic);
        expertName.setText(author);
        mTxtPosition.setText(position);
        Glide.with(this).load(servicePhotoUrl).into(serviceImage);
        why.setHint("What do you expect to learn from " + author
                + "? Any specific questions? More details will help " + author
                + " prepare for your meeting.");
        userProfile = (UserProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY, UserProfilePOJO.class);
        if (userProfile != null && !StringUtils.isEmpty(userProfile.description))
            aboutMe.setText(userProfile.description);


        arrayListTime = new ArrayList<>();
        userLocationList = new ArrayList<>();
        timeListAdapter = new TimeSwipeAdapter(this, arrayListTime);
        timeList.setAdapter(timeListAdapter);
        locationListAdapter = new LocationCheckAdapter2(this, userLocationList);
        locationList.setAdapter(locationListAdapter);
        for (EventLocationPOJO location : defaultLocationList) {
            location.isSelected = true;
            location.isDefaultLocation = true;
        }
        expLocationListAdapter = new LocationCheckAdapter2(this, defaultLocationList);
        expLocationList.setAdapter(expLocationListAdapter);

        // init TimeZone data
        mTxtTimeZone.setText(DateUtils.displayTimeZone(TimeZone.getDefault()));
    }

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        why.setKeyListener(null);
        aboutMe.setKeyListener(null);
        why.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        why.clearFocus();
                        break;
                }
                return false;
            }
        });
        why.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputType = 0;
                mLayInput.setVisibility(View.VISIBLE);
                mTxtInputContent.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                mTxtInputTitle.setText("Questions and concerns");
                if (!StringUtils.isEmpty(why.getText().toString())) {
                    mTxtInputContent.setText(why.getText().toString());
                } else
                    mTxtInputContent.setHint("What do you expect to learn from " + author
                            + "? Any specific questions? More details will help " + author
                            + " prepare for your meeting");
            }
        });
        mBtnInputSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayInput.setVisibility(View.GONE);
                //mTxtInputContent.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTxtInputContent.getWindowToken(), 0);

                if (inputType == 0) {
                    why.setText(mTxtInputContent.getText().toString());
                    mTxtInputContent.setText("");
                } else if (inputType == 1) {
                    aboutMe.setText(mTxtInputContent.getText().toString());
                    mTxtInputContent.setText("");
                }
            }
        });
        mBtnInputSave.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTxtInputContent.getText().length() > 2000) {
                    PromeetsDialog.show(UserRequestSettingActivity.this, "Cannot enter more than 2000 characters", new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            mTxtInputContent.setText(mTxtInputContent.getText().subSequence(0, mTxtInputContent.getText().length() - 1));
                        }
                    });
                }
                if (mTxtInputContent.getText().toString().split(" ").length >= 300) {
                    PromeetsDialog.show(UserRequestSettingActivity.this, "Cannot enter more than 300 words");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        aboutMe.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        aboutMe.clearFocus();
                        break;
                }

                return false;
            }
        });
        aboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputType = 1;
                mLayInput.setVisibility(View.VISIBLE);
                mTxtInputContent.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                mTxtInputTitle.setText("About yourself");
                if (!StringUtils.isEmpty(aboutMe.getText().toString()))
                    mTxtInputContent.setText(aboutMe.getText().toString());
                else
                    mTxtInputContent.setHint("What's your name? What are you working on? Just graduated? Starting a new venture? Interesting facts? We want to get to know you");
            }
        });
        mTxtInputContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTxtInputContent.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        addTime.setOnClickListener(this);
        addTime0.setOnClickListener(this);
        addLocation0.setOnClickListener(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valid()) makeRequest();
            }
        });

        mapFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRequestSettingActivity.this, MapActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<EventLocationPOJO> list = new ArrayList<>();
                list.addAll(defaultLocationList);
                list.addAll(userLocationList);
                bundle.putSerializable("list", list);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mChkMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChkMeeting.isChecked()) {
                    mChkMeeting.setChecked(false);
                    mChkMeeting.setTypeface(tf_regular);
                    mLayMeeting.setVisibility(View.GONE);
                } else {
                    mChkMeeting.setChecked(true);
                    mChkMeeting.setTypeface(tf_semi);
                    mLayMeeting.setVisibility(View.VISIBLE);
                }
            }
        });
        mChkCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChkCall.isChecked()) {
                    mChkCall.setChecked(false);
                    mChkCall.setTypeface(tf_regular);
                    mLayCall.setVisibility(View.GONE);
                } else {
                    mChkCall.setChecked(true);
                    mChkCall.setTypeface(tf_semi);
                    mLayCall.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mLayInput.getVisibility() == View.VISIBLE) {
            mLayInput.setVisibility(View.GONE);
            mTxtInputContent.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTxtInputContent.getWindowToken(), 0);
            mTxtInputContent.setText("");
        } else
            super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isLocationEnabled = false;
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            String[] key = {FirebaseUtil.EVENT_TYPE, Constant.EXPERTID, Constant.SERVICEID, Constant.USERID};
            String[] value = {"location_select", expId, serviceId, userId};
            FirebaseUtil.getInstance(this).buttonClick(FirebaseUtil.MAKE_AN_APPOINTMENT_SCREEN_LOAD, key, value);

            EventLocationPOJO eventLocationPOJO = new EventLocationPOJO();
            Place place = PlacePicker.getPlace(this, data);

            String locTxt = place.getAddress().toString();
            if (locTxt.endsWith(", USA"))
                locTxt = locTxt.substring(0, locTxt.length() - 5);
            eventLocationPOJO.location = locTxt;
            eventLocationPOJO.latitude = place.getLatLng().latitude + "";
            eventLocationPOJO.longitude = place.getLatLng().longitude + "";
            eventLocationPOJO.isSelected = true;

            if (!StringUtils.isEmpty(eventLocationPOJO.location)
                    &&!isLocationEx(eventLocationPOJO)) {
                userLocationList.add(eventLocationPOJO);
                locationListAdapter.notifyDataSetChanged();
                //mLayLocationList.setVisibility(View.VISIBLE);
                addMapMarker(eventLocationPOJO);
            } else {
                Toast.makeText(getBaseContext(), "Location you selected is already in the list", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 500 && resultCode == RESULT_OK) {
            mWVEventStrList = data.getStringArrayListExtra("eventsFromCalendar");
            //ArrayList<EventTimePOJO> eventTimeList = new ArrayList<>();
            arrayListTime.clear();
            if (mWVEventStrList == null || mWVEventStrList.size() == 0) {
                addTime.setVisibility(View.VISIBLE);
                mLayTimeList.setVisibility(View.GONE);
            } else {
                addTime.setVisibility(View.GONE);
                mLayTimeList.setVisibility(View.VISIBLE);
                Gson gson = new Gson();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm aa");
                SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (String mStrEvent : mWVEventStrList) {
                    WeekViewEvent mEvent = gson.fromJson(mStrEvent, WeekViewEvent.class);
                    String fromTime = timeFormat.format(mEvent.getStartTime().getTime());
                    String toTime = timeFormat.format(mEvent.getEndTime().getTime());
                    String timeZone = mEvent.getStartTime().getTimeZone().getID();
                    String date = dataFormat.format(mEvent.getStartTime().getTime());
                    if (fromTime != null && toTime != null) {
                        EventTimePOJO eventTimePOJO = new EventTimePOJO();
                        eventTimePOJO.id = mEvent.getId();
                        eventTimePOJO.beginHourOfDay = fromTime;
                        eventTimePOJO.pstBeginTime = fromTime;
                        eventTimePOJO.endHourOfDay = toTime;
                        eventTimePOJO.pstEndTime = toTime;
                        eventTimePOJO.detailDay = date;
                        eventTimePOJO.serviceId = Integer.valueOf(this.serviceId);
                        eventTimePOJO.timeZone = timeZone;
                        arrayListTime.add(eventTimePOJO);
                    }
                }
            }
            timeListAdapter.notifyDataSetChanged();
        }
    }

    private void callPlacePicker() {
        String[] key = {FirebaseUtil.EVENT_TYPE, Constant.EXPERTID, Constant.SERVICEID, Constant.USERID};
        String[] value = {"location_click", expId, serviceId, userId};
        FirebaseUtil.getInstance(UserRequestSettingActivity.this).buttonClick(FirebaseUtil.MAKE_AN_APPOINTMENT_SCREEN_LOAD, key, value);

        isLocationEnabled = true;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = builder.build(this);
            intent.putExtra("primary_color", Color.WHITE);
            intent.putExtra("primary_color_dark", getResources().getColor(R.color.transparent_black));
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            isLocationEnabled = false;
        }
    }

    private boolean valid() {
        if (why.getText().toString().split(" ").length < 20) {
            PromeetsDialog.show(this,
                    "We need more information to serve you better, please enter 20-300 words each");
            return false;
        }
        if (aboutMe.getText().toString().split(" ").length < 20) {
            PromeetsDialog.show(UserRequestSettingActivity.this,
                    "We need more information to serve you better, please enter 20-300 words each");
            return false;
        }
        if (timeListAdapter.getCount() <= 0) {
            PromeetsDialog.show(UserRequestSettingActivity.this,
                    "When do you want to meet? Please pick at least one time slot");
            return false;
        }
        if (!mChkMeeting.isChecked() && !mChkCall.isChecked()) {
            PromeetsDialog.show(UserRequestSettingActivity.this,
                    "How do you want to meet? Please pick at least one meeting option");
            return false;
        }
        if (locationListAdapter.getResult().size() + expLocationListAdapter.getResult().size() <= 0
                && mChkMeeting.isChecked()) {
            PromeetsDialog.show(UserRequestSettingActivity.this,
                    "Where do you want to meet? Please pick at least one location");
            return false;
        }
        return true;
    }

    private void makeRequest() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        String[] key = {FirebaseUtil.EVENT_TYPE, Constant.EXPERTID, Constant.SERVICEID, Constant.USERID};
        String[] value = {"submit", expId, serviceId, userId};
        FirebaseUtil.getInstance(this).buttonClick(FirebaseUtil.MAKE_AN_APPOINTMENT_SCREEN_LOAD, key, value);

        UserInfoHelper helper = new UserInfoHelper(this);
        UserPOJO user = helper.getUserObject();

        if (user == null) {
            startActivity(MainActivity.class);
            return;
        }

        PromeetsDialog.showProgress(this);
        final EventPost eventPost = new EventPost();
        eventPost.aboutMe = aboutMe.getText().toString();
        eventPost.whyInterested = why.getText().toString();
        eventPost.userId = user.id;
        int serviceIdInteger;
        int expIdInteger;
        try {
            serviceIdInteger = Integer.parseInt(this.serviceId);
            expIdInteger = Integer.parseInt(this.expId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        eventPost.serviceId = serviceIdInteger;
        eventPost.expId = expIdInteger;

        ArrayList<EventTimePOJO> postTime = new ArrayList<>();
        postTime.addAll(arrayListTime);
        for (EventTimePOJO time : arrayListTime) {
            time.id = 0;
        }
        EventUpdatePost eventUpdatePost = new EventUpdatePost();
        eventUpdatePost.eventDateList = postTime;
        if (mChkMeeting.isChecked()) {
            eventUpdatePost.eventLocation.addAll(locationListAdapter.getResult());
            eventUpdatePost.eventLocation.addAll(expLocationListAdapter.getResult());
        }
        if (mChkCall.isChecked()) {
            EventLocationPOJO loc = new EventLocationPOJO();
            loc.status = 2;
            loc.location = "Online Call";
            loc.latitude = "-1";
            loc.longitude = "-1";
            eventUpdatePost.eventLocation.add(loc);
        }
        eventUpdatePost.eventRequest = eventPost;

        Gson gson = new Gson();
        String eventUpdateBodyPOJOJson = gson.toJson(eventUpdatePost);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/updateALL"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), eventUpdateBodyPOJOJson);

        EventApi service = retrofit.create(EventApi.class);
        Call<BaseResp> call = service.createEvent(requestBody);//get request, need to be post!

        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                BaseResp result = response.body();
                if (result == null || result.info == null) {
                    PromeetsDialog.show(UserRequestSettingActivity.this, "API error");
                    return;
                }
                if (result.info.code.equals("200")) {
                    finish();
                    userProfile.description = eventPost.aboutMe;
                    ServiceResponseHolder.getInstance().setUserProfile(userProfile);

                    if (result.eventDate != null) {
                        Intent intent = new Intent(UserRequestSettingActivity.this, AppointStatusActivity.class);
                        intent.putExtra("eventRequestId", result.eventDate.id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(UserRequestSettingActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(UserRequestSettingActivity.this, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(UserRequestSettingActivity.this, t.getLocalizedMessage());
            }
        });
    }

    public boolean isLocationEx(EventLocationPOJO location) {
        ArrayList<EventLocationPOJO> tmp = locationListAdapter.getAll();
        for (int i = 0; i < tmp.size(); i++) {
            if (tmp.get(i).location.equals(location.location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_time0:
            case R.id.add_time:
                String[] key = {FirebaseUtil.EVENT_TYPE, Constant.EXPERTID, Constant.SERVICEID, Constant.USERID};
                String[] value = {"time_click", expId, serviceId, userId};
                FirebaseUtil.getInstance(UserRequestSettingActivity.this).buttonClick(FirebaseUtil.MAKE_AN_APPOINTMENT_SCREEN_LOAD, key, value);

                Intent intent = new Intent(UserRequestSettingActivity.this, CalendarViewActivity.class);
                intent.putExtra("expId", expId);
                intent.putExtra("duratingTime", duratingTime);
                intent.putStringArrayListExtra("eventsToCalendar", mWVEventStrList);
                intent.putExtra("type", availabilityType);
                intent.putExtra("defaultDate", defaultDate);
                startActivityForResult(intent, 500);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.add_location:
            case R.id.add_location0:
                if (!isLocationEnabled)
                    callPlacePicker();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setMaxZoomPreference(15);
        for (EventLocationPOJO location : defaultLocationList) {
            LatLng loc = new LatLng(Double.valueOf(location.latitude), Double.valueOf(location.longitude));
            Marker marker = mMap.addMarker(new MarkerOptions().position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary)));
            markerList.add(marker);
            map.put(location, marker);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                        ScreenUtil.convertDpToPx(50, UserRequestSettingActivity.this)));
            }
        });
    }

    public ArrayList<String> getEventList() {
        return mWVEventStrList;
    }
    public HashMap<EventLocationPOJO, Marker> getMap() {
        return map;
    }

    public void updateTimeUI() {
        if (mWVEventStrList.size() == 0) {
            addTime.setVisibility(View.VISIBLE);
            mLayTimeList.setVisibility(View.GONE);
        }
    }

    private void addMapMarker(EventLocationPOJO location) {
        LatLng loc = new LatLng(Double.valueOf(location.latitude), Double.valueOf(location.longitude));
        Marker marker = mMap.addMarker(new MarkerOptions().position(loc)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary)));
        markerList.add(marker);
        map.put(location, marker);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker m : markerList) {
            builder.include(m.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                        ScreenUtil.convertDpToPx(50, UserRequestSettingActivity.this)));
            }
        });
    }
}