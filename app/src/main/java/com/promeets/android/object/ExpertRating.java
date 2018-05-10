package com.promeets.android.object;

public class ExpertRating {
    private String id;

    private String numberOfMeeting;

    private String expertId;

    private String wantToMeeting;

    private String expertAvgRating;

    public String getNumberOfMeeting ()
    {
        return numberOfMeeting;
    }

    public void setNumberOfMeeting (String numberOfMeeting)
    {
        this.numberOfMeeting = numberOfMeeting;
    }

    public String getExpertId ()
    {
        return expertId;
    }

    public void setExpertId (String expertId)
    {
        this.expertId = expertId;
    }

    public String getWantToMeeting ()
    {
        return wantToMeeting;
    }

    public void setWantToMeeting (String wantToMeeting)
    {
        this.wantToMeeting = wantToMeeting;
    }

    public String getExpertAvgRating ()
    {
        return expertAvgRating;
    }

    public void setExpertAvgRating (String expertAvgRating)
    {
        this.expertAvgRating = expertAvgRating;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", numberOfMeeting = "+numberOfMeeting+", expertId = "+expertId+", wantToMeeting = "+wantToMeeting+", expertAvgRating = "+expertAvgRating+"]";
    }
}
