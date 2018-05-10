package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.GroupChatActivity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import com.promeets.android.util.Md5;
import com.promeets.android.util.UserInfoHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.R;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.UserMessage;
import com.promeets.android.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sosasang on 11/22/17.
 */

public class RecycleChannelAdapter extends RecyclerView.Adapter {
    private List<GroupChannel> mChannelList;
    private LayoutInflater mInflater;
    private BaseActivity mBaseActivity;
    private UserPOJO userPOJO;

    public RecycleChannelAdapter(BaseActivity mBaseActivity) {
        this.mBaseActivity = mBaseActivity;
        mInflater = LayoutInflater.from(mBaseActivity);
        UserInfoHelper userInfoHelper = new UserInfoHelper(mBaseActivity);
        userPOJO = userInfoHelper.getUserObject();
        mChannelList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycle_notification_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            GroupChannel pojo = mChannelList.get(position);

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            for (Member member : pojo.getMembers()) {
                if (!member.getUserId().substring(8).equals(userPOJO.id.toString())) {
                    viewHolder.targetId = member.getUserId();
                    viewHolder.targetName = member.getNickname();
                    viewHolder.txttitle.setText(member.getNickname());
                    Glide.with(mBaseActivity).load(member.getProfileUrl()).into(viewHolder.icon);
                }
            }

            if (pojo.getLastMessage() != null) {
                if (pojo.getLastMessage() instanceof UserMessage) {
                    UserMessage message = (UserMessage) pojo.getLastMessage();
                    if (message.getCustomType().equals(GroupChatAdapter.APPOINT_CUSTOM_TYPE))
                        viewHolder.txtcontent.setText("[Appointment]");
                    else
                        viewHolder.txtcontent.setText(message.getMessage());
                } else if (pojo.getLastMessage() instanceof FileMessage) {
                    FileMessage message = (FileMessage) pojo.getLastMessage();
                    String type = message.getType().toLowerCase();
                    if (type.contains("image"))
                        viewHolder.txtcontent.setText("[Image]");
                    else
                        viewHolder.txtcontent.setText("[File]");
                }
            }

            // todo: wrap to a method
            Calendar now = Calendar.getInstance();
            long nowMs = now.getTimeInMillis();
            long preMs = pojo.getLastMessage().getCreatedAt();
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

            int unreadCount = pojo.getUnreadMessageCount();
            if (unreadCount > 0){
                viewHolder.unreadmark.setVisibility(View.VISIBLE);
            } else {
                viewHolder.unreadmark.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public void setGroupChannelList(List<GroupChannel> channelList) {
        mChannelList = channelList;
        notifyDataSetChanged();
    }

    public void load() {
        try {
            File appDir = new File(mBaseActivity.getCacheDir(), SendBird.getApplicationId());
            appDir.mkdirs();

            File dataFile = new File(appDir, Md5.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

            String content = FileUtils.loadFromFile(dataFile);
            String [] dataArray = content.split("\n");

            // Reset channel list, then add cached data.
            mChannelList.clear();
            for(int i = 0; i < dataArray.length; i++) {
                mChannelList.add((GroupChannel) BaseChannel.buildFromSerializedData(Base64.decode(dataArray[i], Base64.DEFAULT | Base64.NO_WRAP)));
            }

            notifyDataSetChanged();
        } catch(Exception e) {
            // Nothing to load.
        }
    }

    public void save() {
        try {
            StringBuilder sb = new StringBuilder();
            if (mChannelList != null && mChannelList.size() > 0) {
                // Convert current data into string.
                GroupChannel channel = null;
                for (int i = 0; i < Math.min(mChannelList.size(), 100); i++) {
                    channel = mChannelList.get(i);
                    sb.append("\n");
                    sb.append(Base64.encodeToString(channel.serialize(), Base64.DEFAULT | Base64.NO_WRAP));
                }
                // Remove first newline.
                sb.delete(0, 1);

                String data = sb.toString();
                String md5 = Md5.generateMD5(data);

                // Save the data into file.
                File appDir = new File(mBaseActivity.getCacheDir(), SendBird.getApplicationId());
                appDir.mkdirs();

                File hashFile = new File(appDir, Md5.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".hash");
                File dataFile = new File(appDir, Md5.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

                try {
                    String content = FileUtils.loadFromFile(hashFile);
                    // If data has not been changed, do not save.
                    if(md5.equals(content)) {
                        return;
                    }
                } catch(IOException e) {
                    // File not found. Save the data.
                }

                FileUtils.saveToFile(dataFile, data);
                FileUtils.saveToFile(hashFile, md5);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // If the channel is not in the list yet, adds it.
    // If it is, finds the channel in current list, and replaces it.
    // Moves the updated channel to the front of the list.
    public void updateOrInsert(BaseChannel channel) {
        if (!(channel instanceof GroupChannel)) {
            return;
        }

        GroupChannel groupChannel = (GroupChannel) channel;

        for (int i = 0; i < mChannelList.size(); i++) {
            if (mChannelList.get(i).getUrl().equals(groupChannel.getUrl())) {
                mChannelList.remove(mChannelList.get(i));
                mChannelList.add(0, groupChannel);
                notifyDataSetChanged();
                //Log.v(GroupChannelListAdapter.class.getSimpleName(), "Channel replaced.");
                return;
            }
        }

        mChannelList.add(0, groupChannel);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txttitle,txtcontent,txtdate;
        CircleImageView icon, unreadmark;
        String targetId, targetName;

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
        public void onClick(View view) {
            Intent intent = new Intent(mBaseActivity, GroupChatActivity.class);
            intent.putExtra("targetId", targetId);
            intent.putExtra("targetName", targetName);
            mBaseActivity.startActivity(intent);

            /*int position = getAdapterPosition();
            GroupChannel channel = mChannelList.get(position);
            channel.leave(null);*/
        }
    }
}
