package com.promeets.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.ExpSignupFrag2;
import com.promeets.android.fragment.ExpSignupFrag4;
import com.linkedin.platform.listeners.ApiListener;
import com.promeets.android.object.ExpertProfilePOJO;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.adapter.FragAdapter;
import com.promeets.android.fragment.ExpSignupFrag1;
import com.promeets.android.fragment.ExpSignupFrag3;
import com.promeets.android.fragment.ExpSignupFrag5;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.internals.BuildConfig;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;
import com.promeets.android.util.AndroidBug5497Workaround;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Become Expert process
 *
 * Viewpager + Fragment
 *
 */
public class ExpSignUpActivity extends BaseActivity implements View.OnClickListener{

    private static final int LINKEDIN_WEB_REQUEST = 200;
    private static final String OAUTH_ACCESS_TOKEN_PARAM = "oauth2_access_token";
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS = "=";
    private static final String host = "api.linkedin.com";
    private static final String url = "https://" + host
            + "/v1/people/~"
            //+ ":(picture-urls::(original),formatted-name,email-address,public-profile-url,location,positions,summary)";
            + ":(picture-urls::(original),formatted-name,email-address,positions)";

    @BindView(R.id.view_pager)
    public ViewPager mViewpager;
    @BindView(R.id.toolbar_title)
    TextView mTxtTitle;

    @BindView(R.id.back)
    public ImageView mBack;
    @BindView(R.id.next)
    public ImageView mNext;

    @BindView(R.id.step1)
    View step1;
    @BindView(R.id.step2)
    View step2;
    @BindView(R.id.step3)
    View step3;
    @BindView(R.id.step4)
    View step4;
    @BindView(R.id.step5)
    View step5;

    @BindView(R.id.li_skip)
    TextView mLISkip;
    @BindView(R.id.li_back)
    ImageView mLIBack;
    @BindView(R.id.li_lay)
    LinearLayout mLayLI;
    @BindView(R.id.li_import)
    TextView mLIImport;
    @BindView(R.id.info_lay)
    LinearLayout mLayInfo;
    @BindView(R.id.thank_lay)
    View mLayThank;

    private FragAdapter mAdapter;
    private List<Fragment> mList;
    private Animation leftOut;
    private ProgressDialog dialog;
    public int curPage;
    public UserPOJO user;
    private ExpertProfilePOJO draftExp;

    private ExpSignupFrag1 frag1;
    private ExpSignupFrag2 frag2;
    private ExpSignupFrag3 frag3;
    private ExpSignupFrag4 frag4;
    private ExpSignupFrag5 frag5;

    @Override
    public void initElement() {
        // INVISIBLE animation
        leftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
    }

