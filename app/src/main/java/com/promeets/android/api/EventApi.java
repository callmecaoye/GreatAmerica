package com.promeets.android.api;

import com.promeets.android.pojo.EventDetailResp;

import com.google.gson.JsonObject;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.EventRequestResp;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.pojo.SurveyResp;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by xiaoyudong on 10/27/16.
 */

public interface EventApi {
    @GET("eventrequest")
    Call<EventRequestResp> getEventList(@Query("userId") int userId);

    @POST("eventrequest/updateALL")
    Call<BaseResp> createEvent(@Body RequestBody body);

    @GET("expertservice/fetchServiceDateLocation")
    Call<JsonObject> getServiceLocationDate(@Query("id") String id, @Query("timeZone") String timeZone);

    @GET("eventrequest/fetchEventPage")
    Call<EventRequestResp> getAllList(@Query("viewId") int viewId, @Query("pageNumber") int pageNumber, @Query("eventCommand") String eventCommand, @Query("ifExpert") boolean ifExpert);

    @GET("expertservice/fetchbyexpid")
    Call<BaseResp> fetchServiceByExpId(@Query("expId") int expId);

    @POST("eventrequest/expertRefused")
    Call<BaseResp> expertRefused(@Body RequestBody body);

    @GET("eventrequest/fetchEventRequest")
    Call<SuperResp> fetchEventRequest(@Query("eventRequestId") int eventRequestId, @Query("timeZone") String timeZone);

    @GET("eventrequest/fetchEventDetail")
    Call<EventDetailResp> fetchEventDetail(@Query("eventRequestId") int eventRequestId, @Query("userId") int userId, @Query("timeZone") String timeZone);

    @POST("eventrequest/userPaymentAll")
    Call<SuperResp> userPaymentAll(@Body RequestBody body);

    @GET("eventrequest/doSurvey")
    Call<SurveyResp> fetchSurvey(@Query("orderId") int orderId);

    @POST("eventrequest/saveSurvey")
    Call<BaseResp> submitSurvey(@Body RequestBody body);


//    @GET("/api/register")
//    Call<LoginResp> register(@Query("accountNumber") String phone, @Query("password") String Password);
//
//    @GET("/api/register/other")
//    Call<LoginResp> registerWithOther(@Query("accountNumber") String phone, @Query("password") String Password);
//
//    @GET("/api/register/verify")
//    Call<LoginResp> verify(@Query("password") String password, @Query("accountNumber") String email, @Query("verifyCode") String verifyCode);
//
//    @Multipart
//    @POST("/api/s3/upload")
//    Call<LoginResp> upload(@Part("id") RequestBody Id, @Part("name") RequestBody Name, @Part MultipartBody.Part file);
//
//    @GET
//    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
//
//    @POST("/api/userprofile/update")
//    Call<ResponseBody> updateUserProfile(@Body RequestBody json);
//
//

}
