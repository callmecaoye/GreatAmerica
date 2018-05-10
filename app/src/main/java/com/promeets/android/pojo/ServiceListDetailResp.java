package com.promeets.android.pojo;

/**
 * Created by Shashank Shekhar on 08-02-2017.
 */

public class ServiceListDetailResp {
    private String serviceTitle;

    private String eventExpId;

    private int eventRequestId;

    private String eventUserId;

    private int ifExpert;

    public String getServiceTitle ()
    {
        return serviceTitle;
    }

    public void setServiceTitle (String serviceTitle)
    {
        this.serviceTitle = serviceTitle;
    }

    public String getEventExpId ()
    {
        return eventExpId;
    }

    public void setEventExpId (String eventExpId)
    {
        this.eventExpId = eventExpId;
    }

    public int getEventRequestId ()
    {
        return eventRequestId;
    }

    public void setEventRequestId (int eventRequestId)
    {
        this.eventRequestId = eventRequestId;
    }

    public String getEventUserId ()
    {
        return eventUserId;
    }

    public void setEventUserId (String eventUserId)
    {
        this.eventUserId = eventUserId;
    }

    public int getIfExpert ()
    {
        return ifExpert;
    }

    public void setIfExpert (int ifExpert)
    {
        this.ifExpert = ifExpert;
    }

    @Override
    public String toString()
    {
        return serviceTitle;
    }
}