    @Override
    public void registerListeners() {
        mLayInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
            }
        });

        mLISkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayLI.setVisibility(View.GONE);
                mLayLI.startAnimation(leftOut);
            }
        });

        mLIBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mLIImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importFromLinkedIn();
            }
        });

        mBack.setOnClickListener(this);
        mNext.setOnClickListener(this);

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideKeyboard();
                curPage = position;
                switch (position) {
                    case 4:
                        mTxtTitle.setText("Step 5");
                        step5.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        break;
                    case 3:
                        mTxtTitle.setText("Step 4");
                        step4.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        step5.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                        break;
                    case 2:
                        mTxtTitle.setText("Step 3");
                        step3.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        step4.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                        break;
                    case 1:
                        mTxtTitle.setText("Step 2");
                        step2.setBackground(getResources().getDrawable(R.drawable.step_bar_primary));
                        step3.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                        break;
                    case 0:
                        mTxtTitle.setText("Step 1");
                        step2.setBackground(getResources().getDrawable(R.drawable.step_bar_gray));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_sign_up);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);

        user = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (user == null) return;
        draftExp = new ExpertProfilePOJO();
        mLayLI.setVisibility(View.VISIBLE);

        frag1 = new ExpSignupFrag1();
        frag2 = new ExpSignupFrag2();
        frag3 = new ExpSignupFrag3();
        frag4 = new ExpSignupFrag4();
        frag5 = new ExpSignupFrag5();
        mList = new ArrayList<>();
        mList.add(frag1);
        mList.add(frag2);
        mList.add(frag3);
        mList.add(frag4);
        mList.add(frag5);

        mAdapter = new FragAdapter(getSupportFragmentManager(), mList);
        mViewpager.setAdapter(mAdapter);
        mViewpager.setCurrentItem(0);
        curPage = 0;

        frag5.setCallback(new ExpSignupFrag5.ThankCallback() {
            @Override
            public void showThank() {
                mLayThank.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }
        });
    }

    public void hideKeyboard() {
        hideSoftKeyboard();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                switch (curPage) {
                    case 0:
                        onBackPressed();
                        break;
                    case 1:
                        mViewpager.setCurrentItem(0);
                        curPage = 0;
                        break;
                    case 2:
                        mViewpager.setCurrentItem(1);
                        curPage = 1;
                        break;
                    case 3:
                        mViewpager.setCurrentItem(2);
                        curPage = 2;
                        break;
                    case 4:
                        mViewpager.setCurrentItem(3);
                        curPage = 3;
                        break;
                }
                break;
            case R.id.next:
                switch (curPage) {
                    case 0:
                        frag1.mTxtNext.performClick();
                        break;
                    case 1:
                        frag2.mTxtNext.performClick();
                        break;
                    case 2:
                        frag3.mTxtNext.performClick();
                        break;
                    case 3:
                        frag4.mTxtNext.performClick();
                        break;
                    case 4:
                        frag5.mTxtNext.performClick();
                        break;
                }
                break;
        }
    }

    /**
     * LinkedIn data
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case LINKEDIN_WEB_REQUEST:
                if (data != null) {
                    String mLIToken = data.getStringExtra("accessToken");
                    String profileUrl = url + QUESTION_MARK + OAUTH_ACCESS_TOKEN_PARAM + EQUALS + mLIToken;
                    fetchLinkedInProfileWithToken(profileUrl);
                }
                break;
            case 3672: // LinkedIn APK Auth
                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
                fetchLinkedInProfile();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // return to previous page
            mBack.performClick();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void setPage(int page) {
        mViewpager.setCurrentItem(page);
        curPage = page;
    }

    public ExpertProfilePOJO getDraftExp() {
        return draftExp;
    }

    //region LinkedIn import
    private void importFromLinkedIn() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.linkedin.android", PackageManager.GET_ACTIVITIES);
            if (packageInfo.versionCode >= BuildConfig.LI_APP_SUPPORTED_VER_CODE) {
                // LinkedIn APK installed
                if (GlobalVariable.isLILogin)
                    fetchLinkedInProfile();
                else {
                    LISessionManager.getInstance(getApplicationContext()).init(ExpSignUpActivity.this, Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS), new AuthListener() {
                        @Override
                        public void onAuthSuccess() {
                        }

                        @Override
                        public void onAuthError(LIAuthError error) {
                            PromeetsDialog.show(ExpSignUpActivity.this, "Failed : " + error);
                        }
                    }, false);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            // LinkedIn APK not installed
            Intent intent = new Intent(ExpSignUpActivity.this, AuthActivity.class);
            startActivityForResult(intent, LINKEDIN_WEB_REQUEST);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    private void fetchLinkedInProfile() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(ExpSignUpActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                JSONObject response = result.getResponseDataAsJson();
                setLinkedInInfo(response);
            }

            @Override
            public void onApiError(LIApiError error) {

            }
        });
    }

    private void fetchLinkedInProfileWithToken(String url) {
        dialog = ProgressDialog.show(ExpSignUpActivity.this, "", "Loading...", true);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("x-li-format", "json")
                .url(url)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response result) throws IOException {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (!result.isSuccessful()) {
                    PromeetsDialog.show(ExpSignUpActivity.this, result.message());
                }
                // Read data on the worker thread
                final String responseStr = result.body().string();
                // Run view-related code back on the main thread
                ExpSignUpActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject response = null;
                        try {
                            response = new JSONObject(responseStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (response != null) {
                            setLinkedInInfo(response);
                        }
                    }
                });
            }
        });
    }

    /**
     * picture-urls::(original)
     * formatted-name
     * email-address
     * public-profile-url
     * location
     * positions
     * summary
     */
    private void setLinkedInInfo(JSONObject response) {
        mLayLI.setVisibility(View.GONE);
        mLayLI.startAnimation(leftOut);
        try {
            // email-address,formatted-name,location,positions,picture-urls::(original)
            String name = response.has("formattedName") ? response.get("formattedName").toString() : "";
            String emailAddr = response.has("emailAddress") ? response.get("emailAddress").toString() : "";

            String picUrl = "";
            if (response.has("pictureUrls")) {
                JSONObject url = response.getJSONObject("pictureUrls");
                if (url.has("values"))
                    picUrl = url.getJSONArray("values").get(0).toString();
            }

            String position = "";
            JSONObject posValue;
            if (response.has("positions")) {
                JSONObject pos = response.getJSONObject("positions");
                if (pos.has("values")) {
                    posValue = pos.getJSONArray("values").getJSONObject(0);
                    if (posValue.has("title"))
                        position = posValue.get("title").toString();
                }
            }

            draftExp.contactEmail = emailAddr;
            draftExp.photoUrl = picUrl;
            draftExp.positon = position;
            mCallback.updateLI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    private Callback mCallback;
    public interface Callback {
        void updateLI();
    }
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
}
