package com.promeets.android.util;

import com.promeets.android.MyApplication;
import com.promeets.android.api.URL;
import com.promeets.android.api.UserActionApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.object.ChatUserInfo;
import com.promeets.android.pojo.ChatAccountResp;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatHelper {
    SharedPreferences mPrefs;
    Context context;
    Gson gson = new Gson();

    public ChatHelper(Context context){
        this.context = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public ChatUserInfo getUserObject(){
        String chatUserInfoObject = mPrefs.getString("ChatUserInfoObject",null);
        if(chatUserInfoObject == null)
            return null;
        ChatUserInfo user = gson.fromJson(chatUserInfoObject, ChatUserInfo.class);
        return user;
    }
    public void putUserObject(ChatUserInfo chatUserInfoPOJO){
        String json = gson.toJson(chatUserInfoPOJO);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("ChatUserInfoObject", json);
        prefsEditor.commit();
    }
    public void getChatAccountInfoFromServer(int userId){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("sendbirdChat/fetchUserByNameAndId"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserActionApi service = retrofit.create(UserActionApi.class);
        Call<ChatAccountResp> call = service.getChatAccount(userId);

        call.enqueue(new Callback<ChatAccountResp>() {
            @Override
            public void onResponse(Call<ChatAccountResp> call, Response<ChatAccountResp> response) {
                ChatAccountResp result=response.body();
                if (result == null) {
                    PromeetsDialog.show((Activity) context, response.errorBody().toString());
                    return;
                }

                if(result.info.code.equals("200")){
                    putUserObject(result.chatUser);
                    //System.out.println);
                    if(result.chatUser!=null){
                        SendBird.connect(result.chatUser.username, result.chatUser.accessToken, new SendBird.ConnectHandler() {
                            @Override
                            public void onConnected(User user, SendBirdException e) {
                                if (e != null) {
                                    return;
                                }
                                SendBird.setAutoBackgroundDetection(true);
                                if (FirebaseInstanceId.getInstance().getToken() == null) return;

                                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                                        new SendBird.RegisterPushTokenWithStatusHandler() {
                                            @Override
                                            public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                                                if (e != null) {
                                                    // Error.
                                                    return;
                                                }
                                            }
                                        });
                            }
                        });
                    }

                    if (result.socketInfo != null
                            && !TextUtils.isEmpty(result.socketInfo.socketUrl)
                            && !TextUtils.isEmpty(result.socketInfo.topic)) {
                        MyApplication.socketUrl = result.socketInfo.socketUrl;
                        MyApplication.topic = result.socketInfo.topic;
                        MyApplication.connectStomp();
                    }
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP)|| result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue((Activity) context, result.info.code);
                } else {
                    PromeetsDialog.show((Activity) context, result.info.description);
                }
            }
            @Override
            public void onFailure(Call<ChatAccountResp> call, Throwable t) {

            }
        });
    }
}

