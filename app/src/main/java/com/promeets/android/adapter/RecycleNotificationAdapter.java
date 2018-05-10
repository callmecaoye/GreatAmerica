package com.promeets.android.adapter;

import com.promeets.android.MyApplication;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.BillManagementActivity;
import com.promeets.android.activity.ExpertDashboardActivity;
import com.promeets.android.activity.ExpertDetailActivity;
import com.promeets.android.activity.NotificationDetailActivity;
import com.promeets.android.activity.SurveyActivity;
import com.promeets.android.api.URL;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.Constant;
import com.promeets.android.object.GlobalVariable;
import com.promeets.android.object.NotificationPOJO;
import com.promeets.android.pojo.NotificationResp;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.activity.ExpertProfileActivity;
import com.promeets.android.activity.UserProfileActivity;
import com.promeets.android.api.NotificationRelatedApi;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecycleNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NotificationPOJO> listService = new ArrayList<>();
    private ArrayList<NotificationPOJO> list = new ArrayList<>();

    private LayoutInflater mInflater;
    private BaseActivity mBaseActivity;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Gson gson = new Gson();

    public RecycleNotificationAdapter(ArrayList<NotificationPOJO> results, ArrayList<NotificationPOJO> oneSignal, BaseActivity baseActivity){
        listService = results;
        list = oneSignal;
        this.mBaseActivity = baseActivity;
        mInflater = LayoutInflater.from(mBaseActivity);
        mPrefs = MyApplication.getContext().getSharedPreferences("PromeetsTmp", Context.MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.recycle_notification_item, parent, false);
            return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            NotificationPOJO pojo = listService.get(position);
            if (!StringUtils.isEmpty(GlobalVariable.msgId)
                    && pojo.msgId.equalsIgnoreCase(GlobalVariable.msgId)) {
                setNotificationAsRead(pojo.msgId, position);
            }

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.txttitle.setText(pojo.msgTitle);
            viewHolder.txtcontent.setText(pojo.msgContent);
            viewHolder.txtdate.setText(pojo.lastSendTime);

            // todo: wrap to a method
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Calendar now = Calendar.getInstance();
                Calendar past = (Calendar) now.clone();
                past.setTime(format.parse(pojo.lastSendTime));




                long nowMs = now.getTimeInMillis();
                long preMs = past.getTimeInMillis();
                long diff = (nowMs - preMs) / 1000;
                long minMs = 60;


                long diffMin = diff / minMs;
                long diffHour = diff / (minMs * 60);
                long diffDay = diff / (minMs * 60 * 24);
                long diffMonth = diff / (minMs * 60 * 24 * 30);
                long diffYear = diff / (minMs * 60 * 24 * 30 * 365);

                if (diffMin < 60) {
                    if (diffMin % 60 == 0)
                        viewHolder.txtdate.setText("Now");
                    else if (diff % 60 == 1)
                        viewHolder.txtdate.setText("1 minute ago");
                    else
                        viewHolder.txtdate.setText(diffMin + " minutes ago");
                } else if (diffHour < 24) {
                    if (diffHour % 24 == 1)
                        viewHolder.txtdate.setText("1 hour ago");
                    else
                        viewHolder.txtdate.setText(diffHour + " hours ago");
                } else if (diffDay < 30) {
                    if (diffDay % 30 == 1)
                        viewHolder.txtdate.setText("Yesterday");
                    else
                        viewHolder.txtdate.setText(diffDay + " days ago");
                } else if (diffMonth < 12) {
                    if (diffMonth % 12 == 1)
                        viewHolder.txtdate.setText("1 month ago");
                    else
                        viewHolder.txtdate.setText(diffMonth + " months ago");
                } else {
                    if (diffYear == 1)
                        viewHolder.txtdate.setText("1 year ago");
                    else
                        viewHolder.txtdate.setText(diffYear + " years ago");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!StringUtils.isEmpty(pojo.iconUrl)){
                Glide.with(viewHolder.icon.getContext()).load(pojo.iconUrl).into(viewHolder.icon);
            } else {
                viewHolder.icon.setImageResource(R.drawable.app_icon);
            }

            if(listService.get(position).readFlag==0){
                viewHolder.unreadmark.setVisibility(View.VISIBLE);
            }else{
                viewHolder.unreadmark.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (listService == null
                || listService.isEmpty()) return 0;
        return listService.size();
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txttitle,txtcontent,txtdate;
        CircleImageView icon, unreadmark;

        public ItemViewHolder(View view){
            super(view);
            txttitle = (TextView) view.findViewById(R.id.list_notification_list_title);
            txtcontent = (TextView) view.findViewById(R.id.list_notification_list_content);
            txtdate = (TextView) view.findViewById(R.id.list_notification_list_date);
            icon = (CircleImageView) view.findViewById(R.id.list_notification_list_icon);
            unreadmark = (CircleImageView) view.findViewById(R.id.list_notification_unread_mark);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /**
             * Case of msgUrl:
             * if promeets://order/678 -> go to EventProcessStatusActivity
             * if promeets://expert/108 -> go to ExpertDetailActivity
             * if promeets://balance -> go to BillManagementActivity
             * if promeets://profile/108 -> go to UserProfileActivity
             * if promeets://becomeexp -> ExpertProfileActivity
             * if promeets://expertdashboard -> ExpertDashboardActivity
             * if promeets://ordersurvey/orderId/748
             * if null -> go to notificationDetailActivity
             */
            int position = getAdapterPosition();
            NotificationPOJO notificationPOJO = listService.get(position);
            if (list.contains(notificationPOJO)) {
                markAsRead(position);
                String json = gson.toJson(notificationPOJO);
                prefsEditor.putString(notificationPOJO.msgId, json);
                prefsEditor.commit();
            } else
                setNotificationAsRead(notificationPOJO.msgId, position);
            if(notificationPOJO.msgUrl!=null){
                if (notificationPOJO.msgUrl.contains("promeets://ordersurvey/orderId/")) {
                    int orderId = 0;
                    String tmp[] = notificationPOJO.msgUrl.split("/");
                    try {
                        orderId = Integer.parseInt(tmp[tmp.length-1]);
                    } catch (Exception e){ }
                    //resultIntent= new Intent(this, EventProcessStatusActivity.class);
                    Intent intent = new Intent(mBaseActivity, SurveyActivity.class);
                    intent.putExtra("orderId", orderId);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (notificationPOJO.msgUrl.contains("order")) {
                    int requestId = 0;
                    String tmp[] = notificationPOJO.msgUrl.split("/");
                    try {
                        requestId = Integer.parseInt(tmp[tmp.length-1]);
                    } catch (Exception e){ }
                    //Intent intent = new Intent(mBaseActivity, EventProcessStatusActivity.class);
                    Intent intent = new Intent(mBaseActivity, AppointStatusActivity.class);
                    intent.putExtra("eventRequestId", requestId);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (notificationPOJO.msgUrl.contains("expertdashboard")) {
                    mBaseActivity.startActivity(ExpertDashboardActivity.class);
                } else if (notificationPOJO.msgUrl.contains("expert")) {
                    String expId = "";
                    String tmp[] = notificationPOJO.msgUrl.split("/");
                    try {
                        expId = tmp[tmp.length-1];
                    } catch (Exception e){ }
                    Intent intent = new Intent(mBaseActivity, ExpertDetailActivity.class);
                    intent.putExtra("expId", expId);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (notificationPOJO.msgUrl.contains("balance")) {
                    Intent intent = new Intent(mBaseActivity, BillManagementActivity.class);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (notificationPOJO.msgUrl.contains("profile")) {
                    int userId = 0;
                    String tmp[] = notificationPOJO.msgUrl.split("/");
                    try {
                        userId = Integer.parseInt(tmp[tmp.length-1]);
                    } catch (Exception e){ }
                    Intent intent = new Intent(mBaseActivity,UserProfileActivity.class);
                    intent.putExtra("id", userId);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (notificationPOJO.msgUrl.contains("becomeexp")) {
                    Intent intent = new Intent(mBaseActivity, ExpertProfileActivity.class);
                    mBaseActivity.startActivity(intent);
                    mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            } else {
                Gson gson = new Gson();
                String jsonStr = gson.toJson(notificationPOJO);
                Intent intent = new Intent(mBaseActivity, NotificationDetailActivity.class);
                intent.putExtra("notificationPOJO", jsonStr);
                mBaseActivity.startActivity(intent);
                mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        }
    }

    public void markAsRead(int position){
        listService.get(position).readFlag = 1;
        notifyDataSetChanged();
    }

    public void setNotificationAsRead(String msgId, final int position){
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("notification/updateReadFlag"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("msgId", msgId);
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        NotificationRelatedApi service = retrofit.create(NotificationRelatedApi.class);
        Call<NotificationResp> call = service.updateReadFlag(requestBody);

        call.enqueue(new Callback<NotificationResp>() {
            @Override
            public void onResponse(Call<NotificationResp> call, Response<NotificationResp> response) {
                NotificationResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if(mBaseActivity.isSuccess(result.info.code)){
                    markAsRead(position);
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(mBaseActivity,result.info.code);
                } else
                    PromeetsDialog.show(mBaseActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<NotificationResp> call, Throwable t) {
                PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
            }
        });
    }
}
