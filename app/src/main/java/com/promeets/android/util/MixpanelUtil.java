package com.promeets.android.util;

import android.app.Activity;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sosasang on 6/28/17.
 */

public class MixpanelUtil {
    public static final String PROJECT_TOKEN = "279ee048e2b164833c10d35fbb831525";
    public static final String API_KEY = "af1d6062dcd7ba8b05b6207d5c7ac4a2";
    public static final String API_SECRET = "2ce5480100cebb54b8e4389e64f5ef25";

    private static MixpanelUtil instance;
    private MixpanelAPI mixpanel;

    public MixpanelUtil(Activity activity) {
        mixpanel = MixpanelAPI.getInstance(activity, PROJECT_TOKEN);
    }

    public synchronized static MixpanelUtil getInstance(Activity activity) {
        if(instance==null)
            instance = new MixpanelUtil(activity);
        return instance;
    }

    public void trackEvent(String eventName) {
        mixpanel.track(eventName);
    }

    public void trackEvent(String eventName, HashMap<String, String> map) {
        try {
            JSONObject props = new JSONObject();
            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    props.put(key, map.get(key));
                }
            }
            mixpanel.track(eventName, props);
        } catch (JSONException e) {
            Log.e("Mixpanel", "Unable to add properties to JSONObject", e);
        }
    }
}
