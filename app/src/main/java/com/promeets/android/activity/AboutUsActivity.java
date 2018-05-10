package com.promeets.android.activity;

import android.os.Bundle;
import com.promeets.android.BuildConfig;
import android.view.View;
import android.widget.TextView;

import com.promeets.android.fragment.TermsFragment;
import com.promeets.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Brief introduction about Promeets and Terms of Use and Privacy Policy
 *
 * @Source: AccountFragment in HomeActivity
 *
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.version)
    TextView mTxtVersion;

    @BindView(R.id.policy)
    TextView mTxtPolicy;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mTxtPolicy.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        mTxtVersion.setText("Promeets " + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.policy:
                TermsFragment termsFragment = new TermsFragment();
                termsFragment.show(getFragmentManager(), "Terms of Use");
                break;
        }
    }
}
