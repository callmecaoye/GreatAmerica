package com.promeets.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.promeets.android.activity.VideoChatActivity;
import com.promeets.android.fragment.ComingMeetingFragment;
import com.promeets.android.notification.MyNotificationOpenedHandler;
import com.promeets.android.notification.MyNotificationReceivedHandler;
import com.promeets.android.pojo.SocketResp;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.sendbird.android.SendBird;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import com.onesignal.OneSignal;
import com.techdew.stomplibrary.Stomp;
import com.techdew.stomplibrary.StompClient;

import org.java_websocket.WebSocket;

/**
 * This is the Android App entry point, following the same life cycle as the app
 *
 * By default, Application class is a singleton class
 *
 */

public class MyApplication extends Application {
    /**
     * We do some necessary initializations for the whole project:
     * chat module, GCM, custom fonts etc.
     *
     * ATTENTION: no time-consuming action during the onCreate()
     */

    private static Context context;
    public static Context getContext() {
        return context;
    }

    //public static String SERVER_URL;
    //public static String TOPIC_URL;

    private static int stateCount = 0;
    private static StompClient mStompClient;
    public static String socketUrl;
    public static String topic;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // different key values for debug and release version
        Bundle bundle = ai.metaData;
        // Chat
        final String SENDBIRD_APP_ID = bundle.getString("SENDBIRD_APPKEY");
        SendBird.init(SENDBIRD_APP_ID, getApplicationContext());

        // Custom Default Fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();

        //LeakCanary.install(this);
        //Stetho.initializeWithDefaults(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (stateCount == 0 && !TextUtils.isEmpty(socketUrl) && !TextUtils.isEmpty(topic))
                    connectStomp();
                stateCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setTopActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                stateCount--;
                if (stateCount == 0 && mStompClient != null && mStompClient.isConnected())
                    mStompClient.disconnect();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.primary, R.color.white);
                layout.setEnableHeaderTranslationContent(false);
                return new MaterialHeader(context).setColorSchemeColors(
                        context.getResources().getColor(R.color.primary));
            }
        });
    }

    public static void connectStomp() {
        if (TextUtils.isEmpty(socketUrl) || TextUtils.isEmpty(topic))
            return;
        mStompClient = Stomp.over(WebSocket.class, socketUrl);
        mStompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    if (MyActivityManager.getInstance().getTopActivity() != null
                            && stateCount > 0) {
                        Activity curActivity = MyActivityManager.getInstance().getTopActivity();
                        Gson gson = new Gson();
                        SocketResp result = gson.fromJson(topicMessage.getPayload(), SocketResp.class);
                        if (result.info.code.equals("200")
                                && !(curActivity instanceof VideoChatActivity)
                                && !TextUtils.isEmpty(result.videoData.appId)
                                && result.videoData.uid > 0
                                && !TextUtils.isEmpty(result.videoData.channelName)) {
                            ComingMeetingFragment dialogFragment = ComingMeetingFragment.newInstance(result.videoData);
                            if (dialogFragment.isAdded())
                                return;
                            dialogFragment.show(MyActivityManager.getInstance().getTopActivity().getFragmentManager(), "coming meeting");
                        }
                    }
                });

        mStompClient.connect();
    }

    public static void disconnectStomp() {
        if (mStompClient != null && mStompClient.isConnected())
            mStompClient.disconnect();
    }
}
