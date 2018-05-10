package com.promeets.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.promeets.android.adapter.GroupChatAdapter;
import com.promeets.android.api.EventApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.DefaultRationale;
import com.promeets.android.custom.PermissionSetting;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.GroupChatExpertFragment;
import com.promeets.android.fragment.GroupChatUserFragment;
import com.promeets.android.fragment.ServiceSelectChatFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.kevin.crop.UCrop;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.object.ChatAppointPOJO;
import com.promeets.android.object.UrlPreviewInfo;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.pojo.EventDetailResp;
import com.promeets.android.pojo.ServiceListDetailResp;
import com.promeets.android.pojo.ServiceListResp;
import com.promeets.android.pojo.ServiceResponse;
import com.promeets.android.R;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.promeets.android.services.GenericServiceHandler;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.FileUtils;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import com.promeets.android.util.WebUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupChatActivity extends BaseActivity implements IServiceResponseHandler {

    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";

    private static final String LOG_TAG = "SENDBIRD_GROUP_CHAT";

    private static final int STATE_NORMAL = 0;
    private static final int STATE_EDIT = 1;

    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT";

    private static final int REQUEST_CAMERA = 10, REQUEST_GALLERY = 11;

    @BindView(R.id.target)
    TextView mTxtTarget;
    @BindView(R.id.button_group_chat_upload)
    ImageButton mUploadFileButton;
    @BindView(R.id.edittext_group_chat_message)
    EditText mMessageEditText;
    @BindView(R.id.button_group_chat_send)
    Button mMessageSendButton;
    @BindView(R.id.recycler_group_chat)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_group_chat_root)
    RelativeLayout mRootLayout;
    @BindView(R.id.text_group_chat_current_event)
    TextView mCurrentEventText;
    @BindView(R.id.layout_group_chat_current_event)
    LinearLayout mCurrentEventLayout;
    @BindView(R.id.set_time)
    TextView mViewSetup;

    private GroupChatAdapter mChatAdapter;

    private InputMethodManager mIMM;
    private LinearLayoutManager mLayoutManager;
    private HashMap<BaseChannel.SendFileMessageWithProgressHandler, FileMessage> mFileProgressHandlerMap;

    private String targetId;
    private String targetName;
    private GroupChannel mChannel;
    private String mChannelUrl;

    private boolean mIsTyping;

    private int mCurrentState = STATE_NORMAL;
    private BaseMessage mEditingMessage = null;

    ArrayList<ServiceListDetailResp> dataList;

    private String mTempPhotoPath;
    private Uri mDestinationUri;

    private Rationale mRationale;
    private PermissionSetting mSetting;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mMessageSendButton.setEnabled(true);
                } else {
                    mMessageSendButton.setEnabled(false);
                }
            }
        });

        mMessageSendButton.setEnabled(false);
        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentState == STATE_EDIT) {
                    String userInput = mMessageEditText.getText().toString();
                    if (userInput.length() > 0) {
                        if (mEditingMessage != null) {
                            editMessage(mEditingMessage, userInput);
                        }
                    }
                    setState(STATE_NORMAL, null, -1);
                } else {
                    String userInput = mMessageEditText.getText().toString();
                    if (userInput.length() > 0) {
                        sendUserMessage(userInput);
                        mMessageEditText.setText("");
                    }
                }
            }
        });

        mUploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestMedia();
                requestPhotoPermission();
            }
        });

        mIsTyping = false;
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mIsTyping) {
                    setTypingStatus(true);
                }

                if (s.length() == 0) {
                    setTypingStatus(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mViewSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataList != null && dataList.size() > 0) {
                    if (dataList.size() == 1)
                        OnItemSelect(dataList, 0);
                    else {
                        ServiceSelectChatFragment dialogFragment = ServiceSelectChatFragment.newInstance(dataList);
                        dialogFragment.show(getSupportFragmentManager(), "send");
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);

        mIMM = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mFileProgressHandlerMap = new HashMap<>();
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), "cropImage.jpeg"));

        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(this);

        targetId = getIntent().getStringExtra("targetId");
        targetName = getIntent().getStringExtra("targetName");
        mChannelUrl = getIntent().getStringExtra("channelUrl");
        mTxtTarget.setText(targetName);

        if (!StringUtils.isEmpty(mChannelUrl)) {
            // from Notification
            Log.d("SendBird", "channelUrl: " + mChannelUrl);
            setUpSendBird();
        } else if (!StringUtils.isEmpty(targetId)) {
            List<String> userIds = new ArrayList<>();
            userIds.add(targetId);
            GroupChannel.createChannelWithUserIds(userIds, true, new GroupChannel.GroupChannelCreateHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        return;
                    } else {
                        groupChannel.markAsRead();
                        mChannelUrl = groupChannel.getUrl();
                        Log.d("SendBird", "channelUrl: " + mChannelUrl);
                        setUpSendBird();
                    }
                }
            });
        }

        mChatAdapter = new GroupChatAdapter(this);
        setUpChatListAdapter();

        // Load messages from cache.
        mChatAdapter.load(mChannelUrl);
        setUpRecyclerView();

        if (dataList == null)
            dataList = new ArrayList<>();
        else
            dataList.clear();
        getServiceList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatAdapter.setContext(this); // Glide bug fix (java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        setTypingStatus(false);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        SendBird.removeConnectionHandler(CONNECTION_HANDLER_ID);

        // Save messages to cache.
        mChatAdapter.save();
    }

    private void refresh() {
        if (mChannel == null) {
            GroupChannel.getChannel(mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    mChannel = groupChannel;
                    mChatAdapter.setChannel(mChannel);
                    mChatAdapter.loadLatestMessages(30, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            mChatAdapter.markAllMessagesAsRead();
                        }
                    });
                }
            });
        } else {
            mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    mChatAdapter.loadLatestMessages(30, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            mChatAdapter.markAllMessagesAsRead();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_CAMERA:
                File temp = new File(mTempPhotoPath);
                startCropActivity(Uri.fromFile(temp));
                break;
            case REQUEST_GALLERY:
                startCropActivity(data.getData());
                break;
            case UCrop.REQUEST_CROP:
                handleCropResult(data);
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(data);
                break;
        }

        // Set this as true to restore background connection management.
        SendBird.setAutoBackgroundDetection(true);
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    mChatAdapter.loadPreviousMessages(30, null);
                }
            }
        });
    }

    private void setUpChatListAdapter() {
        mChatAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
            @Override
            public void onUserMessageItemClick(UserMessage message) {
                // Restore failed message and remove the failed message from list.
                if (mChatAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }


                if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
                    try {
                        UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
                        startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (message.getCustomType().equals(GroupChatAdapter.APPOINT_CUSTOM_TYPE)
                        && !message.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    Gson gson = new Gson();
                    //EventDetailResp resp = gson.fromJson(message.getData(), EventDetailResp.class);

                    ChatAppointPOJO chatData = gson.fromJson(message.getData(), ChatAppointPOJO.class);
                    fetchChargeDetail(chatData);

                }
            }

            @Override
            public void onFileMessageItemClick(FileMessage message) {
                // Load media chooser and remove the failed message from list.
                if (mChatAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }

                onFileMessageClicked(message);
            }
        });
    }

    private void setUpSendBird() {
        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                if (baseChannel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.markAllMessagesAsRead();
                    // Add new message to view
                    mChatAdapter.addFirst(baseMessage);
                }
            }

            @Override
            public void onMessageDeleted(BaseChannel baseChannel, long msgId) {
                super.onMessageDeleted(baseChannel, msgId);
                if (baseChannel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.delete(msgId);
                }
            }

            @Override
            public void onMessageUpdated(BaseChannel channel, BaseMessage message) {
                super.onMessageUpdated(channel, message);
                if (channel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.update(message);
                }
            }

            @Override
            public void onReadReceiptUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    List<Member> typingUsers = channel.getTypingMembers();
                    displayTyping(typingUsers);
                }
            }

        });

        SendBird.addConnectionHandler(CONNECTION_HANDLER_ID, new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
            }

            @Override
            public void onReconnectSucceeded() {
                refresh();
            }

            @Override
            public void onReconnectFailed() {
            }
        });

        if (SendBird.getConnectionState() == SendBird.ConnectionState.OPEN) {
            refresh();
        } else {
            if (SendBird.reconnect()) {
                // Will call onReconnectSucceeded()
            } else {
                //String userId = PreferenceUtils.getUserId(getActivity());
                UserPOJO userPOJO = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY,UserPOJO.class);
                String userId = userPOJO.id.toString();
                if (userId == null) {
                    Toast.makeText(GroupChatActivity.this, "Require user ID to connect to SendBird.", Toast.LENGTH_LONG).show();
                    return;
                }

                SendBird.connect("userName" + userId, new SendBird.ConnectHandler() {
                    @Override
                    public void onConnected(User user, SendBirdException e) {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        refresh();

                        if (FirebaseInstanceId.getInstance().getToken() == null) return;

                        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                                new SendBird.RegisterPushTokenWithStatusHandler() {
                                    @Override
                                    public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                                        if (e != null) {
                                            // Error.
                                            return;
                                        }
                                    }
                                });
                    }
                });
            }
        }
    }

    private void setState(int state, BaseMessage editingMessage, final int position) {
        switch (state) {
            case STATE_NORMAL:
                mCurrentState = STATE_NORMAL;
                mEditingMessage = null;

                mUploadFileButton.setVisibility(View.VISIBLE);
                mMessageSendButton.setText("SEND");
                mMessageEditText.setText("");

//                mIMM.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
                break;

            case STATE_EDIT:
                mCurrentState = STATE_EDIT;
                mEditingMessage = editingMessage;

                mUploadFileButton.setVisibility(View.GONE);
                mMessageSendButton.setText("SAVE");
                String messageString = ((UserMessage)editingMessage).getMessage();
                if (messageString == null) {
                    messageString = "";
                }
                mMessageEditText.setText(messageString);
                if (messageString.length() > 0) {
                    mMessageEditText.setSelection(0, messageString.length());
                }

                mMessageEditText.requestFocus();
                mIMM.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.scrollToPosition(position);
                    }
                }, 500);
                break;
        }
    }

    public void retryFailedMessage(final BaseMessage message) {
        new AlertDialog.Builder(this)
                .setMessage("Retry?")
                .setPositiveButton("Resend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (message instanceof UserMessage) {
                                String userInput = ((UserMessage) message).getMessage();
                                sendUserMessage(userInput);
                            } else if (message instanceof FileMessage) {
                                Uri uri = mChatAdapter.getTempFileMessageUri(message);
                                sendFileWithThumbnail(uri);
                            }
                            mChatAdapter.removeFailedMessage(message);
                        }
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            mChatAdapter.removeFailedMessage(message);
                        }
                    }
                }).show();
    }

    /**
     * Display which users are typing.
     * If more than two users are currently typing, this will state that "multiple users" are typing.
     *
     * @param typingUsers The list of currently typing users.
     */
    private void displayTyping(List<Member> typingUsers) {

        if (typingUsers.size() > 0) {
            mCurrentEventLayout.setVisibility(View.VISIBLE);
            String string;

            if (typingUsers.size() == 1) {
                string = typingUsers.get(0).getNickname() + " is typing";
            } else if (typingUsers.size() == 2) {
                string = typingUsers.get(0).getNickname() + " " + typingUsers.get(1).getNickname() + " is typing";
            } else {
                string = "Multiple users are typing";
            }
            mCurrentEventText.setText(string);
        } else {
            mCurrentEventLayout.setVisibility(View.GONE);
        }
    }

    private void sendUserMessage(String text) {
        List<String> urls = WebUtils.extractUrls(text);
        if (urls.size() > 0) {
            sendUserMessageWithUrl(text, urls.get(0));
            return;
        }

        UserMessage tempUserMessage = mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Log.e(LOG_TAG, e.toString());
                    Toast.makeText(
                           GroupChatActivity.this,
                            "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    mChatAdapter.markMessageFailed(userMessage.getRequestId());
                    return;
                }

                // Update a sent message to RecyclerView
                mChatAdapter.markMessageSent(userMessage);
            }
        });

        // Display a user message to RecyclerView
        mChatAdapter.addFirst(tempUserMessage);
    }

    public void sendCustomUserMessage(String json) {
        UserMessage tempUserMessage = mChannel.sendUserMessage("", json, GroupChatAdapter.APPOINT_CUSTOM_TYPE,new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Log.e(LOG_TAG, e.toString());
                    Toast.makeText(
                            GroupChatActivity.this,
                            "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    mChatAdapter.markMessageFailed(userMessage.getRequestId());
                    return;
                }

                // Update a sent message to RecyclerView
                mChatAdapter.markMessageSent(userMessage);
            }
        });

        // Display a user message to RecyclerView
        mChatAdapter.addFirst(tempUserMessage);
    }

    private void sendUserMessageWithUrl(final String text, String url) {
        new WebUtils.UrlPreviewAsyncTask() {
            @Override
            protected void onPostExecute(UrlPreviewInfo info) {
                UserMessage tempUserMessage = null;
                BaseChannel.SendUserMessageHandler handler = new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            // Error!
                            Log.e(LOG_TAG, e.toString());
                            Toast.makeText(
                                    GroupChatActivity.this,
                                    "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                            mChatAdapter.markMessageFailed(userMessage.getRequestId());
                            return;
                        }

                        // Update a sent message to RecyclerView
                        mChatAdapter.markMessageSent(userMessage);
                    }
                };

                try {
                    // Sending a message with URL preview information and custom type.
                    String jsonString = info.toJsonString();
                    tempUserMessage = mChannel.sendUserMessage(text, jsonString, GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE, handler);
                } catch (Exception e) {
                    // Sending a message without URL preview information.
                    tempUserMessage = mChannel.sendUserMessage(text, handler);
                }


                // Display a user message to RecyclerView
                mChatAdapter.addFirst(tempUserMessage);
            }
        }.execute(url);
    }

    /**
     * Notify other users whether the current user is typing.
     *
     * @param typing Whether the user is currently typing.
     */
    private void setTypingStatus(boolean typing) {
        if (mChannel == null) {
            return;
        }

        if (typing) {
            mIsTyping = true;
            mChannel.startTyping();
        } else {
            mIsTyping = false;
            mChannel.endTyping();
        }
    }

    /**
     * Sends a File Message containing an image file.
     * Also requests thumbnails to be generated in specified sizes.
     *
     * @param uri The URI of the image, which in this case is received through an Intent request.
     */
    private void sendFileWithThumbnail(Uri uri) {
        // Specify two dimensions of thumbnails to generate
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

        Hashtable<String, Object> info = FileUtils.getFileInfo(this, uri);

        if (info == null) {
            Toast.makeText(this, "Extracting file information failed.", Toast.LENGTH_LONG).show();
            return;
        }

        final String path = (String) info.get("path");
        final File file = new File(path);
        final String name = file.getName();
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path.equals("")) {
            Toast.makeText(this, "File must be located in local storage.", Toast.LENGTH_LONG).show();
        } else {
            BaseChannel.SendFileMessageWithProgressHandler progressHandler = new BaseChannel.SendFileMessageWithProgressHandler() {
                @Override
                public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {

                }

                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(GroupChatActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        mChatAdapter.markMessageFailed(fileMessage.getRequestId());
                        return;
                    }

                    mChatAdapter.markMessageSent(fileMessage);
                }
            };

            // Send image with thumbnails in the specified dimensions
            FileMessage tempFileMessage = mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes, progressHandler);

            mFileProgressHandlerMap.put(progressHandler, tempFileMessage);

            mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri);
            mChatAdapter.addFirst(tempFileMessage);
        }
    }

    private void editMessage(final BaseMessage message, String editedMessage) {
        mChannel.updateUserMessage(message.getMessageId(), editedMessage, null, null, new BaseChannel.UpdateUserMessageHandler() {
            @Override
            public void onUpdated(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText(GroupChatActivity.this, "Error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                mChatAdapter.loadLatestMessages(30, new BaseChannel.GetMessagesHandler() {
                    @Override
                    public void onResult(List<BaseMessage> list, SendBirdException e) {
                        mChatAdapter.markAllMessagesAsRead();
                    }
                });
            }
        });
    }

    public void getServiceList() {
        HashMap<String, String> header = new HashMap<>();
        header.put(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_VALUE);
        header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
        header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.CHAT_SERVICE_LIST));
        header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
        header.put("API_VERSION", Utility.getVersionCode());

        //Check for internet Connection
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        PromeetsPreferenceUtil promeetsPreferenceUtil = new PromeetsPreferenceUtil();
        UserPOJO userPOJO = promeetsPreferenceUtil.getValue(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        String[] value = {userPOJO.id + "", targetId};
        String[] key = {"viewId", "chatTargetId"};
        new GenericServiceHandler(Constant.ServiceType.HOME_PAGE_BANNER, this, PromeetsUtils.buildURL(Constant.CHAT_SERVICE_LIST, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();
    }

    private void fetchEventDetail(int eventId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/fetchEventDetail"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventApi service = retrofit.create(EventApi.class);
        PromeetsPreferenceUtil promeetsPreferenceUtil = new PromeetsPreferenceUtil();

        UserPOJO userPOJO = promeetsPreferenceUtil.getValue(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);

        Call<EventDetailResp> call = service.fetchEventDetail(eventId, userPOJO.id, TimeZone.getDefault().getID());
        call.enqueue(new Callback<EventDetailResp>() {
            @Override
            public void onResponse(Call<EventDetailResp> call, Response<EventDetailResp> response) {
                EventDetailResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(GroupChatActivity.this, response.errorBody().toString());
                    return;
                }

                if (result.info.code.equals("200")) {
                    GroupChatExpertFragment dialogFragment = GroupChatExpertFragment.newInstance(result);
                    dialogFragment.show(getFragmentManager(), "accept");
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(GroupChatActivity.this, result.info.code);
                }
            }

            @Override
            public void onFailure(Call<EventDetailResp> call, Throwable t) {

            }
        });
    }

    private void fetchChargeDetail(final ChatAppointPOJO chatData) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/fetchEventDetail"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EventApi service = retrofit.create(EventApi.class);
        PromeetsPreferenceUtil promeetsPreferenceUtil = new PromeetsPreferenceUtil();

        UserPOJO userPOJO = promeetsPreferenceUtil.getValue(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);

        int eventRequestId = chatData.eventRequest == null ? chatData.eventId : chatData.eventRequest.id;
        Call<EventDetailResp> call = service.fetchEventDetail(eventRequestId, userPOJO.id, TimeZone.getDefault().getID());
        call.enqueue(new Callback<EventDetailResp>() {
            @Override
            public void onResponse(Call<EventDetailResp> call, Response<EventDetailResp> response) {
                EventDetailResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(GroupChatActivity.this, response.errorBody().toString());
                    return;
                }

                if (result.info.code.equals("200")) {
                    if (result.eventAction.displayStep == 2) {
                        result.eventDateList.clear();
                        result.eventLocationList.clear();
                        result.eventDateList.addAll(chatData.eventDateList);
                        result.eventLocationList.addAll(chatData.eventLocationList);

                        GroupChatUserFragment dialogFragment = GroupChatUserFragment.newInstance(result);
                        dialogFragment.show(getFragmentManager(), "accept");
                    }
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(GroupChatActivity.this, result.info.code);
                }
            }

            @Override
            public void onFailure(Call<EventDetailResp> call, Throwable t) {

            }
        });
    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        Log.d("TAG", serviceResponse.toString());
        PromeetsDialog.hideProgress();

        ServiceListResp serviceListResp = (ServiceListResp) serviceResponse.getServiceResponse(ServiceListResp.class);
        if (serviceListResp.getInfo().getCode().equals("200")) {
            if (serviceListResp.getChatFlag().equals("0")) {
                onErrorResponse("You are not allowed to chat");
                finish();
            }

            if (serviceListResp.getDataList() != null) {
                for (ServiceListDetailResp item : serviceListResp.getDataList())
                    if (item.getIfExpert() == 1) {
                        mViewSetup.setVisibility(View.VISIBLE);
                        dataList.add(item);
                    }
            }
        } else if (serviceListResp.getInfo().getCode().equals(Constant.RELOGIN_ERROR_CODE) || serviceListResp.getInfo().getCode().equals(Constant.UPDATE_TIME_STAMP) || serviceListResp.getInfo().getCode().equals(Constant.UPDATE_THE_APPLICATION)) {
            Utility.onServerHeaderIssue(this, serviceListResp.getInfo().getCode());
        }
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        Log.e("TAG", errorMessage);
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    public GroupChatAdapter getChatAdapter() {
        return mChatAdapter;
    }

    private void onFileMessageClicked(FileMessage message) {
        String type = message.getType().toLowerCase();
        if (type.contains("image")) {
            Intent i = new Intent(this, PhotoViewerActivity.class);
            i.putExtra("url", message.getUrl());
            i.putExtra("type", message.getType());
            startActivity(i);
        }
    }

    //region upload photo
    private void requestPhotoPermission() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    AndPermission.with(GroupChatActivity.this)
                            .permission(Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE)
                            .rationale(mRationale)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    try {
                                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
                                        startActivityForResult(takeIntent, REQUEST_CAMERA);
                                    } catch (Exception e){
                                        mSetting.showSetting(permissions);
                                    }
                                }
                            })
                            .onDenied(new Action() {
                                @Override
                                public void onAction(@NonNull List<String> permissions) {
                                    if (AndPermission.hasAlwaysDeniedPermission(GroupChatActivity.this, permissions)) {
                                        mSetting.showSetting(permissions);
                                    }
                                }
                            })
                            .start();
                } else if (items[item].equals("Choose from Library")) {
                    AndPermission.with(GroupChatActivity.this)
                            .permission(Permission.WRITE_EXTERNAL_STORAGE)
                            .rationale(mRationale)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    try {
                                        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        // "image/jpeg, image/png"
                                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(pickIntent, REQUEST_GALLERY);
                                    } catch (Exception e){
                                        mSetting.showSetting(permissions);
                                    }
                                }
                            })
                            .onDenied(new Action() {
                                @Override
                                public void onAction(@NonNull List<String> permissions) {
                                    if (AndPermission.hasAlwaysDeniedPermission(GroupChatActivity.this, permissions)) {
                                        mSetting.showSetting(permissions);
                                    }
                                }
                            })
                            .start();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    //endregion

    //region Crop Image
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .useSourceImageAspectRatio()
                //.withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }

    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        // URI: file:///
        final Uri resultUri = UCrop.getOutput(result);
        File file = new File(resultUri.getPath());

        // URI: content:///
        Uri uri = Utility.getImageContentUri(this, file);
        sendFileWithThumbnail(uri);
    }

    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error when cropping image", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }
    //endregion

    public void OnItemSelect(ArrayList<ServiceListDetailResp> dataList, int position) {
        ServiceListDetailResp resp = dataList.get(position);
        int eventId = resp.getEventRequestId();
        fetchEventDetail(eventId);
    }
}
