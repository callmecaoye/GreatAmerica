package com.promeets.android.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import com.promeets.android.pojo.ExpertLikeResp;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promeets.android.adapter.RecycleServiceAdapter;
import com.promeets.android.adapter.RecycleReviewAdapter;
import com.promeets.android.adapter.RecycleRecommendAdapter;
import com.bumptech.glide.Glide;
import com.promeets.android.custom.AdsImageLoader;
import com.promeets.android.custom.HeadZoomScrollView;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.ServiceSelectFragment;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;

import com.promeets.android.object.ExpertProfile;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.ExpertService;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.ExpertLikePost;
import com.promeets.android.pojo.ServiceDetailResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.MixpanelUtil;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.Utility;
import com.willy.ratingbar.ScaleRatingBar;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import com.yanzhenjie.permission.Permission;
import com.youth.banner.Banner;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.promeets.android.Constant.ServiceType.EXPERT_LIKE;
import static com.promeets.android.util.PromeetsUtils.getUserData;

/**
 * IMPORTANT ACTIVITY!!!
 *
 * This is for showing expert details
 *
 * User can make appointment here
 *
 * Include expert profile basic information, topic list,
 * review list, recommend list, security plan
 *
 */

public class ExpertDetailActivity extends BaseActivity
        implements IServiceResponseHandler, View.OnClickListener, YouTubePlayer.OnInitializedListener  {

    static final String YOUTUBE_API_KEY = "AIzaSyAdEiNSFeoJ_KsSfsuReeCZDyTvZHluj7k";

    @BindView(R.id.activity_service_detail_name)
    TextView mTxtDetailName;

    @BindView(R.id.rating_bar)
    ScaleRatingBar mRatingBar;

    @BindView(R.id.meeting_num)
    TextView mMeetNum;

    @BindView(R.id.activity_service_detail_position)
    TextView mTxtDetailPosition;

    @BindView(R.id.activity_service_detail_location)
    TextView mTxtDetailLocation;

    @BindView(R.id.activity_service_detail_want_meeting)
    TextView mTxtWantToMeeting;

    @BindView(R.id.activity_service_detail_service_list)
    //NonScrollListView mListViewDetailService;
    RecyclerView mRVService;

    @BindView(R.id.activity_service_detail_recommend_list)
    RecyclerView mRVRecommend;

    @BindView(R.id.activity_service_detail_review_list)
    RecyclerView mRVReview;

    @BindView((R.id.expand_text_view))
    WebView mWebViewAboutMe;

    @BindView((R.id.expand_note))
    TextView mTxtExpandNote;

    @BindView(R.id.activity_service_detail_appointment_btn)
    Button mBtnAppointment;

    //@BindView(R.id.view_pager)
    //ViewPager mSliderDetail;

    //@BindView(R.id.indicator)
    //CircleIndicator mIndicator;

    @BindView(R.id.activity_service_detail_view_more)
    TextView mTxtViewMore;

    @BindView(R.id.security)
    TextView mTxtSecurity;

    @BindView(R.id.activity_service_detail_customer_service)
    TextView mBtnCustomerService;

    @BindView(R.id.activity_service_detail_expert_icon)
    CircleImageView mImgViewExpertIcon;

    @BindView(R.id.activity_service_detail_expert_small_photo_page_lay)
    RelativeLayout mLinearLayoutSmallPhoto;

    @BindView(R.id.activity_service_detail_expert_full_size_image)
    ImageView mImgViewFullSize;

    @BindView(R.id.activity_service_detail_like)
    public ImageView mImgViewLike;

    @BindView(R.id.activity_service_detail_review_title)
    TextView mTitleReview;

    @BindView(R.id.activity_service_detail_review_lay)
    CardView mLayReview;

    @BindView(R.id.activity_service_detail_recommend_title)
    TextView mTitleRecommended;

    @BindView(R.id.share)
    ImageView mImgViewShare;

    @BindView(R.id.flexBox)
    FlexboxLayout mFlexBox;

    @BindView(R.id.zoom_scroll_view)
    HeadZoomScrollView mZoomScrollView;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mLayoutAppBar;

    @BindView(R.id.banner_about)
    Banner mBannerAbout;
    @BindView(R.id.logo_divider)
    View mDivider;
    @BindView(R.id.logo)
    ImageView mImgLogo;

    @BindView(R.id.li_verify)
    ImageView mVerifyLI;

    @BindView(R.id.fb_verify)
    ImageView mVerifyFB;

    @BindView(R.id.video_player_lay)
    LinearLayout mLayPlayer;

    private String mExpId;
    private UserPOJO mUserPOJO;
    private String userId;
    private ServiceDetailResp serviceDetailResp;
    private ExpertLikeResp expertLikeResp;
    private ArrayList<ExpertService> arrayList;
    private Intent mIntent;
    private ArrayList<ExpertService> mServiceList;
    boolean isLogincalled = false;
    private ExpertProfile mExpertProfile;
    public boolean mIsLiked = false;
    private ServiceType serviceType;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };
    private boolean mFromLink = false;
    private boolean isWebViewExpand = false;
    private Toolbar toolbar;

    private String photoPath;
    private String smallPhotoPath;

    RecycleReviewAdapter reviewAdapter;
    RecycleServiceAdapter serviceAdapter;

    boolean isIamExpert = false;
    int mWebHeight = -1;
    int width;


    private Animator anim;

    private String videoId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_detail);
        ButterKnife.bind(this);
        width = getWidth();
        mWebViewAboutMe.setScrollbarFadingEnabled(true);

        anim = AnimatorInflater.loadAnimator(this, R.animator.like_scale);
        anim.setDuration(150);
        anim.setTarget(mImgViewLike);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        mLayoutAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    mZoomScrollView.setZoomView(mImgViewFullSize);
                    mImgViewShare.setColorFilter(Color.rgb(255, 255, 255));
                } else {
                    mZoomScrollView.setZoomView(null);
                    float px = ScreenUtil.convertPxToDp(verticalOffset, ExpertDetailActivity.this);
                    float h = ScreenUtil.convertPxToDp(appBarLayout.getHeight(), ExpertDetailActivity.this);
                    int color = (int) (255 - 255 * Math.abs(px) / h);
                    mImgViewShare.setColorFilter(Color.rgb(color, color, color));
                }
            }
        });

        mUserPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (mUserPOJO == null)
            userId = "0";
        else
            userId = mUserPOJO.id + "";

        String action = getIntent().getAction();
        Uri data = getIntent().getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            mFromLink = true;
            mExpId = data.getLastPathSegment();
            if (userId.equals("0")) {
                startActivity(MainActivity.class);
            }
        } else {
            mExpId = getIntent().getExtras().getString("expId");
            photoPath = getIntent().getExtras().getString("photoPath");
            smallPhotoPath = getIntent().getExtras().getString("smallPhotoPath");
            if (!StringUtils.isEmpty(photoPath)) {
                mImgViewFullSize.setImageURI(Uri.parse(photoPath));
                mImgViewExpertIcon.setVisibility(View.GONE);
            } else if (!StringUtils.isEmpty(smallPhotoPath)) {
                mImgViewFullSize.setImageResource(R.drawable.bg_expert);
                mImgViewExpertIcon.setImageURI(Uri.parse(smallPhotoPath));
                mImgViewExpertIcon.setVisibility(View.VISIBLE);
            }
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRVService.setLayoutManager(mLayoutManager);
        mRVService.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRVRecommend.setLayoutManager(mLayoutManager);
        mRVRecommend.setNestedScrollingEnabled(false);
    }


    @Override
    public void initElement() {
        /*boolean isPreview = false;
        try {
            isPreview = getIntent().getExtras().getBoolean("isPreview");
        } catch (Exception ex) {

        }
        if (isPreview) {
            handleOwnProfile();
            prepareUI(Utility.getServiceDetailResp());
            //handleOwnProfile();
        } else*/
        PromeetsUtils.getExperProfile(this, this, userId, mExpId);

        if (mTxtSecurity.getText().length() == 0) {
            new DownloadFileTask().execute();
        }

        mImgViewFullSize.getLayoutParams().height = width / 3 * 2;
        mImgViewFullSize.requestLayout();
    }

    @Override
    public void registerListeners() {
        mBtnCustomerService.setOnClickListener(this);
        mBtnAppointment.setOnClickListener(this);
        mImgViewLike.setOnClickListener(this);
        mImgViewShare.setOnClickListener(this);
        mTxtExpandNote.setOnClickListener(this);
        mTxtWantToMeeting.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (reviewAdapter != null)
            reviewAdapter.onActivityResult(requestCode, resultCode);
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        if (serviceType == ServiceType.EXPERT_PROFILE_DETAIL) {
            ServiceDetailResp serviceDetailResp = (ServiceDetailResp) serviceResponse.getServiceResponse(ServiceDetailResp.class);
            if (isSuccess(serviceDetailResp.getInfo().getCode())) {
                handleOwnProfile();
                prepareUI(serviceDetailResp);
                //handleOwnProfile();
            } else {
                onErrorResponse(serviceDetailResp.getInfo().getDescription());
                mHandler.postDelayed(mRunnable, 2000);
            }
        } else if (serviceType == ServiceType.EXPERT_LIKE) {
            ExpertLikeResp expertLikeResp = (ExpertLikeResp) serviceResponse.getServiceResponse(ExpertLikeResp.class);
            updateExpertLike(expertLikeResp);
        }
    }

    private void handleOwnProfile() {
        ExpertProfilePOJO expertProfile = (ExpertProfilePOJO) getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY, ExpertProfilePOJO.class);
        if (expertProfile != null && (mExpId == null || mExpId.equalsIgnoreCase(expertProfile.expId + ""))) {
            findViewById(R.id.bottomBar).setVisibility(View.GONE);
            isIamExpert = true;
        }
    }

    private void prepareUI(ServiceDetailResp serviceDetailResp) {
        findViewById(R.id.zoom_scroll_view).setVisibility(View.VISIBLE);
        this.serviceDetailResp = serviceDetailResp;
        mExpertProfile = serviceDetailResp.getExpertProfile();

        if (mExpertProfile == null) return;
        mTxtDetailName.setText(mExpertProfile.getFullName());

        if (mExpertProfile.avgRating > 0) {
            if (mExpertProfile.avgRating == 5)
                mExpertProfile.avgRating = 5f;
            else if (mExpertProfile.avgRating > 4)
                mExpertProfile.avgRating = 4.5f;
            else if (mExpertProfile.avgRating > 3)
                mExpertProfile.avgRating = 3.5f;
            else if (mExpertProfile.avgRating > 2)
                mExpertProfile.avgRating = 2.5f;
            else if (mExpertProfile.avgRating > 1)
                mExpertProfile.avgRating = 1.5f;
            else
                mExpertProfile.avgRating = 0.5f;

            mRatingBar.setVisibility(View.VISIBLE);
            mMeetNum.setVisibility(View.VISIBLE);
            mRatingBar.setRating(mExpertProfile.avgRating);
            mMeetNum.setText("(" + mExpertProfile.numberOfMeeting + ")");
        }

        if (!StringUtils.isEmpty(mExpertProfile.getPosition()))
            mTxtDetailPosition.setText(mExpertProfile.getPosition());
        mTxtDetailLocation.setText(mExpertProfile.getActiveCity());
        mTxtWantToMeeting.setText(mExpertProfile.getWantToMeeting());

        if (!StringUtils.isEmpty(mExpertProfile.getDescription())) {
            String tmp = "<div class='content'> <style> *{font-family:'OpenSans' !important; src: url(\"file:///android_asset/fonts/\")}"
                    + "body {line-height:20px; color:#4A4A4A; font-family:'OpenSans'; font-size: medium;} </style>"
                    + "<body style='margin:0; padding:0'>" + mExpertProfile.getDescription() + "</body> </div>";
            mWebViewAboutMe.loadDataWithBaseURL("file:///android_asset", tmp, "text/html", "UTF-8", null);
        }

        if (!StringUtils.isEmpty(mExpertProfile.videoUrl)
                && mExpertProfile.videoUrl.contains("youtube")) {
            videoId = Utility.extractYTId(mExpertProfile.videoUrl);
            YouTubePlayerSupportFragment frag =
                    (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
            frag.initialize(YOUTUBE_API_KEY, this);
        }

        if (mExpertProfile.getHadLiked().equals("0")) {
            mIsLiked = false;
            mImgViewLike.setImageResource(R.drawable.ic_like_outline);
        } else if (mExpertProfile.getHadLiked().equals("1")) {
            mIsLiked = true;
            mImgViewLike.setImageResource(R.drawable.ic_like);
        }
        if (!StringUtils.isEmpty(mExpertProfile.getPhotoUrl())) {
            if (StringUtils.isEmpty(photoPath)) {
                Glide.with(this).load(mExpertProfile.getPhotoUrl()).into(mImgViewFullSize);
                mImgViewExpertIcon.setVisibility(View.GONE);
            }
        } else if (!StringUtils.isEmpty(mExpertProfile.getSmallphotoUrl())) {
            if (StringUtils.isEmpty(smallPhotoPath)) {
                Glide.with(this).load(R.drawable.bg_expert).into(mImgViewFullSize);
                Glide.with(this).load(mExpertProfile.getSmallphotoUrl()).into(mImgViewExpertIcon);
                mImgViewExpertIcon.setVisibility(View.VISIBLE);
            }
        }

        if (!StringUtils.isEmpty(mExpertProfile.companyLogo)) {
            mDivider.setVisibility(View.VISIBLE);
            mImgLogo.setVisibility(View.VISIBLE);
            Glide.with(this).load(mExpertProfile.companyLogo).into(mImgLogo);
        } else if (!StringUtils.isEmpty(mExpertProfile.getLinkedinLink())) {
            mDivider.setVisibility(View.VISIBLE);
            mImgLogo.setVisibility(View.VISIBLE);
            mImgLogo.setOnClickListener(this);
            Glide.with(this).load(R.drawable.logo_li2).into(mImgLogo);
        }

        if (!StringUtils.isEmpty(mExpertProfile.linkedinVerified)
                && mExpertProfile.linkedinVerified.equals("1"))
            mVerifyLI.setVisibility(View.VISIBLE);

        if (!StringUtils.isEmpty(mExpertProfile.facebookVerified)
                && mExpertProfile.facebookVerified.equals("1"))
            mVerifyFB.setVisibility(View.VISIBLE);

        if (serviceDetailResp.getServiceList() == null || serviceDetailResp.getServiceList().size() == 0) {
            mRVService.setVisibility(View.GONE);
        } else {
            mServiceList = serviceDetailResp.getServiceList();
            serviceAdapter = new RecycleServiceAdapter(this, mServiceList, serviceDetailResp.getExpertProfile());
            mRVService.setAdapter(serviceAdapter);

            if (mServiceList.size() == 1) {
                ExpertService servicePOJO = mServiceList.get(0);
                if (servicePOJO.ifExistRequest.equals("-1")) {
                    //view.findViewById(R.id.bottomBar).setVisibility(View.GONE);
                }  else if(servicePOJO.ifExistRequest.equals("1")){
                    mBtnAppointment.setText("View Appointment");
                }
            }
        }

        if (mExpertProfile.getPhotoList() != null && mExpertProfile.getPhotoList().length > 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(getWidth() / 3 * 1.8));
            mBannerAbout.setLayoutParams(params);
            mBannerAbout.setImages(Arrays.asList(mExpertProfile.getPhotoList())).setImageLoader(new AdsImageLoader()).start();
        } else {
            mBannerAbout.setVisibility(View.GONE);
        }

        if (StringUtils.isEmpty(mExpertProfile.getDescription()) && mExpertProfile.getPhotoList() != null && mExpertProfile.getPhotoList().length == 0) {
            findViewById(R.id.about_me_main).setVisibility(View.GONE);
        }
        if (StringUtils.isEmpty(mExpertProfile.getDescription())) {
            //mTxtExpand.setVisibility(View.GONE);
            mWebViewAboutMe.setVisibility(View.GONE);
            mTxtExpandNote.setVisibility(View.GONE);
        } else {
            mWebViewAboutMe.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                        toggle();
                    return false;
                }
            });
        }

        if (serviceDetailResp.getServiceReviewList() == null || serviceDetailResp.getServiceReviewListCount() == 0) {
            mTitleReview.setVisibility(View.GONE);
            mLayReview.setVisibility(View.GONE);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRVReview.setLayoutManager(linearLayoutManager);
            mRVReview.setNestedScrollingEnabled(false);
            reviewAdapter = new RecycleReviewAdapter(this, serviceDetailResp.getServiceReviewList(), isIamExpert);
            mRVReview.setAdapter(reviewAdapter);

            if (serviceDetailResp.getServiceReviewListCount() > 2) {
                mTxtViewMore.setVisibility(View.VISIBLE);
                mTxtViewMore.setText("Read all " + serviceDetailResp.getServiceReviewListCount() + " reviews");
                mTxtViewMore.setOnClickListener(this);
            }
        }
        arrayList = serviceDetailResp.getRecommendList();
        if (arrayList != null && arrayList.size() > 0) {
            RecycleRecommendAdapter adapter = new RecycleRecommendAdapter(this, serviceDetailResp.getRecommendList());
            mRVRecommend.setAdapter(adapter);

        } else {
            mTitleRecommended.setVisibility(View.GONE);
        }

        // expert tag
        Typeface tf_semi = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        if (mExpertProfile.getTags() != null && mExpertProfile.getTags().length > 0) {
            if (mFlexBox.getVisibility() == View.VISIBLE) return;

            mFlexBox.setVisibility(View.VISIBLE);
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            int tmp1 = ScreenUtil.convertDpToPx(4, this);
            int tmp2 = ScreenUtil.convertDpToPx(5, this);
            lp.setMargins(tmp1, tmp2, tmp1, tmp2);
            for (String tag : mExpertProfile.getTags()) {
                final TextView mTxtTag = new TextView(this);
                mTxtTag.setLayoutParams(lp);
                mTxtTag.setText(tag);
                mTxtTag.setTextColor(getResources().getColor(R.color.primary));
                mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                mTxtTag.setTypeface(tf_semi);
                tmp1 = ScreenUtil.convertDpToPx(10, this);
                tmp2 = ScreenUtil.convertDpToPx(5, this);
                mTxtTag.setPadding(tmp1, tmp2, tmp1, tmp2);
                mTxtTag.setBackgroundResource(R.drawable.tag_border_primary);
                mFlexBox.addView(mTxtTag);
                mTxtTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = ((TextView) v).getText().toString().replace("#", "");
                        Intent intent = new Intent(ExpertDetailActivity.this, HomeActivity.class);
                        HomeActivity.currentTabIndex = 2;
                        HomeActivity.mQuery = key;
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
            }
        }
    }

    private void updateExpertLike(ExpertLikeResp expertLikeResp) {
        this.expertLikeResp = expertLikeResp;
        mImgViewLike.setEnabled(true);
        mTxtWantToMeeting.setText(expertLikeResp.getWantToMeeting());
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

    @Override
    protected void onRestart() {
        super.onRestart();
        mIntent = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mExpertProfile != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("expertId", mExpertProfile.getExpId());
            MixpanelUtil.getInstance(this).trackEvent("Expert page view", map);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExpertProfile == null) return;
        File file = new File(Environment.getExternalStorageDirectory()
                + "/Promeets/" + mExpertProfile.getExpId() + "_shared_profile.png");
        if (file.exists())
            file.delete();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mTxtViewMore.getId()) {
            Intent intent = new Intent(ExpertDetailActivity.this, ServiceReviewActivity.class);
            intent.putExtra("expId", mExpId);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (view.getId() == mBtnCustomerService.getId()) {
            Intent intent = new Intent(ExpertDetailActivity.this, CustomerServiceActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (view.getId() == mBtnAppointment.getId()) {
            UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
            if (userPOJO == null) {
                isLogincalled = true;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            } else {
                HashMap<String, String> map = new HashMap<>();
                map.put("expertId", mExpId);
                MixpanelUtil.getInstance(this).trackEvent("Expert page -> make an appointment", map);

                if (mServiceList != null && mExpertProfile != null) {
                    if (mServiceList.size() == 1) {
                        final ExpertProfilePOJO expertProfile = (ExpertProfilePOJO) getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY, ExpertProfilePOJO.class);
                        final ExpertService servicePOJO = mServiceList.get(0);
                        if(expertProfile!=null && (mExpertProfile.getExpId().equalsIgnoreCase("self")|| mExpertProfile.getExpId().equalsIgnoreCase(expertProfile.expId+""))){

                        } else {
                            if (servicePOJO.ifExistRequest.equals("-1")) {
                                //view.findViewById(R.id.bottomBar).setVisibility(View.GONE);
                            } else if (servicePOJO.ifExistRequest.equals("1")) {
                                HashMap<String, String> map2 = new HashMap<>();
                                map2.put("serviceId", servicePOJO.id.toString());
                                MixpanelUtil.getInstance(ExpertDetailActivity.this).trackEvent("Expert page -> topic -> Make an appointment", map2);

                                Intent intent = new Intent(ExpertDetailActivity.this, AppointStatusActivity.class);
                                int id = 0;
                                try{
                                    id=Integer.parseInt(servicePOJO.eventDataId);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                intent.putExtra("eventRequestId", id);
                                intent.putExtra("parentPage",0 );
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ExpertDetailActivity.this, UserRequestSettingActivity.class);
                                intent.putExtra("serviceId", servicePOJO.id+"");
                                intent.putExtra("expId", mExpertProfile.getExpId()+"");
                                intent.putExtra("price", servicePOJO.price);
                                intent.putExtra("discount", servicePOJO.originalPrice);
                                intent.putExtra("topic", servicePOJO.title);
                                intent.putExtra("author", mExpertProfile.getFullName());
                                intent.putExtra("position", mExpertProfile.getPosition());
                                if (!StringUtils.isEmpty(mExpertProfile.getPhotoUrl()))
                                    intent.putExtra("photo", mExpertProfile.getPhotoUrl());
                                else if (!StringUtils.isEmpty(mExpertProfile.getSmallphotoUrl()))
                                    intent.putExtra("photo", mExpertProfile.getSmallphotoUrl());
                                if (!StringUtils.isEmpty(servicePOJO.duratingTime))
                                    intent.putExtra("duratingTime", NumberFormatUtil.getInstance().getFloat(servicePOJO.duratingTime));
                                if (!StringUtils.isEmpty(mExpertProfile.getDefaultDate()))
                                    intent.putExtra("defaultDate", new Integer(mExpertProfile.getDefaultDate()).intValue());
                                intent.putExtra("defaultLocationList", mExpertProfile.getExpertDefaultLocationList());
                                intent.putExtra("availabilityType", mExpertProfile.getAvailabilityType());
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        }
                    } else {
                        ServiceSelectFragment dialogFragment = ServiceSelectFragment.newInstance(
                                serviceDetailResp.getServiceList(), serviceDetailResp.getExpertProfile());
                        dialogFragment.show(this.getSupportFragmentManager(), "topics");
                        dialogFragment.setCancelable(true);
                    }
                }
            }
        } else if (view.getId() == mImgViewLike.getId()) {
            UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
            if (userPOJO == null) {
                isLogincalled = true;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            } else {
                if (!mIsLiked) {
                    mIsLiked = true;
                    mImgViewLike.setImageResource(R.drawable.ic_like);
                } else {
                    mIsLiked = false;
                    mImgViewLike.setImageResource(R.drawable.ic_like_outline);
                }
                anim.start();
                mImgViewLike.setEnabled(false);
                postExpertLike();
            }
        } else if (view.getId() == mImgViewShare.getId()) {
            // save expert profile to local
            requestStoragePermission();
        } else if (view.getId() == mTxtExpandNote.getId()) {
            toggle();
        } else if (view.getId() == mImgLogo.getId()) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", mExpertProfile.getLinkedinLink());
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (view.getId() == mTxtWantToMeeting.getId()) {
            MixpanelUtil.getInstance(this).trackEvent("Expert page -> Clicked small gray heart");
        }
    }

    private void requestStoragePermission() {
        AndPermission.with(ExpertDetailActivity.this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mExpertProfile == null || StringUtils.isEmpty(mExpertProfile.getPhotoUrl()))
                                        return;
                                    Bitmap bitmap = Utility.getBitmapFromURL(mExpertProfile.getPhotoUrl());
                                    String imageName = mExpId + "_shared_profile.png";
                                    Utility.saveFile(bitmap, imageName);
                                } catch (IOException e) {

                                }
                            }
                        }).start();
                        shareProfile();
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        shareProfile();
                    }
                })
                .start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mFromLink) startActivity(HomeActivity.class);
    }

    public void postExpertLike() {
         /*
        * Set the service type to handle the request
        * */
        serviceType = EXPERT_LIKE;

        /*
        * Hashmap in case we want to pass any parameterized headers
        * */
        HashMap<String, String> header = new HashMap<>();
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.UPDATE_EXPERT_LIKE));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
        } else {
            /* Create the pojo for POST Request*/
            ExpertLikePost expertLikePost = new ExpertLikePost();
            expertLikePost.setExpId(mExpId);
            expertLikePost.setViewId(userId);
            if (mIsLiked) {
                expertLikePost.setLike("1");
            } else {
                expertLikePost.setLike("0");
            }

            //Call the service
            new GenericServiceHandler(EXPERT_LIKE, this, Constant.BASE_URL + Constant.UPDATE_EXPERT_LIKE, "", expertLikePost, header, IServiceResponseHandler.POST, false, null, null).execute();
        }
    }

    private void shareProfile() {
        if (StringUtils.isEmpty(mExpId)) return;

        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = ai.metaData;
        final String SHARE_LINK = bundle.getString("SHARE_LINK");

        // get available share apps
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_TEXT, SHARE_LINK + mExpId);

        // customize share content
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share_intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (!resInfo.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<>();
            for (ResolveInfo info : resInfo) {
                Intent targeted = new Intent(Intent.ACTION_SEND);
                ActivityInfo activityInfo = info.activityInfo;
                //targeted.putExtra(Intent.EXTRA_SUBJECT, "Book a free session with " + mExpertProfile.getFullName() + " on Promeets!");
                targeted.putExtra(Intent.EXTRA_SUBJECT, mExpertProfile.getShareTitle());
                targeted.setPackage(activityInfo.packageName);
                targeted.setClassName(activityInfo.packageName, activityInfo.name);
                if (activityInfo.packageName.toLowerCase().contains("email")
                        || activityInfo.packageName.toLowerCase().contains("com.google.android.gm")) {
                    // Email
                    targeted.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    targeted.setType("image/*");
                    targeted.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + Environment.getExternalStorageDirectory()
                            + "/Promeets/" + mExpId + "_shared_profile.png"));
                    targeted.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mExpertProfile.getShareHtml()));
                } else if (activityInfo.packageName.toLowerCase().contains("com.android.mms")) {
                    // Msg
                    targeted.setType("text/plain");
                    targeted.putExtra(Intent.EXTRA_TEXT, mExpertProfile.getShareTxt());
                } else {
                    targeted.setType("text/plain");
                    targeted.putExtra(Intent.EXTRA_TEXT, SHARE_LINK + mExpId);
                }
                targetedShareIntents.add(targeted);
            }
            if (targetedShareIntents.size() == 0) {
                PromeetsDialog.show(this, "No app to share");
                return;
            }
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), null);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
            try {
                startActivity(chooserIntent);
            } catch (ActivityNotFoundException ex) {
                PromeetsDialog.show(this, "No app to share");
            }
        }
    }

    private void toggle() {
        if (isWebViewExpand) {
            HashMap<String, String> map = new HashMap<>();
            map.put("expertId", mExpId);
            MixpanelUtil.getInstance(this).trackEvent("Expert page -> collapse Bio", map);

            mTxtExpandNote.setText("See More");
            PromeetsUtils.collapse(mWebViewAboutMe, 350, ScreenUtil.convertDpToPx(80, this));
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("expertId", mExpId);
            MixpanelUtil.getInstance(this).trackEvent("Expert page -> expand Bio", map);

            if (mWebHeight == -1) {
                mWebViewAboutMe.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mWebHeight = mWebViewAboutMe.getMeasuredHeight();
            }
            mTxtExpandNote.setText("See Less");
            PromeetsUtils.expand(mWebViewAboutMe, 350, mWebHeight);
        }
        isWebViewExpand = !isWebViewExpand;
    }

    public RecycleReviewAdapter getReviewAdapter() {
        return reviewAdapter;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!StringUtils.isEmpty(videoId)) {
            mLayPlayer.setVisibility(View.VISIBLE);
            if (!wasRestored)
                player.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure (YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        /*if (error.isUserRecoverableError()) {
            error.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else */
            mLayPlayer.setVisibility(View.GONE);
    }

    private class DownloadFileTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // read Security Plan from server https://www.promeets.us/security/security_plan
            final String txtSource = "https://www.promeets.us/security/security_plan";
            String content = "";
            URL txtUrl;
            try {
                txtUrl = new URL(txtSource);
                BufferedReader reader = new BufferedReader(new InputStreamReader(txtUrl.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    content += line + "\n";
                }
                reader.close();
            } catch (IOException e) {

            }
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                mTxtSecurity.setText(result);
            }
        }
    }
}