package com.promeets.android.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * Created by xiaoyudong on 10/27/16.
 */
@Generated("org.jsonschema2pojo")
public class UserPOJO implements Serializable {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("accountNumber")
    @Expose
    public String accountNumber;

    @SerializedName("accountConfirmed")
    @Expose
    public Boolean accountConfirmed;

    @SerializedName("userName")
    @Expose
    public String userName;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("createTime")
    @Expose
    public String createTime;

    @SerializedName("lastUpdateTime")
    @Expose
    public String lastUpdateTime;

    @SerializedName("userType")
    @Expose
    public String userType;

    @SerializedName("userStatus")
    @Expose
    public String userStatus;

    /**
     *
     *
     *
     */

}
