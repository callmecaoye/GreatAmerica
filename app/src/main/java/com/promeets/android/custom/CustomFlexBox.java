package com.promeets.android.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.flexbox.FlexboxLayout;
import com.promeets.android.util.ScreenUtil;

/**
 * Created by sosasang on 7/13/17.
 */

public class CustomFlexBox extends FlexboxLayout {
    private int maxHeight;

    public CustomFlexBox(Context context) {
        super(context);
        maxHeight = ScreenUtil.convertDpToPx(70, context);
    }

    public CustomFlexBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        maxHeight = ScreenUtil.convertDpToPx(70, context);
    }

    public CustomFlexBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maxHeight = ScreenUtil.convertDpToPx(70, context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight > 0){
            int hSize = MeasureSpec.getSize(heightMeasureSpec);
            int hMode = MeasureSpec.getMode(heightMeasureSpec);

            switch (hMode){
                case MeasureSpec.AT_MOST:
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(hSize, maxHeight), MeasureSpec.AT_MOST);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
                    break;
                case MeasureSpec.EXACTLY:
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(hSize, maxHeight), MeasureSpec.EXACTLY);
                    break;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
