package com.promeets.android.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sosasang on 8/17/17.
 */

public class Category implements Parcelable {
    private Integer id;

    private String title;

    private String iconUrl;

    private String bgUrl;

    public ArrayList<SubCate> list;

    private boolean isExpanding = false;

    public boolean isExpanding() {
        return isExpanding;
    }

    public void setExpanding(boolean expanding) {
        isExpanding = expanding;
    }

    public int getId ()
    {
        return id;
    }

    public void setId (int id)
    {
        this.id = id;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getIconUrl ()
    {
        return iconUrl;
    }

    public void setIconUrl (String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public ArrayList<SubCate> getList ()
    {
        return list;
    }

    public void setList (ArrayList<SubCate> list)
    {
        this.list = list;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", title = "+title+", iconUrl = "+iconUrl+", list = "+list+"]";
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeString(this.iconUrl);
        dest.writeString(this.bgUrl);
        dest.writeTypedList(this.list);
        dest.writeByte(this.isExpanding ? (byte) 1 : (byte) 0);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.iconUrl = in.readString();
        this.bgUrl = in.readString();
        this.list = in.createTypedArrayList(SubCate.CREATOR);
        this.isExpanding = in.readByte() != 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
