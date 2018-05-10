package com.promeets.android.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sosasang on 8/17/17.
 */

public class PollingPost {
    @SerializedName("industryIdList")
    @Expose
    public ArrayList<Integer> industryIdList;
}
