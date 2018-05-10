package com.promeets.android.object;

/**
 * Created by Shashank Shekhar on 31-01-2017.
 */

public class CustomList {
    ExpertService expertService;
    ExpertProfile expertProfile;
    ExpertCardPOJO expertCard;

    public ExpertCardPOJO getExpertCard() {
        return expertCard;
    }

    public void setExpertCard(ExpertCardPOJO expertCard) {
        this.expertCard = expertCard;
    }

    public ExpertService getExpertService() {
        return expertService;
    }

    public ExpertProfile getExpertProfile() {
        return expertProfile;
    }

    public void setExpertService(ExpertService expertService) {
        this.expertService = expertService;
    }

    public void setExpertProfile(ExpertProfile expertProfile) {
        this.expertProfile = expertProfile;
    }
}
