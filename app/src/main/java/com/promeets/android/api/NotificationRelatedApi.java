package com.promeets.android.api;


import com.promeets.android.pojo.NotificationResp;
import com.promeets.android.pojo.SuperResp;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by xiaoyudong on 10/27/16.
 */

public interface NotificationRelatedApi {
    /*
        uploat device to server;
     */
    @POST("token/updateDeviceToken")
    Call<SuperResp> updateDeviceToken(@Body RequestBody body);

    @GET("notification/history")
    Call<NotificationResp> getNotificationList(@Query("viewId") int userId, @Query("pageNumber") int pageNumber);

    @POST("notification/updateReadFlag")
    Call<NotificationResp> updateReadFlag(@Body RequestBody body);

    @GET("notification/getUnreadMsgCount")
    Call<NotificationResp> getUnreadMsgCount(@Query("userId") int userId);

}
