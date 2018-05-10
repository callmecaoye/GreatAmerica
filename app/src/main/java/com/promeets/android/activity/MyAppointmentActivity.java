package com.promeets.android.activity;

import com.promeets.android.api.EventApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.object.Appoint;
import android.os.Bundle;
import com.promeets.android.pojo.EventRequestResp;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.View;
import android.widget.TextView;
import com.promeets.android.adapter.RecycleAppointmentAdapter;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is list of appointment
 *
 * Two tabs: In process || Completed
 * Two roles: As user || As expert
 *
 * @source: AccountFragment
 *
 * @destination: AppointStatusActivity
 *
 */

public class MyAppointmentActivity extends BaseActivity {

    @BindView(R.id.process)
    TextView mTxtLeft;
    @BindView(R.id.complete)
    TextView mTxtRight;
    @BindView(R.id.recycler_view)
    RecyclerView mRVEvent;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mLayRefresh;

    private UserPOJO userPOJO;
    private int selection = 0; // 0:left 1:right

    private int pageNumber = 1;
    private boolean ifExpert;
    private String eventCommand;

    private ArrayList<Appoint> mListAppoint = new ArrayList<>();
    private RecycleAppointmentAdapter adapter;

    @Override
    public void initElement() {
        /*pageNumber = 1;
        mListAppoint.clear();
        adapter.notifyDataSetChanged();
        refresh();*/
    }

    @Override
    public void registerListeners() {
        mLayRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(500);
            }
        });
        mLayRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refresh();
            }
        });

        mTxtLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selection != 0) {
                    selection = 0;
                    mTxtLeft.setBackground(getResources().getDrawable(R.drawable.left_select));
                    mTxtLeft.setTextColor(getResources().getColor(R.color.white));
                    mTxtRight.setBackground(getResources().getDrawable(R.drawable.right_unselect));
                    mTxtRight.setTextColor(getResources().getColor(R.color.primary));
                    if (ifExpert)
                        eventCommand = "EXPERT_INPROCESS";
                    else
                        eventCommand = "USER_INPROCESS";

                    pageNumber = 1;
                    mListAppoint.clear();
                    adapter.notifyDataSetChanged();
                    refresh();
                }
            }
        });

        mTxtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selection != 1) {
                    selection = 1;
                    mTxtLeft.setBackground(getResources().getDrawable(R.drawable.left_unselect));
                    mTxtLeft.setTextColor(getResources().getColor(R.color.primary));
                    mTxtRight.setBackground(getResources().getDrawable(R.drawable.right_select));
                    mTxtRight.setTextColor(getResources().getColor(R.color.white));

                    if (ifExpert)
                        eventCommand = "EXPERT_ENDED";
                    else
                        eventCommand = "USER_ENDED";

                    pageNumber = 1;
                    mListAppoint.clear();
                    adapter.notifyDataSetChanged();
                    refresh();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);
        ButterKnife.bind(this);

        userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        ifExpert = getIntent().getBooleanExtra("ifExpert", false);
        if (ifExpert) {
            eventCommand = "EXPERT_INPROCESS";
        } else {
            eventCommand = "USER_INPROCESS";
        }

        adapter = new RecycleAppointmentAdapter(this, mListAppoint);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRVEvent.setLayoutManager(mLayoutManager);
        mRVEvent.setAdapter(adapter);
        DividerItemDecoration divider = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.divider));
        mRVEvent.addItemDecoration(divider);

        refresh();
    }

    public void refresh() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/fetchEventPage"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventApi service = retrofit.create(EventApi.class);
        Call<EventRequestResp> call = service.getAllList(userPOJO.id, pageNumber, eventCommand, ifExpert);
        call.enqueue(new Callback<EventRequestResp>() {
            @Override
            public void onResponse(Call<EventRequestResp> call, Response<EventRequestResp> response) {
                PromeetsDialog.hideProgress();
                mLayRefresh.finishLoadmore();
                EventRequestResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(MyAppointmentActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    if (pageNumber == 1)
                        mLayRefresh.setLoadmoreFinished(false);

                    if (result.dataList.size() > 0) {
                        pageNumber++;
                        mListAppoint.addAll(result.dataList);
                        adapter.notifyDataSetChanged();
                    } else {
                        mLayRefresh.setLoadmoreFinished(true);
                    }
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP) || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(MyAppointmentActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(MyAppointmentActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<EventRequestResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                mLayRefresh.finishLoadmore();
            }
        });
    }
}
