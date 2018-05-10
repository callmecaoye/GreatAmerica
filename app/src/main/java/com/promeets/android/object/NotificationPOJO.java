package com.promeets.android.object;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class NotificationPOJO implements Comparable {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("userId")
    @Expose
    public int userId;
    @SerializedName("msgTitle")
    @Expose
    public String msgTitle;

    @SerializedName("msgContent")
    @Expose
    public String msgContent;

    @SerializedName("msgUrl")
    @Expose
    public String msgUrl;

    @SerializedName("iconUrl")
    @Expose
    public String iconUrl;

    @SerializedName("sentFlag")
    @Expose
    public int sentFlag;

    @SerializedName("lastSendTime")
    @Expose
    public String lastSendTime;

    @SerializedName("readFlag")
    @Expose
    public int readFlag;

    @SerializedName("readTime")
    @Expose
    public String readTime;

    @SerializedName("msgId")
    @Expose
    public String msgId;


    @Override
    public int compareTo(@NonNull Object o) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar m = Calendar.getInstance();
        Calendar n = (Calendar) m.clone();
        try {
            m.setTime(format.parse(this.lastSendTime));
            n.setTime(format.parse(((NotificationPOJO)o).lastSendTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long mSec = m.getTimeInMillis();
        long nSec = n.getTimeInMillis();
        if (nSec - mSec > 0)
            return 1;
        else if (nSec - mSec < 0)
            return -1;
        else return 0;
    }
}
