package com.promeets.android.activity;

import com.promeets.android.adapter.LocationAdapter;
import com.promeets.android.adapter.LocationSingleCheckAdapter;
import com.promeets.android.adapter.RecycleReviewAdapter;
import com.promeets.android.adapter.TimeAdapter;
import com.promeets.android.adapter.TimeSingleCheckAdapter;
import com.promeets.android.api.EventApi;
import com.promeets.android.api.ReviewApi;
import com.promeets.android.api.URL;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.promeets.android.custom.NoScrollListView;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.EventAcceptFragment;
import com.promeets.android.fragment.EventOptionFragment;
import com.promeets.android.fragment.JoinMeetingFragment;
import com.promeets.android.fragment.RescheduleFragment;
import android.graphics.Paint;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import android.net.Uri;
import com.promeets.android.object.EventData;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.ServiceReview;
import com.promeets.android.object.UserPOJO;
import android.os.Bundle;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.CancelMeetingPost;
import com.promeets.android.pojo.EventDetailResp;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.SuperResp;

import android.os.CountDownTimer;
import android.provider.CalendarContract;
import com.promeets.android.services.GenericServiceHandler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.FirebaseUtil;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.UserInfoHelper;
import com.promeets.android.util.Utility;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.pojo.SuperPost;
import com.promeets.android.util.ScreenUtil;

import com.promeets.android.R;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import at.blogc.android.views.ExpandableTextView;
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
 * IMPORTANT ACTIVITY!!!
 *
 * This is for showing appointment status
 *
 * Include information about: (Visibility depends on step status)
 * Topic detail,
 * Time & Location (different adapter for view only, single selection, multiple selections),
 * Question & About me,
 * Cancel reason, review content, order details
 *
 * Popup view:
 * EventAcceptFragment, event_cancel_layout, evaluation_layout
 *
 * @source: My Appointment in AccountFragment
 *
 * @destination: UserProfileActivity/ExpertDetailActivity, MapActivity, ServiceReviewActivity, ChatActivity
 * startActivityForResult: PaymentActivity
 *
 */

