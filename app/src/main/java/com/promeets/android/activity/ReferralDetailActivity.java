package com.promeets.android.activity;

import com.promeets.android.custom.PromeetsDialog;
import android.os.Bundle;
import com.promeets.android.services.GenericServiceHandler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.pojo.SuperResp;

import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Current not in use
 */
public class ReferralDetailActivity extends BaseActivity implements IServiceResponseHandler {

    @BindView(R.id.image_view)
    ImageView mImgView;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_detail);
        ButterKnife.bind(this);

        HashMap<String, String> header = new HashMap<>();
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsDialog.showProgress(this);
            //Call the service
            new GenericServiceHandler(null,this, Constant.BASE_URL + Constant.FETCH_REFERRAL_IMAGE, header, "", null, IServiceResponseHandler.GET, false, "", "Processing..").execute();
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        SuperResp json = (SuperResp) serviceResponse.getServiceResponse(SuperResp.class);
        if (isSuccess(json.info.code)) {
            if (!StringUtils.isEmpty(json.url))
                Glide.with(this).load(json.url).into(mImgView);
        } else {
            PromeetsDialog.show(this, json.info.description);
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }
}
