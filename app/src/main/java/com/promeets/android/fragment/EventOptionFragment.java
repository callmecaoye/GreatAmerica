package com.promeets.android.fragment;

import com.promeets.android.activity.BaseActivity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promeets.android.activity.EventDetailActivity;
import com.promeets.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sosasang on 12/4/17.
 */

public class EventOptionFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.option)
    TextView mBtnOption;

    @BindView(R.id.cancel)
    TextView mBtnCancel;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;

    private BaseActivity mBaseActivity;
    private int state;

    public static EventOptionFragment newInstance(int state) {
        EventOptionFragment f = new EventOptionFragment();
        f.state = state;
        return f;
    }

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
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_going, container);
        ButterKnife.bind(this, view);

        mLayRoot.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        if (state == 1) {
            mBtnOption.setText("Not going");
            mBtnOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((EventDetailActivity)mBaseActivity).submitNo();
                    dismiss();
                }
            });
        } else if (state == 2) {
            mBtnOption.setText("Going");
            mBtnOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((EventDetailActivity)mBaseActivity).preSubmitYes();
                    dismiss();
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.root_layout:
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
