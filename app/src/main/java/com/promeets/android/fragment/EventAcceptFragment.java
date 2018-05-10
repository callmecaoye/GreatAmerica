package com.promeets.android.fragment;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.CalendarViewActivity;
import android.annotation.SuppressLint;
import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.promeets.android.object.EventData;
import com.promeets.android.object.EventLocationPOJO;
import android.os.Bundle;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.EventDetailResp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.adapter.LocationCheckAdapter;
import com.promeets.android.adapter.TimeCheckAdapter;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.promeets.android.Constant;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.pojo.SuperPost;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


/**
 * This is for expert accepting user request
 *
 * Add or edit time and location
 *
 * @source: AppointStatusActivity
 *
 * @destination: activity self-refresh
 *
 */

public class EventAcceptFragment extends DialogFragment {

    static final int PLACE_PICKER_REQUEST = 1;
    static final int WEEK_VIEW_REQUEST = 2;

    @BindView(R.id.root_layout)
    View mLayRoot;
    @BindView(R.id.dialog_layout)
    View mLayDialog;
    @BindView(R.id.time_list)
    ListView mLVTime;
    @BindView(R.id.location_list)
    ListView mLVLocation;
    @BindView(R.id.add_location)
    ImageView addLocation;
    @BindView(R.id.add_time)
    ImageView addTime;
    @BindView(R.id.submit)
    Button mBtnSubmit;
    @BindView(R.id.call)
    CheckedTextView mChkCall;

    private BaseActivity mBaseActivity;
    private EventDetailResp mEventDetail;
    private SuperPost post = new SuperPost();
    private ArrayList<EventTimePOJO> timeList = new ArrayList<>();
    private ArrayList<String> mWVEventStrList;
    private ArrayList<EventLocationPOJO> locList = new ArrayList<>();
    private LocationCheckAdapter locationAdapter;
    private TimeCheckAdapter timeAdapter;
    private boolean isLocationEnabled;
    private Gson gson = new Gson();

    public static EventAcceptFragment newInstance(EventDetailResp mEventDetail) {
        EventAcceptFragment f = new EventAcceptFragment();
        f.mEventDetail = mEventDetail;
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBaseActivity = (BaseActivity) getActivity();
        final RelativeLayout root = new RelativeLayout(mBaseActivity);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(mBaseActivity);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setCancelable(true);
        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_accept, container);
        ButterKnife.bind(this, view);

        EventData eventData = new EventData();
        eventData.id = mEventDetail.eventData.id;
        post.eventRequest = eventData;

        timeList.addAll(mEventDetail.eventDateList);
        timeAdapter = new TimeCheckAdapter(mBaseActivity, timeList);
        mLVTime.setAdapter(timeAdapter);
        locList.addAll(mEventDetail.eventLocationList);
        locationAdapter = new LocationCheckAdapter(mBaseActivity, locList);
        mLVLocation.setAdapter(locationAdapter);

        if (timeAdapter.getCount() > 2) {
            ViewGroup.LayoutParams params = mLVTime.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ScreenUtil.convertDpToPx(105, mBaseActivity);
            mLVTime.setLayoutParams(params);
        }
        if (locationAdapter.getCount() > 2) {
            ViewGroup.LayoutParams params = mLVLocation.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ScreenUtil.convertDpToPx(105, mBaseActivity);
            mLVLocation.setLayoutParams(params);
        }

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mLayDialog.setOnClickListener(null);

        mChkCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChkCall.setChecked(!mChkCall.isChecked());
            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWVEventStrList == null || mWVEventStrList.size() == 0) {
                    mWVEventStrList = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for (EventTimePOJO pojo : timeList) {
                        Calendar start = Calendar.getInstance();
                        Calendar end = (Calendar) start.clone();
                        String startStr = pojo.detailDay + " " + pojo.beginHourOfDay;
                        String endStr = pojo.detailDay + " " + pojo.endHourOfDay;
                        try {
                            start.setTime(sdf.parse(startStr));
                            end.setTime(sdf.parse(endStr));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        WeekViewEvent mWVEvent = new WeekViewEvent(start.getTimeInMillis(), null, start, end);
                        mWVEventStrList.add(gson.toJson(mWVEvent));
                    }
                }

                Intent intent = new Intent(mBaseActivity, CalendarViewActivity.class);
                intent.putExtra("expId", mEventDetail.eventData.expId);
                intent.putExtra("duratingTime", new Float(mEventDetail.expertService.duratingTime).floatValue());
                intent.putStringArrayListExtra("eventsToCalendar", mWVEventStrList);
                startActivityForResult(intent, WEEK_VIEW_REQUEST);
                //mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocationEnabled) {
                    isLocationEnabled = true;
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        Intent intent = builder.build(mBaseActivity);
                        intent.putExtra("primary_color", Color.WHITE);
                        intent.putExtra("primary_color_dark", getResources().getColor(R.color.transparent_black));
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isLocationEnabled = false;
                    }
                }
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.eventDateList = new ArrayList<>();
                post.eventLocationList = new ArrayList<>();

                for (EventTimePOJO pojo : timeList) {
                    pojo.id = 0;
                    if (pojo.isSelected)
                        post.eventDateList.add(pojo);
                }
                for (EventLocationPOJO pojo : locList) {
                    if (pojo.isSelected)
                        post.eventLocationList.add(pojo);
                }
                if (mChkCall.isChecked()) {
                    EventLocationPOJO loc = new EventLocationPOJO();
                    loc.status = 2;
                    loc.location = "Online Call";
                    loc.latitude = "-1";
                    loc.longitude = "-1";
                    post.eventLocationList.add(loc);
                }

                expertSubmit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isLocationEnabled = false;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    EventLocationPOJO eventLocationPOJO = new EventLocationPOJO();
                    Place place = PlacePicker.getPlace(data, mBaseActivity);
                    String locTxt = place.getAddress().toString();
                    if (locTxt.endsWith(", USA"))
                        locTxt = locTxt.substring(0, locTxt.length() - 5);
                    eventLocationPOJO.location = locTxt;
                    eventLocationPOJO.latitude = place.getLatLng().latitude + "";
                    eventLocationPOJO.longitude = place.getLatLng().longitude + "";
                    //eventLocationPOJO.area = "With In 5 Mile";
                    eventLocationPOJO.isSelected = true;

                    if (!StringUtils.isEmpty(eventLocationPOJO.location)
                            && !isLocationEx(eventLocationPOJO)) {
                        locList.add(eventLocationPOJO);
                        locationAdapter.notifyDataSetChanged();

                        if (locationAdapter.getCount() > 2) {
                            ViewGroup.LayoutParams params = mLVLocation.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = ScreenUtil.convertDpToPx(105, mBaseActivity);
                            mLVLocation.setLayoutParams(params);
                        }
                    } else {
                        Toast.makeText(mBaseActivity, "Location you selected is already in the list", Toast.LENGTH_LONG).show();
                    }
                    break;
                case WEEK_VIEW_REQUEST:
                    mWVEventStrList = data.getStringArrayListExtra("eventsFromCalendar");
                    timeList.clear();
                    if (mWVEventStrList != null && mWVEventStrList.size() > 0) {
                        Gson gson = new Gson();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
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
                                eventTimePOJO.serviceId = mEventDetail.eventData.serviceId;
                                eventTimePOJO.eventRequestId = mEventDetail.eventData.id;
                                eventTimePOJO.timeZone = timeZone;
                                eventTimePOJO.isSelected = true;
                                timeList.add(eventTimePOJO);
                            }
                        }
                    }
                    timeAdapter.notifyDataSetChanged();

                    if (timeAdapter.getCount() > 2) {
                        ViewGroup.LayoutParams params = mLVTime.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height = ScreenUtil.convertDpToPx(105, mBaseActivity);
                        mLVTime.setLayoutParams(params);
                    }
                    break;
            }
        }
    }

    private boolean isLocationEx(EventLocationPOJO location) {
        ArrayList<EventLocationPOJO> tmp = locationAdapter.getAll();
        for (int i = 0; i < tmp.size(); i++) {
            if (tmp.get(i).location.equals(location.location)) {
                return true;
            }
        }
        return false;
    }

    private void expertSubmit() {
        if (post.eventDateList.size() == 0 || post.eventLocationList.size() == 0) {
            PromeetsDialog.show(mBaseActivity, "Please select at least one time slot and one location.");
            return;
        }

        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/expertConfirmedAll"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Gson gson = new Gson();
        String postJson = gson.toJson(post);
        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), postJson.toString());

        Call<BaseResp> call = service.expertConfirmedAll(requestBody);//get request, need to be post!
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
                    PromeetsDialog.show(mBaseActivity, "Request submitted successfully!", new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            dismiss();
                            if (mBaseActivity instanceof AppointStatusActivity) {
                                mBaseActivity.finish();
                                startActivity(mBaseActivity.getIntent());
                            }
                        }
                    });
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP) || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(mBaseActivity, result.info.code);
                } else {
                    PromeetsDialog.show(mBaseActivity, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }
}
