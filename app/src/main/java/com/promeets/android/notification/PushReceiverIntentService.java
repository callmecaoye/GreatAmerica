package com.promeets.android.notification;

import com.promeets.android.activity.BillManagementActivity;
import com.promeets.android.activity.ExpertDashboardActivity;
import com.promeets.android.activity.ExpertDetailActivity;
import com.promeets.android.activity.HomeActivity;
import com.promeets.android.activity.MainActivity;
import com.promeets.android.activity.SurveyActivity;
import com.promeets.android.api.URL;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.promeets.android.pojo.SuperResp;

import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.activity.UserProfileActivity;
import com.promeets.android.api.UserActionApi;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;
import com.promeets.android.util.ServiceResponseHolder;

import org.jsoup.helper.StringUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xiaoyudong on 1/19/17.
 */

public class PushReceiverIntentService extends IntentService {

    private UserPOJO userPOJO;

    public PushReceiverIntentService(){
        super("PushReceiverIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String url = extras.getString("url");
            String title = extras.getString("title");
            String body = extras.getString("body");

            GlobalVariable.msgId = extras.getString("msgId");

            if(title!=null&&body!=null)
                sendNotification(title, body, url);

        }
    }


    public void sendNotification(String title, String content, String url) {

//Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "");
        mBuilder.setSmallIcon(R.drawable.app_icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        Intent resultIntent = null;
        if(!TextUtils.isEmpty(url)){
            /*resultIntent= new Intent(this, EventProcessStatusActivity.class);

            String tmp[] = url.split("/");
            try {
                int requestId = Integer.parseInt(tmp[tmp.length-1]);
                resultIntent.putExtra("eventRequestId", requestId);
            }catch (Exception e){

            }*/
            if (url.contains("promeets://ordersurvey/orderId/")) {
                int orderId = 0;
                String tmp[] = url.split("/");
                try {
                    orderId = Integer.parseInt(tmp[tmp.length-1]);
                } catch (Exception e){ }
                //resultIntent= new Intent(this, EventProcessStatusActivity.class);
                resultIntent= new Intent(this, SurveyActivity.class);
                resultIntent.putExtra("orderId", orderId);
            } else if (url.contains("order")) {
                int requestId = 0;
                String tmp[] = url.split("/");
                try {
                    requestId = Integer.parseInt(tmp[tmp.length-1]);
                } catch (Exception e){ }
                //resultIntent= new Intent(this, EventProcessStatusActivity.class);
                resultIntent= new Intent(this, AppointStatusActivity.class);
                resultIntent.putExtra("eventRequestId", requestId);
            } else if (url.contains("expertdashboard")) {
                resultIntent = new Intent(this, ExpertDashboardActivity.class);
            }
            else if (url.contains("expert")) {
                String expId = "";
                String tmp[] = url.split("/");
                try {
                    expId = tmp[tmp.length-1];
                } catch (Exception e){ }
                resultIntent = new Intent(this, ExpertDetailActivity.class);
                resultIntent.putExtra("expId", expId);
            } else if (url.contains("balance")) {
                resultIntent = new Intent(this, BillManagementActivity.class);

            } else if (url.contains("profile")) {
                int userId = 0;
                String tmp[] = url.split("/");
                try {
                    userId = Integer.parseInt(tmp[tmp.length-1]);
                } catch (Exception e){ }
                resultIntent = new Intent(this,UserProfileActivity.class);
                resultIntent.putExtra("id", userId);

            } else if (url.contains("becomeexp")) {
                HomeActivity.currentTabIndex = 4;
                resultIntent = new Intent(this, HomeActivity.class);
            }
        }else{
            resultIntent = new Intent(this, HomeActivity.class);
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(001, mBuilder.build());
    }
}