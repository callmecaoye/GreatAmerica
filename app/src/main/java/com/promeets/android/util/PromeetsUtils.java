package com.promeets.android.util;

import android.animation.ValueAnimator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.Gson;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Pattern;

public final class PromeetsUtils {
    /*
     * Build the url for the get request call according to the required field.
     * */
    public static String buildURL(String operation,String[] key,String[] value){
        String url= Constant.BASE_URL+operation;
        for(int index = 0;index<key.length;index++){
            if(index==0)
                url+="?"+key[index]+"="+value[index];
            else
                url+="&"+key[index]+"="+value[index];
        }

        return url.replace(" ", "%20");
    }

    public static void saveUserData(BaseActivity mBaseActivity, LoginResp loginRespPojo) {
        String json;
        if (loginRespPojo.user != null) {
            json = new Gson().toJson(loginRespPojo.user);
            mBaseActivity.promeetsPreferenceUtil.setValue(PromeetsPreferenceUtil.USER_OBJECT_KEY,json);
        }
        if (loginRespPojo.userProfile != null) {
            ServiceResponseHolder.getInstance().setUserProfile(loginRespPojo.userProfile);
        }

        if (loginRespPojo.expertProfile != null) {
            ServiceResponseHolder.getInstance().setExpertProfile(loginRespPojo.expertProfile);
        }

    }
    public static <T> Object getUserData(String key, Class classType) {
        if(key.equalsIgnoreCase(PromeetsPreferenceUtil.USER_OBJECT_KEY))
        {
            PromeetsPreferenceUtil promeetsPreferenceUtil = new PromeetsPreferenceUtil();
            return promeetsPreferenceUtil.getValue(key,classType);
        }
        else if(key.equalsIgnoreCase(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY))
            return ServiceResponseHolder.getInstance().getExpertProfile();
        else if(key.equalsIgnoreCase(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY))
            return ServiceResponseHolder.getInstance().getUserProfile();
        else return null;
    }

    public static <T> Object getUserDataWithContext(String key, Class classType) {
        if(key.equalsIgnoreCase(PromeetsPreferenceUtil.USER_OBJECT_KEY))
        {
            PromeetsPreferenceUtil promeetsPreferenceUtil = new PromeetsPreferenceUtil();
            return promeetsPreferenceUtil.getValue(key,classType);
        }
        else if(key.equalsIgnoreCase(PromeetsPreferenceUtil.EXPERT_PROFILE_OBJECT_KEY))
            return ServiceResponseHolder.getInstance().getExpertProfile();
        else if(key.equalsIgnoreCase(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY))
            return ServiceResponseHolder.getInstance().getUserProfile();
        else return null;
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void getExperProfile(BaseActivity mBaseActivity, IServiceResponseHandler iServiceResponseHandler,String userId, String expertId){

            HashMap<String, String> header = new HashMap<String, String>();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            mBaseActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels/2;
            int width = displaymetrics.widthPixels/2;
            //header.put(Constant.PROMEETS_SCREEN_HEIGHT,height+"");
            //header.put(Constant.PROMEETS_SCREEN_WIDTH,width+"");


            //Check for internet Connection
            if (!mBaseActivity.hasInternetConnection()) {
                PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
                return;
            }
            PromeetsDialog.showProgress(mBaseActivity);
                String[] key = {Constant.USERID,Constant.EXPERTID,Constant.TIMEZON};
                String[] value = {userId,expertId, TimeZone.getDefault().getID()};
                new GenericServiceHandler(Constant.ServiceType.EXPERT_PROFILE_DETAIL,iServiceResponseHandler, buildURL(Constant.FETCH_EXPERT_DETAIL,key,value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }

    public static double[] getLatLongFromGivenAddress(String youraddress) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                youraddress.replace(" ","%20") + "&result=0&sensor=false";
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            return new double[]{lng,lat};
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String removeHtmlTag(String inputString) {
        if (inputString == null)
            return null;

        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        java.util.regex.Pattern p_special;
        java.util.regex.Matcher m_special;
        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

            // 定义HTML标签的正则表达式
            String regEx_html = "<[^>]+>";

            // 定义一些特殊字符的正则表达式
            String regEx_special = "\\&[a-zA-Z]{1,10};";

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
            m_special = p_special.matcher(htmlStr);
            htmlStr = m_special.replaceAll(""); // 过滤特殊标签
            textStr = htmlStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textStr;
    }

    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight  = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight  = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}
