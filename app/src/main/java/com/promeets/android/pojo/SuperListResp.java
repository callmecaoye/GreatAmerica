package com.promeets.android.pojo;

import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.ExpertService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class SuperListResp {

    @SerializedName("expertProfile")
    @Expose
    public ExpertProfilePOJO expertProfile ;

    @SerializedName("expertService")
    @Expose
    public ExpertService expertService ;

}
