package com.promeets.android.pojo;

import com.promeets.android.object.ExpertProfile;
import com.promeets.android.object.ExpertService;
import com.promeets.android.object.ServiceReview;

import java.util.ArrayList;

public class ServiceDetailResp {
    private ArrayList<ExpertService> recommendList;

    private ArrayList<ExpertService> serviceList;

    private ArrayList<ServiceReview> serviceReviewList;
    private ArrayList<ServiceReview> dataList;

    private int serviceReviewListCount;

    private StatusResponse info;

    private ExpertProfile expertProfile;



    public ExpertProfile getExpertProfile() {
        return expertProfile;
    }

    public StatusResponse getInfo() {
        return info;
    }

    public ArrayList<ExpertService> getRecommendList() {
        return recommendList;
    }

    public ArrayList<ExpertService> getServiceList() {
        return serviceList;
    }

    public int getServiceReviewListCount() {
        return serviceReviewListCount;
    }

    public ArrayList<ServiceReview> getServiceReviewList() {
        return serviceReviewList;
    }

    public void setExpertProfile(ExpertProfile expertProfile) {
        this.expertProfile = expertProfile;
    }

    public void setInfo(StatusResponse info) {
        this.info = info;
    }

    public void setRecommendList(ArrayList<ExpertService> recommendList) {
        this.recommendList = recommendList;
    }

    public void setServiceList(ArrayList<ExpertService> serviceList) {
        this.serviceList = serviceList;
    }

    public void setServiceReviewList(ArrayList<ServiceReview> serviceReviewList) {
        this.serviceReviewList = serviceReviewList;
    }

    public void setServiceReviewListCount(int serviceReviewListCount) {
        this.serviceReviewListCount = serviceReviewListCount;
    }

    public void setDataList(ArrayList<ServiceReview> dataList) {
        this.dataList = dataList;
    }

    public ArrayList<ServiceReview> getDataList() {
        return dataList;
    }
}
