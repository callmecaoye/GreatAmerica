package com.promeets.android.pojo;

import com.promeets.android.object.ExpertProfile;

import java.util.ArrayList;

/**
 * Created by Shashank Shekhar on 06-03-2017.
 */

public class WishResp {
    private ArrayList<ExpertProfile> dataList;

    private StatusResponse info;

    public ArrayList<ExpertProfile> getDataList ()
    {
        return dataList;
    }

    public void setDataList (ArrayList<ExpertProfile> dataList)
    {
        this.dataList = dataList;
    }

    public StatusResponse getInfo ()
    {
        return info;
    }

    public void setInfo (StatusResponse info)
    {
        this.info = info;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [dataList = "+dataList+", info = "+info+"]";
    }
}
