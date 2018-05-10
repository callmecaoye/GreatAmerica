package com.promeets.android.fragment;

import com.promeets.android.MyApplication;
import android.content.Context;
import android.content.SharedPreferences;
import com.promeets.android.custom.PromeetsBottomBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.HomeActivity;
import com.promeets.android.adapter.RecycleChannelAdapter;
import com.promeets.android.adapter.RecycleNotificationAdapter;
import com.promeets.android.api.NotificationRelatedApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.Gson;
import com.promeets.android.Constant;
import com.promeets.android.object.NotificationPOJO;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.NotificationResp;
import com.promeets.android.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.UserInfoHelper;
import com.promeets.android.util.Utility;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * 4th page of HomeActivity
 *
 * @destination: AppointStatusActivity, ExpertDetailActivity
 *
 */

public class NotificationListFragment extends Fragment {

    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";

    @BindView(R.id.activity_notification_list_tabHost)
    TabHost tabHost;

    @BindView(R.id.activity_notification_list_notification_list)
    RecyclerView mRVNotification;

    @BindView(R.id.channel_list)
    RecyclerView mRVChannel;

    @BindView(R.id.activity_notification_list_notification_btn)
    RelativeLayout notification_btn;

    @BindView(R.id.activity_notification_list_notification_icon)
    ImageView notification_icon;

    @BindView(R.id.activity_notification_list_message_btn)
    RelativeLayout message_btn;

    @BindView(R.id.activity_notification_list_message_icon)
    ImageView message_icon;

    @BindView(R.id.activity_notification_list_unread_notification_number)
    TextView notification_number;

    @BindView(R.id.activity_notification_list_unread_message_number)
    TextView message_number;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mLayRefresh;

    RecycleNotificationAdapter mAdapterNotify;
    RecycleChannelAdapter mAdapterChannel;
    ArrayList<NotificationPOJO> notifyList = new ArrayList<NotificationPOJO>();
    ArrayList<NotificationPOJO> oneSignalList = new ArrayList<NotificationPOJO>();
    //List<GroupChannel> channelList = new ArrayList<>();
    //EaseConversationListFragment conversationListFragment;

    String conversationId = "";
    UserPOJO userPOJO;

    private int pageNumber = 1;
    private BaseActivity mBaseActivity;
    private PromeetsBottomBar bottomBar;

    private int numOfNotify;
    private int numOfMsg;

    private GroupChannelListQuery mChannelListQuery;

    public NotificationListFragment() {

    }

