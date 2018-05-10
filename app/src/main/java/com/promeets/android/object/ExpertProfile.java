package com.promeets.android.object;

import com.promeets.android.pojo.StatusResponse;

import java.util.ArrayList;

public class ExpertProfile {
    String id;
    String expId;
    String industry;
    String industryExperience;
    String industryCategory;
    String contactNumber;
    String contactEmail;
    String activeCity;
    String activeArea;
    String education;
    String degree;
    String photoUrl;
    String description;
    String releasePhoneNumber;
    String releaseEmail;
    String position;
    String smallphotoUrl;
    String institution;
    String fullName;
    String referral;
    String referralPhotoUrl;
    String[] photoList;
    String defaultDate;
    String wantToMeeting;
    String hadLiked;
    String originalPrice;
    StatusResponse info;
    String shareTitle;
    String shareHtml;
    String shareTxt;
    String[] tags;
    String linkedinLink;
    public String companyLogo;
    public float avgRating;
    public int numberOfMeeting;

    public String linkedinVerified;
    public String facebookVerified;

    public String videoUrl;

    public String getLinkedinLink() {
        return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
        this.linkedinLink = linkedinLink;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareHtml() {
        return shareHtml;
    }

    public void setShareHtml(String shareHtml) {
        this.shareHtml = shareHtml;
    }

    public String getShareTxt() {
        return shareTxt;
    }

    public void setShareTxt(String shareTxt) {
        this.shareTxt = shareTxt;
    }

    public Integer getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(Integer availabilityType) {
        this.availabilityType = availabilityType;
    }

    Integer availabilityType;
    private ArrayList<ExpertService> serviceList;

    public String getWantToMeeting() {
        return wantToMeeting;
    }

    public void setWantToMeeting(String wantToMeeting) {
        this.wantToMeeting = wantToMeeting;
    }

    public String getHadLiked() {
        return hadLiked;
    }

    public void setHadLiked(String hadLiked) {
        this.hadLiked = hadLiked;
    }

    private ArrayList<EventLocationPOJO> expertDefaultLocationList;

    public String getActiveArea() {
        return activeArea;
    }

    public String getActiveCity() {
        return activeCity;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEducation() {
        return education;
    }

    public String getExpId() {
        return expId;
    }

    public String getIndustry() {
        return industry;
    }

    public String getDegree() {
        return degree;
    }

    public String getIndustryCategory() {
        return industryCategory;
    }

    public String getIndustryExperience() {
        return industryExperience;
    }

    public String getDescription() {
        return description;
    }

    public String getFullName() {
        return fullName;
    }

    public String getInstitution() {
        return institution;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPosition() {
        return position;
    }

    public String getReferral() {
        return referral;
    }

    public String getReferralPhotoUrl() {
        return referralPhotoUrl;
    }

    public String getReleaseEmail() {
        return releaseEmail;
    }

    public String getReleasePhoneNumber() {
        return releasePhoneNumber;
    }

    public String getSmallphotoUrl() {
        return smallphotoUrl;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setActiveCity(String activeCity) {
        this.activeCity = activeCity;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public void setActiveArea(String activeArea) {
        this.activeArea = activeArea;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setIndustryCategory(String industryCategory) {
        this.industryCategory = industryCategory;
    }

    public void setIndustryExperience(String industryExperience) {
        this.industryExperience = industryExperience;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public void setReferralPhotoUrl(String referralPhotoUrl) {
        this.referralPhotoUrl = referralPhotoUrl;
    }

    public void setReleaseEmail(String releaseEmail) {
        this.releaseEmail = releaseEmail;
    }

    public void setReleasePhoneNumber(String releasePhoneNumber) {
        this.releasePhoneNumber = releasePhoneNumber;
    }

    public void setSmallphotoUrl(String smallphotoUrl) {
        this.smallphotoUrl = smallphotoUrl;
    }

    public String[] getPhotoList() {
        return photoList;
    }

    public void setPhotoList(String[] photoList) {
        this.photoList = photoList;
    }

    public String getDefaultDate() {
        return defaultDate;
    }

    public void setDefaultDate(String defaultDate) {
        this.defaultDate = defaultDate;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public ArrayList<EventLocationPOJO> getExpertDefaultLocationList() {
        return expertDefaultLocationList;
    }

    public void setExpertDefaultLocationList(ArrayList<EventLocationPOJO> expertDefaultLocationList) {
        this.expertDefaultLocationList = expertDefaultLocationList;
    }

    public ArrayList<ExpertService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<ExpertService> serviceList) {
        this.serviceList = serviceList;
    }

    public StatusResponse getInfo() {
        return info;
    }

    public void setInfo(StatusResponse info) {
        this.info = info;
    }
}
