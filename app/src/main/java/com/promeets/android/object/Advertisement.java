package com.promeets.android.object;

/**
 * Created by Shashank Shekhar on 20-02-2017.
 */

public class Advertisement {
    private String id;

    private String photoUrl;

    private String popupUrl;

    private String linkUrl;

    private String url;

    private String autoPopupUrl;

    public String getAutoPopupUrl() {
        return autoPopupUrl;
    }

    public void setAutoPopupUrl(String autoPopupUrl) {
        this.autoPopupUrl = autoPopupUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getPhotoUrl ()
    {
        return photoUrl;
    }

    public void setPhotoUrl (String photoUrl)
    {
        this.photoUrl = photoUrl;
    }

    public String getPopupUrl() {
        return popupUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", photoUrl = "+photoUrl+"]";
    }
}
