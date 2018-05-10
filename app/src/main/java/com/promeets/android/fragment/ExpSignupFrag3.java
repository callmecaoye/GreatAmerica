package com.promeets.android.fragment;

import com.promeets.android.activity.ExpSignUpActivity;
import com.promeets.android.activity.WeekViewActivity;
import com.promeets.android.adapter.TagAdapter;
import com.promeets.android.api.CategoryApi;
import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.NoScrollListView;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.object.Category;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.SubCate;
import android.os.Bundle;
import com.promeets.android.pojo.CategoryResp;
import com.promeets.android.pojo.DatalistResp;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.promeets.android.R;

import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sosasang on 1/26/18.
 */

public class ExpSignupFrag3 extends Fragment {

    static final int WEEKVIEW_REQUEST_CODE = 100;

    @BindView(R.id.next_txt)
    public TextView mTxtNext;
    @BindView(R.id.root_layout)
    View mLayRoot;
    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;

    @BindView(R.id.topic_img)
    ImageView mImgTopic;
    @BindView(R.id.demo_topic_lay)
    View mLayDemoTopic;
    @BindView(R.id.topic_txt)
    TextView mTxtTopic;

    @BindView(R.id.cate_lay)
    View mLayCate;
    @BindView(R.id.cate_txt)
    TextView mTxtCate;
    @BindView(R.id.tag_txt)
    EditText mTxtTag;

    @BindView(R.id.suggest_list)
    NoScrollListView mLVSuggest;
    @BindView(R.id.tag_list)
    NoScrollListView mLVTag;
    @BindView(R.id.add_tag)
    TextView mTxtAddTag;

    @BindView(R.id.book_lay)
    View mLayBook;
    @BindView(R.id.workshop_txt)
    TextView mTxtWorkshop;

    private ExpSignUpActivity mActivity;
    private ExpertProfilePOJO draftExp;

    private ArrayList<String> mListSuggest;
    private ArrayAdapter<String> mAdapterSug;
    private ArrayList<String> mListTag;
    private TagAdapter mAdapterTag;

