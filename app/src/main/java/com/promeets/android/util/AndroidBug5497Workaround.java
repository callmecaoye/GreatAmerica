package com.promeets.android.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

/**
 * Created by sosasang on 8/28/17.
 */

public class AndroidBug5497Workaround {

    Activity activity;

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    public static void assistActivity (Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity) {
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Rect frame = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                usableHeightSansKeyboard -= statusBarHeight;
            }





            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard - getBottomStatusHeight(activity);
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);

        //这个判断是为了解决19之后的版本在弹出软键盘时，键盘和推上去的布局（adjustResize）之间有黑色区域的问题
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            return (r.bottom - r.top)+statusBarHeight;
        }
        return (r.bottom - r.top);
    }


    public static int getDpi(Context context){

        int dpi =0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();

        @SuppressWarnings("rawtypes")

        Class c;

        try{

            c = Class.forName("android.view.Display");

            @SuppressWarnings("unchecked")

            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);

            method.invoke(display, displayMetrics);

            dpi=displayMetrics.heightPixels;

        }catch(Exception e){

            e.printStackTrace();

        }

        return dpi;
    }

/**

 * 获取 虚拟按键的高度

 *@paramcontext

 *@return

 */

    public static int getBottomStatusHeight(Context context){

        int totalHeight =getDpi(context);

        int contentHeight =getScreenHeight(context);

        return totalHeight - contentHeight;

    }

    /**

     * 获得屏幕高度

     *

     *@paramcontext

     *@return

     */

    public static int getScreenHeight(Context context)

    {

        WindowManager wm = (WindowManager) context

                .getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics outMetrics =new DisplayMetrics();

        wm.getDefaultDisplay().getMetrics(outMetrics);

        return outMetrics.heightPixels;

    }
}
