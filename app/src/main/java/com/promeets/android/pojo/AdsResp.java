package com.promeets.android.pojo;

import com.promeets.android.object.Advertisement;

/**
 * Created by Shashank Shekhar on 20-02-2017.
 */

public class AdsResp {
    private Advertisement[] dataList;

    private StatusResponse info;

    public Advertisement[] getDataList ()
    {
        return dataList;
    }

    public void setDataList (Advertisement[] dataList)
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