public class AppointStatusActivity extends BaseActivity
        implements OnMapReadyCallback, IServiceResponseHandler {

    @BindView(R.id.primary_line)
    View primaryLine;
    @BindView(R.id.gray_line)
    View grayLine;
    @BindView(R.id.text1)
    TextView step_text_1;
    @BindView(R.id.circle1)
    View step_circle_1;
    @BindView(R.id.text2)
    TextView step_text_2;
    @BindView(R.id.circle2)
    View step_circle_2;
    @BindView(R.id.text3)
    TextView step_text_3;
    @BindView(R.id.circle3)
    View step_circle_3;
    @BindView(R.id.text4)
    TextView step_text_4;
    @BindView(R.id.circle4)
    View step_circle_4;
    @BindView(R.id.text5)
    TextView step_text_5;
    @BindView(R.id.circle5)
    View step_circle_5;

    // expert service
    @BindView(R.id.status_image)
    ImageView mImgStatus;
    @BindView(R.id.status_desc)
    TextView mTxtStatus;
    @BindView(R.id.photo)
    CircleImageView mImgPhoto;
    @BindView(R.id.topic)
    TextView mTxtTopic;
    @BindView(R.id.exp_name)
    TextView mTxtExpName;
    @BindView(R.id.price)
    TextView mTxtPrice;
    @BindView(R.id.orig_price)
    TextView mTxtOrgPrice;
    @BindView(R.id.position)
    TextView mTxtPosition;
    @BindView(R.id.chat_btn)
    ImageView mBtnChat;

    @BindView(R.id.time_title)
    TextView mTitleTime;
    @BindView(R.id.location_title)
    TextView mTitleLoc;
    @BindView(R.id.call_layer)
    View mLayCall;
    @BindView(R.id.meet_layer)
    View mLayMeet;
    @BindView(R.id.call_txt)
    View mTxtCall;
    @BindView(R.id.call_check_layer)
    View mLayChkCall;
    @BindView(R.id.call_check)
    CheckedTextView mChkCall;
    @BindView(R.id.time_list)
    NoScrollListView mLVTime;
    @BindView(R.id.location_list)
    NoScrollListView mLVLocation;
    @BindView(R.id.question_title)
    TextView mTitleQues;
    @BindView(R.id.question)
    ExpandableTextView mTxtQues;
    @BindView(R.id.question_lay)
    LinearLayout mLayQues;
    @BindView(R.id.about)
    ExpandableTextView mTxtAbout;
    @BindView(R.id.about_lay)
    LinearLayout mLayAbout;
    @BindView(R.id.about_title)
    TextView mTitleAbout;
    @BindView(R.id.user_card)
    View mUserCard;
    @BindView(R.id.map_front)
    View mapFront;
    @BindView(R.id.countdown)
    TextView mTxtCountDown;
    @BindView(R.id.join_online)
    View mLayJoinOnline;

    // Cancel Reason
    @BindView(R.id.cancel_lay)
    LinearLayout mLayCancel;
    @BindView(R.id.cancel_title)
    TextView mTitleCancel;
    @BindView(R.id.cancel_reason)
    ExpandableTextView mTxtCancelReason;

    // Review Content
    @BindView(R.id.review_contents_lay)
    LinearLayout mLayReviewContents;
    @BindView(R.id.review_list)
    RecyclerView mRVReview;

    // Order detail
    @BindView(R.id.order_date)
    TextView mTxtOrderDate;
    @BindView(R.id.order_num)
    TextView mTxtOrderNum;
    @BindView(R.id.order_total)
    TextView mTxtOrderTotal;


    @BindView(R.id.decline)
    TextView mBtnDecline;
    @BindView(R.id.accept)
    TextView mBtnAccept;
    @BindView(R.id.interest_lay)
    LinearLayout mLayInterest;
    @BindView(R.id.cancel_request)
    TextView mTxtCancel;
    @BindView(R.id.payment_lay)
    LinearLayout mLayPayment;
    @BindView(R.id.payment)
    TextView mTxtPayment;
    @BindView(R.id.review_lay)
    LinearLayout mLayReview;
    @BindView(R.id.leave_review)
    TextView review_btn;

    @BindView(R.id.reschedule_txt)
    TextView mTxtReschedule;


    // Cancel popup
    @BindView(R.id.cancel_popup)
    View mPopupCancel;
    @BindView(R.id.cancel_popup_content)
    EditText mTxtContent;
    // Evaluation popup
    @BindView(R.id.evaluation_popup)
    View mPopupEvaluation;
    @BindView(R.id.rating_bar)
    ScaleRatingBar ratingBar;
    @BindView(R.id.fragment_evaluation_popup_message)
    EditText mTxtMessage;
    @BindView(R.id.fragment_evaluation_popup_submit)
    TextView submit;

    private AppointStatusActivity instance;
    private int eventId;
    private int displayStep;
    private UserPOJO userPOJO;
    private OnMapReadyCallback mapReadyCallback;
    private ArrayList<EventLocationPOJO> defaultLocationList = new ArrayList<>();
    private ArrayList<EventLocationPOJO> singleLocationList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private HashMap<EventLocationPOJO, Marker> map = new HashMap<>();
    private EventDetailResp result;
    private boolean isPaymentClicked = false;
    public boolean timeSelected = false, locationSelected = false;
    private TimeSingleCheckAdapter singleTimeAdapter;
    private LocationSingleCheckAdapter singleLocAdapter;
    private RecycleReviewAdapter reviewAdapter;

    private Animation inAnimation;
    private Animation outAnimation;
    boolean boolTag1,boolTag2,boolTag3,boolTag4;

    private EventLocationPOJO mLocCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint_status);
        ButterKnife.bind(this);
        instance = this;
        mapReadyCallback = this;
        AndroidBug5497Workaround.assistActivity(this);

        eventId = getIntent().getIntExtra("eventRequestId", 0);
        if (eventId == 0) finish();
        userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (userPOJO == null) startActivity(MainActivity.class);
        fetchEventDetail();

        inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_dialog);
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_dialog);
    }

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTxtQues.isExpanded()) mTxtQues.collapse();
                else mTxtQues.expand();
            }
        });
        mTxtQues.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", mTxtQues.getText());
                clipboard.setPrimaryClip(clip);
                PromeetsDialog.show(instance, "Copied to clipboard");
                return true;
            }
        });
        mTxtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTxtAbout.isExpanded()) mTxtAbout.collapse();
                else mTxtAbout.expand();
            }
        });
        mTxtAbout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", mTxtAbout.getText());
                clipboard.setPrimaryClip(clip);
                PromeetsDialog.show(instance, "Copied to clipboard");
                return true;
            }
        });

        mapFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<EventLocationPOJO> list = new ArrayList<>();
                if (defaultLocationList != null && defaultLocationList.size() > 0)
                    list.addAll(defaultLocationList);
                else if (singleLocationList != null && singleLocationList.size() > 0)
                    list.addAll(singleLocationList);

                if (list.size() > 0) {
                    Intent intent = new Intent(instance, MapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", list);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        mUserCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.userData.isExpert==1){
                    Intent intent = new Intent(instance,ExpertDetailActivity.class);
                    intent.putExtra("expId",result.eventData.userId+"");
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                    String[] key = {Constant.EXPERTID, "prev_page"};
                    String[] value = {intent.getStringExtra("expId"), "Event Process"};
                    FirebaseUtil.getInstance(instance).buttonClick(FirebaseUtil.EXPERT_SCREEN_LOAD, key, value);
                }else{
                    Intent intent = new Intent(instance,UserProfileActivity.class);
                    intent.putExtra("id",result.eventData.userId+"");
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupEvaluation();
            }
        });
        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromeetsDialog.show(instance, "Do you want to cancel this appointment?", "No", "Yes", new PromeetsDialog.OnSubmitListener() {
                    @Override
                    public void onSubmitListener() {
                        showPopupCancel(false);
                    }
                });
            }
        });

        mBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InputEventCancelFragment dialogFragment = InputEventCancelFragment.newInstance(eventId, true);
                //dialogFragment.show(getFragmentManager(), "decline");
                //dialogFragment.setCancelable(true);
                showPopupCancel(true);
            }
        });

        // expert only
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SuperPost post = new SuperPost();
                EventData eventData = new EventData();
                eventData.id = eventId;
                post.eventRequest = eventData;
                EventAcceptFragment dialogFragment = EventAcceptFragment.newInstance(result);
                dialogFragment.show(getFragmentManager(), "accept");
            }
        });
        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChat();
            }
        });

        mTxtPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtPayment.setEnabled(false);
                isPaymentClicked = true;
                Intent intent = new Intent(instance, PaymentActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("servicePhotoUrl", result.expertProfile.smallphotoUrl);
                bundle.putString("ServiceTopic", result.expertService.title);
                bundle.putString("author", result.expertProfile.fullName);
                bundle.putString("position", result.expertProfile.positon);
                bundle.putString("durationTime", result.expertService.duratingTime);
                bundle.putString("origPrice", result.expertService.originalPrice);
                bundle.putString("ServicePrice", result.expertService.price);
                bundle.putInt("expId", result.expertProfile.expId);
                bundle.putInt("eventRequestId", eventId);
                bundle.putInt("serviceId", result.expertService.id);
                bundle.putString("userActiveNeedPay", result.chargeDetail.get("userActiveNeedPay"));
                bundle.putString("chargeTip", result.chargeDetail.get("chargeTip"));
                bundle.putString("balance", result.chargeDetail.get("balance"));

                bundle.putSerializable("time", singleTimeAdapter.getSingleSelected());
                if (mChkCall.isChecked() && mLocCall != null)
                    bundle.putSerializable("location", mLocCall);
                else
                    bundle.putSerializable("location", singleLocAdapter.getSingleSelected());

                intent.putExtras(bundle);
                startActivityForResult(intent, 110);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void fetchEventDetail() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/fetchEventDetail"))
                .addConverterFactory(GsonConverterFactory.create())

                .build();

        EventApi service = retrofit.create(EventApi.class);
        UserInfoHelper userInfoHelper = new UserInfoHelper(this);
        userPOJO = userInfoHelper.getUserObject();
        Call<EventDetailResp> call = service.fetchEventDetail(eventId, userPOJO.id, TimeZone.getDefault().getID());
        call.enqueue(new Callback<EventDetailResp>() {
            @Override
            public void onResponse(Call<EventDetailResp> call, Response<EventDetailResp> response) {
                PromeetsDialog.hideProgress();
                result = response.body();
                if (isSuccess(result.info.code)) {
                    // status
                    displayStep = result.eventAction.displayStep;
                    setProcessSteps(displayStep);
                    mTxtStatus.setText(result.eventAction.readableStatus);

                    showReschedule(displayStep);

                    if (result.eventAction.actionStatus.contains("USER")) {
                        if (displayStep == 0) { // CANCEL
                            setStatusImage(0);
                        } else {
                            setStatusImage(displayStep + 5);
                        }
                        mTitleAbout.setText("About Me");
                        mTitleQues.setText("My Question");
                    } else if (result.eventAction.actionStatus.contains("EXPERT")) {
                        setStatusImage(displayStep);
                        mTitleAbout.setText("About Him/Her");
                        mTitleQues.setText("His/Her Question");
                    }

                    // expert service
                    if (result.expertProfile.smallphotoUrl != null)
                        Glide.with(instance).load(result.expertProfile.smallphotoUrl).into(mImgPhoto);
                    else if (result.expertProfile.photoUrl != null)
                        Glide.with(instance).load(result.expertProfile.photoUrl).into(mImgPhoto);
                    mTxtTopic.setText(result.expertService.title);
                    mTxtExpName.setText(result.expertProfile.fullName);
                    mTxtPrice.setText("$" + NumberFormatUtil.getInstance().getCurrency(result.expertService.price));
                    if (!StringUtils.isEmpty(result.expertService.originalPrice)) {
                        mTxtOrgPrice.setText("$" + NumberFormatUtil.getInstance().getCurrency(result.expertService.originalPrice));
                        mTxtOrgPrice.setPaintFlags(mTxtOrgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    mTxtPosition.setText(result.expertProfile.positon);

                    // time & location
                    if (displayStep == 2 && result.eventAction.actionStatus.contains("USER")) {
                        mTitleTime.setText("Expert Proposed Time");
                        //mTitleLoc.setText("Expert Proposed Location");
                        singleTimeAdapter = new TimeSingleCheckAdapter(instance, result.eventDateList);
                        mLVTime.setAdapter(singleTimeAdapter);

                        for (EventLocationPOJO loc : result.eventLocationList) {
                            if (loc.status == 2) {
                                mLocCall = loc;
                                result.eventLocationList.remove(loc);
                                mLayCall.setVisibility(View.VISIBLE);
                                mTxtCall.setVisibility(View.GONE);
                                mLayChkCall.setVisibility(View.VISIBLE);
                                mLayChkCall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mChkCall.setChecked(true);
                                        if (singleLocAdapter != null)
                                            singleLocAdapter.clearSelection();
                                        locationSelected = true;
                                        updatePayment();

                                    }
                                });
                            }
                        }
                        if (result.eventLocationList != null && result.eventLocationList.size() > 0) {
                            mLayMeet.setVisibility(View.VISIBLE);

                            singleLocAdapter = new LocationSingleCheckAdapter(instance, result.eventLocationList);
                            mLVLocation.setAdapter(singleLocAdapter);
                            singleLocationList.clear();
                            singleLocationList.addAll(result.eventLocationList);
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(mapReadyCallback);
                        }
                    } else {
                        TimeAdapter timeAdapter = new TimeAdapter(instance, result.eventDateList);
                        mLVTime.setAdapter(timeAdapter);

                        for (EventLocationPOJO loc : result.eventLocationList) {
                            if (loc.status == 2) {
                                result.eventLocationList.remove(loc);
                                mLayCall.setVisibility(View.VISIBLE);
                            }
                        }
                        if (displayStep == 3 && mLayCall.getVisibility() == View.VISIBLE) {
                            mLayJoinOnline.setVisibility(View.VISIBLE);
                            long startTime = result.eventDateList.get(0).utcBeginTime * 1000;
                            long curTime = System.currentTimeMillis();
                            long gapInMin = (startTime - curTime) / (60 * 1000);

                            // show CountDownTimer for coming meeting in 60 mins
                            if (gapInMin <= 60) {
                                mTxtCountDown.setVisibility(View.VISIBLE);
                                new CountDownTimer(startTime - curTime, 60000) {
                                    public void onTick(long millisUntilFinished) {
                                        mTxtCountDown.setText("In " + (int)(millisUntilFinished / 1000 / 60)+ " Mins");
                                    }

                                    public void onFinish() {
                                        mTxtCountDown.setText("START");
                                    }
                                }.start();
                            }

                            // popup JoinMeeting Dialog
                            if (gapInMin > 10) {
                                mLayJoinOnline.setOnClickListener(view -> {
                                    if (result.videoData != null) {
                                        JoinMeetingFragment dialogFragment = JoinMeetingFragment.newInstance(result.videoData);
                                        dialogFragment.show(getFragmentManager(), "join meeting");
                                    }
                                });
                            } else {
                                mLayJoinOnline.setOnClickListener(view -> {
                                    if (result.videoData != null) {
                                        Intent intent = new Intent(AppointStatusActivity.this, VideoChatActivity.class);
                                        intent.putExtra("name", result.videoData.fullName);
                                        intent.putExtra("photoUrl", result.videoData.smallPhotoUrl);
                                        intent.putExtra("appId", result.videoData.appId);
                                        intent.putExtra("uid", result.videoData.uid);
                                        intent.putExtra("channelName", result.videoData.channelName);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                    }
                                });
                            }
                        }

                        if (result.eventLocationList != null && result.eventLocationList.size() > 0) {
                            mLayMeet.setVisibility(View.VISIBLE);

                            LocationAdapter locAdapter = new LocationAdapter(instance, result.eventLocationList);
                            mLVLocation.setAdapter(locAdapter);
                            defaultLocationList.clear();
                            defaultLocationList.addAll(result.eventLocationList);
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(mapReadyCallback);
                        }
                    }

                    // event
                    mTxtQues.setText(result.eventData.whyInterested);
                    mTxtAbout.setText(result.eventData.aboutMe);
                    CircleImageView mImgUser = (CircleImageView) mUserCard.findViewById(R.id.user_photo);
                    TextView mTxtUserName = (TextView) mUserCard.findViewById(R.id.user_name);
                    TextView mTxtUserCity = (TextView) mUserCard.findViewById(R.id.user_city);
                    if (!StringUtils.isEmpty(result.userData.smallPhotoUrl))
                        Glide.with(instance).load(result.userData.smallPhotoUrl).into(mImgUser);
                    mTxtUserName.setText(result.userData.fullName);
                    mTxtUserCity.setText(result.userData.cityName);

                    // Cancel Reason
                    if (displayStep == 0) {
                        mLayCancel.setVisibility(View.VISIBLE);
                        if(result.eventAction.readableStatus.toLowerCase().contains("user")){
                            mTitleCancel.setText("Reason to cancel");
                        } else if(result.eventAction.readableStatus.toLowerCase().contains("expert")){
                            mTitleCancel.setText("Reason to decline");
                        }
                        mTxtCancelReason.setText(result.eventData.rejectResult);
                    }

                    // Review Content
                    //review
                    if (result.eventAction.actionStatus.contains("ENDED") && displayStep == 5 && result.review != null) {
                        mLayReviewContents.setVisibility(View.VISIBLE);
                        //handleReviewLayout();
                        ArrayList<ServiceReview> serviceReview = new ArrayList<>();
                        serviceReview.add(result.review);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(instance);
                        mRVReview.setLayoutManager(linearLayoutManager);

                        if (userPOJO.id == result.review.expertId)
                            reviewAdapter = new RecycleReviewAdapter(instance, serviceReview, true);
                        else
                            reviewAdapter = new RecycleReviewAdapter(instance, serviceReview, false);
                        mRVReview.setAdapter(reviewAdapter);
                    }

                    // Leave a review
                    if (displayStep == 4 && result.eventAction.actionStatus.toLowerCase().contains("user")) {
                        mLayReview.setVisibility(View.VISIBLE);
                    }

                    // Order
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    try {
                        Date date = sdf.parse(result.eventData.createTime);
                        sdf = new SimpleDateFormat("MMM dd, yyyy");
                        mTxtOrderDate.setText(sdf.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        mTxtOrderDate.setText(result.eventData.createTime);
                    }
                    mTxtOrderNum.setText(result.eventData.serialNumber);
                    mTxtOrderTotal.setText("$" + NumberFormatUtil.getInstance().getCurrency(result.expertService.price));

                    /**
                     * Layout visibility according to displayStep
                     */
                    /**
                     * USER_UPCOMING
                     * USER_PENDING
                     * EXPERT_UPCOMING
                     * EXPERT_PENDING
                     */
                    /**
                     * EXP_ACCEPT
                     * EXP_CANCEL
                     * EXP_DECLINE
                     * EXP_UPDATE
                     * USER_CANCEL
                     * USER_CONTACT
                     * USER_PAYMENT
                     * USER_REVIEW
                     */
                    if (result.eventAction.displayButton.USER_CONTACT) {
                        mBtnChat.setVisibility(View.VISIBLE);
                    }
                    if (result.eventAction.displayButton.USER_CANCEL
                            || result.eventAction.displayButton.EXP_CANCEL) {
                        mTxtCancel.setVisibility(View.VISIBLE);
                    }
                    if (result.eventAction.displayButton.EXP_ACCEPT
                            && result.eventAction.displayButton.EXP_DECLINE) {
                        mLayInterest.setVisibility(View.VISIBLE);
                    }
                    if (result.eventAction.displayButton.USER_PAYMENT)
                        mLayPayment.setVisibility(View.VISIBLE);


                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP) || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(AppointStatusActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(instance, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<EventDetailResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(instance, t.getLocalizedMessage());
            }
        });
    }

    private void showReschedule(int step) {
        if (step == 2 || step == 7 || step == 3 || step == 8) {
            mTxtReschedule.setVisibility(View.VISIBLE);
            mTxtReschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RescheduleFragment dialogFragment = new RescheduleFragment();
                    dialogFragment.show(instance.getSupportFragmentManager(), "reschedule");
                    dialogFragment.setCancelable(true);
                }
            });
        } else
            mTxtReschedule.setVisibility(View.GONE);
    }

    /**
     * Title layout
     *
     * @param step
     */
    private void setProcessSteps(int step) {
        /**
         * 0: cancel
         */
        LinearLayout.LayoutParams param;

        switch (step) {
            case 0:
                break;
            case 1:
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 1.5f);
                primaryLine.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 4.5f);
                grayLine.setLayoutParams(param);
                step_text_1.setTextColor(getResources().getColor(R.color.primary));
                step_circle_1.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                break;
            case 2:
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 2.5f);
                primaryLine.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 3.5f);
                grayLine.setLayoutParams(param);
                step_text_1.setTextColor(getResources().getColor(R.color.primary));
                step_circle_1.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_2.setTextColor(getResources().getColor(R.color.primary));
                step_circle_2.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                break;
            case 3:
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 3.5f);
                primaryLine.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 2.5f);
                grayLine.setLayoutParams(param);
                step_text_1.setTextColor(getResources().getColor(R.color.primary));
                step_circle_1.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_2.setTextColor(getResources().getColor(R.color.primary));
                step_circle_2.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_3.setTextColor(getResources().getColor(R.color.primary));
                step_circle_3.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                break;
            case 4:
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 4.5f);
                primaryLine.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 1.5f);
                grayLine.setLayoutParams(param);
                step_text_1.setTextColor(getResources().getColor(R.color.primary));
                step_circle_1.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_2.setTextColor(getResources().getColor(R.color.primary));
                step_circle_2.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_3.setTextColor(getResources().getColor(R.color.primary));
                step_circle_3.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_4.setTextColor(getResources().getColor(R.color.primary));
                step_circle_4.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                break;
            case 5:
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 6);
                primaryLine.setLayoutParams(param);
                param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 0);
                grayLine.setLayoutParams(param);
                step_text_1.setTextColor(getResources().getColor(R.color.primary));
                step_circle_1.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_2.setTextColor(getResources().getColor(R.color.primary));
                step_circle_2.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_3.setTextColor(getResources().getColor(R.color.primary));
                step_circle_3.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_4.setTextColor(getResources().getColor(R.color.primary));
                step_circle_4.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                step_text_5.setTextColor(getResources().getColor(R.color.primary));
                step_circle_5.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
                break;
        }
    }

    private void setStatusImage(int status) {
        switch (status) {
            case 0:
                mImgStatus.setImageResource(R.drawable.ic_cancel);
                break;
            case 1:
                mImgStatus.setImageResource(R.drawable.ic_confirm);
                break;
            case 2:
                mImgStatus.setImageResource(R.drawable.expert_payment);
                break;
            case 3:
                mImgStatus.setImageResource(R.drawable.ic_meet);
                break;
            case 4:
                mImgStatus.setImageResource(R.drawable.expert_review);
                break;
            case 5:
                mImgStatus.setImageResource(R.drawable.ic_complete);
                break;
            case 6:
                mImgStatus.setImageResource(R.drawable.ic_confirm);
                break;
            case 7:
                mImgStatus.setImageResource(R.drawable.user_payment);
                break;
            case 8:
                mImgStatus.setImageResource(R.drawable.ic_meet);
                break;
            case 9:
                mImgStatus.setImageResource(R.drawable.user_review);
                break;
            case 10:
                mImgStatus.setImageResource(R.drawable.ic_complete);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(isPaymentClicked){
            Intent intent = getIntent();
            startActivity(intent);
            finish();
            isPaymentClicked = false;
        }

        if (reviewAdapter != null)
            reviewAdapter.onActivityResult(requestCode, resultCode);
    }

    public void handleChat() {
        Intent intent = new Intent(this, GroupChatActivity.class);
        UserInfoHelper helper = new UserInfoHelper(this);
        if (helper.getExpertProfile() != null
                && result.eventData.expId == helper.getExpertProfile().expId) {
            intent.putExtra("targetId", "userName" + result.eventData.userId);
            intent.putExtra("targetName", result.userData.fullName);
        } else {
            intent.putExtra("targetId", "userName" + result.eventData.expId);
            intent.putExtra("targetName", result.expertProfile.fullName);
        }
        this.startActivity(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setMaxZoomPreference(15);

        if (defaultLocationList != null && defaultLocationList.size() > 0) {
            for (EventLocationPOJO location : defaultLocationList) {
                if (StringUtils.isEmpty(location.latitude) || StringUtils.isEmpty(location.longitude)) return;

                LatLng loc = new LatLng(Double.valueOf(location.latitude), Double.valueOf(location.longitude));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary)));
                markerList.add(marker);
                map.put(location, marker);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markerList) {
                builder.include(marker.getPosition());
            }
            final LatLngBounds bounds = builder.build();
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                            ScreenUtil.convertDpToPx(50, instance)));
                }
            });
        } else if (singleLocationList != null && singleLocationList.size() > 0) {
            for (EventLocationPOJO location : singleLocationList) {
                if (StringUtils.isEmpty(location.latitude) || StringUtils.isEmpty(location.longitude)) return;

                LatLng loc = new LatLng(Double.valueOf(location.latitude), Double.valueOf(location.longitude));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_gray)));
                markerList.add(marker);
                map.put(location, marker);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markerList) {
                builder.include(marker.getPosition());
            }
            final LatLngBounds bounds = builder.build();
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                            ScreenUtil.convertDpToPx(50, instance)));
                }
            });
        }
    }

    public void updatePayment() {
        if (timeSelected && locationSelected) {
            mTxtPayment.setBackground(getResources().getDrawable(R.drawable.btn_solid_primary));
            mTxtPayment.setEnabled(true);
        } else {
            mTxtPayment.setBackground(getResources().getDrawable(R.drawable.btn_solid_grey));
            mTxtPayment.setEnabled(false);
        }
    }

    public HashMap<EventLocationPOJO, Marker> getMap() {
        return map;
    }

    public RecycleReviewAdapter getReviewAdapter() {
        return reviewAdapter;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        LoginResp loginResp = (LoginResp) serviceResponse.getServiceResponse(LoginResp.class);
        if(loginResp.info.code.equals("2003")){
            PromeetsDialog.show(this, loginResp.info.description, "Keep my meeting", "Confirm Cancellation", new PromeetsDialog.OnSubmitListener() {
                @Override
                public void onSubmitListener() {
                    callForceCancel();
                }
            });
        } else if(isSuccess(loginResp.info.code)){
            PromeetsDialog.show(this, "Request submitted successfully!", new PromeetsDialog.OnOKListener() {
                @Override
                public void onOKListener() {
                    finish();
                }
            });

            // delete event in Calendar and SharedPref
            SharedPreferences pref = getSharedPreferences("calendar_event", MODE_PRIVATE);
            String calEventId = pref.getString(String.valueOf(eventId), "-1");
            if (!calEventId.equals("-1")) {
                Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.valueOf(calEventId));
                getContentResolver().delete(deleteUri, null, null);

                SharedPreferences.Editor editor = pref.edit();
                editor.remove(String.valueOf(eventId));
                editor.apply();
            }
        }else{
            onErrorResponse(loginResp.info.description);
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    //region Cancel Popup
    private void showPopupCancel(final boolean isExpert) {
        mPopupCancel.startAnimation(inAnimation);
        mPopupCancel.setVisibility(View.VISIBLE);

        View mShadow = findViewById(R.id.shadow);
        TextView mTxtTitle = (TextView) findViewById(R.id.title);
        ImageView mImgClose = (ImageView) findViewById(R.id.close);
        //final EditText mTxtContent = (EditText) findViewById(R.id.content);
        final TextView mBtnSend = (TextView) findViewById(R.id.cancel_popup_send);
        if (isExpert)
            mTxtTitle.setText("Reason to decline");
        else
            mTxtTitle.setText("Reason to cancel");
        mShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                mPopupCancel.startAnimation(outAnimation);
                mPopupCancel.setVisibility(View.GONE);
            }
        });
        mImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                mPopupCancel.startAnimation(outAnimation);
                mPopupCancel.setVisibility(View.GONE);
            }
        });
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                String desc = mTxtContent.getText().toString();

                if (!StringUtils.isEmpty(desc) && desc.split(" ").length >= 10) {
                    if (isExpert) expertCancel();
                    else userCancel();
                } else
                    PromeetsDialog.show(instance, "Cannot enter less than 10 words");
            }
        });

        mTxtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    mBtnSend.setTextColor(getResources().getColor(R.color.pm_gray));
                    mBtnSend.setEnabled(false);
                } else {
                    mBtnSend.setTextColor(getResources().getColor(R.color.primary));
                    mBtnSend.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTxtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTxtContent.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void expertCancel() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(instance, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/expertRefused"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JSONObject json = new JSONObject();
        try {
            json.put("id",eventId);
            json.put("rejectResult",mTxtContent.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        EventApi service = retrofit.create(EventApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Call<BaseResp> call = service.expertRefused(requestBody);//get request, need to be post!
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(instance, response.errorBody().toString());
                    return;
                }

                if(isSuccess(result.info.code)){
                    PromeetsDialog.show(instance, "Request submitted successfully!", new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            startActivity(getIntent());
                            finish();
                        }
                    });
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP)|| result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(instance,result.info.code);
                } else
                    PromeetsDialog.show(instance, result.info.description);
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(AppointStatusActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void userCancel() {
        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
            HashMap<String, String> header = new HashMap<>();
            header.put(Constant.CONTENT_TYPE,Constant.CONTENT_TYPE_VALUE);

            header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
            header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
            header.put("promeetsT",ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.CANCEL_MEETING_REQUEST));
            header.put("API_VERSION", Utility.getVersionCode());

            CancelMeetingPost cancelMeetingPost = new CancelMeetingPost();
            cancelMeetingPost.setId(eventId+"");
            cancelMeetingPost.setRejectResult(mTxtContent.getText().toString());
            new GenericServiceHandler(Constant.ServiceType.NORMAL_USER_LOGIN, this, Constant.BASE_URL + Constant.CANCEL_MEETING_REQUEST, "", cancelMeetingPost, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private void callForceCancel() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
            HashMap<String, String> header = new HashMap<>();
            header.put(Constant.CONTENT_TYPE,Constant.CONTENT_TYPE_VALUE);

            header.put("accessToken",ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
            header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
            header.put("promeetsT",ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FORCE_CANCEL_MEETING_REQUEST));

            CancelMeetingPost cancelMeetingPost = new CancelMeetingPost();
            cancelMeetingPost.setId(eventId + "");
            cancelMeetingPost.setRejectResult(mTxtContent.getText().toString());

            new GenericServiceHandler(Constant.ServiceType.NORMAL_USER_LOGIN, this, Constant.BASE_URL + Constant.FORCE_CANCEL_MEETING_REQUEST, "", cancelMeetingPost, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }
    //endregion

    //region Evaluation Popup
    private void showPopupEvaluation(){

        mPopupEvaluation.startAnimation(inAnimation);
        mPopupEvaluation.setVisibility(View.VISIBLE);

        final TextView mTxtRating = (TextView) findViewById(R.id.rating_txt);
        final TextView tag1 = (TextView) findViewById(R.id.fragment_evaluation_popup_tag1);
        final TextView tag2 = (TextView) findViewById(R.id.fragment_evaluation_popup_tag2);
        final TextView tag3 = (TextView) findViewById(R.id.fragment_evaluation_popup_tag3);
        final TextView tag4 = (TextView) findViewById(R.id.fragment_evaluation_popup_tag4);
        FrameLayout mLayRoot = (FrameLayout) findViewById(R.id.root_layout);
        LinearLayout mDialog = (LinearLayout) findViewById(R.id.dialog);

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                mPopupEvaluation.startAnimation(outAnimation);
                mPopupEvaluation.setVisibility(View.GONE);
            }
        });
        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTxtMessage.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar baseRatingBar, float rating) {
                switch ((int)rating) {
                    case 0:
                        mTxtRating.setText("Not Set");
                        mTxtRating.setTextColor(getResources().getColor(R.color.pm_gray));
                        break;
                    case 1:
                    case 2:
                        mTxtRating.setText("Bad");
                        mTxtRating.setTextColor(getResources().getColor(R.color.primary));
                        break;
                    case 3:
                    case 4:
                        mTxtRating.setText("Good");
                        mTxtRating.setTextColor(getResources().getColor(R.color.primary));
                        break;
                    case 5:
                        mTxtRating.setText("Perfect");
                        mTxtRating.setTextColor(getResources().getColor(R.color.primary));
                        break;
                }
                checkEnable();
            }
        });

        tag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!boolTag1){
                    tag1.setTextColor(getResources().getColor(R.color.white));
                    tag1.setBackground(getResources().getDrawable(R.drawable.tag_solid_primary));
                    boolTag1 =true;
                }else{
                    tag1.setTextColor(getResources().getColor(R.color.pm_gray));
                    tag1.setBackground(getResources().getDrawable(R.drawable.tag_border_gray));
                    boolTag1 = false;
                }
                checkEnable();
            }
        });
        tag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!boolTag2){
                    tag2.setTextColor(getResources().getColor(R.color.white));
                    tag2.setBackground(getResources().getDrawable(R.drawable.tag_solid_primary));
                    boolTag2 =true;
                }else{
                    tag2.setTextColor(getResources().getColor(R.color.pm_gray));
                    tag2.setBackground(getResources().getDrawable(R.drawable.tag_border_gray));
                    boolTag2 = false;
                }
                checkEnable();
            }
        });
        tag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!boolTag3){
                    tag3.setTextColor(getResources().getColor(R.color.white));
                    tag3.setBackground(getResources().getDrawable(R.drawable.tag_solid_primary));
                    boolTag3 =true;
                }else{
                    tag3.setTextColor(getResources().getColor(R.color.pm_gray));
                    tag3.setBackground(getResources().getDrawable(R.drawable.tag_border_gray));
                    boolTag3 = false;
                }
                checkEnable();
            }
        });
        tag4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!boolTag4){
                    tag4.setTextColor(getResources().getColor(R.color.white));
                    tag4.setBackground(getResources().getDrawable(R.drawable.tag_solid_primary));
                    boolTag4 =true;
                }else{
                    tag4.setTextColor(getResources().getColor(R.color.pm_gray));
                    tag4.setBackground(getResources().getDrawable(R.drawable.tag_border_gray));
                    boolTag4 = false;
                }
                checkEnable();
            }
        });
        mTxtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTxtMessage.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTxtMessage.getText().length() >= 1000)
                    PromeetsDialog.show(instance, "Cannot enter more than 1000 character");
                checkEnable();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasInternetConnection()) {
                    PromeetsDialog.show(instance, getString(R.string.no_internet));
                    return;
                }

                PromeetsDialog.showProgress(AppointStatusActivity.this);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL.HOST)
                        .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/userReview"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ReviewApi service = retrofit.create(ReviewApi.class);
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("description",mTxtMessage.getText());
                    jsonObject.put("eventRequestId", eventId);
                    jsonObject.put("rating",ratingBar.getRating());
                    jsonObject.put("ontime",boolTag1);
                    jsonObject.put("organization",boolTag4);
                    jsonObject.put("expertise",boolTag2);
                    jsonObject.put("effectiveness",boolTag3);
                    jsonObject.put("expertId",result.expertProfile.expId);
                    jsonObject.put("userId", userPOJO.id);
                    jsonObject.put("serviceId",result.expertService.id);
                }catch (Exception e){
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<SuperResp> call = service.createReview(requestBody);//get request, need to be post!
                call.enqueue(new Callback<SuperResp>() {
                    @Override
                    public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                        PromeetsDialog.hideProgress();
                        hideSoftKeyboard();
                        mPopupEvaluation.startAnimation(outAnimation);
                        mPopupEvaluation.setVisibility(View.GONE);

                        SuperResp resp = response.body();
                        if (resp == null) {
                            PromeetsDialog.show(AppointStatusActivity.this, response.errorBody().toString());
                            return;
                        }

                        if(isSuccess(resp.info.code)){
                            PromeetsDialog.show(instance, "Your review has been submitted.", new PromeetsDialog.OnOKListener() {
                                @Override
                                public void onOKListener() {
                                    if (!boolTag1 && !boolTag2 && !boolTag3 && !boolTag4
                                            && StringUtils.isEmpty(mTxtMessage.getText().toString())) {
                                        startActivity(getIntent());
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(instance, ServiceReviewActivity.class);
                                        intent.putExtra("expId", result.expertProfile.expId + "");
                                        intent.putExtra("isFreshReview", true);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        finish();
                                    }
                                }
                            });
                        } else if(resp.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                                || resp.info.code.equals(Constant.UPDATE_TIME_STAMP)
                                || resp.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                            Utility.onServerHeaderIssue(instance,resp.info.code);
                        } else {
                            PromeetsDialog.show(instance, resp.info.description);
                        }
                    }

                    @Override
                    public void onFailure(Call<SuperResp> call, Throwable t) {
                        PromeetsDialog.hideProgress();
                        PromeetsDialog.show(AppointStatusActivity.this, t.getLocalizedMessage());
                    }
                });
            }
        });
    }

    private void checkEnable() {
        if (ratingBar.getRating() > 0
                && ((boolTag1 || boolTag2 || boolTag3 || boolTag4)
                || mTxtMessage.getText().toString().trim().length() > 0)) {
            submit.setEnabled(true);
            submit.setBackground(getResources().getDrawable(R.drawable.btn_solid_primary));
            submit.setTextColor(getResources().getColor(R.color.white));
        } else {
            submit.setEnabled(false);
            submit.setBackground(getResources().getDrawable(R.drawable.btn_solid_gray));
            submit.setTextColor(getResources().getColor(R.color.pm_gray));
        }
    }
    //endregion

    @Override
    public void onBackPressed() {
        if (mPopupCancel.getVisibility() == View.VISIBLE) {
            mPopupCancel.startAnimation(outAnimation);
            mPopupCancel.setVisibility(View.GONE);
        } else if (mPopupEvaluation.getVisibility() == View.VISIBLE) {
            mPopupEvaluation.startAnimation(outAnimation);
            mPopupEvaluation.setVisibility(View.GONE);
        } else
            super.onBackPressed();
    }

    public CheckedTextView getChkCall() {
        return mChkCall;
    }
}
