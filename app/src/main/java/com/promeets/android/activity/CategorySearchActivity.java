package com.promeets.android.activity;

import com.promeets.android.adapter.CateServiceAdapter;
import com.promeets.android.adapter.CategoryTabAdapter;
import com.promeets.android.adapter.RecycleCategoryTitleAdapter;
import com.promeets.android.adapter.RecycleSearchAdapter;
import android.content.Intent;
import com.promeets.android.custom.CategoryView;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.drawable.Drawable;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.listeners.RecyclerTouchListener;
import com.promeets.android.object.Category;
import com.promeets.android.object.CustomList;
import com.promeets.android.object.SubCate;
import com.promeets.android.object.Tab;
import android.os.Bundle;
import com.promeets.android.pojo.GridResp;
import com.promeets.android.pojo.HomeResp;
import com.promeets.android.services.GenericServiceHandler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceResponseHolder;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.gson.Gson;

import com.promeets.android.pojo.ServiceResponse;

import com.promeets.android.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is for showing topic list with selected subcategory tag
 *
 * Two views are optional to show:
 * Category list View
 * Filter View
 *
 * @source: CategoryListActivity(select subcategory),
 * UserProfileActivity(select subcategory tag)
 *
 */
public class CategorySearchActivity extends BaseActivity implements View.OnClickListener, IServiceResponseHandler, RecyclerTouchListener.ClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mLayRefresh;

    @BindView(R.id.activity_subcategories_service_search_service_list)
    ListView mLVResult;

    @BindView(R.id.activity_subcategories_service_search_tab_list)
    RecyclerView mRVSubTitle;

    @BindView(R.id.category)
    ImageView mImgAllCate;

    @BindView(R.id.filter)
    ImageView mImgFilter;


    @BindView(R.id.trans_layer)
    View mTransView;
    // Filter view
    @BindView(R.id.filter_trans_lay)
    View mFilterTransView;
    @BindView(R.id.filter_view)
    LinearLayout mViewFilter;
    @BindView(R.id.sort_rank)
    TextView mSortRank;
    @BindView(R.id.sort_meet)
    TextView mSortMeet;
    @BindView(R.id.sort_score)
    TextView mSortScore;
    @BindView(R.id.sort_price)
    TextView mSortPrice;

    // Category View
    @BindView(R.id.cate_trans_layer)
    View mCateTransView;
    @BindView(R.id.cate_view)
    LinearLayout mViewCate;
    @BindView(R.id.cate_title)
    RecyclerView mRVTitle;
    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;


    RecycleSearchAdapter adapter;
    CategoryTabAdapter tabAdapter;
    ArrayList<Tab> tabArrayList = new ArrayList<>();

    Animation inAnimation;
    Animation outAnimation;
    @BindView(R.id.cate_image)
    ImageView mImgCate;

    @BindView(R.id.cate_name)
    TextView mTxtTitle;
    @BindView(R.id.price_range)
    TextView mTxtRange;
    @BindView(R.id.rangeSeekbar)
    CrystalRangeSeekbar mRangeBar;

    @BindView(R.id.apply_filter)
    TextView mBtnApplyFilter;
    @BindView(R.id.no_result)
    TextView mTxtNoResult;

    private int categoryId;
    private int subCategoryId;

    private int pageIndex = 1;
    private ArrayList<CustomList> customList = new ArrayList<>();
    CateServiceAdapter cateServiceAdapter;

    private String sortText = "";
    private String begin = "0";
    private String end = "1200";


    private Drawable check;

    private Category[] mArrayCategory;
    private Gson gson = new Gson();
    private ArrayList<String> mListTitle = new ArrayList<>();
    private SparseArray<View> map = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);
        ButterKnife.bind(this);

        initAnimations();
        check = getResources().getDrawable(R.drawable.ic_nike_primary);
        mSortRank.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
        sortText = "ComprehensiveRanking";

        categoryId = getIntent().getIntExtra("categoryId", 0);
        subCategoryId = getIntent().getIntExtra("subCategoryId", 0);
        String json = getIntent().getStringExtra("items");
        if (!StringUtils.isEmpty(json))
            mArrayCategory = gson.fromJson(json, Category[].class);
        else {
            ServiceResponse response = ServiceResponseHolder.getInstance().getHomeService();
            GridResp gridResp = (GridResp) response.getServiceResponse(GridResp.class);
            mArrayCategory = gridResp.getCategoryList();
        }


        for (int i = 0; i < mArrayCategory.length; i++) {
            if (mArrayCategory[i].getId() == categoryId) {
                prepareTab(mArrayCategory[i].getList());
                callIndustryList(mArrayCategory[i].getList());
                Glide.with(this).load(mArrayCategory[i].getIconUrl()).into(mImgCate);
                mTxtTitle.setText(mArrayCategory[i].getTitle());
            }
        }

        // cate list
        for (int i = 0; i < mArrayCategory.length; i++) {
            if (mArrayCategory[i].getTitle().equalsIgnoreCase("all")) continue;
            CategoryView view = new CategoryView(this, mArrayCategory[i]);
            if (i == mArrayCategory.length - 2)
                view.setPadding(0, 0, 0, ScreenUtil.convertDpToPx(30, this));
            mLayRoot.addView(view);
            map.put(i, view);
        }

        // Category title
        mListTitle.clear();
        for (int i = 0; i < mArrayCategory.length; i++) {
            if (mArrayCategory[i].getTitle().equals("All")) continue;
            mListTitle.add(mArrayCategory[i].getTitle());
        }
        RecycleCategoryTitleAdapter adapter = new RecycleCategoryTitleAdapter(this, mListTitle);
        mRVTitle.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRVTitle.setLayoutManager(mLayoutManager);
        //adapter.setInitSelect(sel);
    }

    @Override
    public void initElement() {

    }

    public void updateSubcate(int cateId, int subCateId) {
        categoryId = cateId;
        subCategoryId = subCateId;

        // Subcate title
        for (int i = 0; i < mArrayCategory.length; i++) {
            if (mArrayCategory[i].getId() == categoryId) {
                prepareTab(mArrayCategory[i].getList());
                callIndustryList(mArrayCategory[i].getList());
                Glide.with(this).load(mArrayCategory[i].getIconUrl()).into(mImgCate);
                mTxtTitle.setText(mArrayCategory[i].getTitle());
            }
        }

        // Category title
        mListTitle.clear();
        for (int i = 0; i < mArrayCategory.length; i++) {
            if (mArrayCategory[i].getTitle().equals("All")) continue;
            mListTitle.add(mArrayCategory[i].getTitle());
        }
        RecycleCategoryTitleAdapter adapter = new RecycleCategoryTitleAdapter(this, mListTitle);
        mRVTitle.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRVTitle.setLayoutManager(mLayoutManager);
        //adapter.setInitSelect(sel);
    }

    @Override
    public void registerListeners() {
        mImgAllCate.setOnClickListener(this);
        mImgFilter.setOnClickListener(this);
        mLVResult.setOnItemClickListener(this);

        // Category View
        mViewCate.setOnClickListener(this);
        mCateTransView.setOnClickListener(this);
        // Filter View
        mViewFilter.setOnClickListener(this);
        mFilterTransView.setOnClickListener(this);
        mSortRank.setOnClickListener(this);
        mSortMeet.setOnClickListener(this);
        mSortScore.setOnClickListener(this);
        mSortPrice.setOnClickListener(this);
        mBtnApplyFilter.setOnClickListener(this);
        mRangeBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mTxtRange.setText("$ " + minValue + " - $ " + maxValue);
            }
        });
        mRangeBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                begin = minValue.toString();
                end = maxValue.toString();
            }
        });

        mLayRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(500);
            }
        });
        mLayRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                applyFilter();
            }
        });
        mLVResult.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter:
                dismissView();
                mTransView.setVisibility(View.VISIBLE);
                show(mViewFilter);
                break;
            case R.id.trans_layer:
                dismissView();
                break;
            case R.id.cate_trans_layer:
            case R.id.filter_trans_lay:
                dismissView();
                break;
            case R.id.category:
                dismissView();
                mTransView.setVisibility(View.VISIBLE);
                show(mViewCate);
                break;


            case R.id.sort_rank:
                resetSort();
                mSortRank.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
                sortText = "ComprehensiveRanking";
                break;
            case R.id.sort_meet:
                resetSort();
                mSortMeet.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
                sortText = "MeetTheMost";
                break;
            case R.id.sort_score:
                resetSort();
                mSortScore.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
                sortText = "HighestScore";
                break;
            case R.id.sort_price:
                resetSort();
                mSortPrice.setCompoundDrawablesWithIntrinsicBounds(null, null, check, null);
                sortText = "TheLowestPrice";
                break;

            case R.id.apply_filter:
                dismissView();
                pageIndex = 1;
                applyFilter();
                break;
        }
    }

    public void applyFilter() {
        HashMap<String, String> header = new HashMap<>();
            //Check for internet Connection
            if (!hasInternetConnection()) {
                PromeetsDialog.show(this, getString(R.string.no_internet));
                return;
            }
                PromeetsDialog.showProgress(this);
                String[] key = {Constant.SORTKEY, Constant.INDUSTRY, Constant.FIRST_PRICE, Constant.LAST_PRICE, Constant.PAGENUMBER};
                String[] value = {sortText, subCategoryId+"", begin, end, pageIndex + ""};
                new GenericServiceHandler(Constant.ServiceType.INDUSTRY_LIST, this, PromeetsUtils.buildURL(Constant.FILTER_AND_SORT, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }

    private void resetSort() {
        mSortRank.setCompoundDrawables(null, null, null, null);
        mSortMeet.setCompoundDrawables(null, null, null, null);
        mSortScore.setCompoundDrawables(null, null, null, null);
        mSortPrice.setCompoundDrawables(null, null, null, null);
    }

    private void initAnimations() {
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_dialog);
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_dialog);
    }

    public void show(View view) {
        view.startAnimation(inAnimation);
        view.setVisibility(View.VISIBLE);
    }


    public void hide(View view) {
        view.startAnimation(outAnimation);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        mLayRefresh.finishLoadmore();
        //loadingMore = false;
        if (serviceType == Constant.ServiceType.INDUSTRY_LIST) {
            HomeResp result = (HomeResp) serviceResponse.getServiceResponse(HomeResp.class);
            if (isSuccess(result.getInfo().getCode())) {
                if (pageIndex == 1) {
                    customList.clear();
                    mLayRefresh.setLoadmoreFinished(false);
                }
                if (result.getList() != null && result.getList().length > 0) {
                    mTxtNoResult.setVisibility(View.GONE);
                    try {
                        customList.addAll(new ArrayList<>(Arrays.asList(result.getList())));
                        mLVResult.setSelection(0);
                        pageIndex++;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    if (pageIndex == 1)
                        mTxtNoResult.setVisibility(View.VISIBLE);
                    mLayRefresh.setLoadmoreFinished(true);
                }
                if (cateServiceAdapter == null) {
                    cateServiceAdapter = new CateServiceAdapter(this, customList);
                    mLVResult.setAdapter(cateServiceAdapter);
                }
                cateServiceAdapter.notifyDataSetChanged();
            }
        }
    }

    private void callIndustryList(ArrayList<SubCate> subCates) {
        if (subCategoryId == 0) {
            onClick(null, 0);
        } else {
            //int index = 0;
            for (int index = 0; index < subCates.size(); index++) {
                if (subCates.get(index).getId() == subCategoryId) {
                    onClick(null, index);
                    break;
                }
            }
        }
    }

    private void prepareTab(ArrayList<SubCate> subCates) {
        tabArrayList.clear();
        for (SubCate subCate : subCates) {
            Tab tab = new Tab();
            tab.id = subCate.getId();
            tab.tab = subCate.getTitle();
            tabArrayList.add(tab);
        }
        tabAdapter = new CategoryTabAdapter(this, tabArrayList);
        mRVSubTitle.setAdapter(tabAdapter);
        mRVSubTitle.addOnItemTouchListener(new RecyclerTouchListener(this, mRVSubTitle, this));
        //onClick(null, 0);
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        mLayRefresh.finishLoadmore();
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    @Override
    public void onClick(View view, int position) {
        if (position < 0)
            return;
        tabAdapter.setSelect(position);
        mRVSubTitle.smoothScrollToPosition(position);

        Tab tab = tabArrayList.get(position);
        customList.clear();

        if (cateServiceAdapter != null)
            cateServiceAdapter.notifyDataSetChanged();

        pageIndex = 1;

        //searchByCategoryId(tab.id);
        subCategoryId = tab.id;
        applyFilter();
        //closeMenu();
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CustomList customList = CategorySearchActivity.this.customList.get(i);

        Intent intent = new Intent(this, ExpertDetailActivity.class);
        intent.putExtra("expId", customList.getExpertProfile().getExpId());
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onBackPressed() {
        if (mViewFilter.getVisibility() == View.VISIBLE ||
                mViewCate.getVisibility() == View.VISIBLE)
            dismissView();

        else
            super.onBackPressed();
    }

    public RecyclerView getRecyclerView() {
        return mRVTitle;
    }

    public void dismissView() {
        mTransView.setVisibility(View.GONE);
        if (mViewFilter.getVisibility() == View.VISIBLE)
            hide(mViewFilter);
        if (mViewCate.getVisibility() == View.VISIBLE) {
            hide(mViewCate);
            ((RecycleCategoryTitleAdapter)mRVTitle.getAdapter()).reset();
        }
    }

    public int getPosition(int index) {
        int result = 0;
        if (index == 0) return 0;

        for (int i = 0; i < index; i++) {
            result += Math.ceil(mArrayCategory[i].getList().size() / 3.0);
            result++;
        }

        return result;
    }

    public SparseArray<View> getMap() {
        return map;
    }

    public ScrollView getScrollView() {
        return mScrollView;
    }
}