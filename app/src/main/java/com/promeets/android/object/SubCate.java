package com.promeets.android.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shashank Shekhar on 31-01-2017.
 */
public class SubCate implements Parcelable {
    private Integer id;

    private String title;

    private String categoryTitle;

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    private Integer categoryId;

    private boolean isSelect = false;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", title = "+title+"]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeValue(this.categoryId);
        dest.writeString(this.categoryTitle);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
    }

    public SubCate() {
    }

    protected SubCate(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.categoryId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.categoryTitle = in.readString();
        this.isSelect = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SubCate> CREATOR = new Parcelable.Creator<SubCate>() {
        @Override
        public SubCate createFromParcel(Parcel source) {
            return new SubCate(source);
        }

        @Override
        public SubCate[] newArray(int size) {
            return new SubCate[size];
        }
    };
}
