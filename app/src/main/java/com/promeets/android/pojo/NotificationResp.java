package com.promeets.android.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.NotificationPOJO;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class NotificationResp extends BaseResp {

    @SerializedName("dataList")
    @Expose
    public ArrayList<NotificationPOJO> dataList = new ArrayList<NotificationPOJO>();

    @SerializedName("msgCount")
    @Expose
    public int msgCount;

}
