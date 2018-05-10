package com.promeets.android.pojo;

/**
 * Created by sosasang on 2/6/17.
 */

public class ExpertLikeResp {

    private String wantToMeeting;

    private String expId;

    private StatusResponse info;

    public String getWantToMeeting() {
        return wantToMeeting;
    }

    public void setWantToMeeting(String wantToMeeting) {
        this.wantToMeeting = wantToMeeting;
    }

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public StatusResponse getInfo() {
        return info;
    }

    public void setInfo(StatusResponse info) {
        this.info = info;
    }
}
