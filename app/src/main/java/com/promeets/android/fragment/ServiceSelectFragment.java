package com.promeets.android.fragment;

import com.promeets.android.activity.BaseActivity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.promeets.android.adapter.RecycleServiceAdapter;
import com.promeets.android.object.ExpertProfile;
import com.promeets.android.object.ExpertService;
import com.promeets.android.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is for showing list of expert topic for user to choose and make appointment
 *
 * @source: ExpertDetailActivity
 *
 * @destination: ServiceDetailPopupFragment
 *
 */

public class ServiceSelectFragment extends DialogFragment {

    @BindView(R.id.service_list)
    RecyclerView mRVService;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    private BaseActivity mBaseActivity;
    private ArrayList<ExpertService> mListService;
    private ExpertProfile mExpProfile;

    public static ServiceSelectFragment newInstance(ArrayList<ExpertService> mListService, ExpertProfile mExpProfile) {
        ServiceSelectFragment f = new ServiceSelectFragment();
        f.mListService = mListService;
        f.mExpProfile = mExpProfile;
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
        View view = inflater.inflate(R.layout.fragment_service_select, container);
        ButterKnife.bind(this, view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.HORIZONTAL, false);
        mRVService.setLayoutManager(mLayoutManager);
        mRVService.setHasFixedSize(true);
        RecycleServiceAdapter serviceAdapter = new RecycleServiceAdapter(mBaseActivity, mListService, mExpProfile, this);
        mRVService.setAdapter(serviceAdapter);

        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
