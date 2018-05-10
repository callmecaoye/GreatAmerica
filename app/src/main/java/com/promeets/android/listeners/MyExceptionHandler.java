package com.promeets.android.listeners;

import com.promeets.android.MyApplication;
import com.promeets.android.activity.BaseActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.promeets.android.activity.PromeetsSplashActivity;

/**
 * Restart app for unhandled crash
 */

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
    private BaseActivity mBaseActivity;

    public MyExceptionHandler(BaseActivity a) {
        mBaseActivity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e("caoye", throwable.getLocalizedMessage());

        Intent intent = new Intent(mBaseActivity, PromeetsSplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager mgr = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);

        mBaseActivity.finish();
        System.exit(2);
    }
}
