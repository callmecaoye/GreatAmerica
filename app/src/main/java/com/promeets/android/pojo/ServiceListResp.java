package com.promeets.android.pojo;

import java.util.ArrayList;

/**
 * Created by Shashank Shekhar on 08-02-2017.
 */

public class ServiceListResp
{
    private ArrayList<ServiceListDetailResp> dataList;

    private String chatFlag;

    private String targetExpId;

    private StatusResponse info;

    public ArrayList<ServiceListDetailResp> getDataList ()
    {
        return dataList;
    }

    public void setDataList (ArrayList<ServiceListDetailResp> dataList)
    {
        this.dataList = dataList;
    }

    public String getChatFlag ()
    {
        return chatFlag;
    }

    public void setChatFlag (String chatFlag)
    {
        this.chatFlag = chatFlag;
    }

    public String getTargetExpId ()
    {
        return targetExpId;
    }

    public void setTargetExpId (String targetExpId)
    {
        this.targetExpId = targetExpId;
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
        return "ClassPojo [dataList = "+dataList+", chatFlag = "+chatFlag+", targetExpId = "+targetExpId+", info = "+info+"]";
    }
}
