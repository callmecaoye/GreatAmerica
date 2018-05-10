package com.promeets.android.custom;

import com.promeets.android.activity.CategorySelectActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.adapter.SubcateAdapter;
import com.bumptech.glide.Glide;
import com.promeets.android.object.SubCate;
import com.promeets.android.object.Category;
import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by sosasang on 7/24/17.
 */

public class CategoryView extends LinearLayout {

    private BaseActivity mBaseActivity;
    private Category mCate;
    private ImageView mImg;
    private TextView mTxtTitle;
    private NoScrollGridView mGridView;
    private SubcateAdapter subAdapter;

    public CategoryView(Context context, Category mCate) {
        super(context);
        this.mBaseActivity = (BaseActivity) context;
        this.mCate = mCate;
        inflateLayout();
    }

    private void inflateLayout() {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.category_view, null);
        LayoutParams mainParam = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(mainParam);

        // header
        mImg = (ImageView) view.findViewById(R.id.cate_image);
        mTxtTitle = (TextView) view.findViewById(R.id.cate_title);
        Glide.with(mBaseActivity).load(mCate.getIconUrl()).into(mImg);
        mTxtTitle.setText(mCate.getTitle());

        // subcate
        mGridView = (NoScrollGridView) view.findViewById(R.id.grid_view);
        if (mBaseActivity instanceof CategorySelectActivity) {
            ArrayList<SubCate> list = new ArrayList<>();
            for (SubCate subCate : mCate.getList()) {
                if (!subCate.getTitle().equalsIgnoreCase("all"))
                    list.add(subCate);
            }
            subAdapter = new SubcateAdapter(mBaseActivity, list);
        } else
            subAdapter = new SubcateAdapter(mBaseActivity, mCate.getList());
        mGridView.setAdapter(subAdapter);

        this.addView(view);
    }

    public ArrayList<SubCate> getSelList() {return subAdapter.getSelList();}
}
