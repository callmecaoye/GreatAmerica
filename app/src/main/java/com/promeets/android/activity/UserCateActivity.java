package com.promeets.android.activity;

import com.promeets.android.api.CategoryApi;
import com.promeets.android.api.URL;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import com.promeets.android.Constant;
import android.os.Bundle;
import com.promeets.android.pojo.CategoryResp;
import com.promeets.android.pojo.PollingPost;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.promeets.android.object.SubCate;
import com.promeets.android.util.Utility;

import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;

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

public class UserCateActivity extends BaseActivity implements View.OnClickListener{

    static final int REQUEST_POLLING = 1;

    @BindView(R.id.save)
    TextView mTxtSave;
    @BindView(R.id.add_industry)
    ImageView mImgAdd;
    @BindView(R.id.flexBox)
    FlexboxLayout mFlexBox;

    private ArrayList<SubCate> industryList;
    private Gson gson = new Gson();

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtSave.setOnClickListener(this);
        mImgAdd.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cate);
        ButterKnife.bind(this);

        industryList = getIntent().getParcelableArrayListExtra("industryList");
        updateSave();
        createSubcateTag(industryList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                submitPolling();
                break;
            case R.id.add_industry:
                Intent intent = new Intent(this, PollingActivity.class);
                if (industryList != null && industryList.size() > 0) {
                    intent.putExtra("industryList", industryList);
                }
                startActivityForResult(intent, REQUEST_POLLING);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POLLING:
                    if (industryList != null) industryList.clear();
                    industryList = data.getParcelableArrayListExtra("industryList");
                    updateSave();
                    createSubcateTag(industryList);
                    break;
            }
        }
    }

    private void updateSave() {
        if (industryList != null && industryList.size() > 0) {
            mTxtSave.setTextColor(getResources().getColor(R.color.primary));
            mTxtSave.setEnabled(true);
        } else {
            mTxtSave.setTextColor(getResources().getColor(R.color.pm_light));
            mTxtSave.setEnabled(false);
        }
    }

    private void createSubcateTag(final ArrayList<SubCate> list) {
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.convertDpToPx(4, this),
                ScreenUtil.convertDpToPx(5, this),
                ScreenUtil.convertDpToPx(4, this),
                ScreenUtil.convertDpToPx(5, this));
        mFlexBox.removeAllViewsInLayout();
        if (list != null && list.size() > 0) {
            for (final SubCate subCate : list) {
                final TextView mTxtTag = new TextView(this);
                mTxtTag.setLayoutParams(lp);
                mTxtTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_primary, 0);
                mTxtTag.setCompoundDrawablePadding(ScreenUtil.convertDpToPx(10, this));
                mTxtTag.setText(subCate.getTitle());
                mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                mTxtTag.setTextColor(Color.WHITE);
                mTxtTag.setGravity(Gravity.CENTER_VERTICAL);
                mTxtTag.setPadding(ScreenUtil.convertDpToPx(15, this),
                        ScreenUtil.convertDpToPx(5, this),
                        ScreenUtil.convertDpToPx(15, this),
                        ScreenUtil.convertDpToPx(5, this));
                mTxtTag.setBackgroundResource(R.drawable.tag_solid_primary);
                mFlexBox.addView(mTxtTag);
                mTxtTag.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP) {
                            if(event.getRawX() >= mTxtTag.getRight() - mTxtTag.getTotalPaddingRight()) {
                                // drawableRight click event
                                mFlexBox.removeView(mTxtTag);
                                mFlexBox.requestLayout();
                                list.remove(subCate);
                                subCate.setSelect(false);
                                updateSave();
                                return true;
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void submitPolling() {
        if (industryList == null || industryList.size() == 0) return;
        ArrayList<Integer> industryIdList = new ArrayList<>();
        for (SubCate subCate : industryList)
            industryIdList.add(subCate.getId());

        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("polling/update"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PollingPost post = new PollingPost();
        post.industryIdList = industryIdList;
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
                    Utility.onServerHeaderIssue(UserCateActivity.this, result.info.code);
                } else {
                    PromeetsDialog.show(UserCateActivity.this, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<CategoryResp> call, Throwable t) {
                PromeetsDialog.show(UserCateActivity.this, t.getLocalizedMessage());
            }
        });
    }
}
