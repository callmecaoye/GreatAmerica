package com.promeets.android.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.promeets.android.MyApplication;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * Date : 28-09-2015
 *
 * @author SESA388944 : Shashank Shekhar
 * for Schneider electric : MEA project
 **/

public final class RestfulServiceHandler {

    private String mOperation; // this is the service method name

    private HashMap<String, String> mHeaderMap; // Contains header to passed to
    // during service call

    private String TAG = getClass().getName(); // User for logs

    /**
     * @param operation : String
     * @param header    : HashMap
     */
    public RestfulServiceHandler(String operation,
                                 HashMap<String, String> header) {
        this.mOperation = operation;
        this.mHeaderMap = header;
    }

    public String doGetOperation(String url) throws Exception{

        Log.i("Request URL : ", url + " ::this is the url");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(); // using

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        //connection.setDoOutput(true);


        if (mHeaderMap != null && mHeaderMap.size() > 0) { // Checking if any
            // header needs to
            // be added

            Set<String> keys = mHeaderMap.keySet(); // getting all the keys in
            // the hashtable

            for (String headerKeys : keys) { // looping through the keys
                connection.setRequestProperty(headerKeys, mHeaderMap.get(headerKeys));
            }
        }

        // connection.connect();
        InputStream inputStream = connection.getInputStream();
        return IOUtils.toString(inputStream);
    }

    /**
     * @param baseUrl
     * @param pojoGSon
     * @return
     * @throws Exception
     */
    public String doPostOperation(String baseUrl, Object pojoGSon)
            throws Exception {

        Log.i(TAG, "Request URL : " + baseUrl + mOperation);
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + mOperation).openConnection(); // using

        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        if (mHeaderMap != null && mHeaderMap.size() > 0) { // Checking if any
            // header needs to
            // be added

            Set<String> keys = mHeaderMap.keySet(); // getting all the keys in
            // the hashtable

            for (String headerKeys : keys) { // looping through the keys
                connection.setRequestProperty(headerKeys, mHeaderMap.get(headerKeys));
            }
        }


        connection.connect();

        if(pojoGSon!=null){
            String json = new Gson().toJson(pojoGSon); // buidling the String object
            // containing JSON to be
            // used to pass to service

            Log.i(TAG, "request: " + json);
            new DataOutputStream(connection.getOutputStream()).write(json.getBytes());
        }
        InputStream inputStream = connection.getInputStream();
        return IOUtils.toString(inputStream);

    }

    public String doPutOperation(String url) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(); // using

        connection.setRequestMethod("PUT");

        if (mHeaderMap != null && mHeaderMap.size() > 0) { // Checking if any
            // header needs to
            // be added

            Set<String> keys = mHeaderMap.keySet(); // getting all the keys in
            // the hashtable

            for (String headerKeys : keys) { // looping through the keys
                connection.setRequestProperty(headerKeys, mHeaderMap.get(headerKeys));
            }
        }

        connection.connect();

        InputStream inputStream = connection.getInputStream();
        String response = IOUtils.toString(inputStream);
        Log.e("Response : ", response);
        return response;

    }

    public String doReadFromAssets(String mOperation)
            throws IOException {

        return IOUtils.toString(MyApplication.getContext().getAssets().open(mOperation)).trim();
    }
}
