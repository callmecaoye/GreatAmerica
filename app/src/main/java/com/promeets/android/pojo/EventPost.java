package com.promeets.android.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventPost {
    @SerializedName("aboutMe")
    @Expose
    public String aboutMe;

    @SerializedName("expId")
    @Expose
    public int expId;

    @SerializedName("finalAddress")
    @Expose
    public String finalAddress;

    @SerializedName("finalTime")
    @Expose
    public String finalTime;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("preferAddress")
    @Expose
    public String preferAddress;

    @SerializedName("serviceId")
    @Expose
    public int serviceId;

    @SerializedName("settled")
    @Expose
    public int settled;

    @SerializedName("userId")
    @Expose
    public int userId;

    @SerializedName("validForTime")
    @Expose
    public String validForTime;

    @SerializedName("whyInterested")
    @Expose
    public String whyInterested;


    public EventPost() {

    }
}
