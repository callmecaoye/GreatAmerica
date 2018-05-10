package com.promeets.android.pojo;

/**
 * Created by Shashank Shekhar on 08-02-2017.
 */

public class ServiceListPost {
    private String accessToken;
    private String accessUserId;
    private String chatTargetId;
    private String timeZone;
    private String userId;
    private String viewId;

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessUserId() {
        return accessUserId;
    }

    public String getChatTargetId() {
        return chatTargetId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getViewId() {
        return viewId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessUserId(String accessUserId) {
        this.accessUserId = accessUserId;
    }

    public void setChatTargetId(String chatTargetId) {
        this.chatTargetId = chatTargetId;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
}
