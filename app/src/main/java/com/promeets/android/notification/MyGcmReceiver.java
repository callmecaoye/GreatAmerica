package com.promeets.android.notification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GcmReceiver;

/**
 * Created by xiaoyudong on 1/13/17.
 */

public class MyGcmReceiver extends GcmReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName cn = new ComponentName(context.getPackageName(), PushReceiverIntentService.class.getName());
        startWakefulService(context, intent.setComponent(cn));
        setResultCode(Activity.RESULT_OK);
    }
}
