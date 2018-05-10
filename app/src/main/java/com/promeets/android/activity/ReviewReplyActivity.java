package com.promeets.android.activity;

import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Typeface;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.ServiceReview;
import android.os.Bundle;
import com.promeets.android.pojo.AllReviewsResp;
import com.promeets.android.pojo.PostReview;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.services.GenericServiceHandler;
import android.text.Editable;
import android.text.TextWatcher;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import android.util.TypedValue;
import com.promeets.android.util.Utility;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is a general activity for expert leave/edit users' reviews
 *
 * accept a ServiceReview instance if for edit
 *
 */
public class ReviewReplyActivity extends BaseActivity
        implements IServiceResponseHandler {

    @BindView(R.id.input_title)
    TextView mTxtTitle;
    @BindView(R.id.input_save)
    TextView mTxtSave;
    @BindView(R.id.user_icon)
    CircleImageView mImgUser;
    @BindView(R.id.user_name)
    TextView mTxtUserName;
    @BindView(R.id.review_date)
    TextView mTxtReviewDate;
    @BindView(R.id.flexBox)
    FlexboxLayout mFlexbox;
    @BindView(R.id.topic)
    TextView mTxtTopic;
    @BindView(R.id.review_content)
    ExpandableTextView mTxtReview;
    @BindView(R.id.expert_icon)
    CircleImageView mImgExp;
    @BindView(R.id.expert_name)
    TextView mTxtExpName;
    @BindView(R.id.reply_date)
    TextView mTxtReplyDate;
    @BindView(R.id.exp_lay)
    LinearLayout mLayExp;
    @BindView(R.id.edit_reply)
    EditText mTxtReply;
    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    private Typeface tf_semi;
    private ServiceReview review;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtReply.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        mTxtReply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTxtReply.clearFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mTxtReply.addTextChangedListener(new TextWatcher() {
            String[] arr;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    mTxtSave.setTextColor(getResources().getColor(R.color.pm_light));
                    mTxtSave.setEnabled(false);
                } else {
                    mTxtSave.setTextColor(getResources().getColor(R.color.primary));
                    mTxtSave.setEnabled(true);

                    arr = s.toString().split(" ");
                    if (arr.length > 100) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 100; i++) {
                            sb.append(arr[i] + " ");
                        }
                        mTxtReply.setText(sb.toString());
                        mTxtReply.setSelection(sb.toString().length() - 1);
                        PromeetsDialog.show(ReviewReplyActivity.this, "Cannot enter more than 100 words");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_reply);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);
        tf_semi = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");

        review = (ServiceReview) getIntent().getSerializableExtra("review");
        if (review == null) finish();

        if (!StringUtils.isEmpty(review.photoURL))
            Glide.with(this).load(review.photoURL).into(mImgUser);
        if (!StringUtils.isEmpty(review.fullName) && !review.fullName.contains("null"))
            mTxtUserName.setText(review.fullName);
        else
            mTxtUserName.setText("");

        String input = review.reviewDate.split(" ")[0];
        String output = "";
        try {
            output = new SimpleDateFormat("MMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(input));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mTxtReviewDate.setText(output);

        mFlexbox.removeAllViewsInLayout();
        if (review.ontime) {
            mFlexbox.addView(createReviewTag("On-Time"));
        }
        if (review.expertise) {
            mFlexbox.addView(createReviewTag("Strong Expertise"));
        }
        if (review.organization) {
            mFlexbox.addView(createReviewTag("Good Preparation"));
        }
        if (review.effectiveness) {
            mFlexbox.addView(createReviewTag("Effective Advice"));
        }

        if (StringUtils.isEmpty(review.title))
            mTxtTopic.setVisibility(View.GONE);
        else {
            mTxtTopic.setText(review.title);
        }

        if (StringUtils.isEmpty(review.description))
            mTxtReview.setVisibility(View.GONE);
        else {
            mTxtReview.setText(review.description);
            mTxtReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTxtReview.toggle();
                }
            });
        }

        // submit new review || update current review
        if (StringUtils.isEmpty(review.replyContent)) {
            mLayExp.setVisibility(View.GONE);
            mTxtSave.setText("Submit");
            mTxtSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostReview postReview = new PostReview();
                    postReview.id = review.id;
                    postReview.expId = review.expertId;
                    postReview.description = mTxtReply.getText().toString();
                    submitReviewReply(postReview);
                }
            });
        } else {
            mTxtSave.setText("Update");
            mTxtSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostReview postReview = new PostReview();
                    postReview.id = review.id;
                    postReview.expId = review.expertId;
                    postReview.description = mTxtReply.getText().toString();
                    updateReviewReply(postReview);
                }
            });

            Glide.with(this).load(review.expSmallphotoUrl).into(mImgExp);
            mTxtExpName.setText(review.expName + " Replied:");
            input = review.replyTime.split(" ")[0];
            output = "";
            try {
                output = new SimpleDateFormat("MMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(input));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mTxtReplyDate.setText(output);
            mTxtReply.setText(review.replyContent);
        }
    }

    private void submitReviewReply(PostReview postReview) {
        HashMap<String, String> header = new HashMap<>();
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.POST_REPLY));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
            //Call the service
            new GenericServiceHandler(Constant.ServiceType.POST_REVIEW, this, Constant.BASE_URL + Constant.POST_REPLY, "", postReview, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private void updateReviewReply(PostReview postReview) {
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.MODIFY_REPLY));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
            //Call the service
            new GenericServiceHandler(Constant.ServiceType.POST_REVIEW, this, Constant.BASE_URL + Constant.MODIFY_REPLY, "", postReview, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    private TextView createReviewTag(String tagStr) {
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        int tmp1 = ScreenUtil.convertDpToPx(4, this);
        int tmp2 = ScreenUtil.convertDpToPx(5, this);
        lp.setMargins(tmp1, tmp2, tmp1, tmp2);
        TextView mTxtTag = new TextView(this);
        mTxtTag.setLayoutParams(lp);
        tmp1 = ScreenUtil.convertDpToPx(10, this);
        tmp2 = ScreenUtil.convertDpToPx(5, this);
        mTxtTag.setPadding(tmp1, tmp2, tmp1, tmp2);
        mTxtTag.setText(tagStr);
        mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mTxtTag.setTextColor(getResources().getColor(R.color.pm_light));
        mTxtTag.setBackground(getResources().getDrawable(R.drawable.tag_border_gray));
        mTxtTag.setTypeface(tf_semi);
        return mTxtTag;
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        AllReviewsResp result = (AllReviewsResp) serviceResponse.getServiceResponse(AllReviewsResp.class);
        if (isSuccess(result.info.code)) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        } else onErrorResponse(result.info.description);
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }
}
