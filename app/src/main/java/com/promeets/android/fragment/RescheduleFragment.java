package com.promeets.android.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.custom.DefaultRationale;
import com.promeets.android.custom.PermissionSetting;
import com.promeets.android.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;


import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sosasang on 2/13/18.
 */

public class RescheduleFragment extends DialogFragment implements View.OnClickListener{

    static final int REQUEST_CALL_PHONE = 100;

    @BindView(R.id.email_txt)
    TextView mTxtEmail;
    @BindView(R.id.phone_txt)
    TextView mTxtPhone;
    @BindView(R.id.root_layout)
    FrameLayout mLayRoot;
    @BindView(R.id.dialog_layout)
    View mLayDialog;

    private BaseActivity mBaseActivity;

    private Rationale mRationale;
    private PermissionSetting mSetting;

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reschedule, container);
        ButterKnife.bind(this, view);

        mLayRoot.setOnClickListener(this);
        mLayDialog.setOnClickListener(this);
        mTxtEmail.setOnClickListener(this);
        mTxtPhone.setOnClickListener(this);

        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(getContext());

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
                dismiss();
                break;
            case R.id.email_txt:
                dismiss();
                Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:support@promeets.us"));
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reschedule meeting time/location");
                //mailIntent.putExtra(Intent.EXTRA_TEXT, "Pre-fill Content");
                startActivity(Intent.createChooser(mailIntent, "Send Email"));
                break;
            case R.id.phone_txt:
                dismiss();
                /*AndPermission.with(mBaseActivity)
                        .requestCode(REQUEST_CALL_PHONE)
                        .permission(Manifest.permission.CALL_PHONE)
                        .callback(permissionListener)
                        .rationale(rationaleListener)
                        .start();*/
                AndPermission.with(mBaseActivity)
                        .permission(Permission.CALL_PHONE)
                        .rationale(mRationale)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                try {
                                    Intent diaIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:9292860339"));
                                    startActivity(diaIntent);
                                } catch (Exception e){
                                    mSetting.showSetting(permissions);
                                }
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(@NonNull List<String> permissions) {
                                if (AndPermission.hasAlwaysDeniedPermission(mBaseActivity, permissions)) {
                                    mSetting.showSetting(permissions);
                                }
                            }
                        })
                        .start();
                break;
        }
    }

   /*private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CALL_PHONE:
                    try {
                        Intent diaIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:9292860339"));
                        startActivity(diaIntent);
                    } catch (Exception e){
                        AndPermission.defaultSettingDialog(mBaseActivity, requestCode).show();
                    }
                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            if (AndPermission.hasAlwaysDeniedPermission(mBaseActivity, deniedPermissions))
                AndPermission.defaultSettingDialog(mBaseActivity, REQUEST_CODE_SETTING).show();
        }
    };

    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
            AndPermission.rationaleDialog(mBaseActivity, rationale).show();
        }
    };*/
}
