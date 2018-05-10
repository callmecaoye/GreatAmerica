package com.promeets.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.CategorySearchActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.promeets.android.object.SubCate;
import com.promeets.android.util.MixpanelUtil;
import com.promeets.android.object.Category;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sosasang on 5/2/17.
 */

public class RecycleCategoryAdapter extends RecyclerView.Adapter<RecycleCategoryAdapter.ViewHolder>{

    private List<Category> mListCategory;
    private Context mContext;
    private LayoutInflater mInflater;
    private Gson gson = new Gson();

    private int dp5AsPixels;
    private int dp16AsPixels;

    public RecycleCategoryAdapter(Context context, List<Category> items) {
        this.mContext = context;
        this.mListCategory = items;
        mInflater = LayoutInflater.from(mContext);

        float density = mContext.getResources().getDisplayMetrics().density;
        dp5AsPixels = (int) (5*density + 0.5f);
        dp16AsPixels = (int) (16*density + 0.5f);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.
                inflate(R.layout.recycle_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.mRootView.setPadding(dp16AsPixels,dp5AsPixels,dp5AsPixels,dp5AsPixels);
        } else if (position == mListCategory.size() - 1) {
            holder.mRootView.setPadding(dp5AsPixels,dp5AsPixels,dp16AsPixels,dp5AsPixels);
        } else
            holder.mRootView.setPadding(dp5AsPixels,dp5AsPixels,dp5AsPixels,dp5AsPixels);

        Category category = mListCategory.get(position);
        if (!StringUtils.isEmpty(category.getBgUrl()))
            Glide.with(mContext).load(category.getBgUrl()).into(holder.mImgView);
    }

    @Override
    public int getItemCount() {
        return mListCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImgView;
        LinearLayout mRootView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgView = (ImageView) itemView.findViewById(R.id.imageView);
            mRootView = (LinearLayout) itemView.findViewById(R.id.rootView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Category cate = mListCategory.get(position);
            HashMap<String, String> map = new HashMap<>();
            map.put("ContentCategoryList", cate.getTitle());
            MixpanelUtil.getInstance((BaseActivity)mContext).trackEvent("Home page -> Click one of categories", map);
            ArrayList<SubCate> subCates = cate.getList();
            for (SubCate subCate : subCates) {
                if (subCate.getTitle().equalsIgnoreCase("all")) {
                    String json = gson.toJson(mListCategory);
                    Intent intent = new Intent(mContext, CategorySearchActivity.class);
                    intent.putExtra("subCategoryId", subCate.getId());
                    intent.putExtra("categoryId", subCate.getCategoryId());
                    intent.putExtra("items", json);
                    mContext.startActivity(intent);
                    ((BaseActivity)mContext).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        }
    }

}
