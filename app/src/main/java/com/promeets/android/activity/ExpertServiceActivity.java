package com.promeets.android.activity;

import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.EditTextWithPrefix;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.object.ExpertService;
import android.os.Bundle;
import com.promeets.android.pojo.ServicePost;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.promeets.android.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

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
 * This is for showing expert topic detail
 *
 * Experts can add and edit their topic here
 *
 * @source: ExpertServiceListActivity
 *
 */
public class ExpertServiceActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.title_topic)
    TextView mTitleTopic;
    @BindView(R.id.txt_topic)
    EditText mTxtTopic;
    @BindView(R.id.duration)
    BetterSpinner mSpinnerDuration;
    @BindView(R.id.rate_txt)
    EditTextWithPrefix mTxtPrice;
    @BindView(R.id.title_desc)
    TextView mTitleDesc;
    @BindView(R.id.txt_desc)
    EditText mTxtDesc;
    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;
    @BindView(R.id.finish)
    TextView mTxtFinish;

    private int expId;
    private ExpertService servicePOJO;
    private Gson gson = new Gson();
    private String[] list;
    private ArrayAdapter<String> adapter;
    private int spinnerSelected = 0;

    @Override
    public void initElement() {
        // init service for existing service(read only)
        expId = getIntent().getIntExtra("expId", 0);
        String json = getIntent().getStringExtra("servicePOJO");
        servicePOJO = gson.fromJson(json, ExpertService.class);
        if (servicePOJO != null) {
            mTxtTopic.setText(servicePOJO.title);
            switch (servicePOJO.duratingTime) {
                case "30":
                    mSpinnerDuration.setText(adapter.getItem(0));
                    spinnerSelected = 0;
                    break;
                case "60":
                    mSpinnerDuration.setText(adapter.getItem(1));
                    spinnerSelected = 1;
                    break;
                case "90":
                    mSpinnerDuration.setText(adapter.getItem(2));
                    spinnerSelected = 2;
                    break;
                case "120":
                    mSpinnerDuration.setText(adapter.getItem(3));
                    spinnerSelected = 3;
                    break;
                case "150":
                    mSpinnerDuration.setText(adapter.getItem(4));
                    spinnerSelected = 4;
                    break;
                case "180":
                    mSpinnerDuration.setText(adapter.getItem(5));
                    spinnerSelected = 5;
                    break;
                case "210":
                    mSpinnerDuration.setText(adapter.getItem(6));
                    spinnerSelected = 6;
                    break;
                case "240":
                    mSpinnerDuration.setText(adapter.getItem(7));
                    spinnerSelected = 7;
                    break;
                case "270":
                    mSpinnerDuration.setText(adapter.getItem(8));
                    spinnerSelected = 8;
                    break;
                case "300":
                    mSpinnerDuration.setText(adapter.getItem(9));
                    spinnerSelected = 9;
                    break;
                case "330":
                    mSpinnerDuration.setText(adapter.getItem(10));
                    spinnerSelected = 10;
                    break;
                case "360":
                    mSpinnerDuration.setText(adapter.getItem(11));
                    spinnerSelected = 11;
                    break;
            }
            mTxtPrice.setText(servicePOJO.price);
            mTxtDesc.setText(servicePOJO.description);

            if (servicePOJO.serviceStatus == 1) { // pending service
                mTxtTopic.setEnabled(false);
                mSpinnerDuration.setEnabled(false);
                mTxtPrice.setEnabled(false);
                mTxtDesc.setEnabled(false);
                mTxtFinish.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void registerListeners() {
        mLayRoot.setOnClickListener(this);
        mTxtFinish.setOnClickListener(this);
        mTxtTopic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTitleTopic.requestFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mTxtDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTitleDesc.requestFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_service);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);

        list = getResources().getStringArray(R.array.duration_arrays);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        mSpinnerDuration.setAdapter(adapter);
        mSpinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_layout:
                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                hideSoftKeyboard();
                break;
            case R.id.finish:
                if (!valid()) return;
                createService();
                mTxtFinish.setEnabled(false);
                break;
        }
    }

    private void createService() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        ServicePost postPOJO = new ServicePost();
        ExpertService servicePOJO = new ExpertService();
        // update service with existing id
        if (this.servicePOJO != null) {
            servicePOJO.id = this.servicePOJO.id;
        }
        servicePOJO.description = mTxtDesc.getText().toString();
        servicePOJO.title = mTxtTopic.getText().toString();
        servicePOJO.price = mTxtPrice.getText().toString();
        Double durHour = Double.valueOf(list[spinnerSelected].split(" ")[0]);
        servicePOJO.duratingTime = String.valueOf((int) (durHour * 60));
        servicePOJO.expId = expId;
        postPOJO.expertservice = servicePOJO;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("expertservice/updateALL"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String postJson = gson.toJson(postPOJO);

        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), postJson);

        Call<SuperResp> call = service.createService(requestBody);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(ExpertServiceActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    finish();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(ExpertServiceActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(ExpertServiceActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertServiceActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private boolean valid() {
        if (mTxtTopic.getText().toString().trim().length() == 0) {
            PromeetsDialog.show(this, "Topic cannot be empty");
            return false;
        }
        if (mTxtPrice.getText().toString().trim().length() == 0) {
            PromeetsDialog.show(this, "Price cannot be empty");
            return false;
        }
        if (mTxtDesc.getText().toString().trim().length() == 0) {
            PromeetsDialog.show(this, "Description cannot be empty");
            return false;
        }
        return true;
    }
}
