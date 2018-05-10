package com.promeets.android.activity;

import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.promeets.android.custom.DefaultRationale;
import com.promeets.android.custom.PermissionSetting;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import com.promeets.android.Constant;
import com.promeets.android.listeners.OnPictureSelectedListener;
import android.net.Uri;
import com.promeets.android.object.Category;
import com.promeets.android.object.ExpertProfilePOJO;
import com.promeets.android.object.SubCate;
import com.promeets.android.object.UserProfilePOJO;
import android.os.Bundle;
import android.os.Environment;
import com.promeets.android.pojo.ExpertProfilePost;
import com.promeets.android.pojo.ExpertProfileResp;
import com.promeets.android.pojo.SuperResp;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;
import android.util.TypedValue;
import com.promeets.android.util.Utility;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.kevin.crop.UCrop;
import com.promeets.android.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 * This is for showing expert profile basic information
 *
 * @source: ExpertDashboardActivity
 *
 */
public class ExpertProfileActivity extends BaseActivity
        implements View.OnClickListener {

    static final int REQUEST_CAMERA = 0, REQUEST_GALLERY = 1;
    static final int REQUEST_INDUSTRY = 10;

    @BindView(R.id.photo)
    ImageView mImgPhoto;
    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;
    @BindView(R.id.name)
    EditText mTxtName;
    @BindView(R.id.about_title)
    TextView mTitleAbout;
    @BindView(R.id.txt_about)
    EditText mTxtAbout;
    @BindView(R.id.submit)
    TextView mTxtSubmit;

    // Contact
    @BindView(R.id.phone_txt)
    EditText mTxtPhone;
    @BindView(R.id.email_txt)
    EditText mTxtEmail;

    // Work
    @BindView(R.id.add_industry)
    ImageView mImgIndustry;
    //@BindView(R.id.industry_txt)
    //TextView mTxtIndustry;
    @BindView(R.id.flexBox)
    FlexboxLayout mFlexBox;
    @BindView(R.id.title_txt)
    EditText mTxtTitle;

    private Context mInstance = this;
    private UserProfilePOJO userProfile;
    private boolean isUpdate;
    private ExpertProfilePOJO expertProfilePOJO, updateExpProfilePOJO;
    private Gson gson = new Gson();
    private ArrayList<SubCate> industryList;

    private File mFilePhoto;
    private String mTempPhotoPath;
    private Uri mDestinationUri;
    private OnPictureSelectedListener mOnPictureSelectedListener;

    private Rationale mRationale;
    private PermissionSetting mSetting;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mLayRoot.setOnClickListener(this);
        mImgPhoto.setOnClickListener(this);
        mImgIndustry.setOnClickListener(this);
        mTxtSubmit.setOnClickListener(this);
        mTxtAbout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTitleAbout.requestFocus();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
                mImgPhoto.setImageBitmap(bitmap);

                String filePath = fileUri.getEncodedPath();
                String imagePath = Uri.decode(filePath);
                mFilePhoto = new File(imagePath);
                if (mFilePhoto != null) {
                    uploadPhoto();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);
        ButterKnife.bind(this);
        AndroidBug5497Workaround.assistActivity(this);

        int width = getWidth();
        mImgPhoto.getLayoutParams().height = width / 3 * 2;
        mImgPhoto.requestLayout();

        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), "cropImage.jpeg"));

        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(this);

        userProfile = (UserProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY, UserProfilePOJO.class);
        if (userProfile != null) {
            fetchExpertProfile(userProfile.id);
            updateExpProfilePOJO = new ExpertProfilePOJO();
            updateExpProfilePOJO.expId = userProfile.id;
            industryList = new ArrayList<>();
        }
    }

    private void fetchExpertProfile(int expId) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("expertprofile/fetchIncludeServiceMayPending"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        Call<ExpertProfileResp> call = service.fetchExpertProfile(expId, TimeZone.getDefault().getID());
        call.enqueue(new Callback<ExpertProfileResp>() {
            @Override
            public void onResponse(Call<ExpertProfileResp> call, Response<ExpertProfileResp> response) {
                PromeetsDialog.hideProgress();
                ExpertProfileResp result = response.body();

                if (result != null && result.info != null && result.info.code.equals("200")) {
                    expertProfilePOJO = result.expertProfile;
                    if (expertProfilePOJO.photoUrl != null) {
                        Glide.with(mInstance).load(expertProfilePOJO.photoUrl).into(mImgPhoto);
                    } else if (expertProfilePOJO.smallphotoUrl != null) {
                        Glide.with(mInstance).load(expertProfilePOJO.smallphotoUrl).into(mImgPhoto);
                    } else {
                        mImgPhoto.setVisibility(View.GONE);
                    }

                    mTxtName.setText((expertProfilePOJO.fullName));
                    mTxtPhone.setText(expertProfilePOJO.contactNumber);
                    mTxtEmail.setText(expertProfilePOJO.contactEmail);

                    if (expertProfilePOJO.industryList != null && expertProfilePOJO.industryList.size() > 0) {
                        for (Category cate : expertProfilePOJO.industryList)
                            industryList.addAll(cate.getList());
                    }

                    createSubcateTag(industryList);
                    mTxtTitle.setText(expertProfilePOJO.positon);
                    mTxtAbout.setText(expertProfilePOJO.description);
                } else if (result != null &&
                        result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(ExpertProfileActivity.this, result.info.code);
                }
            }

            @Override
            public void onFailure(Call<ExpertProfileResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertProfileActivity.this, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.root_layout:
                hideSoftKeyboard();
                break;
            case R.id.photo:
                requestPhotoPermission();
                break;
            case R.id.add_industry:
                intent = new Intent(this, CategorySelectActivity.class);
                if (industryList != null && industryList.size() > 0) {
                    intent.putExtra("industryList", industryList);
                }
                startActivityForResult(intent, REQUEST_INDUSTRY);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.submit:
                if (!check()) return;
                collectUpdateInfo();
                if (isUpdate) {
                    PromeetsDialog.show(this, "We will review and update your changes within 3 days!", new PromeetsDialog.OnSubmitListener() {
                        @Override
                        public void onSubmitListener() {
                            updateExpertSubmit();
                        }
                    });
                } else {
                    if (updateExpProfilePOJO.contactNumber == null
                            && updateExpProfilePOJO.contactEmail == null) {
                        finish();
                    } else {
                        updateExpertSubmit();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
                case REQUEST_INDUSTRY:
                    if (industryList != null) industryList.clear();
                    industryList = data.getParcelableArrayListExtra("industryList");
                    createSubcateTag(industryList);
                    break;
            }
        }
    }

    //region Crop Image
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }

    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri && null != mOnPictureSelectedListener) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOnPictureSelectedListener.onPictureSelected(resultUri, bitmap);
        } else {
            Toast.makeText(this, "Error when cropping image", Toast.LENGTH_SHORT).show();
        }
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

    public void setOnPictureSelectedListener(OnPictureSelectedListener l) {
        this.mOnPictureSelectedListener = l;
    }
    //endregion

    private boolean check() {
        if (mTxtName.getText().length() <= 0) {
            PromeetsDialog.show(this, "Name can not be empty.");
            return false;
        }

        if (mTxtPhone.getText().length() <= 0) {
            PromeetsDialog.show(this, "Phone number cannot be empty.");
            return false;
        }
        if (!Utility.isValidPhone(mTxtPhone.getText().toString())) {
            PromeetsDialog.show(this, "Phone number you entered is not valid.");
            return false;
        }
        if (mTxtEmail.getText().length() <= 0) {
            PromeetsDialog.show(this, "Email cannot be empty.");
            return false;
        }
        if (!Utility.isValidEmail(mTxtEmail.getText().toString())) {
            PromeetsDialog.show(this, "This email is not valid.");
            return false;
        }
        if (mTxtTitle.getText().length() <= 0) {
            PromeetsDialog.show(this, "Position cannot be empty.");
            return false;
        }
        if (mTxtAbout.getText().length() <= 0) {
            PromeetsDialog.show(this, "About me cannot be empty.");
            return false;
        }
        if (industryList == null || industryList.size() == 0) {
            PromeetsDialog.show(this, "Professional field cannot be empty");
            return false;
        }
        return true;
    }

    private void collectUpdateInfo() {
        if (!mTxtName.getText().toString().equals(expertProfilePOJO.fullName)) {
            updateExpProfilePOJO.fullName = mTxtName.getText().toString();
            isUpdate = true;
        }
        // CONTACT CARD
        if (!mTxtPhone.getText().toString().equals(expertProfilePOJO.contactNumber)) {
            updateExpProfilePOJO.contactNumber = mTxtPhone.getText().toString();
            //mIsUpdate = true;
        }
        if (!mTxtEmail.getText().toString().equals(expertProfilePOJO.contactEmail)) {
            updateExpProfilePOJO.contactEmail = mTxtEmail.getText().toString();
            //mIsUpdate = true;
        }

        // WORK CARD
        //if (!expertProfilePOJO.industryIdList.containsAll(industryIdList)) {
        if (expertProfilePOJO.industryList == null) {
            updateExpProfilePOJO.industryIdList = new ArrayList<>();
            isUpdate = true;
            for (SubCate subCate : industryList) {
                updateExpProfilePOJO.industryIdList.add(subCate.getId());
            }
        } else {
            ArrayList<Integer> idList = new ArrayList<>();
            for (SubCate subCate : industryList) {
                idList.add(subCate.getId());
            }
            if (!expertProfilePOJO.industryIdList.containsAll(idList)
                    || !idList.containsAll(expertProfilePOJO.industryIdList)) {
                isUpdate = true;
                updateExpProfilePOJO.industryIdList = idList;
            }
        }
        if (!mTxtTitle.getText().toString().equals(expertProfilePOJO.positon)) {
            updateExpProfilePOJO.positon = mTxtTitle.getText().toString();
            isUpdate = true;
        }

        // ABOUT ME CARD
        if (!mTxtAbout.getText().toString().equals(expertProfilePOJO.description)) {
            updateExpProfilePOJO.description = mTxtAbout.getText().toString();
            isUpdate = true;
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
                    AndPermission.with(ExpertProfileActivity.this)
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
                                    if (AndPermission.hasAlwaysDeniedPermission(ExpertProfileActivity.this, permissions)) {
                                        mSetting.showSetting(permissions);
                                    }
                                }
                            })
                            .start();
                } else if (items[item].equals("Choose from Library")) {
                    AndPermission.with(ExpertProfileActivity.this)
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
                                    if (AndPermission.hasAlwaysDeniedPermission(ExpertProfileActivity.this, permissions)) {
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
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("photos/uploadOneExpertPhoto"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), mFilePhoto);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", mFilePhoto.getName(), file);
        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), userProfile.id.toString());
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), ".png");

        Call<SuperResp> call = service.upload(id, body);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                SuperResp rep = response.body();
                if (rep != null && rep.info != null && rep.info.code.equals("200")) {
                    if (rep.photoMapPOJO != null) {
                        updateExpProfilePOJO.photoUrl = rep.photoMapPOJO.photoUrl;
                        updateExpProfilePOJO.smallphotoUrl = rep.photoMapPOJO.smallPhotoUrl;
                        isUpdate = true;
                    }
                } else if (rep != null && rep.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || rep.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || rep.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(ExpertProfileActivity.this, rep.info.code);
                }
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertProfileActivity.this, t.getLocalizedMessage());
            }
        });
    }
    // endregion

    private void updateExpertSubmit() {
        PromeetsDialog.showProgress(this);
        ExpertProfilePost expertProfilePost = new ExpertProfilePost();
        expertProfilePost.expertProfile = updateExpProfilePOJO;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("becomeToExpert/toCheck"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final String json = gson.toJson(expertProfilePost);
        ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Call<SuperResp> call = service.becomeExpert(requestBody);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(ExpertProfileActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    finish();
                    //if (ExpertDashboardActivity.instance != null)
                    //    ExpertDashboardActivity.instance.finish();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(ExpertProfileActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(ExpertProfileActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(ExpertProfileActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void createSubcateTag(final ArrayList<SubCate> list) {
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.convertDpToPx(4, mInstance),
                ScreenUtil.convertDpToPx(5, mInstance),
                ScreenUtil.convertDpToPx(4, mInstance),
                ScreenUtil.convertDpToPx(5, mInstance));
        mFlexBox.removeAllViewsInLayout();
        if (list != null && list.size() > 0) {
            for (final SubCate subCate : list) {
                final TextView mTxtTag = new TextView(mInstance);
                mTxtTag.setLayoutParams(lp);
                mTxtTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close2, 0);
                mTxtTag.setCompoundDrawablePadding(ScreenUtil.convertDpToPx(10, mInstance));
                if (subCate.getTitle().equalsIgnoreCase("other"))
                    mTxtTag.setText(subCate.getCategoryTitle() + " - Other");
                else
                    mTxtTag.setText(subCate.getTitle());
                mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                mTxtTag.setTextColor(Color.WHITE);
                mTxtTag.setGravity(Gravity.CENTER_VERTICAL);
                mTxtTag.setPadding(ScreenUtil.convertDpToPx(15, mInstance),
                        ScreenUtil.convertDpToPx(5, mInstance),
                        ScreenUtil.convertDpToPx(15, mInstance),
                        ScreenUtil.convertDpToPx(5, mInstance));
                mTxtTag.setBackgroundResource(R.drawable.btn_solid_grey);
                mFlexBox.addView(mTxtTag);
                mTxtTag.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP) {
                            if(event.getRawX() >= mTxtTag.getRight() - mTxtTag.getTotalPaddingRight()) {
                                // drawableRight click event
                                mFlexBox.removeView(mTxtTag);
                                mFlexBox.requestLayout();
                                list.remove(subCate);
                                subCate.setSelect(false);
                                return true;
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }
}
