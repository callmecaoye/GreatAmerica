package com.promeets.android.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.promeets.android.object.ChatUserInfo;
import com.promeets.android.object.Info;
import com.promeets.android.object.SocketInfo;

public class ChatAccountResp {

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("info")
    @Expose
    public Info info;

    @SerializedName("user")
    @Expose
    public ChatUserInfo chatUser;

    public SocketInfo socketInfo;
}
