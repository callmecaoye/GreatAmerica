package com.promeets.android.object;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventData {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("userId")
    @Expose
    public int userId;

    @SerializedName("expId")
    @Expose
    public int expId;

    @SerializedName("serviceId")
    @Expose
    public int serviceId;

    @SerializedName("whyInterested")
    @Expose
    public String whyInterested;

    @SerializedName("aboutMe")
    @Expose
    public String aboutMe;

    @SerializedName("price")
    @Expose
    public String price;

    @SerializedName("preferTime")
    @Expose
    public String preferTime;

    @SerializedName("preferAddress")
    @Expose
    public String preferAddress;

    @SerializedName("finalTime")
    @Expose
    public String finalTime;

    @SerializedName("finalAddress")
    @Expose
    public String finalAddress;

    @SerializedName("validForTime")
    @Expose
    public String validForTime;

    @SerializedName("expActionStatus")
    @Expose
    public int expActionStatus;

    @SerializedName("userActionStatus")
    @Expose
    public int userActionStatus;

    @SerializedName("createTime")
    @Expose
    public String createTime;

    @SerializedName("userLastSeen")
    @Expose
    public String userLastSeen;

    @SerializedName("expLastSeen")
    @Expose
    public String expLastSeen;

    @SerializedName("rejectResult")
    @Expose
    public String rejectResult;

    @SerializedName("settled")
    @Expose
    public int settled;

    @SerializedName("serialNumber")
    @Expose
    public String serialNumber;

    @SerializedName("ifHasRead")
    @Expose
    public Integer ifHasRead;

    public int meetingType;
}
