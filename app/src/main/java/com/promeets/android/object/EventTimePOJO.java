package com.promeets.android.object;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventTimePOJO implements Serializable{
    @SerializedName("beginHourOfDay")
    @Expose
    public String beginHourOfDay;

    @SerializedName("dayOfWeek")
    @Expose
    public String dayOfWeek;

    @SerializedName("endHourOfDay")
    @Expose
    public String endHourOfDay;

    @SerializedName("id")
    @Expose
    public long id;

    @SerializedName("pstBeginTime")
    @Expose
    public String pstBeginTime;

    @SerializedName("pstEndTime")
    @Expose
    public String pstEndTime;

    // chat
    @SerializedName("utcBeginTime")
    @Expose
    public long utcBeginTime;
    // chat
    @SerializedName("utcEndTime")
    @Expose
    public long utcEndTime;

    @SerializedName("rankOrder")
    @Expose
    public String rankOrder;

    @SerializedName("serviceId")
    @Expose
    public Integer serviceId;

    @SerializedName("timeZone")
    @Expose
    public String timeZone;

    @SerializedName("detailDay")
    @Expose
    public String detailDay;

    @SerializedName("eventRequestId")
    @Expose
    public int eventRequestId;

    public boolean isSelected = false;

    public EventTimePOJO() {

    }

    /**
     *
     * @param id
     * @param code
     * @param description
     */


}
