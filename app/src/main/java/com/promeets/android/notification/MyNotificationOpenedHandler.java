package com.promeets.android.notification;

import com.promeets.android.MyApplication;
import com.promeets.android.activity.NotificationDetailActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.promeets.android.object.NotificationPOJO;

import com.google.gson.Gson;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import org.apache.commons.lang3.StringUtils;

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotification notification = result.notification;

        // get NotificationPOJO
        Gson gson = new Gson();
        SharedPreferences mPrefs = MyApplication.getContext().getSharedPreferences("PromeetsTmp", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        String json = mPrefs.getString(notification.payload.notificationID, "");

        if (!StringUtils.isEmpty(json)) {
            NotificationPOJO pojo = gson.fromJson(json, NotificationPOJO.class);
            pojo.readFlag = 1;
            json = gson.toJson(pojo);
            prefsEditor.putString(notification.payload.notificationID, json);
            prefsEditor.commit();

            Intent intent = new Intent(MyApplication.getContext(), NotificationDetailActivity.class);
            intent.putExtra("notificationPOJO", json);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);
        }
    }
}
