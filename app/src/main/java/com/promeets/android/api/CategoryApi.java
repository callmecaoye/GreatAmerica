package com.promeets.android.api;

import com.promeets.android.pojo.CategoryResp;

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

public interface CategoryApi {
    @GET("contentCategory/fetchAllV2")
    Call<CategoryResp> fetchAllCategory();

    @GET("homepageservice/sortByNumberOfMeeting")
    Call<SuperResp> sortByNumberOfMeeting(@Query("industryId") int industryId);

    @GET("homepageservice/sortByServicePrice")
    Call<SuperResp> sortByServicePrice(@Query("industryId") int industryId);

    @GET("homepageservice/sortByServiceRating")
    Call<SuperResp> sortByServiceRating(@Query("industryId") int industryId);

    @GET("homepageservice/sortByComprehensive")
    Call<SuperResp> sortByComprehensive(@Query("industryId") int industryId);

    @GET("homepageservice/findByPriceBetween")
    Call<SuperResp> findByPriceBetween(@Query("fprice") int fprice, @Query("lprice") int lprice);

    @GET("homepageservice/findByPriceAfter")
    Call<SuperResp> findByPriceAfter(@Query("price") int price);

    @GET("polling/fetchAll")
    Call<CategoryResp> fetchAllPolling();

    @POST("polling/update")
    Call<CategoryResp> updatePolling(@Body RequestBody requestBody);
}
