package com.promeets.android.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.promeets.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Terms of Use and Privacy Policy
 *
 * @Source: AboutUsActivity
 *
 */

public class TermsFragment extends DialogFragment {


    @BindView(R.id.promeets_policy_txt)
    TextView mTxtTerms;

    public static TermsFragment newInstance() {
        TermsFragment f = new TermsFragment();
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_terms, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
