package com.promeets.android.api;

import com.google.gson.JsonObject;
import com.promeets.android.pojo.AllReviewsResp;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.ActiveEventResp;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.SearchKeyResp;
import com.promeets.android.pojo.SuperResp;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by xiaoyudong on 10/27/16.
 */

public interface ServiceApi {
    @GET("subContentCategory/fetchByIndustryId")
    Call<JsonObject> fetchByIndustryId(@Query("industryId") int industryId);

    @GET("subContentCategory/fetchByIndustryId")
    Call<SuperResp> fetchByIndustryIdNew(@Query("industryId") int industryId);

    @GET("subContentCategory/fetchAllSubContentCategoryByParentId")
    Call<JsonObject> fetchSubCategory(@Query("parentId") int id);

    //@GET("expertservice/delete")
    //Call<BaseRep> delete(@Query("expertServiceId") int expertServiceId);

    @GET("homepageservice/fetchExpertDetail")
    Call<SuperResp> fetchExpertDetail(@Query("expId") int expId, @Query("userId") int userId, @Query("timeZone") String timeZone);

    @GET("homepageservice/searchByIndexableKeys")
    Call<SuperResp> searchByIndexableKeys(@Query("keys") String keys, @Query("pageNumber") int pageNumber);

    @GET("homepageservice/fetchExpertServicePage")
    Call<SuperResp> fetchExpertServicePage(@Query("pageNumber") int number);

    @GET("homepageservice/fetchReviewList")
    Call<AllReviewsResp> fetchReviewList(@Query("expId") int expId, @Query("pageNumber") int pageNumber);

    @GET("homepageservice/displaySearchKeys")
    Call<SearchKeyResp> displaySearchKeys();

    @GET("activeEvent/display")
    Call<ActiveEventResp> displayActiveEvent(@Query("pageNumber") int number);

    @GET("activeEvent/displayDetail")
    Call<ActiveEventResp> displayEventDetail(@Query("eventId") String eventId, @Query("userId") int UserId, @Query("uuid") String deviceId);

    @POST("activeEvent/updateGoing")
    Call<ActiveEventResp> updateGoing(@Body RequestBody requestBody);

    @FormUrlEncoded
    @POST("review/createReviewEvent")
    Call<BaseResp> createReviewEvent(@Field("firstName") String firstName, @Field("lastName") String lastName, @Field("uuid") String deviceId,
                                     @Field("eventId") String eventId, @Field("rating") float rating, @Field("description") String content);

    @FormUrlEncoded
    @POST("inviteCustomer/create")
    Call<BaseResp> inviteCustomer(@Field("inviteCode") String inviteCode);

    @GET("login/refresh")
    Call<LoginResp> loginRefresh();

    @POST("personinfo/update")
    Call<BaseResp> updateInfo(@Body RequestBody requestBody);

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
