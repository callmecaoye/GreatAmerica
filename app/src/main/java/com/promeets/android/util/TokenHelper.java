package com.promeets.android.util;

import android.app.Activity;

import com.promeets.android.api.NotificationRelatedApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.notification.GCMClientManager;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.object.UserPOJO;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenHelper {
    String PROJECT_NUMBER="374073472386";
    Activity context;
    public TokenHelper(Activity context){
        this.context=context;
    }

    public void initAppInfo(final Activity activity){
        GCMClientManager pushClientManager = new GCMClientManager(context, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {

                Log.d("Registration id", registrationId);
                //send this registrationId to your server
                updateTokenToServer(registrationId, Utility.getDeviceId());
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });
    }
    private void updateTokenToServer(final String token, String deviceId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserInfoHelper userInfoHelper = new UserInfoHelper(context);
        UserPOJO user = userInfoHelper.getUserObject();
        JSONObject json = new JSONObject();
        try {
            if(user!=null) {
                json.put("viewId", user.id);
            }
            json.put("deviceToken", token);
            json.put("platform", "GCM");
            json.put(Constant.DEVICE_ID, deviceId);
        }catch (JSONException e) {
        }
        NotificationRelatedApi service = retrofit.create(NotificationRelatedApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Call<SuperResp> call = service.updateDeviceToken(requestBody);//get request, need to be post!
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(context, response.errorBody().toString());
                    return;
                }

                if(!result.info.code.equals("200")){
                    PromeetsDialog.show(context, result.info.description);
                }
            }
            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {

            }
        });
    }
}
