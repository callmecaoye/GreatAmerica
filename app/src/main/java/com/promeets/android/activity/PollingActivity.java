package com.promeets.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.api.CategoryApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PollingView;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.Gson;
import com.promeets.android.object.SubCate;
import com.promeets.android.Constant;
import com.promeets.android.pojo.CategoryResp;
import com.promeets.android.pojo.PollingPost;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * After user logged in at first time,
 * show this page and let user to choose tags
 *
 * @source: HomepageFragment
 *
 */
public class PollingActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;
    @BindView(R.id.save)
    TextView mTxtSave;

    private ArrayList<PollingView> viewList = new ArrayList<>();
    private ArrayList<SubCate> industryList;
    private Gson gson = new Gson();
    private boolean firstStart;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtSave.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling);
        ButterKnife.bind(this);

        firstStart = getIntent().getBooleanExtra("firstStart", false);
        if (!firstStart)
            industryList = getIntent().getParcelableArrayListExtra("industryList");

        fetchPolling();
    }

    private void fetchPolling() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("polling/fetchAll"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CategoryApi service = retrofit.create(CategoryApi.class);
        Call<CategoryResp> call = service.fetchAllPolling();//get request, need to be post!

        call.enqueue(new Callback<CategoryResp>() {
            @Override
            public void onResponse(Call<CategoryResp> call, Response<CategoryResp> response) {
                PromeetsDialog.hideProgress();
                CategoryResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(PollingActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    for (int i = 0; i < result.categoryList.length; i++) {
                        if (result.categoryList[i].getTitle().equals("All")) continue;
                        if (!firstStart) {
                            for (SubCate subCate : result.categoryList[i].getList())
                                if (getSelIdList().contains(subCate.getId()))
                                    subCate.setSelect(true);
                        }

                        PollingView view = new PollingView(PollingActivity.this, result.categoryList[i]);
                        if (i == result.categoryList.length - 2)
                            view.setPadding(0, 0, 0, ScreenUtil.convertDpToPx(30, PollingActivity.this));
                        mLayRoot.addView(view);
                        viewList.add(view);

                        if (!firstStart) updateSave();
                    }
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP)|| result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(PollingActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(PollingActivity.this, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<CategoryResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(PollingActivity.this, t.getLocalizedMessage());
            }
        });
    }

    public void updateSave() {
        boolean hasSel = false;
        for (PollingView view : viewList) {
            if (view.getSelIdList().size() > 0) {
                hasSel = true;
                break;
            }
        }
        if (hasSel) {
            mTxtSave.setTextColor(getResources().getColor(R.color.primary));
            mTxtSave.setEnabled(true);
        } else {
            mTxtSave.setTextColor(getResources().getColor(R.color.pm_light));
            mTxtSave.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                if (firstStart)
                    submitPolling();
                else {
                    if (industryList != null && industryList.size() > 0)
                        industryList.clear();
                    else
                        industryList = new ArrayList<>();

                    for (PollingView view : viewList) {
                        if (view.getSelList().size() > 0)
                            industryList.addAll(view.getSelList());
                    }

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("industryList", industryList);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                break;
        }
    }

    private void submitPolling() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        ArrayList<Integer> mSubcateList = new ArrayList<>();
        for (PollingView view : viewList) {
            if (view.getSelIdList().size() > 0) {
                mSubcateList.addAll(view.getSelIdList());
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("polling/update"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PollingPost post = new PollingPost();
        post.industryIdList = mSubcateList;
        String json = gson.toJson(post);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        CategoryApi service = retrofit.create(CategoryApi.class);
        Call<CategoryResp> call = service.updatePolling(requestBody);//get request, need to be post!

        call.enqueue(new Callback<CategoryResp>() {
            @Override
            public void onResponse(Call<CategoryResp> call, Response<CategoryResp> response) {
                CategoryResp result = response.body();
                if (isSuccess(result.info.code)) {
                    finish();
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP)|| result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(PollingActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(PollingActivity.this, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<CategoryResp> call, Throwable t) {
                PromeetsDialog.show(PollingActivity.this, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (firstStart)
            PromeetsDialog.show(this, "To continue, please select at least one tag and click Save");
        else
            super.onBackPressed();
    }

    public ArrayList<Integer> getSelIdList() {
        ArrayList<Integer> result = new ArrayList<>();
        if (industryList != null && industryList.size() > 0) {
            for (SubCate subCate : industryList)
                result.add(subCate.getId());
        }
        return result;
    }
}
