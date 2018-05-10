package com.promeets.android.pojo;

import com.promeets.android.object.VersionData;

/**
 * Created by Shashank Shekhar on 20-02-2017.
 */

public class VersionResp {
    StatusResponse info;
    VersionData data;

    public VersionData getData() {
        return data;
    }

    public StatusResponse getInfo() {
        return info;
    }
}
