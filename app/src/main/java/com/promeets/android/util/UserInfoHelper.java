package com.promeets.android.util;

import com.promeets.android.activity.BaseActivity;
import android.content.Context;
import android.content.SharedPreferences;
import com.promeets.android.object.ExpertProfilePOJO;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.promeets.android.object.UserPOJO;

import com.promeets.android.object.UserProfilePOJO;

/**
 * Created by xiaoyudong on 11/23/16.
 */

public class UserInfoHelper {
    Context context;
    Gson gson = new Gson();
    SharedPreferences mPrefs;

    public UserInfoHelper(Context context){
        this.context = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public UserPOJO getUserObject(){
        UserPOJO user = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY,UserPOJO.class);
        return user;
    }
    public UserProfilePOJO getUserProfileObject(){
        UserProfilePOJO userProfile = (UserProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY,UserProfilePOJO.class);
        return userProfile;
    }

    public ExpertProfilePOJO getExpertProfile(){
        ExpertProfilePOJO expertProfile = (ExpertProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY,ExpertProfilePOJO.class);
        return expertProfile;
    }

    public ExpertProfilePOJO getDraftExpertProfile(){
        String expertProfileJson = mPrefs.getString("DraftExpertProfileObject",null);
        if(expertProfileJson == null)
            return null;
        ExpertProfilePOJO expertProfile = gson.fromJson(expertProfileJson, ExpertProfilePOJO.class);
        return expertProfile;
        //ExpertProfilePOJO expertProfile = (ExpertProfilePOJO) PromeetsUtils.getUserData((BaseActivity) mBaseActivity,PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY,ExpertProfilePOJO.class);
        //return expertProfile;
    }

    public void PutExpertProfile(ExpertProfilePOJO expertProfilePOJO){
        ServiceResponseHolder.getInstance().setExpertProfile(expertProfilePOJO);
    }

    public void PutDraftExpertProfile(ExpertProfilePOJO expertProfilePOJO){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if (expertProfilePOJO == null) {
            prefsEditor.putString("DraftExpertProfileObject", null);
            prefsEditor.commit();
            return;
        }
        String json = gson.toJson(expertProfilePOJO);
        prefsEditor.putString("DraftExpertProfileObject", json);
        prefsEditor.commit();
        //ServiceResponseHolder.getInstance().setExpertProfile(expertProfilePOJO);
    }
    public void cleanData(){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().clear().apply();

        ServiceResponseHolder.getInstance().setExpertProfile(null);
        ServiceResponseHolder.getInstance().setUserProfile(null);
    }
}
