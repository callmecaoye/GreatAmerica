package com.promeets.android.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Generated;

/**
 * Created by xiaoyudong on 10/27/16.
 */
@Generated("org.jsonschema2pojo")
public class UserProfilePOJO implements Serializable {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("photoURL")
    @Expose
    public String photoURL;

    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("firstName")
    @Expose
    public String firstName;

    @SerializedName("lastName")
    @Expose
    public String lastName;

    @SerializedName("sex")
    @Expose
    public String sex;

    @SerializedName("city")
    @Expose
    public String city;

    @SerializedName("industryIdList")
    @Expose
    public ArrayList<Integer> industryIdList;

    @SerializedName("industryList")
    @Expose
    public ArrayList<Category> industryList;

    @SerializedName("pollingIdList")
    @Expose
    public ArrayList<Integer> pollingIdList;

    @SerializedName("pollingList")
    @Expose
    public ArrayList<Category> pollingList;

    @SerializedName("industry")
    @Expose
    public String industry;

    @SerializedName("employer")
    @Expose
    public String employer;

    @SerializedName("position")
    @Expose
    public String position;

    @SerializedName("agelevel")
    @Expose
    public String agelevel;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("lastLoginLocation")
    @Expose
    public String lastLoginLocation;

    @SerializedName("lastLoginTime")
    @Expose
    public String lastLoginTime;

    @SerializedName("expertStatus")
    @Expose
    public String expertStatus;

    @SerializedName("inviteCode")
    @Expose
    public String inviteCode;

    @SerializedName("viewExpProfile")
    @Expose
    public int viewExpProfile;

    public String contactEmail;
}
