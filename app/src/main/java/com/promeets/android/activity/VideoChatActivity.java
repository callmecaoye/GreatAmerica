package com.promeets.android.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.promeets.android.Constant;
import com.promeets.android.R;
import com.promeets.android.api.URL;
import com.promeets.android.api.UserActionApi;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.custom.WaveView;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoChatActivity extends BaseActivity {

    private static final String TAG = VideoChatActivity.class.getSimpleName();
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 0;
    private static final int PERMISSION_REQ_ID_CAMERA = 1;

    @BindView(R.id.root_layout)
    View mLayRoot;
    @BindView(R.id.remote_video_view_container)
    FrameLayout mRemoteView;
    @BindView(R.id.local_video_view_layer)
    FrameLayout mLayLocalView;
    @BindView(R.id.local_video_view_container)
    FrameLayout mLocalView;
    @BindView(R.id.photo)
    CircleImageView mImgPhoto;
    @BindView(R.id.name)
    TextView mTxtName;
    @BindView(R.id.awaiting_layer)
    FrameLayout mLayAwaiting;
    @BindView(R.id.photo1)
    CircleImageView mImgPhoto1;
    @BindView(R.id.name1)
    TextView mTxtName1;
    @BindView(R.id.remote_place_holder)
    RelativeLayout mRemoteHolder;
    @BindView(R.id.switch_camera)
    ImageView mImgSwitchCam;
    @BindView(R.id.timer)
    Chronometer mTimer;
    @BindView(R.id.control_panel)
    View mPanelControl;

    @BindView(R.id.wave_view)
    WaveView mWaveView;

    @BindView(R.id.mute_video)
    View mMuteVideo;
    @BindView(R.id.mute_audio)
    View mMuteAudio;
    @BindView(R.id.hang_up)
    View mHangup;

    private String name, photoUrl, appId, channelName;
    private int uid;
    private int width;
    private boolean isSetup;

    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> onRemoteUserLeft());
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            runOnUiThread(() -> onRemoteUserVideoMuted(uid, muted));
        }
    };

    @Override
    public void initElement() {
        mWaveView.setDuration(5000);
        mWaveView.setStyle(Paint.Style.FILL);
        mWaveView.setColor(Color.LTGRAY);
        mWaveView.setInterpolator(new LinearOutSlowInInterpolator());
    }

    @Override
    public void registerListeners() {
        mLayRoot.setOnTouchListener((view, motionEvent) -> {
            if (mPanelControl.getAlpha() == 0f) {
                mPanelControl.animate().alpha(1f).setDuration(300);
                mMuteVideo.setClickable(true);
                mMuteAudio.setClickable(true);
                mHangup.setClickable(true);
            } else if (mPanelControl.getAlpha() == 1f) {
                mPanelControl.animate().alpha(0f).setDuration(300);
                mMuteVideo.setClickable(false);
                mMuteAudio.setClickable(false);
                mHangup.setClickable(false);
            }
            return false;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        ButterKnife.bind(this);

        name = getIntent().getStringExtra("name");
        photoUrl = getIntent().getStringExtra("photoUrl");
        appId = getIntent().getStringExtra("appId");
        uid = getIntent().getIntExtra("uid", 0);
        channelName = getIntent().getStringExtra("channelName");
        width = getWidth();

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)
                && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel();
        }
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        setupVideoProfile();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    private void setupLocalVideo() {
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        mLocalView.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
    }

    private void joinChannel() {
        // token, channelName, optionalInfo, optionalUid
        mRtcEngine.joinChannel(null, channelName, null, uid);
        if (!TextUtils.isEmpty(name)) {
            mTxtName.setText(name);
            mTxtName1.setText(name);
        }
        if (!TextUtils.isEmpty(photoUrl)) {
            Glide.with(this).load(photoUrl).into(mImgPhoto);
            Glide.with(this).load(photoUrl).into(mImgPhoto1);
        }

        int dp24 = ScreenUtil.convertDpToPx(24, this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width / 3 - dp24, width / 3 - dp24);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mImgPhoto1.setLayoutParams(lp);

        lp = new RelativeLayout.LayoutParams(width * 2 / 3 , width * 2 / 3);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        lp.setMargins(0, 0, 0, dp24);
        mWaveView.setLayoutParams(lp);
        mWaveView.setInitialRadius(mImgPhoto1.getLayoutParams().width / 2);
        mWaveView.setMaxRadius(width / 3);
        mRemoteHolder.requestLayout();

        notifyServer("begin");
    }

    private void setupRemoteVideo(int uid) {
        if (mRemoteView.getChildCount() >= 1) {
            return;
        }

        isSetup = true;
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteView.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

        surfaceView.setTag(uid); // for mark purpose

        // set local view to window mode
        onScaleSize();

        // dismiss awaiting
        mLayAwaiting.setVisibility(View.GONE);

        // show timer
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.start();
        mTimer.setVisibility(View.VISIBLE);
    }

    private void onRemoteUserLeft() {
        Log.d("caoye", "remote left");

        mRemoteView.removeAllViews();

        // local view go full screen
        onFullSize();

        // dismiss remote place holder
        mRemoteHolder.setVisibility(View.GONE);

        // dimmiss timer
        mTimer.stop();
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        if (!isSetup)
            setupRemoteVideo(uid);

        SurfaceView surfaceView = (SurfaceView) mRemoteView.getChildAt(0);

        if (surfaceView != null) {
            Object tag = surfaceView.getTag();
            if (tag != null && (Integer) tag == uid) {
                surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
                mRemoteHolder.setVisibility(muted ? View.VISIBLE : View.GONE);

                if (muted)
                    mWaveView.start();
                else
                    mWaveView.stop();
            }
        }
    }

    // region Permission
    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    // endregion

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtcEngine != null) {
            leaveChannel();
            RtcEngine.destroy();
            mRtcEngine = null;
            notifyServer("exit");
        }
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onEncCallClicked(View view) {
        finish();
    }

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mute_off));
        } else {
            iv.setSelected(true);
            iv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mute_on));
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            //iv.clearColorFilter();
            iv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mute_video_off));
            mLayLocalView.setVisibility(View.VISIBLE);
        } else {
            iv.setSelected(true);
            //iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            iv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mute_video_on));
            mLayLocalView.setVisibility(View.GONE);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
    }

    private void onScaleSize() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width / 3,
                (int) (1.7 * (width / 3)), Gravity.RIGHT);
        lp.setMargins(0, ScreenUtil.convertDpToPx(24, this),
                ScreenUtil.convertDpToPx(0, this), 0);
        mLayLocalView.setLayoutParams(lp);
        mLayLocalView.setPadding(1, 1, 1, 1);
        mLayLocalView.requestLayout();
        mImgSwitchCam.setVisibility(View.VISIBLE);
    }

    private void onFullSize() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mLayLocalView.setLayoutParams(lp);
        mLayLocalView.setPadding(0, 0, 0, 0);
        mLayLocalView.requestLayout();
        mImgSwitchCam.setVisibility(View.GONE);
    }

    private void notifyServer(String op) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("agoraChat/callBack"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UserActionApi service = retrofit.create(UserActionApi.class);
        Call<BaseResp> call = service.callbackServer(channelName, op);
        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                BaseResp result = response.body();
                if (result == null) {
                    Log.d("agoraError", response.errorBody().toString());
                } else
                    Log.d("agoraInfo", result.info.code + result.info.description);
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {

            }
        });
    }
}
