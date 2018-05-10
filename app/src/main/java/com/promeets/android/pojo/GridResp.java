package com.promeets.android.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.Category;

/**
 * Created by Shashank Shekhar on 31-01-2017.
 */

public class GridResp {

    @SerializedName("contentCategoryList")
    @Expose
    private Category[] categoryList;

    private StatusResponse info;

    public StatusResponse getInfo ()
    {
        return info;
    }

    public void setInfo (StatusResponse info)
    {
        this.info = info;
    }

    public Category[] getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(Category[] categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [catePOJO = "+ categoryList +", info = "+info+"]";
    }
}
