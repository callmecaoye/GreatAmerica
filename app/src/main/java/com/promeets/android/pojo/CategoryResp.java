package com.promeets.android.pojo;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.Category;
import com.promeets.android.object.Info;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class CategoryResp {

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("contentCategoryList")
    @Expose
    public Category[] categoryList;
}
