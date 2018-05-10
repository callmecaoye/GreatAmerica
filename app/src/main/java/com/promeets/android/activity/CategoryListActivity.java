package com.promeets.android.activity;

import com.promeets.android.adapter.RecycleCategoryTitleAdapter;
import com.promeets.android.custom.CategoryView;
import com.promeets.android.object.Category;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Horizontal recycler view for Category at top
 *
 * Use custom Category view to show subcategories
 *
 * Click category item in RecyclerView will automatically scroll to corresponding CategoryView
 *
 * @source: HomePageFragment
 *
 * @destination: CategorySearchActivity
 */
public class CategoryListActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.scrollView)
    ScrollView mScrollView;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    private Category[] mArrayCategory;
    private ArrayList<String> mListTitle = new ArrayList<>();
    private Gson gson = new Gson();
    private SparseArray<View> map = new SparseArray<>();

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);

        String json = getIntent().getStringExtra("items");
        mArrayCategory = gson.fromJson(json, Category[].class);

        // cate list
        for (int i = 0; i < mArrayCategory.length; i++) {
            if (mArrayCategory[i].getTitle().equalsIgnoreCase("all")) continue;
            CategoryView view = new CategoryView(this, mArrayCategory[i]);
            if (i == mArrayCategory.length - 2)
                view.setPadding(0, 0, 0, ScreenUtil.convertDpToPx(30, this));
            mLayRoot.addView(view);
            map.put(i, view);
        }

        // cate title
        for (int i = 0; i < mArrayCategory.length; i++) {
            if (mArrayCategory[i].getTitle().equals("All")) continue;
            mListTitle.add(mArrayCategory[i].getTitle());
        }
        RecycleCategoryTitleAdapter adapter = new RecycleCategoryTitleAdapter(this, mListTitle);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public Category[] getCateArray() {
        return mArrayCategory;
    }

    public SparseArray<View> getMap() {
        return map;
    }

    public ScrollView getScrollView() {
        return mScrollView;
    }
}
