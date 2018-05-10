package com.promeets.android.object;

import java.io.Serializable;

public class ServiceReview implements Serializable{

    public Integer id;

    public Integer userId;

    public Integer expertId;

    public Integer eventRequestId;

    public Integer serviceId;

    public float rating;

    public String reviewDate;

    public String description;

    public String photoURL;

    public String expSmallphotoUrl;

    public String expName;

    public String firstName;

    public String lastName;

    public String fullName;

    public String title;

    public String replyContent;

    public String replyTime;

    public boolean effectiveness;
    public boolean expertise;
    public boolean organization;
    public boolean ontime;

    public int isUseful;

    public String position;

    public int usefulCount;

    public String reviewType;
}
