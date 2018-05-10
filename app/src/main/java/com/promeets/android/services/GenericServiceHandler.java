/**
 * Date : 28-09-2015
 *
 * @author SESA388944 : Shashank Shekhar
 * for Schneider electric : MEA project
 **/


package com.promeets.android.services;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.util.Log;

import com.promeets.android.Constant;
import com.promeets.android.MyApplication;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.pojo.ServiceResponse;

import java.util.HashMap;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class GenericServiceHandler extends PromeetsAsyncTask {

    private Constant.ServiceType mServiceType;
    private String mUrl; //used for HTTP Get service

    private Object pojoGson; // to build JSON request

    private int mServiceOperation; // contain type of request type

    private boolean isDialogRequired; // used for checking if dialog is required

    private String mDialogTitle; // contains progress dialog title

    private String mDialogMessage; // contains progress dialog message

    private ProgressDialog mProgressDialog; // progress dialog

    //private Context context; // mBaseActivity object to build progress dialog and to
    // handle any other UI reference

    private RestfulServiceHandler mRestfulServiceHandler; // RestfulService
    // handler contains
    // actual
    // implementation of
    // service calls

    private String mOperation; // String containing operation type to build the
    // service url

    private IServiceResponseHandler mIServiceResponseHandler; // interface
    // instance used
    // for callback
    // purpose

    private HashMap<String, String> mHeaderMap = new HashMap<>(); // Hashmap
    // contains
    // header
    // info
    // to
    // be
    // passed
    // during
    // service
    // call

    private String TAG = getClass().getName(); // String for logs

    private Throwable mThrowable = null;

    /**
     * @param mIServiceResponseHandler
     * @param pojoGson
     * @param operation
     * @param mHeaderMap
     * @param serviceOperation
     * @param isDialogRequired
     * @param dialogTitle
     * @param dialogMessage
     */
    public GenericServiceHandler(Constant.ServiceType serviceType,
                                 IServiceResponseHandler mIServiceResponseHandler, Object pojoGson,
                                 String operation, HashMap<String, String> mHeaderMap,
                                 int serviceOperation, boolean isDialogRequired, String dialogTitle,
                                 String dialogMessage) {
        this.mServiceType = serviceType;
        this.mIServiceResponseHandler = mIServiceResponseHandler;
        this.pojoGson = pojoGson;
        this.mOperation = operation;
        this.mServiceOperation = serviceOperation;
        this.isDialogRequired = isDialogRequired;
        this.mDialogTitle = dialogTitle;
        this.mDialogMessage = dialogMessage;
        this.mHeaderMap = mHeaderMap;
    }

    /**
     * @param mIServiceResponseHandler
     * @param pojoGson
     * @param operation
     * @param mHeaderMap
     * @param serviceOperation
     * @param isDialogRequired
     * @param dialogTitle
     * @param dialogMessage
     */
    public GenericServiceHandler(Constant.ServiceType serviceType,
                                 IServiceResponseHandler mIServiceResponseHandler, String url, Object pojoGson,
                                 String operation, HashMap<String, String> mHeaderMap,
                                 int serviceOperation, boolean isDialogRequired, String dialogTitle,
                                 String dialogMessage) {
        this.mServiceType = serviceType;
        this.mIServiceResponseHandler = mIServiceResponseHandler;
        this.pojoGson = pojoGson;
        this.mOperation = operation;
        this.mServiceOperation = serviceOperation;
        this.isDialogRequired = isDialogRequired;
        this.mDialogTitle = dialogTitle;
        this.mDialogMessage = dialogMessage;
        this.mHeaderMap = mHeaderMap;
        this.mUrl = url;
    }


    /**
     * @param mIServiceResponseHandler
     * @param url
     * @param mHeaderMap
     * @param serviceOperation
     * @param isDialogRequired
     * @param dialogTitle
     * @param dialogMessage
     */
    public GenericServiceHandler(Constant.ServiceType serviceType,
                                 IServiceResponseHandler mIServiceResponseHandler,
                                 String url, String operation, HashMap<String, String> mHeaderMap,
                                 int serviceOperation, boolean isDialogRequired, String dialogTitle,
                                 String dialogMessage) {
        this.mServiceType = serviceType;
        this.mIServiceResponseHandler = mIServiceResponseHandler;
        this.mUrl = url;
        this.mServiceOperation = serviceOperation;
        this.isDialogRequired = isDialogRequired;
        this.mDialogTitle = dialogTitle;
        this.mDialogMessage = dialogMessage;
        this.mHeaderMap = mHeaderMap;
        this.mOperation = operation;
    }


    public GenericServiceHandler(Constant.ServiceType serviceType,
                                 IServiceResponseHandler mIServiceResponseHandler,
                                 String url, String operation, Object pojoGson, HashMap<String, String> mHeaderMap,
                                 int serviceOperation, boolean isDialogRequired, String dialogTitle,
                                 String dialogMessage) {
        this.mServiceType = serviceType;
        this.mIServiceResponseHandler = mIServiceResponseHandler;
        this.mUrl = url;
        this.mServiceOperation = serviceOperation;
        this.isDialogRequired = isDialogRequired;
        this.mDialogTitle = dialogTitle;
        this.mDialogMessage = dialogMessage;
        this.mHeaderMap = mHeaderMap;
        this.mOperation = operation;
        this.pojoGson = pojoGson;
    }



    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.i(TAG, "onPostExecute:GenericServiceHandler started.");

        if (isDialogRequired
                && !(mProgressDialog != null && mProgressDialog.isShowing())) { // Check
            mProgressDialog = new ProgressDialog(MyApplication.getContext());
            mProgressDialog.setTitle(mDialogTitle);
            mProgressDialog.setMessage(mDialogMessage);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show(); // show the dialog
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(Object... params) {

        String serviceResponse = null; // String object to will contain service
        // response

        Log.i(TAG, "doInBackground:GenericServiceHandler started.");

        try {
            mRestfulServiceHandler = new RestfulServiceHandler(mOperation,
                    mHeaderMap); // build the resfult service object, passing
            // service method to be called and header to
            // be added during service call

            switch (mServiceOperation) { // Checking type of service call
                // required
                case IServiceResponseHandler.GET:
                    serviceResponse = mRestfulServiceHandler.doGetOperation(mUrl);
                    break;
                case IServiceResponseHandler.PUT:
                    serviceResponse = mRestfulServiceHandler.doPutOperation(mUrl);
                    break;
                case IServiceResponseHandler.POST:

                    serviceResponse = mRestfulServiceHandler
                            .doPostOperation(mUrl, pojoGson); // calling post method to
                    // make post calls passing
                    // pojo object which will be
                    // used to form json
                    // request.
                    break;
                case IServiceResponseHandler.READ_JSON_FROM_ASSETS:
                    serviceResponse = mRestfulServiceHandler.doReadFromAssets(mOperation);
                    break;
            }

            Log.i(TAG, "Service Response : " + serviceResponse);

        } catch (Exception e) {
            mThrowable = e; // exceptions assigned to throwable object which be
            // use to call onErrorRespose of
            // IServiceResposeHandler
            e.printStackTrace();
        }

        return serviceResponse; // Passing the response to onPostExecute
        // callback method of asynctask
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);

        if (isDialogRequired
                && (mProgressDialog != null && mProgressDialog.isShowing())) { // Dismiss
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (mThrowable != null) { // checking if any exception has occurred
            Exception exception = new Exception("Please check your internet connection. If problem continues please call support.");
            mIServiceResponseHandler.onErrorResponse(exception);
        } else { // Happy flow: call the onServiceResponse to handle the
            // response
            ServiceResponse response = new ServiceResponse();
            response.setServiceResponse(result.toString());
            mIServiceResponseHandler.onServiceResponse(response,mServiceType);
        }
    }
}
