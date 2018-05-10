package com.promeets.android.api;

import com.google.gson.JsonObject;
import com.promeets.android.pojo.CreditCardResp;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.HistoryResp;
import com.promeets.android.pojo.PromoResp;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by sosasang on 12/20/16.
 */

public interface PaymentApi {
    @FormUrlEncoded
    @POST("userpayment/checkout")
    Call<JsonObject> checkoutPayment(@Field("userId") String user_id,
                                     @Field("stripeToken") String token_id);

    @GET("userpayment/retrieveCustomer")
    Call<CreditCardResp> fetchCreditCard(@Query("userId") int user_id);

    @POST("userpayment/deleteCard")
    Call<BaseResp> deleteCard(@Body RequestBody requestBody);

    @GET("displayPayment/history")
    Call<HistoryResp> fetchHistory(@Query("viewId") int user_id, @Query("timeZone") String timeZone,
                                   @Query("pageNumber") int pageNumber);

    @FormUrlEncoded
    @POST("expertwithdraw/transfer")
    Call<JsonObject> transfer(@Field("viewId") int user_id, @Field("email") String email);

    @GET("expertwithdraw/fetchExpAccount")
    Call<JsonObject> fetchEmail(@Query("viewId") int user_id);

    @FormUrlEncoded
    @POST("expertwithdraw/createExpAccount")
    Call<JsonObject> addEmail(@Field("viewId") int user_id, @Field("email") String email);

    @FormUrlEncoded
    @POST("promotion/checkCode")
    Call<PromoResp> checkPromoCode(@Field("promotionCode") String promotionCode, @Field("eventRequestId") int eventRequestId);
}
