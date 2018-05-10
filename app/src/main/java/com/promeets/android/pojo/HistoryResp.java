package com.promeets.android.pojo;

import com.promeets.android.object.HistoryPOJO;
import com.promeets.android.object.Info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sosasang on 1/12/17.
 */

public class HistoryResp {
    @SerializedName("displayAmount")
    @Expose
    public String displayAmount;

    @SerializedName("appointmentCount")
    @Expose
    public Integer appointmentCount;

    @SerializedName("displayFree")
    @Expose
    public String displayFree;

    @SerializedName("dataList")
    @Expose
    public List<HistoryPOJO> dataList = null;

    @SerializedName("smallphotoUrl")
    @Expose
    public String smallphotoUrl;

    @SerializedName("info")
    @Expose
    public Info info;
}
