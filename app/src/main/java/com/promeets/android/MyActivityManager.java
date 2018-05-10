package com.promeets.android;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class MyActivityManager {
    private static MyActivityManager myActivityManager = new MyActivityManager();
    private WeakReference<Activity> topActivity;
    private MyActivityManager() {
    }
    public static MyActivityManager getInstance(){
        return myActivityManager;
    }
    public Activity getTopActivity() {
        if (topActivity!=null){
            return topActivity.get();
        }
        return null;
    }
    public void setTopActivity(Activity topActivity) {
        this.topActivity = new WeakReference<>(topActivity);
    }
}
