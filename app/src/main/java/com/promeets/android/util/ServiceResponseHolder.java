package com.promeets.android.util;

import com.promeets.android.Constant;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.UserProfilePOJO;

import com.promeets.android.pojo.ServiceResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Shashank Shekhar on 09-02-2017.
 */

public class ServiceResponseHolder {
    private static ServiceResponseHolder instance = null;

    private ServiceResponse homeService;
    private ServiceResponse bannerService;
    private UserProfilePOJO userProfile;
    private ExpertProfilePOJO expertProfile;
    private ServiceResponse advertisement;
    private ServiceResponseHolder() {
    }

    public static ServiceResponseHolder getInstance() {
        if (instance == null)
            instance = new ServiceResponseHolder();

        return instance;
    }

    public void setBannerService(ServiceResponse bannerService) {
        this.bannerService = bannerService;
    }

    public void setHomeService(ServiceResponse homeService) {
        this.homeService = homeService;
    }

    public ServiceResponse getBannerService() {
        return bannerService;
    }

    public ServiceResponse getHomeService() {
        return homeService;
    }

    public void setExpertProfile(ExpertProfilePOJO expertProfile) {
        this.expertProfile = expertProfile;
    }

    public void setUserProfile(UserProfilePOJO userProfile) {
        this.userProfile = userProfile;
    }

    public ExpertProfilePOJO getExpertProfile() {
        return expertProfile;
    }

    public UserProfilePOJO getUserProfile() {
        return userProfile;
    }

    public static void setInstance(ServiceResponseHolder instance) {
        ServiceResponseHolder.instance = instance;
    }

    public OkHttpClient  getRetrofitHeader(final String url){
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String accessToken = ServiceHeaderGeneratorUtil.getInstance().getAccessToken();
                String pTimeStamp = ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp();
                String proometsTime = ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(url);
                Request request = chain.request().newBuilder()
                        .addHeader(Constant.PROMEETS_SCREEN_WIDTH, String.valueOf(ScreenUtil.getWidth()))
                        .addHeader(Constant.PROMEETS_SCREEN_HEIGHT, String.valueOf(ScreenUtil.getHeight()))
                        .addHeader("ptimestamp", pTimeStamp)
                        .addHeader("accessToken",accessToken)
                        .addHeader("promeetsT",proometsTime)
                        .addHeader("API_VERSION",Utility.getVersionCode())
                        .addHeader(Constant.DEVICE_ID, Utility.getDeviceId()).build();
                return chain.proceed(request);
            }
        });

        return builder.build();
    }

    public void setAdvertisement(ServiceResponse advertisement) {
        this.advertisement = advertisement;
    }

    public ServiceResponse getAdvertisement() {
        return advertisement;
    }
}
