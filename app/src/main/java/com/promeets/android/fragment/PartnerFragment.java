package com.promeets.android.fragment;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.PartnerActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.promeets.android.object.Advertisement;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;

public class PartnerFragment extends DialogFragment {
    private BaseActivity mBaseActivity;
    private int width;
    private Advertisement ad;

    public static PartnerFragment newInstance(Advertisement ad) {
        PartnerFragment f = new PartnerFragment();
        f.ad = ad;
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

        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(mBaseActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Setup the UI and return the view
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner, container, false);

        width = mBaseActivity.getWidth();
        ImageView mImgView = view.findViewById(R.id.image);
        if (!StringUtils.isEmpty(ad.getAutoPopupUrl()))
            Glide.with(this).load(ad.getAutoPopupUrl()).into(mImgView);
        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent intent = new Intent(mBaseActivity, PartnerActivity.class);
                intent.putExtra("photoUrl", ad.getPhotoUrl());
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        final ImageView mImgClose = view.findViewById(R.id.close);
        mImgView.getLayoutParams().height = (width - ScreenUtil.convertDpToPx(50, mBaseActivity))/ 3 * 4;
        mImgView.requestLayout();
        mImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
