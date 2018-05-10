package com.promeets.android.fragment;

import com.promeets.android.activity.CalendarViewActivity;
import com.promeets.android.activity.GroupChatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.promeets.android.object.EventLocationPOJO;
import android.os.Bundle;
import com.promeets.android.pojo.EventDetailResp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.adapter.LocationSingleCheckAdapter;
import com.promeets.android.adapter.TimeChatExpertAdapter;
import com.alamkanak.weekview.WeekViewEvent;
import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.promeets.android.Constant;
import com.promeets.android.object.ChatAppointPOJO;
import com.promeets.android.object.EventData;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.R;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import com.promeets.android.pojo.BaseResp;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
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

import static android.app.Activity.RESULT_OK;

/**
 * Expert set up Appointment time and location through Chat
 */

public class GroupChatExpertFragment extends DialogFragment {

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
    private ArrayList<EventTimePOJO> timeList;
    private ArrayList<String> mWVEventStrList;
    private ArrayList<EventLocationPOJO> locList;
    private LocationSingleCheckAdapter locationAdapter;
    private TimeChatExpertAdapter timeAdapter;
    private boolean isLocationEnabled;
    private Gson gson = new Gson();

    public static GroupChatExpertFragment newInstance(EventDetailResp mEventDetail) {
        GroupChatExpertFragment f = new GroupChatExpertFragment();
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

        timeList = new ArrayList<>();
        timeAdapter = new TimeChatExpertAdapter(mBaseActivity, timeList);
        mLVTime.setAdapter(timeAdapter);
        locList = new ArrayList<>();
        locationAdapter = new LocationSingleCheckAdapter(mBaseActivity, this, locList);
        mLVLocation.setAdapter(locationAdapter);

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
                mChkCall.setChecked(true);
                locationAdapter.clearSelection();
            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mBaseActivity, CalendarViewActivity.class);
                intent.putExtra("isSingleSelect", true);
                intent.putExtra("expId", mEventDetail.eventData.expId);
                intent.putExtra("duratingTime", new Float(mEventDetail.expertService.duratingTime).floatValue());
                startActivityForResult(intent, WEEK_VIEW_REQUEST);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
                if (timeList.size() == 0 || (locList.size() == 0 && !mChkCall.isChecked())) {
                    PromeetsDialog.show(mBaseActivity, "Please propose one time slot and one location.");
                    return;
                }

                ChatAppointPOJO chatData = new ChatAppointPOJO();
                chatData.eventId = mEventDetail.eventData.id;
                chatData.eventRequest = new EventData();
                chatData.eventRequest.id = mEventDetail.eventData.id;
                chatData.serviceTitle = mEventDetail.expertService.title;

                if (mChkCall.isChecked()) {
                    EventLocationPOJO loc = new EventLocationPOJO();
                    loc.status = 2;
                    loc.location = "Online Call";
                    loc.latitude = "-1";
                    loc.longitude = "-1";
                    chatData.eventLocationList.add(loc);
                } else
                    chatData.eventLocationList.add(locList.get(0));

                EventTimePOJO pojo = timeList.get(0);
                pojo.id = 0;
                chatData.eventDateList.add(pojo);

                String json = gson.toJson(chatData);
                ((GroupChatActivity)mBaseActivity).sendCustomUserMessage(json);

                if (mEventDetail.eventAction.displayStep == 1)
                    expertConfirm(json);
                dismiss();
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
                    eventLocationPOJO.isSelected = true;

                    if (!StringUtils.isEmpty(eventLocationPOJO.location)) {
                        locList.clear();
                        locList.add(eventLocationPOJO);
                        locationAdapter.notifyDataSetChanged();
                        mChkCall.setChecked(false);
                    }
                    break;
                case WEEK_VIEW_REQUEST:
                    mWVEventStrList = data.getStringArrayListExtra("eventsFromCalendar");
                    if (mWVEventStrList != null && mWVEventStrList.size() > 0) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
                        //for (String mStrEvent : mWVEventStrList) {
                        String mStrEvent = mWVEventStrList.get(0);
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
                            eventTimePOJO.utcBeginTime = mEvent.getStartTime().getTimeInMillis();
                            eventTimePOJO.utcEndTime = mEvent.getEndTime().getTimeInMillis();
                            timeList.clear();
                            timeList.add(eventTimePOJO);
                        }
                    }
                    timeAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void expertConfirm(String json) {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/expertConfirmedAll"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Call<BaseResp> call = service.expertConfirmedAll(requestBody);//get request, need to be post!
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if (mBaseActivity.isSuccess(result.info.code)) {
                    dismiss();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP) || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(mBaseActivity, result.info.code);
                } else {
                    PromeetsDialog.show(mBaseActivity, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }

    public CheckedTextView getChkCall() {
        return mChkCall;
    }
}
