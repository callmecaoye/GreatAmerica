package com.promeets.android.custom;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;


/**
 * Created by sosasang on 7/17/17.
 */

public class PromeetsDialog {

    public static AlertDialog dialog;

    public interface OnOKListener {
        void onOKListener();
    }

    public interface OnSubmitListener {
        void onSubmitListener();
    }

    /**
     * Single OK button
     * Dismiss dialog only
     * @param activity
     * @param message
     */
    public static void show(final Activity activity, String message) {
        final View view = View.inflate(activity, R.layout.dialog_custom, null);
        LinearLayout mLayOptions = (LinearLayout) view.findViewById(R.id.options_layer);
        TextView mTxtMsg = (TextView) view.findViewById(R.id.message);
        final TextView mBtnOK = (TextView) view.findViewById(R.id.ok);
        mTxtMsg.setText(message);
        mLayOptions.setVisibility(View.GONE);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog = new AlertDialog.Builder(activity).create();
                dialog.setCancelable(false);
                if (!activity.isFinishing()) {
                    dialog.show();
                    dialog.setContentView(view);
                    dialog.getWindow().setLayout((int)(ScreenUtil.getWidth() * 0.9), LinearLayout.LayoutParams.WRAP_CONTENT);

                    mBtnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    /**
     * Single OK button
     * Custom OK listener
     * @param activity
     * @param message
     * @param listener
     */
    public static void show(final Activity activity, String message, final OnOKListener listener) {
        final View view = View.inflate(activity, R.layout.dialog_custom, null);
        LinearLayout mLayOptions = (LinearLayout) view.findViewById(R.id.options_layer);
        TextView mTxtMsg = (TextView) view.findViewById(R.id.message);
        final TextView mBtnOK = (TextView) view.findViewById(R.id.ok);
        mTxtMsg.setText(message);
        mLayOptions.setVisibility(View.GONE);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog = new AlertDialog.Builder(activity).create();
                dialog.setCancelable(false);
                if (!activity.isFinishing()) {
                    dialog.show();
                    dialog.setContentView(view);
                    dialog.getWindow().setLayout((int)(ScreenUtil.getWidth() * 0.9), LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                mBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onOKListener();
                    }
                });
            }
        });
    }

    /**
     * Cancel, Submit button
     * Custom submit listener
     * @param activity
     * @param message
     * @param listener
     */
    public static void show(final Activity activity, String message, final OnSubmitListener listener) {
        final View view = View.inflate(activity, R.layout.dialog_custom, null);
        TextView mTxtMsg = (TextView) view.findViewById(R.id.message);
        TextView mBtnOK = (TextView) view.findViewById(R.id.ok);
        final TextView mBtnCancel = (TextView) view.findViewById(R.id.cancel);
        final TextView mBtnSubmit = (TextView) view.findViewById(R.id.submit);
        mTxtMsg.setText(message);
        mBtnOK.setVisibility(View.GONE);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog = new AlertDialog.Builder(activity).create();
                dialog.setCancelable(false);
                if (!activity.isFinishing()) {
                    dialog.show();
                    dialog.setContentView(view);
                    dialog.getWindow().setLayout((int)(ScreenUtil.getWidth() * 0.9), LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                mBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onSubmitListener();
                    }
                });
            }
        });
    }

    /**
     * Custom 2 buttons text
     * Custom second listener
     * @param activity
     * @param message
     * @param cancel
     * @param submit
     * @param listener
     */
    public static void show(final Activity activity, String message, String cancel, String submit, final OnSubmitListener listener) {
        final View view = View.inflate(activity, R.layout.dialog_custom, null);
        TextView mTxtMsg = (TextView) view.findViewById(R.id.message);
        TextView mBtnOK = (TextView) view.findViewById(R.id.ok);
        final TextView mBtnCancel = (TextView) view.findViewById(R.id.cancel);
        final TextView mBtnSubmit = (TextView) view.findViewById(R.id.submit);
        mTxtMsg.setText(message);
        mBtnCancel.setText(cancel);
        mBtnSubmit.setText(submit);
        mBtnOK.setVisibility(View.GONE);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog = new AlertDialog.Builder(activity).create();
                dialog.setCancelable(false);
                if (!activity.isFinishing()) {
                    dialog.show();
                    dialog.setContentView(view);
                    dialog.getWindow().setLayout((int)(ScreenUtil.getWidth() * 0.9), LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                mBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onSubmitListener();
                    }
                });
            }
        });
    }

    /**
     * Single OK button
     * Custom OK listener
     * @param activity
     * @param message
     * @param listener
     */
    public static void show(final Activity activity, Drawable icon, String message, final OnOKListener listener) {
        final View view = View.inflate(activity, R.layout.dialog_custom, null);
        LinearLayout mLayOptions = (LinearLayout) view.findViewById(R.id.options_layer);
        ImageView mIcon = (ImageView) view.findViewById(R.id.icon);
        TextView mTxtMsg = (TextView) view.findViewById(R.id.message);
        final TextView mBtnOK = (TextView) view.findViewById(R.id.ok);
        mIcon.setImageDrawable(icon);
        mTxtMsg.setText(message);
        mLayOptions.setVisibility(View.GONE);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog = new AlertDialog.Builder(activity).create();
                dialog.setCancelable(false);
                if (!activity.isFinishing()) {
                    dialog.show();
                    dialog.setContentView(view);
                    dialog.getWindow().setLayout((int)(ScreenUtil.getWidth() * 0.9), LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                mBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onOKListener();
                    }
                });
            }
        });
    }

    public static void showProgress(Activity activity) {
        View view = View.inflate(activity, R.layout.dialog_progress, null);

        dialog = new AlertDialog.Builder(activity).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int)(ScreenUtil.getWidth() * 0.9), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
