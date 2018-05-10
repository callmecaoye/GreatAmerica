package com.promeets.android.object;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by sosasang on 2/8/17.
 */
public class ExpertAlbumPOJO {
    @SerializedName("photoUrl")
    @Expose
    public String photoUrl;

    @SerializedName("smallphotoUrl")
    @Expose
    public String smallphotoUrl;

    @SerializedName("id")
    @Expose
    public Integer id = -1; // -1: expert not submitted photo for album

    @SerializedName("bitmap")
    @Expose
    public Bitmap bitmap; // expert not submitted photo for album

    @SerializedName("bitmapFile")
    @Expose
    public File bitmapFile; // expert not submitted photo for album
}
