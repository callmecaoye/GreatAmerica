package com.promeets.android.pojo;

/**
 * Created by Shashank Shekhar on 16-02-2017.
 */

public class SyncTimeResp {
    private String syncTime;

    private StatusResponse info;

    public String getSyncTime ()
    {
        return syncTime;
    }

    public void setSyncTime (String syncTime)
    {
        this.syncTime = syncTime;
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
        return "ClassPojo [syncTime = "+syncTime+", info = "+info+"]";
    }
}
