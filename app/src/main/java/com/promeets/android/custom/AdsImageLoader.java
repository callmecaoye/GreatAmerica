package com.promeets.android.custom;

import android.content.Context;
import com.promeets.android.object.Advertisement;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by sosasang on 5/1/17.
 */

public class AdsImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object obj, ImageView imageView) {
        Advertisement ad = null;
        try {
            ad = (Advertisement) obj;
        } catch (Exception e) {

        }

        if (ad != null && !StringUtils.isEmpty(ad.getPhotoUrl()))
            Glide.with(context).load(ad.getPhotoUrl()).into(imageView);
        else
            Glide.with(context).load(obj).into(imageView);
    }
}
