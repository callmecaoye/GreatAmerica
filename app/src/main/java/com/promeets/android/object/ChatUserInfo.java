package com.promeets.android.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * Created by xiaoyudong on 10/27/16.
 */
@Generated("org.jsonschema2pojo")
public class ChatUserInfo implements Serializable {

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("accessToken")
    @Expose
    public String accessToken;
}
