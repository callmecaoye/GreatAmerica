package com.promeets.android.activity;

import com.promeets.android.api.PaymentApi;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.promeets.android.adapter.ListviewHistoryAdapter;
import com.promeets.android.api.URL;
import com.bumptech.glide.Glide;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.WithdrawFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.promeets.android.Constant;
import com.promeets.android.object.HistoryPOJO;
import com.promeets.android.pojo.HistoryResp;
import com.promeets.android.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.UserInfoHelper;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * This is for balance management and withdraw
 *
 * including a ListView of balance history and WithDrawFragment
 *
 * @source: ExpertDashboardActivity(expert) / AccountFragment(user)
 *
 * @destination: AddEmailActivity, CustomerServiceActivity, AppointStatusActivity
 *
 */

public class BillManagementActivity extends BaseActivity {

    ListviewHistoryAdapter adapter;
    List<HistoryPOJO> historyList = new ArrayList<>();
    List<String> emails = new ArrayList<>();
    int userId;
    int pageNumber = 1;
    /**
     * Check is balance > 0
     */
    boolean mIsWithdrawAvailable = false;

    @BindView(R.id.photo)
    CircleImageView mImgPhoto;
    @BindView(R.id.activity_bill_management_total_amount)
    TextView tv_total_amount;
    @BindView(R.id.activity_bill_management_withdraw)
    TextView btn_withdraw;
    @BindView(R.id.activity_bill_management_refund)
    TextView btn_refund;
    @BindView(R.id.place_holder)
    TextView mViewHolder;
    /**
     * ListView with sticky header to show history status
     */
    @BindView(R.id.activity_bill_management_history_list)
    StickyListHeadersListView lv_history;

    /**
     * RefreshLayout for StickyListHeadersListView
     */
    @BindView(R.id.activity_bill_management_refresh_layout)
    SmartRefreshLayout ly_refresh;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsWithdrawAvailable)
                    fetchEmail();
                else {
                    PromeetsDialog.show(BillManagementActivity.this, "The total amount is $0");
                }
            }
        });
        btn_refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BillManagementActivity.this, CustomerServiceActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        ly_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(500);
            }
        });
        ly_refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                fetchHistory();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_management);
        ButterKnife.bind(this);

        UserInfoHelper helper = new UserInfoHelper(this);
        userId = helper.getUserObject().id;


        adapter = new ListviewHistoryAdapter(BillManagementActivity.this, historyList);
        lv_history.setDrawingListUnderStickyHeader(true);
        lv_history.setAreHeadersSticky(true);
        lv_history.setAdapter(adapter);
        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lv_history.setEnabled(false);
                ly_refresh.setEnabled(false);

                HistoryPOJO history = historyList.get(position);
                int eventRequestId = history.eventRequestId;
                Intent intent = new Intent(BillManagementActivity.this, AppointStatusActivity.class);
                intent.putExtra("eventRequestId", eventRequestId);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        fetchHistory();
    }

    private void fetchHistory() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("displayPayment/history"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PaymentApi payment = retrofit.create(PaymentApi.class);
        Call<HistoryResp> call = payment.fetchHistory(userId, TimeZone.getDefault().getID(), pageNumber);

        call.enqueue(new Callback<HistoryResp>() {
            @Override
            public void onResponse(Call<HistoryResp> call, Response<HistoryResp> response) {
                PromeetsDialog.hideProgress();
                ly_refresh.finishLoadmore();
                HistoryResp rep = response.body();
                if (rep != null && rep.info != null && rep.info.code.equals("200")) {
                    if (!StringUtils.isEmpty(rep.smallphotoUrl))
                        Glide.with(BillManagementActivity.this).load(rep.smallphotoUrl).into(mImgPhoto);
                    tv_total_amount.setText(NumberFormatUtil.getInstance().getCurrencyTest(rep.displayAmount));

                    if (!rep.displayAmount.equals("0.00")
                            && !rep.displayAmount.equals("0.0")
                            && !rep.displayAmount.equals("0")) {
                        mIsWithdrawAvailable = true;
                    } else mIsWithdrawAvailable = false;

                    if (rep.dataList != null && rep.dataList.size() > 0) {
                        historyList.addAll(rep.dataList);
                        adapter.notifyDataSetChanged();
                        pageNumber++;
                    } else {
                        ly_refresh.setLoadmoreFinished(true);
                        if (pageNumber == 1) {
                            ly_refresh.setVisibility(View.GONE);
                            mViewHolder.setVisibility(View.VISIBLE);
                        }
                    }
                } else if(rep != null && rep.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || rep.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || rep.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(BillManagementActivity.this,rep.info.code);
                }
            }

            @Override
            public void onFailure(Call<HistoryResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                ly_refresh.finishLoadmore();
            }
        });
    }

    private void popupExpAccountDialog() {
        WithdrawFragment dialogFragment = WithdrawFragment.newInstance(userId, emails);
        dialogFragment.show(getSupportFragmentManager(), "withdraw");
    }

    private void fetchEmail() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        emails.clear();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("expertwithdraw/fetchExpAccount"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PaymentApi payment = retrofit.create(PaymentApi.class);
        Call<JsonObject> call = payment.fetchEmail(userId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject json = response.body();
                if (json == null) return;

                JsonObject info = json.get("info").getAsJsonObject();
                String code = info.get("code").getAsString();
                if (code.equals("200")) {
                    JsonArray accountList = json.getAsJsonArray("accountList");
                    if (accountList != null && accountList.size() > 0) {
                        for (int i = 0; i < accountList.size(); i++) {
                            JsonObject account = (JsonObject) accountList.get(i);
                            emails.add(account.get("email").getAsString());
                        }
                    }
                    popupExpAccountDialog();
                } else if(code.equals(Constant.RELOGIN_ERROR_CODE) || code.equals(Constant.UPDATE_TIME_STAMP) || code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(BillManagementActivity.this,code);
                } else {
                    PromeetsDialog.show(BillManagementActivity.this, info.get("description").getAsString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
