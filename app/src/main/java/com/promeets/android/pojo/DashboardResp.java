package com.promeets.android.pojo;

public class DashboardResp {

    private String displayFee;

    private String likeCount;

    private String displayAmount;

    private String appointmentCount;

    private String photoUrl;

    private String smallphotoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSmallphotoUrl() {
        return smallphotoUrl;
    }

    public void setSmallphotoUrl(String smallphotoUrl) {
        this.smallphotoUrl = smallphotoUrl;
    }

    private StatusResponse info;

    public String getDisplayFee ()
    {
        return displayFee;
    }

    public void setDisplayFee (String displayFee)
    {
        this.displayFee = displayFee;
    }

    public String getLikeCount ()
    {
        return likeCount;
    }

    public void setLikeCount (String likeCount)
    {
        this.likeCount = likeCount;
    }

    public String getDisplayAmount ()
    {
        return displayAmount;
    }

    public void setDisplayAmount (String displayAmount)
    {
        this.displayAmount = displayAmount;
    }

    public String getAppointmentCount ()
    {
        return appointmentCount;
    }

    public void setAppointmentCount (String appointmentCount)
    {
        this.appointmentCount = appointmentCount;
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
        return "ClassPojo [displayFee = "+displayFee+", likeCount = "+likeCount+", displayAmount = "+displayAmount+", appointmentCount = "+appointmentCount+", info = "+info+"]";
    }
}
