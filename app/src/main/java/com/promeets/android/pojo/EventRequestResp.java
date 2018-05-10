package com.promeets.android.pojo;

import com.promeets.android.object.Appoint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventRequestResp extends BaseResp {

    @SerializedName("dataList")
    @Expose
    public ArrayList<Appoint> dataList;

}
