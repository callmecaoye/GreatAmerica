package com.promeets.android.pojo;

import com.google.gson.Gson;

/**
 * Date : 28-09-2015
 *
 * @author SESA388944 : Shashank Shekhar
 * for Schneider electric : MEA project
 **/

public class ServiceResponse {
    private String serviceResponse;

    /**
     * @param serviceResponse
     */
    public void setServiceResponse(String serviceResponse) {
        this.serviceResponse = serviceResponse;

    }

    /**
     * @param classOfT
     * @return
     */
    public Object getServiceResponse(Class<?> classOfT) {
        return new Gson().fromJson(serviceResponse, classOfT);
    }

    public <T> String JsonToString(T classOfT){
        return  new Gson().toJson(classOfT);
    }
    @Override
    public String toString() {
        return serviceResponse;
    }
}
