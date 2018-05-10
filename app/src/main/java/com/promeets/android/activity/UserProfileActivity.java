package com.promeets.android.activity;

import com.promeets.android.api.URL;
import com.promeets.android.api.UserActionApi;
import android.content.DialogInterface;
import android.content.Intent;
import com.promeets.android.custom.DefaultRationale;
import com.promeets.android.custom.PermissionSetting;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import com.promeets.android.Constant;
import com.promeets.android.listeners.IServiceResponseHandler;
import com.promeets.android.listeners.OnPictureSelectedListener;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import com.promeets.android.object.Category;
import com.promeets.android.object.SubCate;
import com.promeets.android.object.UserPOJO;
import com.promeets.android.object.UserProfilePOJO;
import android.os.Bundle;
import android.os.Environment;
import com.promeets.android.pojo.LoginResp;
import com.promeets.android.pojo.ProfileResp;
import com.promeets.android.pojo.ServiceResponse;
import android.provider.MediaStore;
import com.promeets.android.pojo.SuperResp;
import com.promeets.android.services.GenericServiceHandler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.PromeetsPreferenceUtil;
import com.promeets.android.util.PromeetsUtils;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceHeaderGeneratorUtil;
import com.promeets.android.util.ServiceResponseHolder;
import android.util.TypedValue;
import com.promeets.android.util.Utility;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.kevin.crop.UCrop;
import com.promeets.android.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfileActivity extends BaseActivity
        implements View.OnClickListener, IServiceResponseHandler {

    private static final int REQUEST_CAMERA = 0, REQUEST_GALLERY = 1;
    private static final int PLACE_PICKER_REQUEST = 2;

    @BindView(R.id.root_layout)
    LinearLayout mLayRoot;
    @BindView(R.id.photo_layout)
    LinearLayout mLayPhoto;
    @BindView(R.id.edit_interest)
    TextView mBtnEdit;
    @BindView(R.id.save)
    TextView mBtnSave;
    @BindView(R.id.fname_txt)
    EditText mTxtFName;
    @BindView(R.id.lname_txt)
    EditText mTxtLName;
    @BindView(R.id.loc_txt)
    EditText mTxtLoc;
    @BindView(R.id.company_txt)
    EditText mTxtComp;
    @BindView(R.id.position_txt)
    EditText mTxtPos;
    @BindView(R.id.photo)
    CircleImageView mImgPhoto;
    @BindView(R.id.photo_change)
    TextView mTxtChange;
    @BindView(R.id.flexBox)
    FlexboxLayout mFlexBox;

    private String userId;
    private UserProfilePOJO userProfile;
    private boolean isUploadPhoto = false;
    private boolean isLocSelected = false;
    private File mFilePhoto;
    private Gson gson = new Gson();
    private String mTempPhotoPath;
    private Uri mDestinationUri;
    private OnPictureSelectedListener mOnPictureSelectedListener;
    private Typeface tf_semi;

    private Rationale mRationale;
    private PermissionSetting mSetting;

    @Override
    public void initElement() {
        userId = getIntent().getStringExtra("id");
        if (StringUtils.isEmpty(userId)) {
            userProfile = (UserProfilePOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_PROFILE_OBJECT_KEY, UserProfilePOJO.class);
            if (userProfile == null) finish();
            requestForUserData(userProfile.id + "");
            mLayRoot.setOnClickListener(this);
            mLayPhoto.setOnClickListener(this);
            mTxtLoc.setOnClickListener(this);
            mBtnEdit.setOnClickListener(this);
            mBtnSave.setOnClickListener(this);
        } else {
            mTxtChange.setVisibility(View.INVISIBLE);
            mBtnSave.setVisibility(View.INVISIBLE);
            mBtnEdit.setVisibility(View.GONE);
            requestForUserData(userId);
        }
    }

    @Override
    public void registerListeners() {
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
                mImgPhoto.setImageBitmap(bitmap);

                String filePath = fileUri.getEncodedPath();
                String imagePath = Uri.decode(filePath);
                mFilePhoto = new File(imagePath);
                if (mFilePhoto != null) {
                    isUploadPhoto = true;
                    //uploadPhoto();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        AndroidBug5497Workaround.assistActivity(this);
        ButterKnife.bind(this);
        tf_semi = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");

        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), "cropImage.jpeg"));

        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_layout:
                mLayPhoto.requestFocus();
                hideSoftKeyboard();
                break;
            case R.id.photo_layout:
                requestPhotoPermission();
                break;
            case R.id.loc_txt:
                if (!isLocSelected) {
                    isLocSelected = true;
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                    } catch (Exception e) {
                        isLocSelected = false;
                    }
                }
                break;
            case R.id.edit_interest:
                Intent intent = new Intent(this, UserCateActivity.class);
                ArrayList<SubCate> subCates = new ArrayList<>();
                if (userProfile.pollingList != null && userProfile.pollingList.size() > 0)
                    for (Category cate : userProfile.pollingList)
                        subCates.addAll(cate.getList());
                intent.putExtra("industryList", subCates);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.save:
                collectALLInfo();
                if (isUploadPhoto)
                    uploadPhoto();
                else
                    updateUserProfile();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isLocSelected = false;
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
            case PLACE_PICKER_REQUEST:
                Place place = PlacePicker.getPlace(data, this);
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    String city = addresses.get(0).getLocality();
                    if (!StringUtils.isEmpty(city))
                        mTxtLoc.setText(city);
                    else {
                        PromeetsDialog.show(this, "Please select a valid location.");
                    }
                } catch (Exception e) {
                }
                break;
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
                    AndPermission.with(UserProfileActivity.this)
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
                                    if (AndPermission.hasAlwaysDeniedPermission(UserProfileActivity.this, permissions)) {
                                        mSetting.showSetting(permissions);
                                    }
                                }
                            })
                            .start();
                } else if (items[item].equals("Choose from Library")) {
                    AndPermission.with(UserProfileActivity.this)
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
                                    if (AndPermission.hasAlwaysDeniedPermission(UserProfileActivity.this, permissions)) {
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

        UserPOJO user = (UserPOJO) PromeetsUtils.getUserData(PromeetsPreferenceUtil.USER_OBJECT_KEY, UserPOJO.class);
        if (user == null) return;

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("photos/uploadOneUserPhoto"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserActionApi service = retrofit.create(UserActionApi.class);
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), mFilePhoto);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", mFilePhoto.getName(), file);
        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), user.id.toString());
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), ".png");

        Call<LoginResp> call = service.upload(id, name, body);
        call.enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {
                PromeetsDialog.hideProgress();
                LoginResp loginResp = response.body();

                if (loginResp != null && loginResp.photoMap != null && loginResp.info != null && loginResp.info.code.equals("200")) {
                    userProfile.photoURL = loginResp.photoMap.photoUrl;
                }
                if (loginResp != null && (loginResp.info.code.equals(Constant.RELOGIN_ERROR_CODE) || loginResp.info.code.equals(Constant.UPDATE_THE_APPLICATION) || loginResp.info.code.equals(Constant.UPDATE_TIME_STAMP))) {
                    Utility.onServerHeaderIssue(UserProfileActivity.this, loginResp.info.code);
                }
                updateUserProfile();
            }

            @Override
            public void onFailure(Call<LoginResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                updateUserProfile();
                Log.d("API error", t.getMessage());
            }
        });
    }
    //endregion

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

    //region User profile
    private void requestForUserData(String id) {


            HashMap<String, String> header = new HashMap<>();

            //Check for internet Connection
            if (!hasInternetConnection()) {
                PromeetsDialog.show(this, getString(R.string.no_internet));
                return;
            }
                PromeetsDialog.showProgress(this);
                String[] key = {Constant.MYID, Constant.TIMEZON};
                String[] value = {id, TimeZone.getDefault().getID()};

                header.put("ptimestamp", ServiceHeaderGeneratorUtil.getInstance().getPTimeStamp());
                header.put("promeetsT", ServiceHeaderGeneratorUtil.getInstance().getPromeetsTHeader(Constant.FETCH_MY_USER_PROFILE));
                header.put("accessToken", ServiceHeaderGeneratorUtil.getInstance().getAccessToken());
                header.put("API_VERSION", Utility.getVersionCode());
                new GenericServiceHandler(Constant.ServiceType.USER_PROFILE_DETAIL, this, PromeetsUtils.buildURL(Constant.FETCH_MY_USER_PROFILE, key, value), null, header, IServiceResponseHandler.GET, false, "Please wait!", "Processing..").execute();

    }

    @Override
    public void onServiceResponse(ServiceResponse serviceResponse, Constant.ServiceType serviceType) {
        PromeetsDialog.hideProgress();
        ProfileResp loginRep = (ProfileResp) serviceResponse.getServiceResponse(ProfileResp.class);
        if (isSuccess(loginRep.getInfo().getCode())) {
            userProfile = loginRep.getUserProfile();
            setProfileInfo();
        } else
            onErrorResponse(loginRep.getInfo().getDescription());
    }

    @Override
    public void onErrorResponse(String errorMessage) {
        PromeetsDialog.hideProgress();
        PromeetsDialog.show(this, errorMessage);
    }

    @Override
    public void onErrorResponse(Throwable serviceException) {
        onErrorResponse(serviceException.getLocalizedMessage());
    }

    private void setProfileInfo() {
        if (userProfile == null) return;
        if (!StringUtils.isEmpty(userProfile.photoURL)
                && mFilePhoto == null)
            Glide.with(this).load(userProfile.photoURL).into(mImgPhoto);

        if (!StringUtils.isEmpty(userProfile.fullName)) {
            if (!userProfile.fullName.contains(" ")) {
                if (!StringUtils.isEmpty(userProfile.firstName))
                    mTxtFName.setText(userProfile.firstName);
                else if (!StringUtils.isEmpty(userProfile.lastName))
                    mTxtLName.setText(userProfile.lastName);
                else mTxtFName.setText(userProfile.fullName);
            } else {
                String[] names = userProfile.fullName.split("\\s+");
                mTxtLName.setText(names[names.length - 1]);
                String fname = "";
                for (int i = 0; i < names.length - 1; i++) {
                    if (i == names.length - 2)
                        fname += names[i];
                    else
                     fname += names[i] + " ";
                }
                mTxtFName.setText(fname);
            }
        }

        if (!StringUtils.isEmpty(userProfile.city))
            mTxtLoc.setText(userProfile.city);
        if (!StringUtils.isEmpty(userProfile.employer))
            mTxtComp.setText(userProfile.employer);
        if (!StringUtils.isEmpty(userProfile.position))
            mTxtPos.setText(userProfile.position);

        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.convertDpToPx(4, this),
                ScreenUtil.convertDpToPx(5, this),
                ScreenUtil.convertDpToPx(4, this),
                ScreenUtil.convertDpToPx(5, this));
        mFlexBox.removeAllViewsInLayout();
        if (userProfile.pollingList != null && userProfile.pollingList.size() > 0) {
            for (final Category cate : userProfile.pollingList) {
                for (final SubCate subCate : cate.getList()) {
                    final TextView mTxtTag = new TextView(this);
                    mTxtTag.setLayoutParams(lp);
                    mTxtTag.setText(subCate.getTitle());
                    mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    mTxtTag.setTextColor(Color.WHITE);
                    mTxtTag.setPadding(ScreenUtil.convertDpToPx(15, this),
                            ScreenUtil.convertDpToPx(5, this),
                            ScreenUtil.convertDpToPx(15, this),
                            ScreenUtil.convertDpToPx(5, this));
                    mTxtTag.setBackgroundResource(R.drawable.tag_solid_primary);
                    mTxtTag.setTypeface(tf_semi);
                    mFlexBox.addView(mTxtTag);
                    mTxtTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(UserProfileActivity.this, CategorySearchActivity.class);
                            intent.putExtra("subCategoryId", subCate.getId());
                            intent.putExtra("categoryId", cate.getId());
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    });
                }
            }
        }
    }

    private void collectALLInfo() {
        userProfile.firstName = mTxtFName.getText().toString();
        userProfile.lastName = mTxtLName.getText().toString();
        userProfile.fullName = userProfile.firstName + " " + userProfile.lastName;
        userProfile.city = mTxtLoc.getText().toString();
        userProfile.employer = mTxtComp.getText().toString();
        userProfile.position = mTxtPos.getText().toString();
    }

    private void updateUserProfile() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("userprofile/update"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String userProfileJson = gson.toJson(userProfile);
        UserActionApi service = retrofit.create(UserActionApi.class);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), userProfileJson);
        Call<SuperResp> call = service.updateUserProfile(requestBody);
        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                PromeetsDialog.hideProgress();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(UserProfileActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    finish();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(UserProfileActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(UserProfileActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(UserProfileActivity.this, t.getLocalizedMessage());
            }
        });
    }
    //endregion
}
