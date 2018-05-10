package com.promeets.android.fragment;

import android.content.Intent;
import com.promeets.android.listeners.OnScreenChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.CategoryListActivity;
import com.promeets.android.activity.EventDetailActivity;
import com.promeets.android.activity.HomeActivity;
import com.promeets.android.activity.PartnerActivity;
import com.promeets.android.activity.PollingActivity;
import com.promeets.android.adapter.RecycleCategoryAdapter;
import com.promeets.android.adapter.RecycleExpertAdapter;
import com.promeets.android.api.ServiceApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.AdsImageLoader;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.Gson;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.listeners.OnDismissPromoCodeListener;
import com.promeets.android.object.Advertisement;
import com.promeets.android.object.Category;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.AdsResp;
import com.promeets.android.object.ExpertCardPOJO;
import com.promeets.android.pojo.GridResp;
import com.promeets.android.pojo.HomeResp;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.promeets.android.services.GenericServiceHandler;

import com.promeets.android.util.MixpanelUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 1st page of HomeActivity
 *
 * @destination: CategoryListActivity, ExpertDetailActivity
 *
 */

public class HomePageFragment extends Fragment
        implements IServiceResponseHandler, View.OnClickListener, OnDismissPromoCodeListener{

    private final String EVENT_DETAIL = "promeets://event/eventId/";
    private final String PARTNER = "promeets://partner/name/BayAngels";

    @BindView(R.id.banner_ads)
    Banner mBannerAds;

    @BindView(R.id.category_list)
    RecyclerView mRVCategory;

    @BindView(R.id.expert_list)
    RecyclerView mRVExpert;

    @BindView(R.id.more_category)
    TextView mTxtMoreCategory;

    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    @BindView(R.id.view_more_exp)
    Button mBtnViewMore;

    private BaseActivity mBaseActivity;

    private List<Category> tmpList = new ArrayList<>();

    private ArrayList<ExpertCardPOJO> expertCards = new ArrayList<>();

    private Constant.ServiceType mServiceType;

    private GridResp gridResp;

    private RecycleCategoryAdapter categoryAdapter;

    private RecycleExpertAdapter expAdapter;

    private Gson gson = new Gson();

    private OnDismissPromoCodeListener listener;

    private PartnerFragment partnerFragment;

    //String cityName;
    int pageNumber = 1;

    private UserPOJO userPOJO;

    public HomePageFragment() {
    }

    public static HomePageFragment newInstance() {
        //Bundle args = new Bundle();
        HomePageFragment sampleFragment = new HomePageFragment();
        //sampleFragment.setArguments(args);
        return sampleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        mBaseActivity = (BaseActivity) getActivity();
        ButterKnife.bind(this, view);
        this.listener = this;

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.HORIZONTAL, false);
        mRVCategory.setLayoutManager(mLayoutManager);
        mRVCategory.setHasFixedSize(true);
        categoryAdapter = new RecycleCategoryAdapter(mBaseActivity, tmpList);
        mRVCategory.setAdapter(categoryAdapter);

        mLayoutManager = new LinearLayoutManager(mBaseActivity);
        mRVExpert.setLayoutManager(mLayoutManager);
        mRVExpert.setNestedScrollingEnabled(false);
        expAdapter = new RecycleExpertAdapter(mBaseActivity, expertCards);
        mRVExpert.setAdapter(expAdapter);

        mBtnViewMore.setOnClickListener(this);
        final View childView = mScrollView.getChildAt(0);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (childView != null && childView.getMeasuredHeight()
                                <= mScrollView.getScrollY() + mScrollView.getHeight()
                                && pageNumber > 1) {
                            fetchExpert();
                            MixpanelUtil.getInstance(mBaseActivity).trackEvent("Home page Scroll down");
                        }
                        break;
                }
                return false;
            }
        });

        ((HomeActivity) mBaseActivity).getNotificationCount();
        callService();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBannerAds.startAutoPlay();
        mTxtMoreCategory.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBannerAds.stopAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        MixpanelUtil.getInstance(mBaseActivity).trackEvent("Home page view");
        userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (userPOJO != null) checkPolling();
    }

    public void callService() {
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.PROMEETS_SCREEN_WIDTH, String.valueOf(mBaseActivity.getWidth()));
        header.put(Constant.PROMEETS_SCREEN_HEIGHT, String.valueOf(mBaseActivity.getHeight()));
        header.put(Constant.DEVICE_ID, Utility.getDeviceId());
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());

        //Check for internet Connection
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
            String[] key = {Constant.PAGENUMBER};
            String[] value = {Integer.toString(pageNumber)};



            if (ServiceResponseHolder.getInstance().getHomeService() != null)
                onServiceResponse(ServiceResponseHolder.getInstance().getHomeService(), Constant.ServiceType.HOME_PAGE_GRID);
            else
                new GenericServiceHandler(Constant.ServiceType.HOME_PAGE_GRID, this, Constant.BASE_URL + Constant.MENU_OPERATION, null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
            if (ServiceResponseHolder.getInstance().getAdvertisement() != null)
                onServiceResponse(ServiceResponseHolder.getInstance().getAdvertisement(), Constant.ServiceType.HOME_PAGE_ADVERTISEMENT);
            else
                new GenericServiceHandler(Constant.ServiceType.HOME_PAGE_ADVERTISEMENT, this, Constant.BASE_URL + Constant.ADVERTISEMENT, null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();

            if (ServiceResponseHolder.getInstance().getBannerService() != null)
               onServiceResponse(ServiceResponseHolder.getInstance().getBannerService(), Constant.ServiceType.HOME_PAGE_BANNER);
            else
                new GenericServiceHandler(Constant.ServiceType.HOME_PAGE_BANNER, this, PromeetsUtils.buildURL(Constant.CITY_OPERATION, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }

    private void fetchExpert() {
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.PROMEETS_SCREEN_WIDTH, String.valueOf(mBaseActivity.getWidth()));
        header.put(Constant.PROMEETS_SCREEN_HEIGHT, String.valueOf(mBaseActivity.getHeight()));
        header.put(Constant.DEVICE_ID, Utility.getDeviceId());
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());

        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(mBaseActivity);
            String[] key = {Constant.PAGENUMBER};
            String[] value = {Integer.toString(pageNumber)};
            new GenericServiceHandler(Constant.ServiceType.HOME_PAGE_BANNER, this, PromeetsUtils.buildURL(Constant.CITY_OPERATION, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();

            if (serviceType == Constant.ServiceType.HOME_PAGE_ADVERTISEMENT) {
                AdsResp adsResp = (AdsResp) serviceResponse.getServiceResponse(AdsResp.class);
                Log.d("homepage", "ad: " + adsResp.getInfo().getCode());

                if (mBaseActivity.isSuccess(adsResp.getInfo().getCode())) {
                    ServiceResponseHolder.getInstance().setAdvertisement(serviceResponse);
                    Log.e("Service Response", adsResp.getDataList() + "");
                    //CampPagerAdapter recommededPagerAdapter = new CampPagerAdapter(mBaseActivity,adsResp.getDataList());
                    final Advertisement[] ads = adsResp.getDataList();

                    /**
                     * Partner Popup
                     */
                    for (int i  = 0; i < ads.length; i++) {
                        if (!StringUtils.isEmpty(ads[i].getAutoPopupUrl()) && GlobalVariable.showPartner) {
                            partnerFragment = PartnerFragment.newInstance(ads[i]);
                        }
                    }


                    mBannerAds.setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("position", position+"");
                            MixpanelUtil.getInstance(mBaseActivity).trackEvent("Home page campaign", map);

                            Advertisement ad = ads[position];
                            if (!StringUtils.isEmpty(ad.getPopupUrl())) {
                                AdsPopupFragment fragment = AdsPopupFragment.newInstance(ad);
                                fragment.show(mBaseActivity.getFragmentManager(), "Popup Ads");
                            } else if (!StringUtils.isEmpty(ad.getLinkUrl()) && ad.getLinkUrl().startsWith(EVENT_DETAIL)) {
                                Intent intent = new Intent(mBaseActivity, EventDetailActivity.class);
                                String linkString = ad.getLinkUrl().split("://")[1];
                                String eventId = linkString.split("/")[2];
                                intent.putExtra("eventId", eventId);
                                mBaseActivity.startActivity(intent);
                                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            } else if (!StringUtils.isEmpty(ad.getLinkUrl()) && ad.getLinkUrl().startsWith("promeets://invitecustomer/url/")) {
                                PromoCodeFragment fragment = PromoCodeFragment.newInstance(GlobalVariable.promoBgUrl, listener);
                                fragment.show(mBaseActivity.getFragmentManager(), "Popup PromoCode");
                            } else if (!StringUtils.isEmpty(ad.getLinkUrl()) && ad.getLinkUrl().startsWith(PARTNER)) {
                                Intent intent = new Intent(mBaseActivity, PartnerActivity.class);
                                intent.putExtra("photoUrl", ad.getPhotoUrl());
                                mBaseActivity.startActivity(intent);
                                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        }
                    });
                    mBannerAds.setImages(Arrays.asList(ads)).setImageLoader(new AdsImageLoader()).start();



                    /*// if show Ads Popup
                    boolean showAds = true;
                    try {
                        showAds = mBaseActivity.promeetsPreferenceUtil.getValue("ads_popup", PromeetsPreferenceUtil.BOOLEAN_RETURN_TYPE);
                    } catch (Exception e) { }
                    if (showAds) {
                        AdsPopupFragment fragment = AdsPopupFragment.newInstance(adsResp.getDataList()[0]);
                        fragment.show(mBaseActivity.getFragmentManager(), "Popup Ads");
                        mBaseActivity.promeetsPreferenceUtil.setValue("ads_popup", false);
                    }*/
                }
            } else if (serviceType == Constant.ServiceType.HOME_PAGE_BANNER) {
                HomeResp homeResp = (HomeResp) serviceResponse.getServiceResponse(HomeResp.class);
                Log.d("homepage", "banner: " + homeResp.getInfo().getCode());
                ServiceResponseHolder.getInstance().setBannerService(serviceResponse);
                if (mBaseActivity.isSuccess(homeResp.getInfo().getCode())) {
                    if (homeResp.getDataList() != null
                            && homeResp.getDataList().size() > 0) {
                        if (pageNumber == 1 && expertCards != null && expertCards.size() > 0)
                            expertCards.clear();

                        expertCards.addAll(homeResp.getDataList());
                        expAdapter.notifyItemInserted(expAdapter.getItemCount());
                        if (pageNumber > 1) {
                            pageNumber++;
                            mBtnViewMore.setVisibility(View.GONE);
                        } else
                            mBtnViewMore.setVisibility(View.VISIBLE);
                    } else {
                        mScrollView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                    }
                } else {
                    onErrorResponse(homeResp.getInfo().getDescription());
                }
            } else if (serviceType == Constant.ServiceType.HOME_PAGE_GRID) {
                ServiceResponseHolder.getInstance().setHomeService(serviceResponse);
                gridResp = (GridResp) serviceResponse.getServiceResponse(GridResp.class);
                Log.d("homepage", "grid: " + gridResp.getInfo().getCode());
                if (mBaseActivity.isSuccess(gridResp.getInfo().getCode())) {
                    tmpList.clear();
                    Category[] tmp = gridResp.getCategoryList();
                    for (int i = 0; i < tmp.length; i++) {
                        if (!tmp[i].getTitle().equalsIgnoreCase("all"))
                            tmpList.add(tmp[i]);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(mBaseActivity, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }


    @Override
    public void onClick(View aView) {
        switch (aView.getId()) {
            case R.id.more_category:
                MixpanelUtil.getInstance(mBaseActivity).trackEvent("Home page -> ContentCategoryList see more");
                String json = gson.toJson(gridResp.getCategoryList());
                Intent intent = new Intent(mBaseActivity, CategoryListActivity.class);
                intent.putExtra("items", json);
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.view_more_exp:
                MixpanelUtil.getInstance(mBaseActivity).trackEvent("Home page -> See more");
                HashMap<String, String> header = new HashMap<String, String>();
                //Check for internet Connection
                if (!mBaseActivity.hasInternetConnection()) {
                    PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
                    return;
                }
                PromeetsDialog.showProgress(mBaseActivity);
                pageNumber = 2;
                    String[] key = {Constant.PAGENUMBER};
                    String[] value = {Integer.toString(pageNumber)};

                    new GenericServiceHandler(Constant.ServiceType.HOME_PAGE_BANNER, this, PromeetsUtils.buildURL(Constant.CITY_OPERATION, key, value),
                            null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
                break;
        }
    }

    @Override
    public void onDismiss() {
        mBaseActivity.onScreenChange(R.id.frame_container, OnScreenChangeListener.SCREEN.HOME_SCREEN_FRAGMENT, "HOME_SCREEN_FRAGMENT", false, null);
    }

    private void checkPolling() {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("login/refresh"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        Call<LoginResp> call = service.loginRefresh();
        call.enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {
                LoginResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if (mBaseActivity.isSuccess(result.info.code)) {
                    if (result.urls != null && result.urls.size() > 0
                            && result.urls.contains("promeets://polling")) {
                        Intent intent = new Intent(mBaseActivity, PollingActivity.class);
                        intent.putExtra("firstStart", true);
                        startActivity(intent);
                    } else {
                        if (result.needEmailFlag == 1) {
                            // show fill-in email fragment
                            EmailInputFragment dialogFragment = EmailInputFragment.newInstance();
                            dialogFragment.setCancelable(false);
                            dialogFragment.show(mBaseActivity.getSupportFragmentManager(), "join meeting");
                            dialogFragment.setCallback(() -> {
                                if (partnerFragment != null) {
                                    partnerFragment.show(mBaseActivity.getSupportFragmentManager(), "Popup partner");
                                    GlobalVariable.showPartner = false;
                                }
                            });
                        } else if (GlobalVariable.showPromo && !StringUtils.isEmpty(GlobalVariable.promoBgUrl)) {
                            PromoCodeFragment fragment = PromoCodeFragment.newInstance(GlobalVariable.promoBgUrl, listener);
                            fragment.show(mBaseActivity.getFragmentManager(), "Popup PromoCode");
                            GlobalVariable.showPromo = false;
                        }
                    }
                } else
                    PromeetsDialog.show(mBaseActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<LoginResp> call, Throwable t) {
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}