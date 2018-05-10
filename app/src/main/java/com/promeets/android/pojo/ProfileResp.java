package com.promeets.android.pojo;

import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.UserProfilePOJO;

/**
 * Created by Shashank Shekhar on 10-02-2017.
 */

public class ProfileResp {
    private StatusResponse info;
    private UserProfilePOJO userProfile;
    private ExpertProfilePOJO expertProfile;

    public StatusResponse getInfo ()
    {
        return info;
    }

    public void setInfo (StatusResponse info)
    {
        this.info = info;
    }

    public ExpertProfilePOJO getExpertProfile ()
    {
        return expertProfile;
    }

    public void setExpertProfile (ExpertProfilePOJO expertProfile)
    {
        this.expertProfile = expertProfile;
    }

    public UserProfilePOJO getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfilePOJO userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [info = "+info+", expertProfile = "+expertProfile+"]";
    }
}
