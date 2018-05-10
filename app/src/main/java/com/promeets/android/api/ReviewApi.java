package com.promeets.android.api;


import com.promeets.android.pojo.SuperResp;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Created by xiaoyudong on 10/27/16.
 */

public interface ReviewApi {
    @POST("eventrequest/userReview")
    Call<SuperResp> createReview(@Body RequestBody review);
}
