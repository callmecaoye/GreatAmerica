package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.CategorySelectActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.promeets.android.object.Category;
import com.promeets.android.object.SubCate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.promeets.android.activity.CategoryListActivity;
import com.promeets.android.activity.CategorySearchActivity;

import com.google.gson.Gson;
import com.promeets.android.R;

import com.promeets.android.util.ScreenUtil;

import java.util.ArrayList;

/**
 * Created by sosasang on 2/20/18.
 */

public class SubcateAdapter extends BaseAdapter {

    private ArrayList<SubCate> subCates;
    private BaseActivity mBaseActivity;
    private int dp5;
    private int dp90;
    FrameLayout.LayoutParams lp;
    Gson gson = new Gson();
    private LayoutInflater mInflater;

    private ArrayList<Integer> selSubcateList;

    public SubcateAdapter(Context context, ArrayList<SubCate> items) {
        this.subCates = items;
        this.mBaseActivity = (BaseActivity) context;
        dp5 = ScreenUtil.convertDpToPx(5, mBaseActivity);
        dp90 = ScreenUtil.convertDpToPx(90, mBaseActivity);
        mInflater = LayoutInflater.from(context);

        if (mBaseActivity instanceof CategorySelectActivity) {
            selSubcateList = ((CategorySelectActivity)mBaseActivity).getSelectId();
            if (selSubcateList != null && selSubcateList.size() > 0) {
                for (SubCate subCate : subCates) {
                    if (selSubcateList.contains(subCate.getId()))
                        subCate.setSelect(true);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return subCates.size();
    }

    @Override
    public Object getItem(int i) {
        return subCates.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_subcate_item, null);
            convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, dp90));
            holder = new ViewHolder();
            holder.mTxtTitle = convertView.findViewById(R.id.text);
            lp = (FrameLayout.LayoutParams) holder.mTxtTitle.getLayoutParams();
            if (position % 3 == 0) {
                lp.setMargins(0, dp5, dp5, dp5);
            } else if (position % 3 == 1) {
                lp.setMargins(dp5, dp5, dp5, dp5);
            } else if (position % 3 == 2) {
                lp.setMargins(dp5, dp5, 0, dp5);
            }
            holder.mTxtTitle.setLayoutParams(lp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SubCate subCate = subCates.get(position);
        holder.mTxtTitle.setText(subCate.getTitle());

        if (mBaseActivity instanceof CategoryListActivity) {
            holder.mTxtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Category[] cates = ((CategoryListActivity)mBaseActivity).getCateArray();
                    String json = gson.toJson(cates);
                    Intent intent = new Intent(mBaseActivity, CategorySearchActivity.class);
                    intent.putExtra("subCategoryId", subCate.getId());
                    intent.putExtra("categoryId", subCate.getCategoryId());
                    intent.putExtra("items", json);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
        }

        else if (mBaseActivity instanceof CategorySearchActivity) {
            holder.mTxtTitle.setBackground(mBaseActivity.getResources().getDrawable(R.drawable.btn_solid_gray));
            holder.mTxtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((CategorySearchActivity)mBaseActivity).updateSubcate(subCate.getCategoryId(), subCate.getId());
                    ((CategorySearchActivity)mBaseActivity).dismissView();
                }
            });
        }

        else if (mBaseActivity instanceof CategorySelectActivity) { // Become Expert
            if (subCates.get(position).isSelect()) {
                holder.mTxtTitle.setBackground(mBaseActivity.getResources().getDrawable(R.drawable.btn_solid_primary));
                holder.mTxtTitle.setTextColor(Color.WHITE);
            } else {
                holder.mTxtTitle.setBackground(mBaseActivity.getResources().getDrawable(R.drawable.btn_solid_white));
                holder.mTxtTitle.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_dark));
            }
            holder.mTxtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subCate.setSelect(!subCate.isSelect());
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    public ArrayList<SubCate> getSelList() {
        ArrayList<SubCate> selList = new ArrayList<>();
        for (SubCate subCate : subCates) {
            if (subCate.isSelect())
                selList.add(subCate);
        }
        return selList;
    }

    final class ViewHolder {
        TextView mTxtTitle;
    }
}
