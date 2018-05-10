package com.promeets.android.pojo;

import com.promeets.android.object.ServiceReview;
import com.promeets.android.object.Info;
import com.promeets.android.object.ServiceEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sosasang on 6/9/17.
 */

public class ActiveEventResp {
    public Info info;
    public List<ServiceEvent> dataList;
    public ServiceEvent eventData;
    public int reviewListSize = 0;
    public ArrayList<ServiceReview> reviewList;
    public String firstName;
    public String lastName;
    public int isReviewed = 0;// 0: not reviewed yet; 1: reviewed
    public int goingFlag;
    public String question1;
}
