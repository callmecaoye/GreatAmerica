package com.promeets.android.activity;

import com.promeets.android.api.ExpertActionApi;
import android.content.Intent;
import com.promeets.android.custom.NoScrollListView;
import com.promeets.android.custom.PromeetsDialog;
import android.location.Location;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.ExpertProfilePOJO;
import android.os.Bundle;
import com.promeets.android.pojo.ExpertProfileResp;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.promeets.android.util.AuthenticationManager;
import com.promeets.android.util.LocationHandlerUtil;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.adapter.ListviewSingleLineLocationAdapter;
import com.promeets.android.api.URL;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.promeets.android.Constant;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.util.AuthenticationCallback;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.ExpertProfilePost;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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
 * This is for expert availability including time and location
 *
 * Integrated with Google and Outlook calendar
 *
 * @source: ExpertDashboardActivity
 *
 * @destination: AvailabilityTimePickerActivity, MapActivity, PlacePickerActivity
 *
 */

public class ExpertAvailActivity extends BaseActivity
        implements View.OnClickListener, OnMapReadyCallback{

    private static final int REQUEST_PLACE_PICKER = 20;
    private static final int REQUEST_TIME_PICKER = 30;

    // Google Calendar Constants
    public static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";
    private String SERVER_CLIENT_ID;
    public static final int GOOGLE_SIGN_IN = 100;
    GoogleApiClient mGoogleApiClient;

    // Availability
    @BindView(R.id.become_expert_mon_am)
    ImageView monAm;
    @BindView(R.id.become_expert_tue_am)
    ImageView tueAm;
    @BindView(R.id.become_expert_wed_am)
    ImageView wedAm;
    @BindView(R.id.become_expert_thu_am)
    ImageView thuAm;
    @BindView(R.id.become_expert_fri_am)
    ImageView friAm;
    @BindView(R.id.become_expert_sat_am)
    ImageView satAm;
    @BindView(R.id.become_expert_sun_am)
    ImageView sunAm;
    @BindView(R.id.become_expert_mon_pm)
    ImageView monPm;
    @BindView(R.id.become_expert_tue_pm)
    ImageView tuePm;
    @BindView(R.id.become_expert_wed_pm)
    ImageView wedPm;
    @BindView(R.id.become_expert_thu_pm)
    ImageView thuPm;
    @BindView(R.id.become_expert_fri_pm)
    ImageView friPm;
    @BindView(R.id.become_expert_sat_pm)
    ImageView satPm;
    @BindView(R.id.become_expert_sun_pm)
    ImageView sunPm;

    @BindView(R.id.avail0)
    CheckBox mChkAvail0;
    @BindView(R.id.avail1)
    CheckBox mChkAvail1;
    @BindView(R.id.avail2)
    CheckBox mChkAvail2;
    @BindView(R.id.avail2_txt)
    TextView mChkTxtAvail2;
    @BindView(R.id.calendar_prev)
    TextView mTxtCalPreview;
    @BindView(R.id.time_slot)
    LinearLayout mLayoutTime;

    /**
     * Only when availability Type == 1
     * 1: Google
     * 2: Outlook
     * 3: Google & Outlook
     */
    @BindView(R.id.sync_google)
    TextView mTxtSyncGoogle;
    @BindView(R.id.sync_outlook)
    TextView mTxtSyncOutlook;

    @BindView(R.id.map_front)
    View mapFront;
    @BindView(R.id.location_list)
    NoScrollListView mListLocation;
    @BindView(R.id.loc_divider)
    View mViewLocDivider;
    @BindView(R.id.add_location)
    ImageView mImgAddLoc;
    @BindView(R.id.submit)
    TextView mTxtSubmit;

    /**
     * -1 : not select
     * 0 : pick time slot
     * 1 : sync calendar
     * 2 : not now
     */
    private int mAvailabilityType = -1;
    /**
     * 0 : not select
     * 1 : google sign in
     * 2 : outlook sign in
     * 3 : google & outlook
     */
    private int mCalendarType = 0;

    //private UserPOJO user;
    private ExpertProfilePOJO expertProfilePOJO, updateExpProfilePOJO;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private List<Marker> markerList = new ArrayList<>();
    private OnMapReadyCallback mapCallback = this;
    private int defaultDate;
    private ArrayList<EventLocationPOJO> mLocations = new ArrayList<>();
    private ListviewSingleLineLocationAdapter mLocAdapter;
    private LatLngBounds.Builder builder;
    private Gson gson = new Gson();
    private boolean isLocationClicked = false;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mChkAvail0.setOnClickListener(this);
        mChkAvail1.setOnClickListener(this);
        mChkAvail2.setOnClickListener(this);
        mChkTxtAvail2.setOnClickListener(this);
        mLayoutTime.setOnClickListener(this);
        mTxtSyncGoogle.setOnClickListener(this);
        mTxtSyncOutlook.setOnClickListener(this);
        mTxtCalPreview.setOnClickListener(this);
        mImgAddLoc.setOnClickListener(this);
        mTxtSubmit.setOnClickListener(this);
        mapFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpertAvailActivity.this, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", mLocations);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_avail);
        ButterKnife.bind(this);

        // Google calendar init
        SERVER_CLIENT_ID = getResources().getString(R.string.default_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new com.google.android.gms.common.api.Scope(CALENDAR_SCOPE))
                .requestServerAuthCode(SERVER_CLIENT_ID, false)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mLocAdapter = new ListviewSingleLineLocationAdapter(this, mLocations);
        mListLocation.setAdapter(mLocAdapter);
        builder = new LatLngBounds.Builder();

        int expId = getIntent().getIntExtra("expId", 0);
        //user = (UserPOJO) PromeetsUtils.getUserData(this, PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (expId != 0) {
            fetchExpertProfile(expId);
            updateExpProfilePOJO = new ExpertProfilePOJO();
            updateExpProfilePOJO.expId = expId;
        }
    }

    private void fetchExpertProfile(int id) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("expertprofile/fetchIncludeServiceMayPending"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        Call<ExpertProfileResp> call = service.fetchExpertProfile(id, TimeZone.getDefault().getID());
        call.enqueue(new Callback<ExpertProfileResp>() {
            @Override
            public void onResponse(Call<ExpertProfileResp> call, Response<ExpertProfileResp> response) {
                PromeetsDialog.hideProgress();
                ExpertProfileResp result = response.body();

                if (isSuccess(result.info.code)) {
                    expertProfilePOJO = result.expertProfile;
                    if (expertProfilePOJO.expertDefaultLocationList != null
                            && expertProfilePOJO.expertDefaultLocationList.size() > 0) {
                        mLocations.addAll(expertProfilePOJO.expertDefaultLocationList);
                        mLocAdapter.notifyDataSetChanged();
                        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(mapCallback);
                    }

                    if (expertProfilePOJO.availabilityType == 0) {
                        mAvailabilityType = 0;
                        mChkAvail0.setChecked(true);
                        mChkAvail1.setChecked(false);
                        mChkAvail2.setChecked(false);
                        defaultDate = expertProfilePOJO.defaultDate;
                        onTimePickerResult(defaultDate);
                    } else if (expertProfilePOJO.availabilityType == 1) {
                        mAvailabilityType = 1;
                        mChkAvail0.setChecked(false);
                        mChkAvail1.setChecked(true);
                        mChkAvail2.setChecked(false);
                        mCalendarType = expertProfilePOJO.calendarType;
                        if (mCalendarType == 1 || mCalendarType == 3) {
                            mTxtSyncGoogle.setBackground(getResources().getDrawable(R.drawable.google_sync));
                        }
                        if (mCalendarType == 2 || mCalendarType == 3) {
                            mTxtSyncOutlook.setBackground(getResources().getDrawable(R.drawable.outlook_sync));
                        }
                    } else if (expertProfilePOJO.availabilityType == 2) {
                        mAvailabilityType = 2;
                        mChkAvail0.setChecked(false);
                        mChkAvail1.setChecked(false);
                        mChkAvail2.setChecked(true);
                    }
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP) || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(ExpertAvailActivity.this, result.info.code);
                }
            }

            @Override
            public void onFailure(Call<ExpertProfileResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertAvailActivity.this, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setMaxZoomPreference(15);

        if (markerList != null && markerList.size() > 0) {
            for (Marker marker : markerList) {
                marker.remove();
            }
        }

        if (mLocations.size() > 0) {
            for (EventLocationPOJO location : mLocations) {
                LatLng loc = new LatLng(Double.valueOf(location.latitude), Double.valueOf(location.longitude));
                Marker marker = mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_gray)));
                markerList.add(marker);
            }
        }

        if (markerList != null && markerList.size() > 0) {
            for (Marker marker : markerList) {
                builder.include(marker.getPosition());
            }
            final LatLngBounds bounds = builder.build();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                            ScreenUtil.convertDpToPx(50, ExpertAvailActivity.this)));
                }
            });
        } else {
            Location cur;
            try {
                cur = LocationHandlerUtil.getInstance(this).getLastKnownLocation();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cur.getLatitude(), cur.getLongitude()), 15));
            } catch (Exception e) {
                e.printStackTrace();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.483254, -122.174395), 9));
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GOOGLE_SIGN_IN:
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if (result.isSuccess()) {
                        GoogleSignInAccount acct = result.getSignInAccount();
                        String authCode = acct.getServerAuthCode();
                        if (!TextUtils.isEmpty(authCode)) {
                            postGoogleCode(authCode);
                        }
                    } else {
                        PromeetsDialog.show(this, "Google Sign in failed\nstatus code : " + result.getStatus().getStatusCode());
                    }
                    break;
                case REQUEST_PLACE_PICKER:
                    isLocationClicked = false;
                    Place place = PlacePicker.getPlace(this, data);
                    if (StringUtils.isEmpty(place.getAddress())) {
                        PromeetsDialog.show(this, "Please select a valid location.");
                        return;
                    }

                    EventLocationPOJO eventLocationPOJO = new EventLocationPOJO();
                    String locTxt = place.getAddress().toString();
                    if (locTxt.endsWith(", USA"))
                        locTxt = locTxt.substring(0, locTxt.length() - 5);
                    eventLocationPOJO.location = locTxt;
                    eventLocationPOJO.latitude = place.getLatLng().latitude + "";
                    eventLocationPOJO.longitude = place.getLatLng().longitude + "";
                    if (!isLocationEx(eventLocationPOJO)) {
                        mLocations.add(eventLocationPOJO);
                        mLocAdapter.notifyDataSetChanged();
                        addMapMarker(eventLocationPOJO);
                    } else
                        PromeetsDialog.show(this, "You have already selected this location.");
                    break;
                case REQUEST_TIME_PICKER:
                    if (data != null) {
                        defaultDate = data.getIntExtra("defaultDate", 0);
                    }
                    onTimePickerResult(defaultDate);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!isLocationClicked)
            isLocationClicked = true;
        Intent intent;
        switch (v.getId()) {
            case R.id.sync_google:
                mAvailabilityType = 1;
                mChkAvail0.setChecked(false);
                mChkAvail1.setChecked(true);
                mChkAvail2.setChecked(false);

                if (mCalendarType == 1) {
                    mCalendarType = 0;
                    mTxtSyncGoogle.setBackground(getResources().getDrawable(R.drawable.google_sync_grey));
                } else if (mCalendarType == 3) {
                    mCalendarType = 2;
                    mTxtSyncGoogle.setBackground(getResources().getDrawable(R.drawable.google_sync_grey));
                } else {
                    intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(intent, GOOGLE_SIGN_IN);
                }
                break;
            case R.id.sync_outlook:
                mAvailabilityType = 1;
                mChkAvail0.setChecked(false);
                mChkAvail1.setChecked(true);
                mChkAvail2.setChecked(false);

                if (mCalendarType == 2) {
                    mCalendarType = 0;
                    mTxtSyncOutlook.setBackground(getResources().getDrawable(R.drawable.outlook_sync_grey));
                } else if (mCalendarType == 3) {
                    mCalendarType = 1;
                    mTxtSyncOutlook.setBackground(getResources().getDrawable(R.drawable.outlook_sync_grey));
                } else {
                    // define the post-auth callback
                    AuthenticationCallback<String> callback =
                            new AuthenticationCallback<String>() {
                                @Override
                                public void onSuccess(final String refreshToken) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!TextUtils.isEmpty(refreshToken))
                                                postOutlookToken(refreshToken);
                                        }
                                    });

                                    //AuthenticationManager.getInstance(ConnectActivity.this).disconnect();
                                }

                                @Override
                                public void onError(Exception exc) {
                                    PromeetsDialog.show(ExpertAvailActivity.this, "Outlook Sign in failed\n" + exc.getLocalizedMessage());
                                }
                            };

                    AuthenticationManager mgr = AuthenticationManager.getInstance(this);
                    mgr.connect(this, callback);
                }
                break;
            case R.id.time_slot:
                intent = new Intent(this, AvailabilityTimePickerActivity.class);
                intent.putExtra("defaultDate", defaultDate);
                startActivityForResult(intent, REQUEST_TIME_PICKER);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            case R.id.avail0:
                mAvailabilityType = 0;
                mChkAvail0.setChecked(true);
                mChkAvail1.setChecked(false);
                mChkAvail2.setChecked(false);
                break;
            case R.id.calendar_prev:
                intent = new Intent(this, CalendarViewActivity.class);
                intent.putExtra("expId", expertProfilePOJO.expId+"");
                intent.putExtra("isPreview", true);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            case R.id.avail1:
                mAvailabilityType = 1;
                mChkAvail0.setChecked(false);
                mChkAvail1.setChecked(true);
                mChkAvail2.setChecked(false);
                break;
            case R.id.avail2:
            case R.id.avail2_txt:
                mAvailabilityType = 2;
                mChkAvail0.setChecked(false);
                mChkAvail1.setChecked(false);
                mChkAvail2.setChecked(true);
                break;
            case R.id.add_location:
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(intentBuilder.build(this), REQUEST_PLACE_PICKER);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.submit:
                if (!check()) return;
                collectUpdateInfo();
                boolean isTimeUpdate = false;
                if (mAvailabilityType != expertProfilePOJO.availabilityType) {
                    isTimeUpdate = true;
                } else {
                    if (mAvailabilityType == 0)
                        isTimeUpdate = defaultDate == expertProfilePOJO.defaultDate;
                    else if (mAvailabilityType == 1)
                        isTimeUpdate = mCalendarType == expertProfilePOJO.calendarType;
                }

                if (!isTimeUpdate
                        && mLocations.equals(expertProfilePOJO.expertDefaultLocationList)) {
                    finish();
                } else {
                    updateExpertSubmit();
                }
                break;
        }
    }

    private void onTimePickerResult(int defaultDate) {
        for (int i = 0; i < 14; i++) {
            if ((defaultDate & (1 << i)) > 0) {
                switch (i) {
                    // Monday
                    case 0:
                        monAm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    case 1:
                        monPm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    // Tuesday
                    case 2:
                        tueAm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    case 3:
                        tuePm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    // Wednesday
                    case 4:
                        wedAm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    case 5:
                        wedPm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    // Thursday
                    case 6:
                        thuAm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    case 7:
                        thuPm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    // Friday
                    case 8:
                        friAm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    case 9:
                        friPm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    // Saturday
                    case 10:
                        satAm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    case 11:
                        satPm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    // Sunday
                    case 12:
                        sunAm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                    case 13:
                        sunPm.setImageResource(R.drawable.ic_nike_primary);
                        break;
                }
            } else {
                switch (i) {
                    // Monday
                    case 0:
                        monAm.setImageResource(0);
                        break;
                    case 1:
                        monPm.setImageResource(0);
                        break;
                    // Tuesday
                    case 2:
                        tueAm.setImageResource(0);
                        break;
                    case 3:
                        tuePm.setImageResource(0);
                        break;
                    // Wednesday
                    case 4:
                        wedAm.setImageResource(0);
                        break;
                    case 5:
                        wedPm.setImageResource(0);
                        break;
                    // Thursday
                    case 6:
                        thuAm.setImageResource(0);
                        break;
                    case 7:
                        thuPm.setImageResource(0);
                        break;
                    // Friday
                    case 8:
                        friAm.setImageResource(0);
                        break;
                    case 9:
                        friPm.setImageResource(0);
                        break;
                    // Saturday
                    case 10:
                        satAm.setImageResource(0);
                        break;
                    case 11:
                        satPm.setImageResource(0);
                        break;
                    // Sunday
                    case 12:
                        sunAm.setImageResource(0);
                        break;
                    case 13:
                        sunPm.setImageResource(0);
                        break;
                }
            }
        }
    }

    public boolean isLocationEx(EventLocationPOJO location) {
        ArrayList<EventLocationPOJO> tmp = mLocAdapter.getAll();
        for (int i = 0; i < tmp.size(); i++) {
            if (tmp.get(i).location.equals(location.location)) {
                return true;
            }
        }
        return false;
    }

    private void addMapMarker(EventLocationPOJO location) {
        LatLng loc = new LatLng(Double.valueOf(location.latitude), Double.valueOf(location.longitude));
        Marker marker = mMap.addMarker(new MarkerOptions().position(loc).title(location.location)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_gray)));
        markerList.add(marker);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker m : markerList) {
            builder.include(m.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                        ScreenUtil.convertDpToPx(50, ExpertAvailActivity.this)));
            }
        });
    }

    private void postGoogleCode(String authCode) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("Calendar/checkGoolgeAuth"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        Call<BaseResp> call = service.checkGoogleAuth(authCode, expertProfilePOJO.expId);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                BaseResp baseResp = response.body();
                if (baseResp != null && baseResp.info != null && baseResp.info.code.equals("200")) {
                    mAvailabilityType = 1;
                    if (mCalendarType == 2 || mCalendarType == 3) mCalendarType = 3;
                    else mCalendarType = 1;
                    PromeetsDialog.show(ExpertAvailActivity.this, baseResp.info.description);
                    mTxtSyncGoogle.setBackground(getResources().getDrawable(R.drawable.google_sync));
                } else if (baseResp != null) {
                    PromeetsDialog.show(ExpertAvailActivity.this, "API Error : \n" + baseResp.info.description);
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertAvailActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void postOutlookToken(String refreshToken) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("Calendar/refreshOutlookToken"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ExpertActionApi service = retrofit.create(ExpertActionApi.class);

        JSONObject json = new JSONObject();
        try {
            json.put("refreshToken", refreshToken);
            json.put("requestType", "android");
            json.put("userId", expertProfilePOJO.expId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Call<BaseResp> call = service.refreshOutlookToken(requestBody);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                BaseResp baseResp = response.body();
                if (baseResp != null && baseResp.info != null && baseResp.info.code.equals("200")) {
                    mAvailabilityType = 1;
                    if (mCalendarType == 1 || mCalendarType == 3) mCalendarType = 3;
                    else mCalendarType = 2;
                    PromeetsDialog.show(ExpertAvailActivity.this, baseResp.info.description);
                    mTxtSyncOutlook.setBackground(getResources().getDrawable(R.drawable.outlook_sync));
                } else if (baseResp != null) {
                    PromeetsDialog.show(ExpertAvailActivity.this, "API Error : \n" + baseResp.info.description);
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertAvailActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private boolean check() {
        if (mAvailabilityType == -1 ||
                (mAvailabilityType == 0 && defaultDate == 0) ||
                        (mAvailabilityType == 1 && mCalendarType == 0)) {
            PromeetsDialog.show(this, "Available time cannot be empty");
            return false;
        }
        if (mLocAdapter.getCount() <= 0) {
            PromeetsDialog.show(this, "Must have location");
            return false;
        }
        return true;
    }

    private void collectUpdateInfo() {
        // AVAILABILITY CARD
        if (mAvailabilityType != expertProfilePOJO.availabilityType) {
            updateExpProfilePOJO.availabilityType = mAvailabilityType;
            if (mAvailabilityType == 0) {
                updateExpProfilePOJO.defaultDate = defaultDate;
            } else if (mAvailabilityType == 1) {
                updateExpProfilePOJO.calendarType = mCalendarType;
            } else if (mAvailabilityType == 2) {
                updateExpProfilePOJO.availabilityType = 2;
            }
        }
        if (!mLocations.equals(expertProfilePOJO.expertDefaultLocationList))
            updateExpProfilePOJO.expertDefaultLocationList = mLocations;
    }

    private void updateExpertSubmit() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        ExpertProfilePost expertProfilePost = new ExpertProfilePost();
        expertProfilePost.expertProfile = updateExpProfilePOJO;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("becomeToExpert/toCheck"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final String json = gson.toJson(expertProfilePost);
        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<SuperResp> call = service.becomeExpert(requestBody);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(ExpertAvailActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    finish();
                    if (ExpertDashboardActivity.instance != null)
                        ExpertDashboardActivity.instance.finish();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                            || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                            || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(ExpertAvailActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(ExpertAvailActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertAvailActivity.this, t.getLocalizedMessage());
            }
        });
    }

    public SupportMapFragment getMapFragment() {
        return mapFragment;
    }

    public OnMapReadyCallback getMapCallback() {
        return mapCallback;
    }
}
