package com.promeets.android.api;

import com.promeets.android.pojo.BaseResp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sosasang on 6/21/17.
 */

public interface InfoApi {
    @GET("version/info")
    Call<BaseResp> getInfo(@Query("keys") String keys);
}
