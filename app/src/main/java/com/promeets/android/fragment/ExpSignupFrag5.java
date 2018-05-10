package com.promeets.android.fragment;

import com.promeets.android.activity.CropActivity;
import com.promeets.android.activity.ExpSignUpActivity;
import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.promeets.android.custom.DefaultRationale;
import com.promeets.android.custom.PermissionSetting;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Bitmap;
import com.promeets.android.Constant;
import com.promeets.android.listeners.OnPictureSelectedListener;
import android.net.Uri;
import com.promeets.android.object.ExpertProfilePOJO;
import android.os.Bundle;
import android.os.Environment;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.SuperResp;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.Utility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kevin.crop.UCrop;

import com.promeets.android.util.ScreenUtil;

import com.promeets.android.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sosasang on 1/26/18.
 */

public class ExpSignupFrag5 extends Fragment {

    private static final int REQUEST_CAMERA = 0, REQUEST_GALLERY = 1;

    //@BindView(R.id.thank_lay)
    //View mLayThank;
    @BindView(R.id.image_view)
    ImageView mImgView;
    @BindView(R.id.next_txt)
    public TextView mTxtNext;

    private ExpSignUpActivity mActivity;
    private ExpertProfilePOJO draftExp;
    private int width;

    private String mTempPhotoPath;
    private Uri mDestinationUri;
    private OnPictureSelectedListener mOnPictureSelectedListener;
    private File mFilePhoto;

    Gson gson = new Gson();

    private Rationale mRationale;
    private PermissionSetting mSetting;

    @Override
    public void onAttach(Context context) {
        mActivity = (ExpSignUpActivity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag5_exp_signup, container, false);
        ButterKnife.bind(this, view);

        width = mActivity.getWidth();
        mImgView.getLayoutParams().height = (width - ScreenUtil.convertDpToPx(48, mActivity)) / 3 * 2;
        mImgView.requestLayout();

        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        mDestinationUri = Uri.fromFile(new File(mActivity.getCacheDir(), "cropImage.jpeg"));

        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(getContext());

        mTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String json = gson.toJson(draftExp);
                draftExp.status = "completed";
                draftExp.step = "5";
                submitExpert();
            }
        });
        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPhotoPermission();
            }
        });

        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
                mImgView.setImageBitmap(bitmap);

                String filePath = fileUri.getEncodedPath();
                String imagePath = Uri.decode(filePath);
                mFilePhoto = new File(imagePath);
                if (mFilePhoto != null) {
                    Log.d("Expert Profile", "photo path: " + imagePath);
                    uploadPhoto();
                }
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            draftExp = mActivity.getDraftExp();
            if (!StringUtils.isEmpty(draftExp.photoUrl)) {
                mImgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(mActivity).load(draftExp.photoUrl).into(mImgView);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    }

    /**
     * Submit draft expert info to server
     */
    private void submitExpert() {
        if (!mActivity.hasInternetConnection()) {
            PromeetsDialog.show(mActivity, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(mActivity);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("becomeToExpert/saveFlowContent"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        Call<BaseResp> call = service.expSubmit(draftExp, TimeZone.getDefault().getID());//get request, need to be post!

        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                PromeetsDialog.hideProgress();
                final BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mActivity, response.errorBody().toString());
                    return;
                }

                if (mActivity.isSuccess(result.info.code)) {
                    mCallback.showThank();
                } else if (result.info.code.equals("601")
                        || result.info.code.equals("602")
                        || result.info.code.equals("603")) {
                    //601 workshop time and call time in the same time
                    //602 workshop time error.
                    //603 call time error.
                    PromeetsDialog.show(mActivity, result.info.description, new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {
                            if (result.info.code.equals("601")) {
                                mActivity.mViewpager.setCurrentItem(2);
                                mActivity.curPage = 2;
                            } else if (result.info.code.equals("602")) {
                                mActivity.mViewpager.setCurrentItem(2);
                                mActivity.curPage = 2;
                                draftExp.workshopTime = 0;
                            } else {
                                mActivity.mViewpager.setCurrentItem(3);
                                mActivity.curPage = 3;
                                draftExp.callTime = 0;
                            }
                        }
                    });
                } else if(result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)){
                    Utility.onServerHeaderIssue(mActivity, result.info.code);
                } else
                    PromeetsDialog.show(mActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(mActivity, t.getLocalizedMessage());
            }
        });
    }

    //region upload photo
    private void requestPhotoPermission() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    AndPermission.with(mActivity)
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
                                    if (AndPermission.hasAlwaysDeniedPermission(mActivity, permissions)) {
                                        mSetting.showSetting(permissions);
                                    }
                                }
                            })
                            .start();
                } else if (items[item].equals("Choose from Library")) {
                    AndPermission.with(mActivity)
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
                                    if (AndPermission.hasAlwaysDeniedPermission(mActivity, permissions)) {
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

    private void uploadPhoto() {
        if (!mActivity.hasInternetConnection()) {
            PromeetsDialog.show(mActivity, getString(R.string.no_internet));
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("photos/uploadOneExpertPhoto"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), mFilePhoto);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", mFilePhoto.getName(), file);
        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), mActivity.user.id.toString());
        Call<SuperResp> call = service.upload(userId, body);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(mActivity, response.errorBody().toString());
                    return;
                }

                if (mActivity.isSuccess(result.info.code)) {
                    if (result.photoMapPOJO != null) {
                        draftExp.photoUrl = result.photoMapPOJO.photoUrl;
                    }
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(mActivity, result.info.code);
                } else
                    PromeetsDialog.show(mActivity, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.show(mActivity, t.getLocalizedMessage());
            }
        });
    }
    //endregion

    //region Crop Image
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1.5f, 1)
                //.withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(mActivity, this);
    }

    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri && null != mOnPictureSelectedListener) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOnPictureSelectedListener.onPictureSelected(resultUri, bitmap);
        } else {
            Toast.makeText(mActivity, "Error when cropping image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(mActivity, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity, "Error when cropping image", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

    public void setOnPictureSelectedListener(OnPictureSelectedListener l) {
        this.mOnPictureSelectedListener = l;
    }
    //endregion

    private ThankCallback mCallback;
    public interface ThankCallback {
        void showThank();
    }
    public void setCallback(ThankCallback callback) {
        this.mCallback = callback;
    }
}
