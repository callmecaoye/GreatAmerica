package com.promeets.android.notification;

import com.promeets.android.activity.GroupChatActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        String channelUrl = null;
        String targetName = null;
        String targetId = null;
        try {
            JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
            JSONObject channel = (JSONObject) sendBird.get("channel");
            channelUrl = (String) channel.get("channel_url");
            JSONObject recipient = (JSONObject) sendBird.get("recipient");
            targetName = recipient.getString("name");
            targetId = recipient.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserDataWithContext(PromeetsPreferenceUtil.USER_OBJECT_KEY,UserPOJO.class);
        if (userPOJO != null && ("userName"+ userPOJO.id).equalsIgnoreCase(targetId))
            sendNotification(this, remoteMessage.getData().get("message"), channelUrl, targetName);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public static void sendNotification(Context context, String messageBody, String channelUrl, String targetName) {
        Intent intent = new Intent(context, GroupChatActivity.class);
        intent.putExtra("channelUrl", channelUrl);
        intent.putExtra("targetName", targetName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setAutoCancel(true) // mark as read
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent); // click event

        //if (PreferenceUtils.getNotificationsShowPreviews(context)) {
            notificationBuilder.setContentText(messageBody);
        //} else {
        //    notificationBuilder.setContentText("New Message");
        //}

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
