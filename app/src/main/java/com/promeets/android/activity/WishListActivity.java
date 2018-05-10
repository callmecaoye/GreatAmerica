package com.promeets.android.activity;

import com.promeets.android.adapter.WishListAdapter;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.os.Bundle;
import com.promeets.android.pojo.WishResp;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.ExpertProfile;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is for showing a list of user's wishlist
 *
 * @source: AccountFragment
 *
 * @destination: ExpertDetailActivity
 *
 */
public class WishListActivity extends BaseActivity
        implements IServiceResponseHandler, AdapterView.OnItemClickListener {

    @BindView(R.id.my_wishlist)
    ListView mListView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mLayRefresh;

    @BindView(R.id.empty_view)
    LinearLayout mViewEmpty;

    private WishListAdapter mAdapter;
    private ArrayList<ExpertProfile> customList;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        ButterKnife.bind(this);

        customList = new ArrayList<>();
        mAdapter = new WishListAdapter(this, customList);
        mListView.setAdapter(mAdapter);
        requestWishList();
    }

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mListView.setOnItemClickListener(this);
        mLayRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(500);
            }
        });
        mLayRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                requestWishList();
            }
        });
    }

    public void requestWishList() {
        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
        } else {
            UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
            if (userPOJO == null) finish();
            String[] key = {Constant.USERID, Constant.PAGENUMBER};
            String[] value = {userPOJO.id + "", page + ""};

            PromeetsDialog.showProgress(this);
            HashMap<String, String> header = new HashMap<>();
            header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
            header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_MY_WISH_LIST));
            header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
            header.put("API_VERSION", Utility.getVersionCode());
            new GenericServiceHandler(Constant.ServiceType.WISH_LIST, this, PromeetsUtils.buildURL(Constant.FETCH_MY_WISH_LIST, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
        }
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        mLayRefresh.finishLoadmore();
        WishResp result = (WishResp) serviceResponse.getServiceResponse(WishResp.class);
        if (isSuccess(result.getInfo().getCode())) {
            if (page == 1) customList.clear();
            if (result.getDataList() != null && result.getDataList().size() > 0) {
                page++;
                customList.addAll(result.getDataList());
                mAdapter.notifyDataSetChanged();
            } else
                mLayRefresh.setLoadmoreFinished(true);

            if (mAdapter.getCount() == 0) {
                mLayRefresh.setVisibility(View.GONE);
                mViewEmpty.setVisibility(View.VISIBLE);
            }
        } else
            onErrorResponse(result.getInfo().getDescription());
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ExpertProfile expertProfile = mAdapter.getItem(i);
        Intent intent = new Intent(this, ExpertDetailActivity.class);
        intent.putExtra("expId", expertProfile.getExpId());
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
