package com.promeets.android.pojo;

import com.promeets.android.object.ServiceReview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.Info;

import java.util.ArrayList;

/**
 * Created by sosasang on 1/17/17.
 */

public class AllReviewsResp {
    @SerializedName("dataList")
    @Expose
    public ArrayList<ServiceReview> dataList = null;

    @SerializedName("reviewList")
    @Expose
    public ArrayList<ServiceReview> reviewList = null;

    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("data")
    @Expose
    public ServiceReview data;
}
