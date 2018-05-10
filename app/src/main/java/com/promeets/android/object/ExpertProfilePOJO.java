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
public class ExpertProfilePOJO implements Serializable {
    @SerializedName("activeArea")
    @Expose
    public String activeArea;

    @SerializedName("activeCity")
    @Expose
    public String activeCity;

    @SerializedName("degree")
    @Expose
    public String degree;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("education")
    @Expose
    public String education;

    @SerializedName("expId")
    @Expose
    public int expId;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("industryCategory")
    @Expose
    public String industryCategory;

    @SerializedName("industryExperience")
    @Expose
    public String industryExperience;

    @SerializedName("industryIdList")
    @Expose
    public ArrayList<Integer> industryIdList;

    @SerializedName("industryList")
    @Expose
    public ArrayList<Category> industryList;

    @SerializedName("lastUpdate")
    @Expose
    public String lastUpdate;



    @SerializedName("surfix")
    @Expose
    public String surfix;

    @SerializedName("position")
    @Expose
    public String positon;

    @SerializedName("smallphotoUrl")
    @Expose
    public String smallphotoUrl;

    @SerializedName("institution")
    @Expose
    public String institution;

    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("photoList")
    @Expose
    public ArrayList<ExpertAlbumPOJO> photoList;

    @SerializedName("link")
    @Expose
    public String link;

    @SerializedName("expertDefaultLocationList")
    @Expose
    public ArrayList<EventLocationPOJO> expertDefaultLocationList;

    @SerializedName("defaultDate")
    @Expose
    public int defaultDate;

    @SerializedName("availabilityType")
    @Expose
    public int availabilityType;

    @SerializedName("calendarType")
    @Expose
    public int calendarType;

    @SerializedName("serviceList")
    @Expose
    public ArrayList<ExpertService> serviceList;

    @SerializedName("expertStatus")
    @Expose
    public String expertStatus;

    @SerializedName("wantToMeeting")
    @Expose
    public String wantToMeeting;

    public boolean mChkStatus;

    public String inviteCode;


    public String expertiseArea;


    // Expert Sign Up
    public String status;
    public String step;
    public String contactEmail;
    public String contactNumber;
    public String professionalTitle;
    public String shortBio;
    public String hourlyRate;
    public String topicTitle;
    public String categories;
    public String tags;
    public long workshopTime;
    public long callTime;
    public String photoUrl;
}
