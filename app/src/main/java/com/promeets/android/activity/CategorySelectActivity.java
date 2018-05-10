package com.promeets.android.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;

import com.promeets.android.api.CategoryApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.CategoryView;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.object.SubCate;
import com.promeets.android.pojo.CategoryResp;
import com.promeets.android.R;
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

public class CategorySelectActivity extends BaseActivity {
    @BindView(R.id.activity_all_categories_new_layout)
    LinearLayout ly_main;
    @BindView(R.id.activity_all_categories_new_submit)
    FloatingActionButton btn_submit;

    private ArrayList<SubCate> industryList = new ArrayList<>();
    private ArrayList<CategoryView> cateViewList = new ArrayList<>();

    Typeface tf_semi;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                industryList.clear();
                for (CategoryView view : cateViewList) {
                    if (view.getSelList() != null && view.getSelList().size() > 0)
                        industryList.addAll(view.getSelList());
                }
                Intent intent = new Intent();
                intent.putExtra("industryList", industryList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);
        ButterKnife.bind(this);
        tf_semi = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");

        if ((getIntent().getSerializableExtra("industryList")) != null) {
            industryList = getIntent().getParcelableArrayListExtra("industryList");
        }
        fetchCategory();
    }

    private void fetchCategory() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("contentCategory/fetchAllV2"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CategoryApi service = retrofit.create(CategoryApi.class);
        Call<CategoryResp> call = service.fetchAllCategory();//get request, need to be post!

        call.enqueue(new Callback<CategoryResp>() {
            @Override
            public void onResponse(Call<CategoryResp> call, Response<CategoryResp> response) {
                PromeetsDialog.hideProgress();
                final CategoryResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(CategorySelectActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    for (int i = 0; i < result.categoryList.length; i++) {
                        if (result.categoryList[i].getTitle().equals("All")) continue;
                        CategoryView view = new CategoryView(CategorySelectActivity.this, result.categoryList[i]);
                        cateViewList.add(view);
                        ly_main.addView(view);
                    }
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(CategorySelectActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(CategorySelectActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<CategoryResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(CategorySelectActivity.this, t.getLocalizedMessage());
            }
        });
    }

    public ArrayList<Integer> getSelectId() {
        ArrayList<Integer> result = new ArrayList<>();
        //for (Category cate: industryList) {
            for (SubCate subCate : industryList) {
                result.add(subCate.getId());
            }
        //}
        return result;
    }
}
