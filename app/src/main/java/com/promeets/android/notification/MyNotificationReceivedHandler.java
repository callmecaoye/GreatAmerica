package com.promeets.android.notification;

import com.promeets.android.MyApplication;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.promeets.android.object.NotificationPOJO;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import java.text.SimpleDateFormat;
import java.util.Date;

//This will be called when a notification is received while your app is running.
public class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        NotificationPOJO pojo = new NotificationPOJO();
        pojo.msgId = notification.payload.notificationID;
        pojo.msgTitle = notification.payload.title;
        pojo.msgContent = notification.payload.body;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        pojo.lastSendTime = format.format(new Date());


        SharedPreferences mPrefs = MyApplication.getContext().getSharedPreferences("PromeetsTmp", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pojo);
        prefsEditor.putString(pojo.msgId, json);

        String list = mPrefs.getString("onesignal", "");
        list += pojo.msgId + ",";
        prefsEditor.putString("onesignal", list);

        prefsEditor.commit();
    }
}