package com.promeets.android.pojo;

import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.ExpertService;
import com.promeets.android.object.LoginSlogan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.EventData;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.object.Info;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class BaseResp {

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("data")
    @Expose
    public LoginSlogan data;

    @SerializedName("expertservice")
    @Expose
    public ArrayList<ExpertService> expertService = new ArrayList<>();

    @SerializedName("eventLocationList")
    @Expose
    public ArrayList<EventLocationPOJO> eventLocationList = new ArrayList<EventLocationPOJO>();

    @SerializedName("eventDateList")
    @Expose
    public ArrayList<EventTimePOJO> eventDateList = new ArrayList<EventTimePOJO>();

    @SerializedName("eventRequest")
    @Expose
    public EventData eventDate ;

}
