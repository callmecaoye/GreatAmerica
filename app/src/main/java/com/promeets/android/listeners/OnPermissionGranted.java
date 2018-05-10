package com.promeets.android.listeners;

/**
 * Created by Shashank Shekhar on 4/3/2017.
 */

public interface OnPermissionGranted {
    public int CAMERA = 1;
    public int READ_EXTERNAL_STORAGE = 2;
    public int WRITE_EXTERNAL_STORAGE = 3;
    public int RECORD_AUDIO = 4;
    public int ACCESS_FINE_LOCATION = 5;
    public int READ_PHONE_STATE=6;
    public int ACCESS_COARSE_LOCATION=7;
    public int READ_CALENDAR=8;
    public int WRITE_CALENDAR=9;
    public int GROUP_PERMISSION=10;
    public int CAMERA_GROUP = 11;
    public int GALLERY_GROUP = 12;

    public void onSuccessfulPermission(int permissionType);
    public void onFailedPermission(int permissionType);
}
