package com.promeets.android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.listeners.OnTabSelectedListner;
import com.promeets.android.R;


/**
 * Created by Shashank Shekhar on 06-02-2017.
 */

public class PromeetsBottomBar extends LinearLayout implements View.OnClickListener {
    private LinearLayout mTab1;
    private LinearLayout mTab2;
    private LinearLayout mTab3;
    private LinearLayout mTab4;
    private LinearLayout mTab5;
    private LinearLayout mTab6;
    private ImageButton mIcon1;
    private ImageButton mIcon2;
    private ImageButton mIcon3;
    private ImageButton mIcon4;
    private ImageButton mIcon5;
    private ImageButton mIcon6;
    private TextView mName1;
    private TextView mName2;
    private TextView mName3;
    private TextView mName4;
    private TextView mName5;
    private TextView mName6;
    private TextView mTxtNum;
    /***
     * */
    private OnTabSelectedListner mListner;

    /**
     * */
    public PromeetsBottomBar(Context context) {
        super(context);
        inflateLayout();
    }

    /**
     * **/
    public PromeetsBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout();
    }

    /***
     * **/
    public PromeetsBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateLayout();
    }

    /***
     *
     * */
    private void inflateLayout() {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.custom_bottom_bar, null);

        mTab1 = (LinearLayout) view.findViewById(R.id.tab1);
        mTab2 = (LinearLayout) view.findViewById(R.id.tab2);
        mTab3 = (LinearLayout) view.findViewById(R.id.tab3);
        mTab4 = (LinearLayout) view.findViewById(R.id.tab4);
        mTab5 = (LinearLayout) view.findViewById(R.id.tab5);
        mTab6 = (LinearLayout) view.findViewById(R.id.tab6);
        mIcon1 = (ImageButton) view.findViewById(R.id.icon1);
        mIcon2 = (ImageButton) view.findViewById(R.id.icon2);
        mIcon3 = (ImageButton) view.findViewById(R.id.icon3);
        mIcon4 = (ImageButton) view.findViewById(R.id.icon4);
        mIcon5 = (ImageButton) view.findViewById(R.id.icon5);
        mIcon6 = (ImageButton) view.findViewById(R.id.icon6);
        mName1 = (TextView) view.findViewById(R.id.name1);
        mName2 = (TextView) view.findViewById(R.id.name2);
        mName3 = (TextView) view.findViewById(R.id.name3);
        mName4 = (TextView) view.findViewById(R.id.name4);
        mName5 = (TextView) view.findViewById(R.id.name5);
        mName6 = (TextView) view.findViewById(R.id.name6);
        mTxtNum = (TextView) view.findViewById(R.id.msg_num);

        mTab1.setOnClickListener(this);
        mTab2.setOnClickListener(this);
        mTab3.setOnClickListener(this);
        mTab4.setOnClickListener(this);
        mTab5.setOnClickListener(this);
        mTab6.setOnClickListener(this);

        this.addView(view);

        LayoutParams mainParam = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        view.setLayoutParams(mainParam);
        LayoutParams param = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1.0f);
        mTab1.setLayoutParams(param);
        mTab2.setLayoutParams(param);
        mTab3.setLayoutParams(param);
        mTab4.setLayoutParams(param);
        mTab5.setLayoutParams(param);
        mTab6.setLayoutParams(param);
    }


    @Override
    public void onClick(View view) {
        reset();
        switch (view.getId()) {
            case R.id.tab1:
                mIcon1.setImageResource(R.drawable.ic_home_sel);
                mName1.setTextColor(getResources().getColor(R.color.primary));
                if (mListner != null)
                    mListner.onTabSelected(1);
                break;
            case R.id.tab2:
                mIcon2.setImageResource(R.drawable.ic_search_sel);
                mName2.setTextColor(getResources().getColor(R.color.primary));
                if (mListner != null)
                    mListner.onTabSelected(2);
                break;
            case R.id.tab3:
                mIcon3.setImageResource(R.drawable.ic_msg_sel);
                mName3.setTextColor(getResources().getColor(R.color.primary));
                if (mListner != null)
                    mListner.onTabSelected(3);
                break;
            case R.id.tab4:
                mIcon4.setImageResource(R.drawable.ic_acc_sel);
                mName4.setTextColor(getResources().getColor(R.color.primary));
                if (mListner != null)
                    mListner.onTabSelected(4);
                break;
            case R.id.tab5:
                mIcon5.setImageResource(R.drawable.ic_app_sel);
                mName5.setTextColor(getResources().getColor(R.color.primary));
                if (mListner != null)
                    mListner.onTabSelected(5);
                break;
            case R.id.tab6:
                mIcon6.setImageResource(R.drawable.ic_event_sel);
                mName6.setTextColor(getResources().getColor(R.color.primary));
                if (mListner != null)
                    mListner.onTabSelected(6);
        }
    }

    public void setOnTabSelectedListner(OnTabSelectedListner aListner, int currentIndex) {
        this.mListner = aListner;
        switch (currentIndex) {
            case 1:
                onClick(mTab1);
                break;
            case 2:
                onClick(mTab2);
                break;
            case 3:
                onClick(mTab3);
                break;
            case 4:
                onClick(mTab4);
                break;
            case 5:
                onClick(mTab5);
                break;
            case 6:
                onClick(mTab6);
                break;
        }
    }

    private void reset() {
        mIcon1.setImageResource(R.drawable.ic_home_default);
        mIcon2.setImageResource(R.drawable.ic_search_default);
        mIcon3.setImageResource(R.drawable.ic_msg_default);
        mIcon4.setImageResource(R.drawable.ic_acc_default);
        mIcon5.setImageResource(R.drawable.ic_app_default);
        mIcon6.setImageResource(R.drawable.ic_event_default);
        mName1.setTextColor(getResources().getColor(R.color.em_black_54));
        mName2.setTextColor(getResources().getColor(R.color.em_black_54));
        mName3.setTextColor(getResources().getColor(R.color.em_black_54));
        mName4.setTextColor(getResources().getColor(R.color.em_black_54));
        mName5.setTextColor(getResources().getColor(R.color.em_black_54));
        mName6.setTextColor(getResources().getColor(R.color.em_black_54));
    }

    public void setCurrentTab(int index) {
        if (index == 1)
            onClick(mTab1);
        if (index == 2)
            onClick(mTab2);
        if (index == 3)
            onClick(mTab3);
        if (index == 4)
            onClick(mTab4);
        if (index == 5)
            onClick(mTab5);
        if (index == 6)
            onClick(mTab6);
    }

    public void setUnreadNumber(int number) {
        if (number > 99) {
            mTxtNum.setVisibility(VISIBLE);
            mTxtNum.setText("...");
        } else if (number > 0) {
            mTxtNum.setVisibility(VISIBLE);
            mTxtNum.setText(number + "");
        } else {
            mTxtNum.setVisibility(View.GONE);
        }
    }
}
