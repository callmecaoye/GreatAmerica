package com.promeets.android.api;

import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.ChatAccountResp;
import com.promeets.android.pojo.InviteResp;
import com.promeets.android.pojo.SuperResp;

import com.google.gson.JsonObject;
import com.promeets.android.pojo.LoginResp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * Created by xiaoyudong on 10/27/16.
 */

public interface UserActionApi {
    @POST("login")
    Call<LoginResp> login(@Body RequestBody requestBody);
    //Call<LoginResp> login(@Query("accountNumber") String LoginName, @Query("password") String Password);

    @POST("register/getRegisterVerifyCode")
    Call<LoginResp> getRegVerifyCode(@Body RequestBody requestBody);

    @POST("register/getResetPasswordVerifyCode")
    Call<LoginResp> getPswVerifyCode(@Body RequestBody requestBody);

    @POST("register/resetPassword")
    Call<LoginResp> resetPsw(@Body RequestBody requestBody);

    @GET("register/other")
    Call<LoginResp> registerWithOther(@Query("accountNumber") String phone, @Query("password") String Password);

    @POST("register/setup")
    Call<LoginResp> setup(@Body RequestBody requestBody);

    @Multipart
    @POST("photos/uploadOneUserPhoto")
    //@POST("s3/upload")
    Call<LoginResp> upload(@Part("userId") RequestBody userId, @Part("name") RequestBody Name, @Part MultipartBody.Part file);

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    @POST("userprofile/update")
    Call<SuperResp> updateUserProfile(@Body RequestBody json);

    @POST("expertprofile")
    Call<SuperResp>becomeExpert(@Body RequestBody json);

    @GET("login/other")
    Call<LoginResp> loginOther(@Query("accountNumber") String accountNmuber, @Query("platform") String platform, @Query("platformToken") String platformToken);

    @GET("userprofile/fetch")
    Call<SuperResp> fechUserProfile(@Query("id") int id);

    @GET("sendbirdChat/fetchUserByNameAndId")
    Call<ChatAccountResp> getChatAccount(@Query("userId") int id);

    @FormUrlEncoded
    @POST("customerIssue/create")
    Call<JsonObject> createCustomerIssue(@Field("fullName") String name, @Field("phoneNumber") String phone, @Field("emailAddress") String email, @Field("content") String content);

    @GET("userprofile/shareInviteCodeLink")
    Call<InviteResp> getInviteCodeLink();

    @GET("agoraChat/callBack")
    Call<BaseResp> callbackServer(@Query("channelName") String channelName, @Query("operation") String operation);
}
