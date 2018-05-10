package com.promeets.android.util;

import android.app.Activity;
import com.promeets.android.object.UserPOJO;

import android.util.Log;

import java.security.MessageDigest;
import java.util.Date;

public class ServiceHeaderGeneratorUtil {

    private static ServiceHeaderGeneratorUtil instance;

    private String mAccessToken = "";

    private long appCurrentTime;

    private final String DEFAULT_CHECK_STRING = "44794fcb592c4ac28de10648aeddb2f7";

    private long mSyncTime;

    private String pTimeStamp="";

    private ServiceHeaderGeneratorUtil(){
        setUp();
    }

    public synchronized  static ServiceHeaderGeneratorUtil getInstance() {
        if(instance==null)
            instance = new ServiceHeaderGeneratorUtil();
        else
            instance.updateBaseActivity();
        return instance;
    }

    private void updateBaseActivity() {
        setUp();
    }

    private void setUp() {
        UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY,UserPOJO.class);
        if(userPOJO!=null)
            mAccessToken = userPOJO.accessToken;
    }

    public String getPromeetsTHeader(String requestUrl){

        String md5String =mAccessToken+pTimeStamp+DEFAULT_CHECK_STRING+"/"+requestUrl;
        Log.d("ServiceHeader", "token: " + mAccessToken);
        Log.d("ServiceHeader", "timestamp " + pTimeStamp);
        Log.d("ServiceHeader", requestUrl);
        Log.d("ServiceHeader","md5str: " + md5String);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(md5String.getBytes());
            byte[] byteData = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            String hashtext = sb.toString();
            Log.d("ServiceHeader", "md5hex: " + hashtext);
            return hashtext;
        } catch (Exception ex){

        }
        return "";
    }
    public String getPTimeStamp(){
        long currentTime = (new Date().getTime()/1000);
        pTimeStamp =  (mSyncTime+(currentTime-appCurrentTime))+"";
        return pTimeStamp;
    }

    public String getSyncTime() {

        return mSyncTime+"";
    }

    public void setmSyncTime(String mSyncTime) {
        this.mSyncTime = new Long(mSyncTime).longValue();
        appCurrentTime = (new Date().getTime()/1000);
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public long getTimeOffset() {
        return mSyncTime - appCurrentTime;
    }
}
