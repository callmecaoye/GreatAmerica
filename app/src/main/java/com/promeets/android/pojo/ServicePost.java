package com.promeets.android.pojo;

import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.EventTimePOJO;
import com.promeets.android.object.ExpertService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.promeets.android.object.Info;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class ServicePost {
    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("eventDefaultDateList")
    @Expose
    public ArrayList<EventTimePOJO> eventDefaultDateList;

    @SerializedName("eventDefaultLocationList")
    @Expose
    public ArrayList<EventLocationPOJO> eventDefaultLocationList;

    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("expertService")
    @Expose
    public ExpertService expertservice;


    /**
     *
     * No args constructor for use in serialization
     *
     */
    public ServicePost() {

    }

    /**
     *
     * @param token
     * @param accessToken
     * @param user
     * @param info
     *
     */
}
