package com.promeets.android.pojo;

import com.promeets.android.object.CustomList;
import com.promeets.android.object.ExpertCardPOJO;

import java.util.ArrayList;

/**
 * Created by Shashank Shekhar on 30-01-2017.
 */

public class HomeResp {
    StatusResponse info;
    CustomList[] list;
    ArrayList<ExpertCardPOJO> dataList;

    public ArrayList<ExpertCardPOJO> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<ExpertCardPOJO> dataList) {
        this.dataList = dataList;
    }

    public CustomList[] getList() {
        return list;
    }

    public StatusResponse getInfo() {
        return info;
    }

    public void setInfo(StatusResponse info) {
        this.info = info;
    }

    public void setList(CustomList[] list) {
        this.list = list;
    }
}
