package com.promeets.android.listeners;

/**
 * Created by sosasang on 6/27/17.
 */

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * 图片选择的回调接口
 */
public interface OnPictureSelectedListener {
    /**
     * 图片选择的监听回调
     *
     * @param fileUri
     * @param bitmap
     */
    void onPictureSelected(Uri fileUri, Bitmap bitmap);
}
