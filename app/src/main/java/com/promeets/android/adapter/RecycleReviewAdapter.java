package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.ExpertDetailActivity;
import com.promeets.android.activity.MainActivity;
import android.content.Context;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.ReviewOptionFragment;
import android.graphics.Typeface;
import com.promeets.android.object.ServiceReview;
import com.promeets.android.pojo.PostReview;
import com.promeets.android.services.GenericServiceHandler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.activity.EventDetailActivity;
import com.promeets.android.activity.EventReviewListActivity;
import com.promeets.android.activity.ReviewReplyActivity;
import com.promeets.android.activity.ServiceReviewActivity;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.AllReviewsResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.util.Utility;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import at.blogc.android.views.ExpandableTextView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.promeets.android.Constant.ServiceType.USEFUL_REVIEW;

public class RecycleReviewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements IServiceResponseHandler {

    final int REQUEST_REVIEW_REPLY = 100;

    private ArrayList<ServiceReview> listService;

    private LayoutInflater mInflater;

    private BaseActivity mBaseActivity;

    static int clickedPosition = -1;

    private boolean isIamExpert = false;

    Typeface tf_semi;

    public RecycleReviewAdapter(BaseActivity aBaseActivity, ArrayList<ServiceReview> results, boolean isIamExpert) {
        listService = results;
        mInflater = LayoutInflater.from(aBaseActivity);
        this.mBaseActivity = aBaseActivity;
        this.isIamExpert = isIamExpert;
        tf_semi = Typeface.createFromAsset(mBaseActivity.getAssets(), "fonts/OpenSans-SemiBold.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycle_review_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        final ItemViewHolder holder = (ItemViewHolder) viewHolder;

        holder.setIsRecyclable(false);
        final ServiceReview review =listService.get(position);

        if (!StringUtils.isEmpty(review.photoURL))
            Glide.with(mBaseActivity).load(review.photoURL).into(holder.photo);

        if (mBaseActivity instanceof EventDetailActivity
                || mBaseActivity instanceof EventReviewListActivity)
            holder.txttitle.setVisibility(View.GONE);
        else if (mBaseActivity instanceof AppointStatusActivity) {
            holder.txttitle.setText(review.title);
            holder.mLayUseful.setVisibility(View.GONE);
        } else if (!StringUtils.isEmpty(review.reviewType)
                && (review.reviewType.equalsIgnoreCase("Topic")
                || review.reviewType.equalsIgnoreCase("Event")))
            holder.txttitle.setText(review.reviewType + ": " + review.title);
        else
            holder.txttitle.setText(review.title);

        if (!StringUtils.isEmpty(review.fullName) && !review.fullName.contains("null"))
            holder.txtusername.setText(review.fullName);
        else
            holder.txtusername.setText("");

        String input = review.reviewDate.split(" ")[0];
        String output = "";
        try {
            output = new SimpleDateFormat("MMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(input));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.txtdate.setText(output);

        if (StringUtils.isEmpty(review.description))
            holder.txtcontent.setVisibility(View.GONE);
        else {
            holder.txtcontent.setText(review.description);
            holder.txtcontent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.txtcontent.toggle();
                }
            });
        }

        holder.mFlexbox.removeAllViewsInLayout();
        if (review.ontime) {
            holder.mFlexbox.addView(createReviewTag("On-Time"));
        }
        if (review.expertise) {
            holder.mFlexbox.addView(createReviewTag("Strong Expertise"));
        }
        if (review.organization) {
            holder.mFlexbox.addView(createReviewTag("Good Preparation"));
        }
        if (review.effectiveness) {
            holder.mFlexbox.addView(createReviewTag("Effective Advice"));
        }

        if(review.usefulCount == 0) {
            holder.mTxtUseful.setText("0 Useful");
        } else {
            holder.mTxtUseful.setText(review.usefulCount + " Useful");
        }

        // no reply yet
        if(StringUtils.isEmpty(review.replyContent)
                || mBaseActivity instanceof EventDetailActivity
                || mBaseActivity instanceof EventReviewListActivity) {
            holder.replyLayout.setVisibility(View.GONE);
            // can reply
            if(isIamExpert &&
                    (mBaseActivity instanceof AppointStatusActivity
                    || mBaseActivity instanceof ExpertDetailActivity
                    || mBaseActivity instanceof ServiceReviewActivity)) {
                holder.txtReply.setVisibility(View.VISIBLE);
                holder.txtReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mBaseActivity instanceof AppointStatusActivity) {
                            AppointStatusActivity act = (AppointStatusActivity) mBaseActivity;
                            Intent intent = new Intent(act, ReviewReplyActivity.class);
                            intent.putExtra("review", review);
                            mBaseActivity.startActivityForResult(intent, REQUEST_REVIEW_REPLY);
                            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        } else if (mBaseActivity instanceof ExpertDetailActivity) {
                            ExpertDetailActivity act = (ExpertDetailActivity) mBaseActivity;
                            Intent intent = new Intent(act, ReviewReplyActivity.class);
                            intent.putExtra("review", review);
                            mBaseActivity.startActivityForResult(intent, REQUEST_REVIEW_REPLY);
                            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        } else if (mBaseActivity instanceof ServiceReviewActivity) {
                            ServiceReviewActivity act = (ServiceReviewActivity) mBaseActivity;
                            Intent intent = new Intent(act, ReviewReplyActivity.class);
                            intent.putExtra("review", review);
                            mBaseActivity.startActivityForResult(intent, REQUEST_REVIEW_REPLY);
                            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }

                    }
                });
            }
        } else {
            holder.replyLayout.setVisibility(View.VISIBLE);
            Glide.with(mBaseActivity).load(review.expSmallphotoUrl).into(holder.expPhoto);
            holder.txtexprname.setText(review.expName + " Replied:");

            input = review.replyTime.split(" ")[0];
            output = "";
            try {
                output = new SimpleDateFormat("MMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(input));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.txtreplydate.setText(output);
            holder.mLayRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) mBaseActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    holder.mLayRoot.requestFocus();
                }
            });

            holder.txtreplycontent.setVisibility(View.VISIBLE);
            holder.mLayEditReply.setVisibility(View.GONE);
            holder.txtreplycontent.setText(review.replyContent);

            if (isIamExpert) {
                holder.replyLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReviewOptionFragment dialogFragment = ReviewOptionFragment.newInstance(review, position);
                        dialogFragment.show(mBaseActivity.getFragmentManager(), "review option");
                        dialogFragment.setCancelable(true);
                    }
                });
            }
        }

        if(review.isUseful == 0) {
            holder.mLayUseful.setBackgroundResource(R.drawable.btn_border_primary);
            holder.mTxtUseful.setTextColor(mBaseActivity.getResources().getColor(R.color.primary));
            holder.mImgUseful.setImageDrawable(mBaseActivity.getResources().getDrawable(R.drawable.ic_thumb_primary));
        } else {
            holder.mLayUseful.setBackgroundResource(R.drawable.btn_solid_primary);
            holder.mTxtUseful.setTextColor(ContextCompat.getColor(mBaseActivity, R.color.white));
            holder.mImgUseful.setImageDrawable(mBaseActivity.getResources().getDrawable(R.drawable.ic_thumb_white));
        }

        holder.mLayUseful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPosition = position;
                PostReview postReview = new PostReview();
                postReview.id = review.id;

                UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
                if(userPOJO!=null) {
                    postReview.userId= userPOJO.id;
                    callUserful(review,postReview);
                } else
                    mBaseActivity.startActivity(MainActivity.class);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listService == null
                || listService.isEmpty()) return 0;
        return listService.size();
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_REVIEW_REPLY && resultCode == RESULT_OK) {
            mBaseActivity.finish();
            mBaseActivity.startActivity(mBaseActivity.getIntent());
        }
    }

    private TextView createReviewTag(String tagStr) {
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        int tmp1 = ScreenUtil.convertDpToPx(4, mBaseActivity);
        int tmp2 = ScreenUtil.convertDpToPx(5, mBaseActivity);
        lp.setMargins(tmp1, tmp2, tmp1, tmp2);
        TextView mTxtTag = new TextView(mBaseActivity);
        mTxtTag.setLayoutParams(lp);
        tmp1 = ScreenUtil.convertDpToPx(10, mBaseActivity);
        tmp2 = ScreenUtil.convertDpToPx(5, mBaseActivity);
        mTxtTag.setPadding(tmp1, tmp2, tmp1, tmp2);
        mTxtTag.setText(tagStr);
        mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mTxtTag.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_light));
        mTxtTag.setBackground(mBaseActivity.getResources().getDrawable(R.drawable.tag_border_gray));
        mTxtTag.setTypeface(tf_semi);
        return mTxtTag;
    }

    private ServiceReview clickReview;
    private void callUserful(ServiceReview serviceReview, PostReview postReview) {
        this.clickReview = serviceReview;
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.USEFUL_REVIEW));
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
            new GenericServiceHandler(USEFUL_REVIEW, this, Constant.BASE_URL + Constant.USEFUL_REVIEW, "", postReview, header, IServiceResponseHandler.POST, false, "Please wait!", "Processing..").execute();
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        AllReviewsResp result = (AllReviewsResp) serviceResponse.getServiceResponse(AllReviewsResp.class);

        if(mBaseActivity.isSuccess(result.info.code)){
            if (serviceType == USEFUL_REVIEW) {
                try {
                    clickReview = listService.get(clickedPosition);
                    if(clickReview.isUseful == 0) {
                        clickReview.isUseful = 1;
                        clickReview.usefulCount++;

                    } else {
                        clickReview.isUseful = 0;
                        clickReview.usefulCount--;
                    }
                    notifyDataSetChanged();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else
            onErrorResponse("Unable to send your request.");
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

    public void updateItem(int position, ServiceReview review) {
        listService.set(position, review);
        notifyDataSetChanged();
    }

    public void updateItems(ArrayList<ServiceReview> listService) {
        this.listService = listService;
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView txttitle, txtusername, txtdate;
        TextView txtexprname, txtreplydate;
        CircleImageView photo, expPhoto;
        ExpandableTextView txtcontent;
        TextView txtreplycontent;
        TextView txtReply;
        LinearLayout replyLayout, mLayEditReply, mLayRoot;
        FlexboxLayout mFlexbox;
        EditText mEditReply;
        TextView mTxtCancel, mTxtUpdade;
        View divider;

        LinearLayout mLayUseful;
        ImageView mImgUseful;
        TextView mTxtUseful;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txttitle = (TextView) itemView.findViewById(R.id.list_service_detail_review_topic);
            txtusername = (TextView) itemView.findViewById(R.id.list_service_detail_review_user_name);
            txtdate = (TextView) itemView.findViewById(R.id.list_service_detail_review_date);

            txtexprname = (TextView) itemView.findViewById(R.id.list_service_detail_review_expert_name);
            txtreplydate = (TextView) itemView.findViewById(R.id.list_service_detail_reply_date);

            txtcontent = (ExpandableTextView) itemView.findViewById(R.id.list_service_detail_review_content);
            mEditReply = (EditText) itemView.findViewById(R.id.edit_reply);
            //txtUseful = (TextView) itemView.findViewById(R.id.useful);
            txtReply = (TextView) itemView.findViewById(R.id.reply);
            txtreplycontent = (TextView) itemView.findViewById(R.id.reply_content);

            photo = (CircleImageView) itemView.findViewById(R.id.list_service_detail_review_user_icon);
            expPhoto = (CircleImageView) itemView.findViewById(R.id.list_service_detail_review_expert_icon);

            replyLayout = (LinearLayout) itemView.findViewById(R.id.view_reply_layout);
            mLayEditReply = (LinearLayout) itemView.findViewById(R.id.edit_reply_layout);
            mLayRoot = (LinearLayout) itemView.findViewById(R.id.root_layout);
            mFlexbox = (FlexboxLayout) itemView.findViewById(R.id.flexBox);

            mTxtCancel = (TextView) itemView.findViewById(R.id.cancel);
            mTxtUpdade = (TextView) itemView.findViewById(R.id.update);
            divider = itemView.findViewById(R.id.divider);

            mLayUseful = (LinearLayout) itemView.findViewById(R.id.useful_lay);
            mImgUseful = (ImageView) itemView.findViewById(R.id.useful_img);
            mTxtUseful = (TextView) itemView.findViewById(R.id.useful_txt);
        }
    }
}
