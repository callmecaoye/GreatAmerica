package com.promeets.android.fragment;

import com.promeets.android.activity.GroupChatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.promeets.android.pojo.ServiceListDetailResp;
import com.promeets.android.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceSelectChatFragment extends DialogFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public GroupChatActivity mActivity;
    public ArrayList<ServiceListDetailResp> dataList;

    @BindView(R.id.service_list)
    ListView mListViewServiceList;
    @BindView(R.id.parent)
    RelativeLayout mRelativeLayoutEmptyView;

    public static ServiceSelectChatFragment newInstance(ArrayList<ServiceListDetailResp> dataList) {
        ServiceSelectChatFragment f = new ServiceSelectChatFragment();
        f.dataList = dataList;
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() instanceof GroupChatActivity) {
            mActivity = (GroupChatActivity) getActivity();
        } else {
            dismiss();
            return null;
        }

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setCancelable(true);
        return dialog;

    }

    @Override
    public void onResume() {
        super.onResume();
        mRelativeLayoutEmptyView.setOnClickListener(this);

        ArrayAdapter<ServiceListDetailResp> adapt = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, dataList);
        mListViewServiceList.setAdapter(adapt);
        mListViewServiceList.setOnItemClickListener(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_list_chat, container);
        ButterKnife.bind(this, view);
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
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mActivity.OnItemSelect(dataList, position);
        dismiss();
    }
}