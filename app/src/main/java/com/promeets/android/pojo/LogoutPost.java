package com.promeets.android.pojo;

/**
 * Created by Shashank Shekhar on 10-02-2017.
 */

public class LogoutPost {
    private String accessToken;
    private String userId;

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
