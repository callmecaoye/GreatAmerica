package com.promeets.android.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.ExpertService;
import com.promeets.android.object.Info;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class ServiceResp {
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
    public ArrayList<ExpertService> expertservice = new ArrayList<>();

    /**
     *
     * No args constructor for use in serialization
     *
     */
    public ServiceResp() {

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
