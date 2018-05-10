package com.promeets.android.api;


import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.pojo.CalendarEventResp;
import com.promeets.android.pojo.ExpertProfileResp;
import com.promeets.android.pojo.ServiceResp;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.DatalistResp;
import com.promeets.android.pojo.SuperResp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * Created by xiaoyudong on 10/27/16.
 */

public interface ExpertActionApi {
    @GET("expertprofile")
    Call<ExpertProfilePOJO> login(@Query("userId") int userId);

    @GET("expertprofile/fetchIncludeServiceMayPending")
    Call<ExpertProfileResp> fetchExpertProfile(@Query("expId") int expId, @Query("timeZone") String timeZone);

    @GET("expertservice/fetchAll")
    Call<ServiceResp> getAllService(@Query("expId") int expId);

    @POST("expertservice/updateALL")
    Call<SuperResp> createService(@Body RequestBody body);

    @POST("expertprofile/update")
    Call<ResponseBody> updateExpertProfile(@Body RequestBody body);

    @GET("expertservice/fetchbyexpid")
    Call<ServiceResp> fetchbyexpid(@Query("expId") int expId);

    @POST("eventrequest/expertConfirmedAll")
    Call<BaseResp> expertConfirmedAll(@Body RequestBody body);

    @POST("becomeToExpert/toCheck")
    Call<SuperResp>becomeExpert(@Body RequestBody json);

    @POST("becomeToExpert/toCheckV2")
    Call<SuperResp>becomeExpertV2(@Body RequestBody json);

    @GET("becomeToExpert/checkInviteCode")
    Call<SuperResp>checkInviteCode(@Query("userId") int userId, @Query("inviteCode") String code);

    @Multipart
    @POST("photos/uploadOneExpertPhoto")
    Call<SuperResp> upload(@Part("userId") RequestBody userId, @Part MultipartBody.Part file);

    @Multipart
    @POST("photos/uploadOneExpertAlbumMayPending")
    Call<SuperResp> uploadOneExpertAlbum(@Part("userId") RequestBody userId, @Part MultipartBody.Part file);

    @Multipart
    @POST("photos/deleteExpertAlbum")
    Call<SuperResp> deleteExpertAlbum(@Part("userId") RequestBody userId, @Part("id") RequestBody id);

    @GET("Calendar/checkGoolgeAuth")
    Call<BaseResp> checkGoogleAuth(@Query("authCode") String authCode, @Query("userId") int userId);

    @GET("Calendar/loadData")
    Call<CalendarEventResp> loadData(@Query("startDay") String startDay, @Query("dayCount") int dayCount, @Query("expId") String expId);

    @GET("Calendar/loadAdminData")
    Call<CalendarEventResp> loadAdminData(@Query("startDay") String startDay, @Query("dayCount") int dayCount, @Query("type") String type, @Query("timeZone") String timeZone);

    @POST("Calendar/refreshOutlookToken")
    Call<BaseResp> refreshOutlookToken(@Body RequestBody json);

    @GET("search/getSuggestion")
    Call<DatalistResp> getSuggestion(@Query("content") String content);

    @POST("becomeToExpert/saveFlowContent")
    Call<BaseResp> expSubmit(@Body ExpertProfilePOJO json, @Query("timeZone") String timeZone);
}
