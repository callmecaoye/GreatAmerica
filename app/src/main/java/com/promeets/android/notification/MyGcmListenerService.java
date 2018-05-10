package com.promeets.android.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.R;
import com.promeets.android.activity.HomeActivity;
import com.promeets.android.activity.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by kundan on 10/22/2015.
 */

/**
 *
 *
 * NOT USING ???
 *
 *
 */




public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {

        Bundle message = data.getBundle("notification");
        if(message!=null&&data!=null) {

            System.out.println("########message" + message);
            String title = message.getString("title");
            String body = message.getString("body");
            String url = null;
            if(data.getString("url")!=null){
                url = data.getString("url");
            }
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
        Intent resultIntent;
        if(url!=null){
            //resultIntent= new Intent(this, EventProcessStatusActivity.class);
            resultIntent= new Intent(this, AppointStatusActivity.class);

                String tmp[] = url.split("/");
                try {
                    int requestId = Integer.parseInt(tmp[tmp.length-1]);
                    resultIntent.putExtra("eventRequestId", requestId);
                }catch (Exception e){

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

