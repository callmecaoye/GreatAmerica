package com.promeets.android.pojo;

import com.promeets.android.object.ExpertProfilePOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class ExpertProfilePost {
    @SerializedName("userId")
    @Expose
    public int userId;

    @SerializedName("expertProfile")
    @Expose
    public ExpertProfilePOJO expertProfile;
    
    @SerializedName("serviceList")
    @Expose
    public ArrayList<ServicePost> serviceList = new ArrayList<ServicePost>();

}
