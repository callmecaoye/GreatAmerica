package com.promeets.android.services;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;


/**
 * Created by SHASHANK on 26-10-2015.
 */
public abstract class PromeetsAsyncTask extends AsyncTask<Object, Object, Object> {
    /**
     * execute method which will handle all execute call of
     * GenericServiceHandler class
     */
    public final void execute() { // this method is to check on which version of
        // android we are running this asynctask, from
        // honeycomb version will use threadpoolexecutor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[]) null);
        } else {
            execute((Void) null);
        }

    }
}
