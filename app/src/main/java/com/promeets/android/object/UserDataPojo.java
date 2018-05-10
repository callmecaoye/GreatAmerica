package com.promeets.android.object;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Shashank Shekhar on 17-02-2017.
 */

public class UserDataPojo {

    @SerializedName("cityName")
    @Expose
    public String cityName;

    @SerializedName("company")
    @Expose
    public String company;

    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("isExpert")
    @Expose
    public int isExpert;

    @SerializedName("photoUrl")
    @Expose
    public String photoUrl;

    @SerializedName("smallPhotoUrl")
    @Expose
    public String smallPhotoUrl;

    @SerializedName("position")
    @Expose
    public String position;

    @Override
    public String toString() {
        Log.d("User Data",isExpert + " IsEXPERT");
        Log.d("User Data",fullName + " fullName");
        return super.toString();
    }
}
