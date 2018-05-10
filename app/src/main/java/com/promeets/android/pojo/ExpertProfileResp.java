package com.promeets.android.pojo;

import com.promeets.android.object.ExpertProfilePOJO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.Info;

public class ExpertProfileResp {
    @SerializedName("accessToken")
    @Expose
    public String accessToken;
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("info")
    @Expose
    public Info info;
    @SerializedName("expertProfile")
    @Expose
    public ExpertProfilePOJO expertProfile;

    /**
     * No args constructor for use in serialization
     *
     */
    public ExpertProfileResp() {
    }

    /**
     *
     * @param token
     * @param accessToken
     * @param info
     * @param expertProfile
     */
    public ExpertProfileResp(String token, String accessToken, Info info, ExpertProfilePOJO expertProfile) {
        this.token = token;
        this.accessToken = accessToken;
        this.info = info;
        this.expertProfile = expertProfile;
    }
}
