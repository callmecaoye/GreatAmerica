package com.promeets.android.pojo;

import com.promeets.android.object.EventData;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.ExpertService;
import com.promeets.android.object.PhotoMapPOJO;
import com.promeets.android.object.ServiceReview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.object.Info;
import com.promeets.android.object.EventAction;
import com.promeets.android.object.UserProfilePOJO;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class SuperResp {

    @SerializedName("serviceReviewListCount")
    @Expose
    public Integer serviceReviewListCount;

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("info")
    @Expose
    public Info info;

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
    public EventData eventRequest;

    @SerializedName("expertProfile")
    @Expose
    public ExpertProfilePOJO expertProfile;

    @SerializedName("serviceList")
    @Expose
    public ArrayList<ExpertService> serviceList = new ArrayList<ExpertService>();

    @SerializedName("recommendList")
    @Expose
    public ArrayList<ExpertService> recommendList = new ArrayList<>();

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

    @SerializedName("userProfile")
    @Expose
    public UserProfilePOJO userProfile;

    @SerializedName("photoMap")
    @Expose
    public PhotoMapPOJO photoMapPOJO;

    @SerializedName("url")
    @Expose
    public String url;

    
}
