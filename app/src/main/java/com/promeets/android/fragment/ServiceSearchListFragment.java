package com.promeets.android.fragment;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.ExpertDetailActivity;
import com.promeets.android.activity.HomeActivity;
import com.promeets.android.adapter.RecycleSearchAdapter;
import com.promeets.android.api.ServiceApi;
import com.promeets.android.api.URL;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.custom.SearchBarLayout;
import android.graphics.Typeface;
import com.promeets.android.listeners.RecyclerTouchListener;
import com.promeets.android.object.ExpertService;
import android.os.Bundle;
import com.promeets.android.pojo.SuperListResp;
import com.promeets.android.pojo.SuperResp;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import com.promeets.android.object.SearchKeyPOJO;
import com.promeets.android.pojo.SearchKeyResp;
import com.promeets.android.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 2nd page of HomeActivity
 *
 * @destination: ExpertDetailActivity
 *
 */

public class ServiceSearchListFragment extends Fragment
        implements View.OnClickListener {

    RecycleSearchAdapter adapter;

    @BindView(R.id.root_layout)
    LinearLayout mLayoutRoot;

    @BindView(R.id.search_key_lay)
    LinearLayout mLayoutSearchKey;

    @BindView(R.id.flexBox)
    FlexboxLayout mFlexBox;

    @BindView(R.id.search_bar_layout)
    SearchBarLayout mSearchBar;

    @BindView(R.id.lvSearchList)
    RecyclerView mRVSearch;

    @BindView(R.id.scroll_view)
    ScrollView mScrollView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mLayRefresh;

    @BindView(R.id.no_result)
    TextView mTxtNoResult;

    private BaseActivity mBaseActivity;

    private ArrayList<ExpertService> arrayList = new ArrayList<>();

    private String mTxtQuery;

    private int pageNumber = 1;

    Typeface tf_reg;

    public ServiceSearchListFragment() {

    }

    public static ServiceSearchListFragment newInstance() {
        //Bundle args = new Bundle();
        ServiceSearchListFragment sampleFragment = new ServiceSearchListFragment();
        //args.putString("query", query);
        //sampleFragment.setArguments(args);
        return sampleFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View accountFragment = inflater.inflate(R.layout.fragment_service_list, container, false);

        mBaseActivity = (BaseActivity) getActivity();

        tf_reg = Typeface.createFromAsset(mBaseActivity.getAssets(), "fonts/OpenSans-Regular.ttf");

        ButterKnife.bind(this, accountFragment);

        getSearchKeys();

        mLayoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseActivity.hideSoftKeyboard();
                mSearchBar.cancelFocus();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity);
        mRVSearch.setLayoutManager(mLayoutManager);
        mRVSearch.setHasFixedSize(true);
        mRVSearch.setItemViewCacheSize(50);
        mRVSearch.setDrawingCacheEnabled(true);
        mRVSearch.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new RecycleSearchAdapter(mBaseActivity, arrayList);
        mRVSearch.setAdapter(adapter);
        mRVSearch.addOnItemTouchListener(new RecyclerTouchListener(mBaseActivity, mRVSearch, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ExpertService service = arrayList.get(position);
                String serviceId = String.valueOf(service);
                String expId = String.valueOf(service.expId);
                Intent intent = new Intent(mBaseActivity, ExpertDetailActivity.class);
                intent.putExtra("serviceId", serviceId);
                intent.putExtra("expId", expId);
                startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                ((HomeActivity) mBaseActivity).setQuery(mSearchBar.getEditor().getText().toString());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mLayRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(500);
            }
        });
        mLayRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                fetchSearchResult();
            }
        });

        mSearchBar.getEditor().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    arrayList.clear();
                    adapter.notifyDataSetChanged();

                    mLayRefresh.setVisibility(View.GONE);
                    mTxtNoResult.setVisibility(View.GONE);
                    mLayoutRoot.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSearchBar.getEditor().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchBar.getEditor().clearFocus();
                    mTxtQuery = mSearchBar.getEditor().getText().toString();
                    pageNumber = 1;
                    fetchSearchResult();
                    return true;
                }
                return false;
            }
        });

        return accountFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTxtQuery = ((HomeActivity) mBaseActivity).getQuery();
        ((HomeActivity) mBaseActivity).setQuery("");
    }

    private void fetchSearchResult() {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("homepageservice/searchByIndexableKeys"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        Call<SuperResp> call = service.searchByIndexableKeys(mTxtQuery, pageNumber);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                mLayRefresh.finishLoadmore();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if (mBaseActivity.isSuccess(result.info.code)) {
                    if (pageNumber == 1) {
                        arrayList.clear();
                        mLayRefresh.setLoadmoreFinished(false);

                        if (result.list == null || result.list.size() == 0) {
                            mTxtNoResult.setVisibility(View.VISIBLE);
                            mLayRefresh.setVisibility(View.GONE);
                            mLayoutRoot.setVisibility(View.GONE);
                        }
                    }

                    if (result.list != null && result.list.size() > 0) {
                        mLayRefresh.setVisibility(View.VISIBLE);
                        mLayoutRoot.setVisibility(View.GONE);
                        pageNumber++;

                        for (SuperListResp item : result.list) {
                            ExpertService serviceHolder = new ExpertService();
                            serviceHolder.id = item.expertService.id;
                            serviceHolder.title = item.expertService.title;
                            serviceHolder.expName = item.expertProfile.fullName;
                            serviceHolder.expId = item.expertProfile.expId;
                            serviceHolder.smallphotoUrl = item.expertProfile.smallphotoUrl;
                            serviceHolder.description = item.expertProfile.positon;
                            arrayList.add(serviceHolder);
                        }
                        adapter.notifyDataSetChanged();
                    } else
                        mLayRefresh.setLoadmoreFinished(true);
                } else
                    PromeetsDialog.show(mBaseActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                mLayRefresh.finishLoadmore();
            }
        });
    }

    private void getSearchKeys() {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("homepageservice/displaySearchKeys"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        Call<SearchKeyResp> call = service.displaySearchKeys();
        call.enqueue(new Callback<SearchKeyResp>() {
            @Override
            public void onResponse(Call<SearchKeyResp> call, Response<SearchKeyResp> response) {
                PromeetsDialog.hideProgress();
                SearchKeyResp result = response.body();
                if (result != null && result.info != null && result.info.code.equals("200")) {
                    if (result.dataList == null || result.dataList.size() == 0) return;
                    for (SearchKeyPOJO pojo : result.dataList) {
                        if (pojo.displayTitle.contains("Top")) {
                            handleSearchKeys(pojo);
                        } else if (pojo.displayTitle.contains("Trending")) {
                            handleExpTags(pojo);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchKeyResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }

    private void handleSearchKeys(SearchKeyPOJO pojo) {
        if (pojo.displayList == null || pojo.displayList.size() == 0) return;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        for (String key : pojo.displayList) {
            TextView mTxtKey = new TextView(mBaseActivity);
            mTxtKey.setLayoutParams(lp);
            mTxtKey.setText(key);
            mTxtKey.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            mTxtKey.setTextColor(mBaseActivity.getResources().getColor(R.color.primary));
            mTxtKey.setPadding(0, ScreenUtil.convertDpToPx(3, mBaseActivity),
                    0, ScreenUtil.convertDpToPx(3, mBaseActivity));
            mTxtKey.setGravity(Gravity.CENTER_HORIZONTAL);
            mTxtKey.setTypeface(tf_reg);
            mLayoutSearchKey.addView(mTxtKey);
            mTxtKey.setOnClickListener(this);
        }
    }

    private void handleExpTags(SearchKeyPOJO pojo) {
        if (pojo.displayList == null || pojo.displayList.size() == 0) return;
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.convertDpToPx(4, mBaseActivity),
                ScreenUtil.convertDpToPx(5, mBaseActivity),
                ScreenUtil.convertDpToPx(4, mBaseActivity),
                ScreenUtil.convertDpToPx(5, mBaseActivity));
        for (String tag : pojo.displayList) {
            TextView mTxtTag = new TextView(mBaseActivity);
            mTxtTag.setLayoutParams(lp);
            mTxtTag.setText(tag);
            mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            mTxtTag.setTextColor(mBaseActivity.getResources().getColor(R.color.primary));
            mTxtTag.setPadding(ScreenUtil.convertDpToPx(15, mBaseActivity),
                    ScreenUtil.convertDpToPx(5, mBaseActivity),
                    ScreenUtil.convertDpToPx(15, mBaseActivity),
                    ScreenUtil.convertDpToPx(5, mBaseActivity));
            mTxtTag.setBackgroundResource(R.drawable.tag_border_primary);
            mTxtTag.setTypeface(tf_reg);
            mFlexBox.addView(mTxtTag);
            mTxtTag.setOnClickListener(this);
        }

        if (!StringUtils.isEmpty(mTxtQuery)) {
            ((HomeActivity) mBaseActivity).setQuery("");
            mSearchBar.getEditor().setText(mTxtQuery);
            pageNumber = 1;
            fetchSearchResult();
        }
    }

    @Override
    public void onClick(View v) {
        mTxtQuery = ((TextView) v).getText().toString().replace("#", "");
        mSearchBar.getEditor().setText(mTxtQuery);
        pageNumber = 1;
        fetchSearchResult();
    }
}