    public static NotificationListFragment newInstance() {
        //Bundle args = new Bundle();
        NotificationListFragment sampleFragment = new NotificationListFragment();
        //args.putString("query", query);
        //sampleFragment.setArguments(args);
        return sampleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapterChannel = new RecycleChannelAdapter((BaseActivity) getActivity());
        mAdapterChannel.load();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapterChannel.save();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View accountFragment = inflater.inflate(R.layout.fragment_notification_list, container, false);
        mBaseActivity = (BaseActivity) getActivity();
        bottomBar = (PromeetsBottomBar) getActivity().findViewById(R.id.bottomBar);
        ButterKnife.bind(this, accountFragment);

        // local notification from One Signal
        SharedPreferences mPrefs = MyApplication.getContext().getSharedPreferences("PromeetsTmp", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String list = mPrefs.getString("onesignal", "");
        if (!StringUtils.isEmpty(list)) {
            String[] ids = list.split(",");
            if (ids != null && ids.length > 0) {
                for (String id : ids) {
                    String json = mPrefs.getString(id, "");
                    NotificationPOJO pojo = gson.fromJson(json, NotificationPOJO.class);
                    oneSignalList.add(pojo);
                }
                Collections.sort(oneSignalList);
            }
        }

        UserInfoHelper userInfoHelper = new UserInfoHelper(mBaseActivity);
        userPOJO = userInfoHelper.getUserObject();

        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Special Selection");
        spec.setContent(R.id.activity_notification_list_tab1);
        spec.setIndicator("Notifications");
        tabHost.addTab(spec);

        //Tab 2
        spec = tabHost.newTabSpec("Recommend");
        spec.setContent(R.id.activity_notification_list_tab2);
        spec.setIndicator("Messages");
        tabHost.addTab(spec);

        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification_number.setTextColor(getResources().getColor(R.color.primary));
                notification_number.setBackground(getResources().getDrawable(R.drawable.circle_message_number_select));
                message_number.setTextColor(getResources().getColor(R.color.white));
                message_number.setBackground(getResources().getDrawable(R.drawable.circle_message_number_unselect));
                notification_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_notify_white));
                notification_btn.setBackground(getResources().getDrawable(R.drawable.left_select));
                notification_btn.setBackground(getResources().getDrawable(R.drawable.left_select));
                message_btn.setBackground(getResources().getDrawable(R.drawable.right_unselect));
                message_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_primary));
                tabHost.setCurrentTab(0);
            }
        });
        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification_number.setTextColor(getResources().getColor(R.color.white));
                notification_number.setBackground(getResources().getDrawable(R.drawable.circle_message_number_unselect));
                message_number.setTextColor(getResources().getColor(R.color.primary));
                message_number.setBackground(getResources().getDrawable(R.drawable.circle_message_number_select));
                notification_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_notify_primary));
                message_btn.setBackground(getResources().getDrawable(R.drawable.right_select));
                notification_btn.setBackground(getResources().getDrawable(R.drawable.left_unselect));
                message_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_msg_white));
                tabHost.setCurrentTab(1);
            }
        });
        if (((HomeActivity) getActivity()).getTabIndex() == 1)
            message_btn.performClick();


        mRVNotification.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        if (userPOJO == null && oneSignalList != null && oneSignalList.size() > 0) {
            notifyList.addAll(oneSignalList);
        }
        mAdapterNotify = new RecycleNotificationAdapter(notifyList, oneSignalList, mBaseActivity);
        mRVNotification.setAdapter(mAdapterNotify);

        mLayRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(500);
            }
        });

        if (userPOJO != null) {
            getNotificationList(pageNumber);
            getUnreadNotificationNumber();
            mLayRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
                @Override
                public void onLoadmore(RefreshLayout refreshlayout) {
                    getNotificationList(++pageNumber);
                }
            });

            mRVChannel.setLayoutManager(new LinearLayoutManager(mBaseActivity));
            //channelListQuery = GroupChannel.createMyGroupChannelListQuery();
            mRVChannel.setAdapter(mAdapterChannel);

        }
        return accountFragment;
    }

    private void getNotificationList(int pageNumber) {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mBaseActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("notification/history"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NotificationRelatedApi service = retrofit.create(NotificationRelatedApi.class);
        Call<NotificationResp> call = service.getNotificationList(userPOJO.id, pageNumber);

        call.enqueue(new Callback<NotificationResp>() {
            @Override
            public void onResponse(Call<NotificationResp> call, Response<NotificationResp> response) {
                PromeetsDialog.hideProgress();
                mLayRefresh.finishLoadmore();
                NotificationResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mBaseActivity, response.errorBody().toString());
                    return;
                }

                if (mBaseActivity.isSuccess(result.info.code)) {
                    if (result.dataList != null && result.dataList.size() > 0) {
                        notifyList.addAll(result.dataList);
                        if (oneSignalList != null && oneSignalList.size() > 0) {
                            for (NotificationPOJO pojo : oneSignalList) {
                                if (result.dataList.get(result.dataList.size() - 1).compareTo(pojo) >= 0)
                                    notifyList.add(pojo);
                            }
                            Collections.sort(notifyList);
                        }
                        mAdapterNotify.notifyDataSetChanged();
                    } else
                        mLayRefresh.setLoadmoreFinished(true);
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(mBaseActivity, result.info.code);
                } else
                    PromeetsDialog.show(mBaseActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<NotificationResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                mLayRefresh.finishLoadmore();
            }
        });
    }

    private void getUnreadNotificationNumber() {
        if (!mBaseActivity.hasInternetConnection()) {
            PromeetsDialog.show(mBaseActivity, mBaseActivity.getString(R.string.no_internet));
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("notification/getUnreadMsgCount"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserInfoHelper userInfoHelper = new UserInfoHelper(mBaseActivity);
        UserPOJO userPOJO = userInfoHelper.getUserObject();
        if (userPOJO != null) {
            NotificationRelatedApi service = retrofit.create(NotificationRelatedApi.class);
            Call<NotificationResp> call = service.getUnreadMsgCount(userPOJO.id);
            call.enqueue(new Callback<NotificationResp>() {
                @Override
                public void onResponse(Call<NotificationResp> call, Response<NotificationResp> response) {
                    NotificationResp result = response.body();
                    if (result != null && result.info != null && result.info.code.equals("200")) {
                        if (result.msgCount != 0 ||
                                (oneSignalList != null && oneSignalList.size() > 0)) {
                            notification_number.setVisibility(View.VISIBLE);
                            numOfNotify = result.msgCount + Utility.unreadOneSignal();
                        } else {
                            notification_number.setVisibility(View.GONE);
                        }
                        if (numOfNotify > 99)
                            notification_number.setText("...");
                        else
                            notification_number.setText(numOfNotify + "");
                        bottomBar.setUnreadNumber(numOfNotify + numOfMsg);
                    } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE) || result.info.code.equals(Constant.UPDATE_TIME_STAMP) || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                        Utility.onServerHeaderIssue(mBaseActivity, result.info.code);
                    }
                }

                @Override
                public void onFailure(Call<NotificationResp> call, Throwable t) {
                    PromeetsDialog.show(mBaseActivity, t.getLocalizedMessage());
                }
            });
        }

    }

    public void getUnreadMessageNumber() {
        //int count = EMClient.getInstance().chatManager().getUnreadMessageCount();
        GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
            @Override
            public void onResult(int count, SendBirdException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                if (count != 0) {
                    message_number.setVisibility(View.VISIBLE);
                } else {
                    message_number.setVisibility(View.GONE);
                }
                message_number.setText(count + "");

                numOfMsg = count;
                bottomBar.setUnreadNumber(numOfNotify + numOfMsg);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (userPOJO != null) {
            getUnreadNotificationNumber();
            getUnreadMessageNumber();

            refreshChannelList();
            SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
                @Override
                public void onMessageReceived(final BaseChannel baseChannel, BaseMessage baseMessage) {
                    // Received a chat message.
                    mAdapterChannel.updateOrInsert(baseChannel);
                    getUnreadMessageNumber();
                    ((HomeActivity)mBaseActivity).getNotificationCount();
                }

                @Override
                public void onReadReceiptUpdated(GroupChannel groupChannel) {
                    // When read receipt has been updated.
                    mAdapterChannel.notifyDataSetChanged();
                }

                @Override
                public void onTypingStatusUpdated(GroupChannel groupChannel) {
                    // When typing status has been updated.
                    mAdapterChannel.notifyDataSetChanged();
                }
            });
        } else if (oneSignalList != null && oneSignalList.size() > 0) {
            int unread = Utility.unreadOneSignal();
            if (unread > 0) {
                notification_number.setVisibility(View.VISIBLE);
                if (unread > 99)
                    notification_number.setText("...");
                else
                    notification_number.setText(unread + "");
            } else
                notification_number.setVisibility(View.GONE);
            bottomBar.setUnreadNumber(unread);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((HomeActivity) getActivity()).setTabIndex(tabHost.getCurrentTab());
    }

    /**
     * Creates a new query to get the list of the user's Group Channels,
     * then replaces the existing dataset.
     *
     */
    private void refreshChannelList() {
        mChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
        mChannelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }
                mAdapterChannel.setGroupChannelList(list);
            }
        });
    }
}