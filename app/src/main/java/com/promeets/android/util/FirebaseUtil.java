package com.promeets.android.util;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Shashank Shekhar on 10-03-2017.
 */

public class FirebaseUtil
{
    /**
     * Parameter Key
     */
    public static final String EVENT_TYPE = "event_type";

    /***
     * Page Names
     *
     */
    public static final String SPLASH_SCREEN_LOAD = "splash";
    public static final String LOGIN_SCREEN_LOAD = "login";
    public static final String REGISTER_SCREEN_LOAD = "registration";
    public static final String EXPERT_SCREEN_LOAD = "expert_page_view";
    public static final String MAKE_AN_APPOINTMENT_SCREEN_LOAD = "make_appointment";
    public static final String ACCEPT_APPOINTMENT_PAGE = "accept_appointment";
    public static final String DECLINE_APPOINTMENT_PAGE = "decline_appointment";
    public static final String PAYMENT_PAGE = "payment";
    public static final String CANCEL_APPOINTMENT = "cancel_appointment";
    public static final String USER_PROFILE_PAGE = "user_profile";
    public static final String EXPERT_PROFILE_PAGE  = "expert_profile";

    private Activity activity;
    private static FirebaseUtil instance;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseUtil(Activity activity){
        this.activity = activity;
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);

    }

    public synchronized static FirebaseUtil getInstance(Activity activity) {
        if(instance==null)
            instance = new FirebaseUtil(activity);
        return instance;
    }
    public void buttonClick(String pageName,String[] key,String[] value){
        Bundle bundle = new Bundle();
        //bundle.putString("event_type","button_click,"+value);
        for(int index=0;index<key.length;index++){
            bundle.putString(key[index],value[index]);
        }
        mFirebaseAnalytics.logEvent(pageName, bundle);
    }
    public void pageLoad(String pageName){
        //Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(pageName, null);
    }

}
