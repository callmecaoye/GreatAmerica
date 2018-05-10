package com.promeets.android.pojo;

import com.promeets.android.object.Info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sosasang on 11/13/17.
 */

public class SurveyResp {
    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("isExpert")
    @Expose
    public int isExpert;
}
