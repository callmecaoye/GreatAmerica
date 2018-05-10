package com.promeets.android.pojo;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.EventTimePOJO;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventUpdatePost {

    @SerializedName("eventDateList")
    @Expose
    public ArrayList<EventTimePOJO> eventDateList = new ArrayList<EventTimePOJO>();

    @SerializedName("eventLocationList")
    @Expose
    public ArrayList<EventLocationPOJO> eventLocation = new ArrayList<EventLocationPOJO>();

    @SerializedName("eventRequest")
    @Expose
    public EventPost eventRequest;

    @SerializedName("id")
    @Expose
    public int id;


}
