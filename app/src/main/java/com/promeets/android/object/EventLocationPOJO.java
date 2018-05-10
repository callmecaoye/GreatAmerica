package com.promeets.android.object;

/**
 * Created by xiaoyudong on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")

public class EventLocationPOJO implements Serializable{

    public boolean isDefaultLocation = false;

    @SerializedName("id")
    @Expose
    public long id;

    @SerializedName("status")
    @Expose
    public int status = 1;

    @SerializedName("latitude")
    @Expose
    public String latitude;

    @SerializedName("location")
    @Expose
    public String location;

    @SerializedName("longitude")
    @Expose
    public String longitude;

    @SerializedName("serviceId")
    @Expose
    public String serviceId;

    @SerializedName("zipcode")
    @Expose
    public String zipcode;

    @SerializedName("area")
    @Expose
    public String area;

    @SerializedName("eventRequestId")
    @Expose
    public int eventRequestId;

    @SerializedName("ifExpCanCome")
    @Expose
    public int ifExpCanCome;

    @SerializedName("ifUserCanCome")
    @Expose
    public int ifUserCanCome;

    @SerializedName("rankOrder")
    @Expose
    public int rankOrder;

    @SerializedName("expId")
    @Expose
    public int expId;

    @SerializedName("city")
    @Expose
    public String city;

    @SerializedName("state")
    @Expose
    public String state;

    public boolean isSelected = false;

    public EventLocationPOJO() {

    }

    /**
     *
     * @param id
     * @param code
     * @param description
     */

}
