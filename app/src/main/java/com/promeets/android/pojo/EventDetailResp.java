package com.promeets.android.pojo;

import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.ExpertProfilePOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.EventData;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.object.ExpertService;
import com.promeets.android.object.Info;
import com.promeets.android.object.ServiceReview;
import com.promeets.android.object.EventAction;
import com.promeets.android.object.UserDataPojo;
import com.promeets.android.object.VideoData;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventDetailResp {

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("info")
    @Expose
    public Info info;



    @SerializedName("eventLocationList")
    @Expose
    public ArrayList<EventLocationPOJO> eventLocationList = new ArrayList<EventLocationPOJO>();

    @SerializedName("eventDateList")
    @Expose
    public ArrayList<EventTimePOJO> eventDateList = new ArrayList<EventTimePOJO>();

    @SerializedName("eventRequest")
    @Expose
    public EventData eventRequest;

    @SerializedName("expertProfile")
    @Expose
    public ExpertProfilePOJO expertProfile;

    @SerializedName("serviceList")
    @Expose
    public ArrayList<ExpertService> serviceList = new ArrayList<ExpertService>();

    @SerializedName("list")
    @Expose
    public ArrayList<SuperListResp> list = new ArrayList<SuperListResp>();

    @SerializedName("serviceReview")
    @Expose
    public ArrayList<ServiceReview> serviceReview = new ArrayList<>();

    @SerializedName("eventAction")
    @Expose
    public EventAction eventAction;

    @SerializedName("eventData")
    @Expose
    public EventData eventData;

    @SerializedName("expertService")
    @Expose
    public ExpertService expertService;


    @SerializedName("userData")
    @Expose
    public UserDataPojo userData;
    //todo:need to convert to pojo

    @SerializedName("chargeDetail")
    @Expose
    public HashMap<String, String> chargeDetail = new HashMap<>();

    @SerializedName("review")
    @Expose
    public ServiceReview review;

    @SerializedName("balance")
    @Expose
    public ServiceReview balance;

    public VideoData videoData;
}
