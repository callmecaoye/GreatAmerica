package com.promeets.android.fragment;

import com.promeets.android.activity.ExpertDetailActivity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.promeets.android.services.GenericServiceHandler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.ReviewReplyActivity;
import com.promeets.android.activity.ServiceReviewActivity;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.ServiceReview;
import com.promeets.android.pojo.AllReviewsResp;
import com.promeets.android.pojo.PostReview;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.Utility;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.promeets.android.Constant.ServiceType.POST_REVIEW;

/**
 * Created by sosasang on 7/12/17.
 */

public class ReviewOptionFragment extends DialogFragment
        implements View.OnClickListener, IServiceResponseHandler {

    final int REQUEST_REVIEW_REPLY = 100;

    @BindView(R.id.edit)
    TextView mBtnEdit;
    @BindView(R.id.copy)
    TextView mBtnCopy;
    @BindView(R.id.delete)
    TextView mBtnDelete;
    @BindView(R.id.cancel)
    TextView mBtnCancel;
    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    private BaseActivity mBaseActivity;
    private ServiceReview review;
    private int position;

    public static ReviewOptionFragment newInstance(ServiceReview review, int position) {
        ReviewOptionFragment f = new ReviewOptionFragment();
        f.review = review;
        f.position = position;
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(mBaseActivity);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(mBaseActivity);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_option, container);
        ButterKnife.bind(this, view);

        mLayRoot.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mBtnCopy.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                PromeetsDialog.show(mBaseActivity, "Are you sure you want to remove this comment?", "Cancel", "Yes", new PromeetsDialog.OnSubmitListener() {
                    @Override
                    public void onSubmitListener() {
                        PostReview postReview = new PostReview();
                        postReview.id = review.id;
                        postReview.expId = review.expertId;
                        postReview.description = "";
                        deleteReviewReply(postReview);
                    }
                });
                dismiss();
                break;
            case R.id.edit:
                if (mBaseActivity instanceof AppointStatusActivity
                        || mBaseActivity instanceof ExpertDetailActivity
                        || mBaseActivity instanceof ServiceReviewActivity) {
                    if (mBaseActivity instanceof AppointStatusActivity) {
                        AppointStatusActivity act = (AppointStatusActivity) mBaseActivity;
                        Intent intent = new Intent(act, ReviewReplyActivity.class);
                        intent.putExtra("review", review);
                        mBaseActivity.startActivityForResult(intent, REQUEST_REVIEW_REPLY);
                    } else if (mBaseActivity instanceof ExpertDetailActivity) {
                        ExpertDetailActivity act = (ExpertDetailActivity) mBaseActivity;
                        Intent intent = new Intent(act, ReviewReplyActivity.class);
                        intent.putExtra("review", review);
                        mBaseActivity.startActivityForResult(intent, REQUEST_REVIEW_REPLY);
                    } else if (mBaseActivity instanceof ServiceReviewActivity) {
                        ServiceReviewActivity act = (ServiceReviewActivity) mBaseActivity;
                        Intent intent = new Intent(act, ReviewReplyActivity.class);
                        intent.putExtra("review", review);
                        mBaseActivity.startActivityForResult(intent, REQUEST_REVIEW_REPLY);
                    }
                }
                dismiss();
                break;
            case R.id.copy:
                dismiss();
                ClipboardManager clipboard = (ClipboardManager) mBaseActivity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", review.replyContent);
                clipboard.setPrimaryClip(clip);
                PromeetsDialog.show(mBaseActivity, "Copied to clipboard.");
                break;
            case R.id.root_layout:
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private void deleteReviewReply(PostReview postReview) {
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.MODIFY_REPLY));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(mBaseActivity);
            //Call the service
            new GenericServiceHandler(POST_REVIEW, this, Constant.BASE_URL + Constant.MODIFY_REPLY, "", postReview, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        AllReviewsResp result = (AllReviewsResp) serviceResponse.getServiceResponse(AllReviewsResp.class);
        if (mBaseActivity.isSuccess(result.info.code)) {
            if (mBaseActivity instanceof ExpertDetailActivity) {
                ((ExpertDetailActivity)mBaseActivity).getReviewAdapter().updateItem(position, result.data);
            } else if (mBaseActivity instanceof ServiceReviewActivity) {
                ((ServiceReviewActivity)mBaseActivity).getReviewAdapter().updateItem(position, result.data);
            } else if (mBaseActivity instanceof AppointStatusActivity) {
                ((AppointStatusActivity)mBaseActivity).getReviewAdapter().updateItem(position, result.data);
            }
        } else
            onErrorResponse(result.info.description);
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(mBaseActivity, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }
}
