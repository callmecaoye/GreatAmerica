package com.promeets.android.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sosasang on 1/12/17.
 */

public class HistoryPOJO {
    @SerializedName("amount")
    @Expose
    public String amount;

    @SerializedName("eventRequestId")
    @Expose
    public int eventRequestId;

    @SerializedName("fee")
    @Expose
    public String fee;

    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("updateTime")
    @Expose
    public String updateTime;

    @SerializedName("userId")
    @Expose
    public int userId;

    @SerializedName("photoURL")
    @Expose
    public String photoURL;

    @SerializedName("displayType")
    @Expose
    public String displayType;

    @SerializedName("displayInfo")
    @Expose
    public String displayInfo;

    @SerializedName("serviceId")
    @Expose
    public int serviceId;

    @SerializedName("expId")
    @Expose
    public int expId;

    @SerializedName("eventDate")
    @Expose
    public String eventDate;
}
