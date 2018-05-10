package com.promeets.android.activity;

import com.promeets.android.api.ServiceApi;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.pojo.ActiveEventResp;
import com.promeets.android.R;
import com.promeets.android.util.ServiceResponseHolder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventQuesActivity extends BaseActivity {

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    @BindView(R.id.submit)
    TextView mBtnSubmit;

    @BindView(R.id.que1)
    TextView mTxtQ1;

    @BindView(R.id.answer1)
    EditText mTxtAns1;

    @BindView(R.id.radio_group)
    RadioGroup mRGDrink;

    String eventId;
    String que1;

    @Override
    public void initElement() {
        eventId = getIntent().getStringExtra("eventId");
        que1 = getIntent().getStringExtra("q1");
        if (StringUtils.isEmpty(eventId) || StringUtils.isEmpty(que1)) {
            finish();
            return;
        }
        mTxtQ1.setText(que1);
    }

    @Override
    public void registerListeners() {
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ans1 = mTxtAns1.getText().toString();
                String ans2 = getRadioText();
                if (!StringUtils.isEmpty(ans1.trim())
                        && !StringUtils.isEmpty(ans2)) {
                    submitYes(ans1, ans2);
                } else
                    PromeetsDialog.show(EventQuesActivity.this, "Please fill in all fields");
            }
        });

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtAns1.clearFocus();
                hideSoftKeyboard();
            }
        });

        mTxtAns1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_ques);
        ButterKnife.bind(this);
    }

    /**
     * submit "Going to event"
     */
    private void submitYes(String ans1, String ans2) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("activeEvent/updateGoing"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);
        JSONObject json = new JSONObject();
        try {
            json.put("eventId", eventId);
            json.put("goingFlag", 1);
            json.put("answer1", ans1);
            json.put("answer2", ans2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Call<ActiveEventResp> call = service.updateGoing(requestBody);
        call.enqueue(new Callback<ActiveEventResp>() {
            @Override
            public void onResponse(Call<ActiveEventResp> call, Response<ActiveEventResp> response) {
                final ActiveEventResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(EventQuesActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("state", result.goingFlag);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else
                    PromeetsDialog.show(EventQuesActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<ActiveEventResp> call, Throwable t) {

            }
        });
    }

    private String getRadioText() {
        int selectedId = mRGDrink.getCheckedRadioButtonId();
        RadioButton mRButton = findViewById(selectedId);
        return mRButton.getText().toString();
    }
}
