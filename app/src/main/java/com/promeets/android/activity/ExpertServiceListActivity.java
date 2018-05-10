package com.promeets.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.promeets.android.adapter.ListviewExpertServiceAdapter;
import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.Gson;
import com.promeets.android.object.ExpertService;
import com.promeets.android.pojo.ServiceResp;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.Utility;
import com.promeets.android.Constant;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.R;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.ServiceResponseHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for showing a list of expert topics
 *
 * @source: ExpertDashboardActivity
 *
 * @destination: ExpertServiceActivity
 *
 */
public class ExpertServiceListActivity extends BaseActivity {

    @BindView(R.id.add_topic)
    TextView mTxtAdd;
    @BindView(R.id.service_list)
    ListView mLVService;

    private ArrayList<ExpertService> expServiceList;
    private ListviewExpertServiceAdapter adapter;
    private ExpertProfilePOJO expertProfile;
    private Gson gson = new Gson();

    @Override
    public void initElement() {
        refreshList();
    }

    @Override
    public void registerListeners() {
        mTxtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpertServiceListActivity.this, ExpertServiceActivity.class);
                intent.putExtra("expId", expertProfile.expId);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_service_list);
        ButterKnife.bind(this);

        expertProfile = (ExpertProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY, ExpertProfilePOJO.class);
        if (expertProfile == null) finish();

        expServiceList = new ArrayList<>();
        adapter = new ListviewExpertServiceAdapter(this, expServiceList);
        mLVService.setAdapter(adapter);
        mLVService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpertService service = expServiceList.get(position);
                String json = gson.toJson(service);
                Intent intent = new Intent(ExpertServiceListActivity.this, ExpertServiceActivity.class);
                intent.putExtra("expId", service.expId);
                intent.putExtra("servicePOJO", json);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void refreshList() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("expertservice/fetchbyexpid"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        Call<ServiceResp> call = service.fetchbyexpid(expertProfile.expId);
        call.enqueue(new Callback<ServiceResp>() {
            @Override
            public void onResponse(Call<ServiceResp> call, Response<ServiceResp> response) {
                PromeetsDialog.hideProgress();
                expServiceList.clear();
                ServiceResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(ExpertServiceListActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    if (result.expertservice != null && result.expertservice.size() > 0) {
                        expServiceList.addAll(result.expertservice);
                    }
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP) || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(ExpertServiceListActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(ExpertServiceListActivity.this, result.info.description);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ServiceResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertServiceListActivity.this, t.getLocalizedMessage());
            }
        });
    }
}
