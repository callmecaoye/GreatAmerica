package com.promeets.android.adapter;

import com.promeets.android.activity.AppointStatusActivity;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.GroupChatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.promeets.android.object.Appoint;
import com.promeets.android.object.ChatUserInfo;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.ChatHelper;
import com.promeets.android.util.UserInfoHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sosasang on 8/24/17.
 */

public class RecycleAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private BaseActivity mBaseActivity;
    private ArrayList<Appoint> listService;
    private LayoutInflater mInflater;

    public RecycleAppointmentAdapter(BaseActivity baseActivity, ArrayList<Appoint> items) {
        this.mBaseActivity = baseActivity;
        this.listService = items;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycle_appointment_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Appoint appoint = listService.get(position);
            ItemViewHolder viewHolder = (ItemViewHolder) holder;

            if (appoint.eventData.ifHasRead == 0) {
                viewHolder.mLayRoot.setBackgroundColor(mBaseActivity.getResources().getColor(R.color.pm_white));
                viewHolder.unread.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mLayRoot.setBackgroundColor(Color.WHITE);
                viewHolder.unread.setVisibility(View.INVISIBLE);
            }

            if(!StringUtils.isEmpty(appoint.displayPhotoUrl)){
                Glide.with(mBaseActivity).load(appoint.displayPhotoUrl).into(viewHolder.photo);
            }

            switch (appoint.eventData.meetingType) {
                case 0:
                    viewHolder.type.setVisibility(View.GONE);
                    break;
                case 1: // In-person meeting
                    viewHolder.type.setVisibility(View.VISIBLE);
                    viewHolder.type.setImageDrawable(ContextCompat.getDrawable(mBaseActivity, R.drawable.ic_users));
                    break;
                case 2: // Online call
                    viewHolder.type.setVisibility(View.VISIBLE);
                    viewHolder.type.setImageDrawable(ContextCompat.getDrawable(mBaseActivity, R.drawable.ic_phone_green));
            }

            viewHolder.mTxtTopic.setText(appoint.expertService.title);
            viewHolder.mTxtName.setText(appoint.displayName);
            viewHolder.mTxtPosition.setText(appoint.displayPosition);
            viewHolder.mTxtPrice.setText("$" + appoint.expertService.price);
            viewHolder.mTxtStatus.setText(appoint.eventAction.readableStatus);
            if (appoint.eventAction.readableStatus.equalsIgnoreCase("completed")
                    || appoint.eventAction.readableStatus.toLowerCase().contains("cancelled")
                    || appoint.eventAction.readableStatus.toLowerCase().contains("declined"))
                viewHolder.mTxtStatus.setTextColor(mBaseActivity.getResources().getColor(R.color.pm_dark));
            else
                viewHolder.mTxtStatus.setTextColor(mBaseActivity.getResources().getColor(R.color.primary));

            viewHolder.mLayRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mBaseActivity, AppointStatusActivity.class);
                    intent.putExtra("eventRequestId", appoint.eventData.id);
                    mBaseActivity.startActivity(intent);
                }
            });

            /*if (appoint.eventAction.readableStatus.toLowerCase().contains("cancelled")
                    || appoint.eventAction.readableStatus.toLowerCase().contains("declined"))
                viewHolder.mTxtChat.setVisibility(View.GONE);
            else
                viewHolder.mTxtChat.setVisibility(View.VISIBLE);*/
            viewHolder.mTxtChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mBaseActivity, GroupChatActivity.class);
                    intent.putExtra("targetName", appoint.displayName);
                    UserInfoHelper helper = new UserInfoHelper(mBaseActivity);
                    if (helper.getExpertProfile() != null
                            && appoint.eventData.expId == helper.getExpertProfile().expId) {
                        intent.putExtra("targetId", "userName" + appoint.eventData.userId);
                    } else
                        intent.putExtra("targetId", "userName" + appoint.eventData.expId);
                    mBaseActivity.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        if (listService == null
                || listService.isEmpty()) return 0;
        return listService.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        LinearLayout mLayRoot;
        CircleImageView unread, photo;
        ImageView type;
        TextView mTxtTopic,mTxtName,mTxtPosition,mTxtStatus,mTxtPrice,mTxtChat;
        Context context;
        String username = null;
        String password = null;


        public ItemViewHolder(View view){
            super(view);
            context = view.getContext();
            mLayRoot = (LinearLayout) view.findViewById(R.id.root_layout);
            unread = (CircleImageView) view.findViewById(R.id.unread);
            photo = (CircleImageView) view.findViewById(R.id.photo);
            type = view.findViewById(R.id.type);
            mTxtTopic = (TextView) view.findViewById(R.id.topic);
            mTxtName = (TextView) view.findViewById(R.id.name);
            mTxtPosition = (TextView) view.findViewById(R.id.position);
            mTxtStatus = (TextView)view.findViewById(R.id.status);
            mTxtPrice = (TextView)view.findViewById(R.id.price);
            mTxtChat = (TextView)view.findViewById(R.id.chat);
            if(username==null&&password==null){
                ChatHelper chatHelper = new ChatHelper(context);
                ChatUserInfo chatUserInfoPOJO = chatHelper.getUserObject();
                if(chatUserInfoPOJO!=null){
                    username = chatUserInfoPOJO.username;
                    password = chatUserInfoPOJO.password;
                }else{
                    //todo:can not initial chat, popup message;
                }
            }
        }
    }
}
