package com.promeets.android.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.promeets.android.Constant;
import com.promeets.android.MyApplication;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.pojo.SyncTimeResp;
import com.promeets.android.R;
import com.promeets.android.services.GenericServiceHandler;

public final class ServerTimeUtil implements IServiceResponseHandler {

    private Activity mBaseActivity;

    public ServerTimeUtil(Activity activity) {
        this.mBaseActivity = activity;
    }



    public void getServerTime() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        } else {
            new GenericServiceHandler(Constant.ServiceType.SYNC_TIME,this, Constant.BASE_URL+Constant.FETCH_SERVER_TIME, null, null, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
        }
    }
    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        if(serviceType == Constant.ServiceType.SYNC_TIME){
            SyncTimeResp syncTimeResp = (SyncTimeResp) serviceResponse.getServiceResponse(SyncTimeResp.class);
            if(syncTimeResp.getInfo().getCode().equals("200")){
                ServiceHeaderGeneratorUtil.getInstance().setmSyncTime(syncTimeResp.getSyncTime());
            }
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {

    }

    @Override
    public void onErrorResponse(Throwable serviceException) {

    }

    private Boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
