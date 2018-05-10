package com.promeets.android.object;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class DisplayButton {
    @SerializedName("EXP_DECLINE")
    @Expose
    public boolean EXP_DECLINE;

    @SerializedName("EXP_CANCEL")
    @Expose
    public boolean EXP_CANCEL;

    @SerializedName("USER_CANCEL")
    @Expose
    public boolean USER_CANCEL;

    @SerializedName("USER_CONTACT")
    @Expose
    public boolean USER_CONTACT;

    @SerializedName("EXP_ACCEPT")
    @Expose
    public boolean EXP_ACCEPT;

    @SerializedName("EXP_UPDATE")
    @Expose
    public boolean EXP_UPDATE;

    @SerializedName("USER_PAYMENT")
    @Expose
    public boolean USER_PAYMENT;

    @SerializedName("USER_REVIEW")
    @Expose
    public boolean USER_REVIEW;

}
