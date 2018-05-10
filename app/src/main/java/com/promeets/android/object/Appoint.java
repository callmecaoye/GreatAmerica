package com.promeets.android.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.pojo.BaseResp;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class Appoint extends BaseResp {
    @SerializedName("eventAction")
    @Expose
    public EventAction eventAction;

    @SerializedName("eventData")
    @Expose
    public EventData eventData;

    @SerializedName("expertService")
    @Expose
    public ExpertService expertService;

    @SerializedName("expertName")
    @Expose
    public String expertName;

    @SerializedName("displayName")
    @Expose
    public String displayName;

    @SerializedName("expertPhotoUrl")
    @Expose
    public String expertPhotoUrl;

    @SerializedName("displayPosition")
    @Expose
    public String displayPosition;

    @SerializedName("displayCompany")
    @Expose
    public String displayCompany;

    @SerializedName("displayPhotoUrl")
    @Expose
    public String displayPhotoUrl;
}