    private String mStrEvent;
    private boolean isScroll;
    private SubCate subCate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ExpSignUpActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag3_exp_signup, container, false);
        ButterKnife.bind(this, view);

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideKeyboard();
            }
        });
        mTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) return;

                mActivity.mViewpager.setCurrentItem(3);
                mActivity.curPage = 3;
            }
        });

        mImgTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideKeyboard();
                mLayDemoTopic.setVisibility(View.VISIBLE);
            }
        });
        mLayDemoTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayDemoTopic.setVisibility(View.GONE);
            }
        });
        mLayBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, WeekViewActivity.class);
                intent.putExtra("what", 0);
                startActivityForResult(intent, WEEKVIEW_REQUEST_CODE);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        mTxtWorkshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, WeekViewActivity.class);
                intent.putExtra("eventToCalendar", mStrEvent);
                intent.putExtra("what", 0);
                startActivityForResult(intent, WEEKVIEW_REQUEST_CODE);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mTxtAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = mTxtTag.getEditableText().toString();
                mTxtTag.setText("");
                if (mListTag.contains(tag)) {
                    PromeetsDialog.show(mActivity, tag + " is already added to list");
                    return;
                }
                mListTag.add(tag);
                mAdapterTag.notifyDataSetChanged();
            }
        });

        mLayCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchCategory();
            }
        });

        mTxtTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mLVSuggest.setVisibility(View.GONE);
                mTxtAddTag.setVisibility(View.GONE);
                String keyword = editable.toString().trim();
                if (!StringUtils.isEmpty(keyword)) {
                    mTxtAddTag.setVisibility(View.VISIBLE);
                    suggestKeyword(keyword);
                }
            }
        });

        mListSuggest = new ArrayList<>();
        mAdapterSug = new ArrayAdapter<>(mActivity, R.layout.list_suggest_item, mListSuggest);
        mLVSuggest.setAdapter(mAdapterSug);
        mLVSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mTxtTag.setText("");
                String tag = mListSuggest.get(i);
                if (mListTag.contains(tag)) {
                    PromeetsDialog.show(mActivity, tag + " is already added to list");
                    return;
                }
                mListTag.add(tag);
                mAdapterTag.notifyDataSetChanged();
            }
        });

        mListTag = new ArrayList<>();
        mAdapterTag = new TagAdapter(mActivity, mListTag);
        mLVTag.setAdapter(mAdapterTag);
        return view;
    }

    /**
     * Get selected time data
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WEEKVIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            Gson gson = new Gson();
            mStrEvent = data.getStringExtra("eventFromCalendar");
            if (StringUtils.isEmpty(mStrEvent)) {
                mLayBook.setVisibility(View.VISIBLE);
                mTxtWorkshop.setVisibility(View.GONE);
                return;
            }

            WeekViewEvent mEvent = gson.fromJson(mStrEvent, WeekViewEvent.class);
            draftExp.workshopTime = mEvent.getStartTime().getTimeInMillis() / 1000;

            SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy hh:mm aa", Locale.US);
            String fromTime = timeFormat.format(mEvent.getStartTime().getTime());
            mLayBook.setVisibility(View.GONE);
            mTxtWorkshop.setVisibility(View.VISIBLE);
            mTxtWorkshop.setText(fromTime);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            draftExp = mActivity.getDraftExp();
            if (!StringUtils.isEmpty(draftExp.topicTitle))
                mTxtTopic.setText(draftExp.topicTitle);
            if (!StringUtils.isEmpty(draftExp.categories) && subCate != null)
                mTxtCate.setText(subCate.getCategoryTitle() + ", " + subCate.getTitle());
            if (!StringUtils.isEmpty(draftExp.tags)) {
                mListTag.clear();
                String[] arrTag = draftExp.tags.split(",");
                for (int i = 0; i < arrTag.length; i++)
                    mListTag.add(arrTag[i]);
                mAdapterTag.notifyDataSetChanged();
            }

            if (draftExp.workshopTime > 0) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy hh:mm aa", Locale.US);
                String fromTime = timeFormat.format(draftExp.workshopTime * 1000);
                mLayBook.setVisibility(View.GONE);
                mTxtWorkshop.setVisibility(View.VISIBLE);
                mTxtWorkshop.setText(fromTime);
            }
        }
    }

    private void fetchCategory() {
        if (!mActivity.hasInternetConnection()) {
            PromeetsDialog.show(mActivity, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mActivity);
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
                    PromeetsDialog.show(mActivity, response.errorBody().toString());
                    return;
                }

                if (mActivity.isSuccess(result.info.code)) {
                    if (result.categoryList == null || result.categoryList.length == 0)
                        return;

                    final ArrayList<Category> cateList = new ArrayList<>();
                    for (int i = 0; i < result.categoryList.length; i++) {
                        if (result.categoryList[i].getTitle().equalsIgnoreCase("all"))
                            continue;
                        cateList.add(result.categoryList[i]);
                    }
                    final ExpCateFragment cateFragment = ExpCateFragment.newInstance(cateList);
                    cateFragment.show(mActivity.getFragmentManager(), "cate");
                    cateFragment.setCancelable(true);
                    cateFragment.setCallback(new ExpCateFragment.Callback() {
                        @Override
                        public void OnCateSelect(int i) {
                            cateFragment.dismiss();
                            final ArrayList<SubCate> subList = cateList.get(i).getList();
                            final ExpSubcateFragment subcateFragment = ExpSubcateFragment.newInstance(subList);
                            subcateFragment.show(mActivity.getFragmentManager(), "subcate");
                            subcateFragment.setCancelable(true);
                            subcateFragment.setCallback(new ExpSubcateFragment.Callback() {
                                @Override
                                public void OnSubcateSelect(int i) {
                                    subcateFragment.dismiss();
                                    subCate = subList.get(i);
                                    mTxtCate.setText(subCate.getCategoryTitle() + ", " + subCate.getTitle());
                                    draftExp.categories = String.valueOf(subCate.getId());
                                }
                            });
                        }
                    });
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(mActivity, result.info.code);
                } else
                    PromeetsDialog.show(mActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<CategoryResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(mActivity, t.getLocalizedMessage());
            }
        });
    }

    private void suggestKeyword(String keyword) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("search/getSuggestion"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        Call<DatalistResp> call = service.getSuggestion(keyword);

        call.enqueue(new Callback<DatalistResp>() {
            @Override
            public void onResponse(Call<DatalistResp> call, Response<DatalistResp> response) {
                DatalistResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mActivity, response.errorBody().toString());
                    return;
                }

                if (mActivity.isSuccess(result.info.code)) {
                    if (result.dataList == null || result.dataList.size() == 0)
                        return;

                    if (!isScroll) {
                        isScroll = true;
                        mScrollView.scrollBy(0, ScreenUtil.convertDpToPx(130, mActivity));
                    }

                    mLVSuggest.setVisibility(View.VISIBLE);
                    mListSuggest.clear();
                    mListSuggest.addAll(result.dataList);
                    mAdapterSug.notifyDataSetChanged();
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(mActivity, result.info.code);
                } else
                    PromeetsDialog.show(mActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<DatalistResp> call, Throwable t) {
                PromeetsDialog.show(mActivity, t.getLocalizedMessage());
            }
        });
    }

    private boolean validate() {
        draftExp.topicTitle = mTxtTopic.getText().toString();
        if (StringUtils.isEmpty(draftExp.topicTitle)) {
            mTxtTopic.setError("This field is required");
            return false;
        } else {
            mTxtTopic.setError(null);
        }

        if (mListTag != null && mListTag.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String tag : mListTag) {
                sb.append(tag + ",");
            }
            draftExp.tags = sb.toString().substring(0, sb.length() - 1);
        }
        return true;
    }
}
