package com.promeets.android.listeners;

import com.promeets.android.Constant;
import com.promeets.android.pojo.ServiceResponse;

/**
 * Date : 28-09-2015
 *
 * @author Shashank Shekhar
 **/

public interface IServiceResponseHandler {

    int POST = 1;

    int GET = 2;

    int PUT = 3;

    int READ_JSON_FROM_ASSETS = 4;


    /**
     * @param serviceResponse
     * @param serviceType
     */
    void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType);

    /**
     * @param errorMessage
     */
    void onErrorResponse(String errorMessage);

    /**
     * @param serviceException
     */
    void onErrorResponse(Throwable serviceException);
}
