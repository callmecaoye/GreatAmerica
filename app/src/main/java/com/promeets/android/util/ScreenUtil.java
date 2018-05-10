package com.promeets.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.promeets.android.MyApplication;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ScreenUtil {
    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPxToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPx(int dp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    /**
     * This method converts Unit time seconds to local time in certain format
     *
     * @param form A value to set format of desired date
     * @param unitSec A value in Unit time seconds, which we need to convert into local time
     * @return
     */
    public static String convertUnitTime(String form, long unitSec) {
        long time = Long.valueOf(unitSec) * 1000L;
        SimpleDateFormat formatter = new SimpleDateFormat(form);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(time);
    }

    /**
     * get screen width and height
     *
     * @param mContext
     * @return
     */
    private static int[] getScreenSize(Context mContext) {
        DisplayMetrics dm = mContext
                .getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        return new int[]{screenWidth, screenHeight};
    }

    /**
     * * Get display width (px)
     *
     * @return width
     */

    public static int getWidth() {
        //DisplayMetrics metrics = new DisplayMetrics();
        //activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        DisplayMetrics metrics = MyApplication.getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        return width;
    }

    /**
     * * Get display height (px)
     *
     * @return height
     */
    public static int getHeight() {
        DisplayMetrics metrics = MyApplication.getContext().getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        return height;
    }
}
