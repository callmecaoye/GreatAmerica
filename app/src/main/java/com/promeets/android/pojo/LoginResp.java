package com.promeets.android.pojo;

import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.PhotoMapPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.promeets.android.object.Info;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.object.UserProfilePOJO;

import java.util.ArrayList;

public class LoginResp {
    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("user")
    @Expose
    public UserPOJO user;

    @SerializedName("userProfile")
    @Expose
    public UserProfilePOJO userProfile;

    @SerializedName("expertProfile")
    @Expose
    public ExpertProfilePOJO expertProfile;

    @SerializedName("photoMap")
    @Expose
    public PhotoMapPOJO photoMap;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("urls")
    @Expose
    public ArrayList<String> urls;

    public int needEmailFlag;

    /**
     * No args constructor for use in serialization
     *
     */
    public LoginResp() {
    }

    /**
     *
     * @param token
     * @param accessToken
     * @param user
     * @param info
     *
     */
    public LoginResp(String token, String accessToken, Info info, UserPOJO user) {
        this.token = token;
        this.accessToken = accessToken;
        this.info = info;
        this.user = user;
    }
}
