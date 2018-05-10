package com.promeets.android.custom;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.PollingActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import com.promeets.android.object.Category;
import com.promeets.android.object.SubCate;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.promeets.android.R;

import com.promeets.android.util.ScreenUtil;

import java.util.ArrayList;


/**
 * Created by sosasang on 8/16/17.
 */

public class PollingView extends LinearLayout {

    private BaseActivity mBaseActivity;
    public Category mCate;
    private ArrayList<SubCate> subCates;
    private Typeface tf_semi;
    //private ArrayList<Integer> mSelSubcate = new ArrayList<>();

    public PollingView(Context context, Category mCate) {
        super(context);
        this.mBaseActivity = (BaseActivity) context;
        this.mCate = mCate;
        this.subCates = mCate.getList();
        tf_semi = Typeface.createFromAsset(mBaseActivity.getAssets(), "fonts/OpenSans-SemiBold.ttf");
        inflateLayout();
    }

    private void inflateLayout() {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.polling_view, null);
        LayoutParams mainParam = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(mainParam);

        LinearLayout mLayCate = (LinearLayout) view.findViewById(R.id.cate_lay);
        TextView mTxtCate = (TextView) view.findViewById(R.id.category);
        final ImageView mImgIcon = (ImageView) view.findViewById(R.id.icon);
        final FlexboxLayout mFlexBox = (FlexboxLayout) view.findViewById(R.id.flexBox);

        mTxtCate.setText(mCate.getTitle());
        mLayCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCate.setExpanding(!mCate.isExpanding());
                if (mCate.isExpanding()) {
                    mImgIcon.setImageDrawable(mBaseActivity.getResources().getDrawable(R.drawable.ic_collapse));
                    mFlexBox.setVisibility(View.VISIBLE);
                } else {
                    mImgIcon.setImageDrawable(mBaseActivity.getResources().getDrawable(R.drawable.ic_expand));
                    mFlexBox.setVisibility(View.GONE);
                }
            }
        });

        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.convertDpToPx(4, mBaseActivity),
                ScreenUtil.convertDpToPx(5, mBaseActivity),
                ScreenUtil.convertDpToPx(4, mBaseActivity),
                ScreenUtil.convertDpToPx(5, mBaseActivity));
        for (final SubCate subCate : subCates) {
            if (subCate.getTitle().equalsIgnoreCase("all")
                    || subCate.getTitle().equalsIgnoreCase("other"))
                continue;
            final TextView mTxtTag = new TextView(mBaseActivity);
            mTxtTag.setLayoutParams(lp);
            mTxtTag.setText(subCate.getTitle());
            mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            mTxtTag.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_gray));
            mTxtTag.setPadding(ScreenUtil.convertDpToPx(15, mBaseActivity),
                    ScreenUtil.convertDpToPx(5, mBaseActivity),
                    ScreenUtil.convertDpToPx(15, mBaseActivity),
                    ScreenUtil.convertDpToPx(5, mBaseActivity));
            if (subCate.isSelect()) {
                mTxtTag.setTextColor(Color.WHITE);
                mTxtTag.setBackgroundResource(R.drawable.tag_solid_primary);
            } else {
                mTxtTag.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_gray));
                mTxtTag.setBackgroundResource(R.drawable.tag_border_gray);
            }
            mTxtTag.setTypeface(tf_semi);
            mFlexBox.addView(mTxtTag);
            mTxtTag.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    subCate.setSelect(!subCate.isSelect());
                    if (subCate.isSelect()) {
                        mTxtTag.setTextColor(Color.WHITE);
                        mTxtTag.setBackgroundResource(R.drawable.tag_solid_primary);
                    } else {
                        mTxtTag.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_gray));
                        mTxtTag.setBackgroundResource(R.drawable.tag_border_gray);
                    }
                    ((PollingActivity)mBaseActivity).updateSave();
                }
            });
        }
        this.addView(view);
    }

    public ArrayList<Integer> getSelIdList() {
        ArrayList<Integer> result = new ArrayList<>();
        for (SubCate subCate : subCates) {
            if (subCate.isSelect())
                result.add(subCate.getId());
        }
        return result;
    }

    public ArrayList<SubCate> getSelList() {
        ArrayList<SubCate> result = new ArrayList<>();
        for (SubCate subCate : subCates) {
            if (subCate.isSelect())
                result.add(subCate);
        }
        return result;
    }
}
