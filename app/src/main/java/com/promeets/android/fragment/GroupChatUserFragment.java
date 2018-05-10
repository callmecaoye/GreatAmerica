package com.promeets.android.fragment;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.GroupChatActivity;
import com.promeets.android.activity.PaymentActivity;
import com.promeets.android.adapter.GroupChatAdapter;
import com.promeets.android.adapter.LocationSingleCheckAdapter;
import com.promeets.android.adapter.TimeSingleCheckAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.EventTimePOJO;
import android.os.Bundle;
import com.promeets.android.pojo.EventDetailResp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import com.promeets.android.object.UrlPreviewInfo;

import com.promeets.android.R;
import com.sendbird.android.FileMessage;
import com.sendbird.android.UserMessage;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sosasang on 11/28/17.
 */

public class GroupChatUserFragment extends DialogFragment {

    @BindView(R.id.root_layout)
    View mLayRoot;
    @BindView(R.id.dialog_layout)
    View mLayDialog;
    @BindView(R.id.time_list)
    ListView mLVTime;
    @BindView(R.id.location_list)
    ListView mLVLocation;
    @BindView(R.id.add_location)
    ImageView addLocation;
    @BindView(R.id.add_time)
    ImageView addTime;
    @BindView(R.id.submit)
    Button mBtnSubmit;

    @BindView(R.id.meet_layer)
    View mLayMeet;
    @BindView(R.id.call_layer)
    View mLayCall;
    @BindView(R.id.divider)
    View mDivider;
    @BindView(R.id.call)
    CheckedTextView mChkCall;

    private BaseActivity mBaseActivity;
    private EventDetailResp mEventDetail;
    private ArrayList<EventTimePOJO> timeList = new ArrayList<>();
    private ArrayList<String> mWVEventStrList;
    private ArrayList<EventLocationPOJO> locList = new ArrayList<>();
    private LocationSingleCheckAdapter singleLocationAdapter;
    private TimeSingleCheckAdapter singleTimeAdapter;
    private boolean isLocationEnabled;
    private Gson gson = new Gson();

    public static GroupChatUserFragment newInstance(EventDetailResp mEventDetail) {
        GroupChatUserFragment f = new GroupChatUserFragment();
        f.mEventDetail = mEventDetail;
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBaseActivity = (BaseActivity) getActivity();
        final RelativeLayout root = new RelativeLayout(mBaseActivity);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(mBaseActivity);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setCancelable(true);
        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_accept, container);
        ButterKnife.bind(this, view);
        addTime.setVisibility(View.GONE);
        addLocation.setVisibility(View.GONE);
        mBtnSubmit.setText("Payment");

        timeList.addAll(mEventDetail.eventDateList);
        singleTimeAdapter = new TimeSingleCheckAdapter(mBaseActivity, timeList);
        mLVTime.setAdapter(singleTimeAdapter);

        mDivider.setVisibility(View.GONE);
        final EventLocationPOJO loc = mEventDetail.eventLocationList.get(0);
        if (loc.status == 1) {
            mLayCall.setVisibility(View.GONE);
            loc.isSelected = true;
            locList.add(loc);
            singleLocationAdapter = new LocationSingleCheckAdapter(mBaseActivity, locList);
            mLVLocation.setAdapter(singleLocationAdapter);
        } else {
            mLayMeet.setVisibility(View.GONE);
            mChkCall.setChecked(true);
        }

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mLayDialog.setOnClickListener(null);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mBtnSubmit.setEnabled(false);

                Intent intent = new Intent(mBaseActivity, PaymentActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("servicePhotoUrl", mEventDetail.expertProfile.smallphotoUrl);
                bundle.putString("ServiceTopic", mEventDetail.expertService.title);
                bundle.putString("author", mEventDetail.expertProfile.fullName);
                bundle.putString("position", mEventDetail.expertProfile.positon);
                bundle.putString("durationTime", mEventDetail.expertService.duratingTime);
                bundle.putString("origPrice", mEventDetail.expertService.originalPrice);
                bundle.putString("ServicePrice", mEventDetail.expertService.price);
                bundle.putInt("expId", mEventDetail.expertProfile.expId);
                bundle.putInt("eventRequestId", mEventDetail.eventData.id);
                bundle.putInt("serviceId", mEventDetail.expertService.id);

                bundle.putSerializable("time", singleTimeAdapter.getSingleSelected());
                bundle.putSerializable("location", loc);

                bundle.putString("userActiveNeedPay", mEventDetail.chargeDetail.get("userActiveNeedPay"));
                bundle.putString("chargeTip", mEventDetail.chargeDetail.get("chargeTip"));
                bundle.putString("balance", mEventDetail.chargeDetail.get("balance"));

                intent.putExtras(bundle);
                startActivityForResult(intent, 110);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isLocationEnabled = false;
        mBtnSubmit.setEnabled(true);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 110:
                    final GroupChatAdapter mChatAdapter = ((GroupChatActivity)mBaseActivity).getChatAdapter();
                    mChatAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
                        @Override
                        public void onUserMessageItemClick(UserMessage message) {
                            // Restore failed message and remove the failed message from list.
                            if (mChatAdapter.isFailedMessage(message)) {
                                ((GroupChatActivity)mBaseActivity).retryFailedMessage(message);
                                return;
                            }

                            // Message is sending. Do nothing on click event.
                            if (mChatAdapter.isTempMessage(message)) {
                                return;
                            }


                            if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
                                try {
                                    UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
                                    startActivity(browserIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFileMessageItemClick(FileMessage message) {

                        }
                    });
                    break;
            }
        }
    }
}
