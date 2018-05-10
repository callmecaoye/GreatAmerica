package com.promeets.android.fragment;

import com.promeets.android.activity.ExpertDetailActivity;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.MainActivity;
import com.promeets.android.activity.UserRequestSettingActivity;
import com.promeets.android.object.ExpertProfile;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.ExpertService;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;
import com.promeets.android.util.MixpanelUtil;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.promeets.android.util.PromeetsUtils.getUserData;

/**
 * This is for showing expert topic detail
 *
 * User requests expert's topic only from here
 *
 * @destination: UserRequestSettingActivity
 *
 */

public class ServiceDetailPopupFragment extends DialogFragment {
    private ServiceDetailPopupFragment registerVerifyFragment;

    @BindView(R.id.fragment_service_detail_popup_title)
    TextView title;
    @BindView(R.id.fragment_service_detail_popup_duration)
    TextView mTxtDuration;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.fragment_service_detail_popup_introduction)
    //TextView introduction;
    WebView mWebViewIntro;
    @BindView(R.id.orig_price)
    TextView mTxtOrigPrice;
    @BindView(R.id.fragment_service_detail_popup_make_appointment)
    Button make_appointment;
    @BindView(R.id.fragment_service_detail_popup_like)
    ImageView mImgViewLike;

    @BindView(R.id.blank_area1)
    View blank_area1;

    @BindView(R.id.blank_area2)
    View blank_area2;

    public ExpertService servicePOJO;
    public ExpertProfile expertProfilePOJO;
    boolean isLoginCalled = false;

    private BaseActivity mBaseActivity;
    private Animator anim;

    public static ServiceDetailPopupFragment newInstance(ExpertService servicePOJO, ExpertProfile expertProfilePOJO) {
        ServiceDetailPopupFragment f = new ServiceDetailPopupFragment();
        f.servicePOJO = servicePOJO;
        f.expertProfilePOJO = expertProfilePOJO;
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(mBaseActivity);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(mBaseActivity);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setCancelable(true);
        return dialog;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_detail_popup, container);
        ButterKnife.bind(this, view);

        anim = AnimatorInflater.loadAnimator(mBaseActivity, R.animator.like_scale);
        anim.setDuration(150);
        anim.setTarget(mImgViewLike);

        final ExpertProfilePOJO expertProfile = (ExpertProfilePOJO) getUserData(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY, ExpertProfilePOJO.class);
        if(expertProfile!=null && (expertProfilePOJO.getExpId().equalsIgnoreCase("self")|| expertProfilePOJO.getExpId().equalsIgnoreCase(expertProfile.expId+""))){
            view.findViewById(R.id.bottomBar).setVisibility(View.GONE);
            //mListViewDetailService.setOnItemClickListener(null);
        } else {
            if (servicePOJO.ifExistRequest.equals("-1")) {
                view.findViewById(R.id.bottomBar).setVisibility(View.GONE);
            }  else if(servicePOJO.ifExistRequest.equals("1")){
                make_appointment.setText("View Appointment");
                make_appointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                        UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY,UserPOJO.class);
                        if(userPOJO==null){
                            Intent intent = new Intent(mBaseActivity, MainActivity.class);
                            startActivity(intent);
                            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                        else{
                            HashMap<String, String> map = new HashMap<>();
                            map.put("serviceId", servicePOJO.id.toString());
                            MixpanelUtil.getInstance(mBaseActivity).trackEvent("Expert page -> topic -> Make an appointment", map);
                            Intent intent = new Intent(mBaseActivity, AppointStatusActivity.class);
                            int id = 0;
                            try{
                                id=Integer.parseInt(servicePOJO.eventDataId);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            intent.putExtra("eventRequestId", id);
                            intent.putExtra("parentPage",0 );
                            startActivity(intent);
                            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }

                    }
                });
            } else {
                make_appointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                        UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY,UserPOJO.class);
                        if(userPOJO==null){
                            Intent intent = new Intent(mBaseActivity, MainActivity.class);
                            startActivity(intent);
                            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                        else{
                            Intent intent = new Intent(mBaseActivity, UserRequestSettingActivity.class);
                            intent.putExtra("serviceId", servicePOJO.id+"");
                            intent.putExtra("expId", expertProfilePOJO.getExpId()+"");
                            intent.putExtra("price", servicePOJO.price);
                            intent.putExtra("discount", servicePOJO.originalPrice);
                            intent.putExtra("topic", servicePOJO.title);
                            intent.putExtra("author", expertProfilePOJO.getFullName());
                            intent.putExtra("position", expertProfilePOJO.getPosition());
                            if (!StringUtils.isEmpty(expertProfilePOJO.getPhotoUrl()))
                                intent.putExtra("photo", expertProfilePOJO.getPhotoUrl());
                            else if (!StringUtils.isEmpty(expertProfilePOJO.getSmallphotoUrl()))
                                intent.putExtra("photo", expertProfilePOJO.getSmallphotoUrl());
                            if (!StringUtils.isEmpty(servicePOJO.duratingTime))
                                intent.putExtra("duratingTime", NumberFormatUtil.getInstance().getFloat(servicePOJO.duratingTime));
                            if (!StringUtils.isEmpty(expertProfilePOJO.getDefaultDate()))
                                intent.putExtra("defaultDate", new Integer(expertProfilePOJO.getDefaultDate()).intValue());
                            intent.putExtra("defaultLocationList", expertProfilePOJO.getExpertDefaultLocationList());
                            intent.putExtra("availabilityType", expertProfilePOJO.getAvailabilityType());

                            startActivity(intent);
                            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    }
                });
            }
        }
        title.setText(servicePOJO.title);
        price.setText("$"+NumberFormatUtil.getInstance().getCurrency(servicePOJO.price));
        if(!StringUtils.isEmpty(servicePOJO.originalPrice)) {
            mTxtOrigPrice.setVisibility(View.VISIBLE);
            mTxtOrigPrice.setText("$"+NumberFormatUtil.getInstance().getCurrency(servicePOJO.originalPrice));
            mTxtOrigPrice.setPaintFlags(mTxtOrigPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        mWebViewIntro.setScrollbarFadingEnabled(true);
        String tmp = "<div class='content'> <style> *{font-family:'OpenSans' !important; src: url(\"file:///android_asset/fonts/\")}"
                + "body {line-height:20px; color:#4A4A4A; font-family:'OpenSans'; font-size: medium;} </style>"
                + "<body style='margin:0; padding:0'>" + servicePOJO.description + "</body> </div>";
        mWebViewIntro.loadDataWithBaseURL("file:///android_asset", tmp, "text/html", "UTF-8", null);

        try {
            if(!StringUtils.isEmpty(servicePOJO.duratingTime))
            {
                double hour = (new Double(servicePOJO.duratingTime).doubleValue()/60);
                mTxtDuration.setText(" / " + NumberFormatUtil.getInstance().getTime(hour+"")+" Hours");
            }
        }catch(Exception ex){ }


        setCancelable(true);

        handleImg();
        mImgViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim.start();
                ((ExpertDetailActivity)mBaseActivity).onClick(((ExpertDetailActivity)mBaseActivity).mImgViewLike);
                handleImg();
            }
        });



        blank_area1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        blank_area2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }


    private void handleImg() {
        if (!((ExpertDetailActivity)mBaseActivity).mIsLiked) {

            mImgViewLike.setImageResource(R.drawable.ic_like_outline);
        }else
            mImgViewLike.setImageResource(R.drawable.ic_like);

    }
}