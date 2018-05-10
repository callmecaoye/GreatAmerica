package com.promeets.android.fragment;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.api.ServiceApi;
import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import com.promeets.android.util.GlideUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.promeets.android.activity.EventDetailActivity;
import com.promeets.android.adapter.ListEventAdapter;
import com.promeets.android.api.URL;
import com.promeets.android.object.ServiceEvent;
import com.promeets.android.pojo.ActiveEventResp;
import com.promeets.android.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.promeets.android.util.MixpanelUtil;

import com.promeets.android.util.ServiceResponseHolder;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * 3rd page of HomeActivity
 *
 * @destination: EventDetailActivity
 *
 */

public class EventFragment extends Fragment {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mLayRefresh;

    @BindView(R.id.event_list)
    StickyListHeadersListView mListViewEvent;


    private int pageNumber = 1;

    private BaseActivity mBaseActivity;

    private ListEventAdapter mEventAdapter;

    private ArrayList<ServiceEvent> eventList = new ArrayList<>();

    public EventFragment() {

    }

    public static EventFragment newInstance() {
        EventFragment sampleFragment = new EventFragment();
        return sampleFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View eventFragment = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, eventFragment);
        MixpanelUtil.getInstance(mBaseActivity).trackEvent("Event page view");

        mEventAdapter = new ListEventAdapter(mBaseActivity, eventList);
        mListViewEvent.setDividerHeight(0);
        mListViewEvent.setDrawingListUnderStickyHeader(true);
        mListViewEvent.setAreHeadersSticky(true);
        mListViewEvent.setAdapter(mEventAdapter);
        mListViewEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("eventId", eventList.get(position).id);
                MixpanelUtil.getInstance(mBaseActivity).trackEvent("Event page -> Click one of events", map);

                mListViewEvent.setEnabled(false);
                mLayRefresh.setEnabled(false);
                Intent intent = new Intent(mBaseActivity, EventDetailActivity.class);
                ImageView imageView = view.findViewById(R.id.image_view);
                intent.putExtra("eventId", eventList.get(position).id);
                if (GlideUtils.haveCache(mBaseActivity, eventList.get(position).photoUrl)) {
                    intent.putExtra("photoPath", GlideUtils.getCache(mBaseActivity, eventList.get(position).photoUrl));
                }
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mBaseActivity,
                                imageView,
                                ViewCompat.getTransitionName(imageView));
                startActivity(intent, options.toBundle());
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
                getEventData();
            }
        });

        getEventData();
        return eventFragment;
    }

    private void getEventData() {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("activeEvent/display"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        Call<ActiveEventResp> call = service.displayActiveEvent(pageNumber);
        call.enqueue(new Callback<ActiveEventResp>() {
            @Override
            public void onResponse(Call<ActiveEventResp> call, Response<ActiveEventResp> response) {
                PromeetsDialog.hideProgress();
                //mLayRefresh.setLoading(false);
                mLayRefresh.finishLoadmore();
                ActiveEventResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if (mBaseActivity.isSuccess(result.info.code)) {
                    if (result.dataList.size() > 0) {
                        pageNumber++;
                        eventList.addAll(result.dataList);
                        mEventAdapter.notifyDataSetChanged();
                    } else {
                        mLayRefresh.setLoadmoreFinished(true);
                    }
                } else
                    PromeetsDialog.show(mBaseActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<ActiveEventResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                //mLayRefresh.setLoading(false);
                mLayRefresh.finishLoadmore();
            }
        });
    }
}
