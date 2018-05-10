package com.promeets.android.object;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventAction {
    @SerializedName("actionStatus")
    @Expose
    public String actionStatus;

    @SerializedName("actionCommandCode")
    @Expose
    public String actionCommandCode;

    @SerializedName("actionDescription")
    @Expose
    public String actionDescription;

    @SerializedName("readableStatus")
    @Expose
    public String readableStatus;

    @SerializedName("displayTitle")
    @Expose
    public String displayTitle;

    @SerializedName("displayStep")
    @Expose
    public int displayStep;

    @SerializedName("displayButton")
    @Expose
    public DisplayButton displayButton;

    public EventAction() {

    }

    /**
     *
     * @param id
     * @param code
     * @param description
     */


}
