package com.promeets.android.pojo;


import com.promeets.android.object.CreditCard;
import com.promeets.android.object.Info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sosasang on 1/18/17.
 */

public class CreditCardResp {
    @SerializedName("cardList")
    @Expose
    public List<CreditCard> cardList = null;

    @SerializedName("info")
    @Expose
    public Info info;
}
