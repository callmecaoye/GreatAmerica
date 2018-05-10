package com.promeets.android.util;

import android.Manifest;
import com.promeets.android.MyApplication;
import com.promeets.android.activity.MainActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.promeets.android.custom.PromeetsDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import com.promeets.android.Constant;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import com.promeets.android.object.NotificationPOJO;
import android.os.Build;
import android.os.Environment;
import com.promeets.android.pojo.ServiceDetailResp;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public static final byte VISA = 0;
    public static final byte MASTERCARD = 1;
    public static final byte AMEX = 2;
    public static final byte DINERS_CLUB = 3;
    public static final byte CARTE_BLANCHE = 4;
    public static final byte DISCOVER = 5;
    public static final byte ENROUTE = 6;
    public static final byte JCB = 7;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 321;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File saveFile(Bitmap bm, String fileName) throws IOException {
        String path = Environment.getExternalStorageDirectory() +"/Promeets/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The mBaseActivity.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority
     * <p>
     * <p>
     * <p>
     * is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        String expression = "^[0-9a-zA-Z@#$%^&+=]{6,}$";
        //String expression = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,})";
        CharSequence inputString = password;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidPhone(String number) {
        String expression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";
        CharSequence inputString = number;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static Bitmap scaleBitmap(Context context, String photoFilePath) {
        // Create and configure BitmapFactory
        final BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, opt);
        opt.inSampleSize = calculateInSampleSize(opt, 320, 320);
        //opt.inTempStorage = new byte[16 * 1024];
        opt.inJustDecodeBounds = false;
        Bitmap bm = null;
        try {
            // load the bitmap 
            bm = BitmapFactory.decodeFile(photoFilePath, opt);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        // Rotate bitmap if necessary (for brand: SAMSUNG)
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        if (rotatedBitmap != bm && bm != null && !bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        // Return result
        return rotatedBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static String getEncryptedString(String psw) {
        if (TextUtils.isEmpty(psw)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("sha-256");
            byte[] bytes = md5.digest((psw).getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setCardLogo(String creditCard, ImageView imageView) {
        try {
            if (validate(creditCard, VISA))
                imageView.setImageResource(R.drawable.card_visa);
            else if (validate(creditCard, MASTERCARD))
                imageView.setImageResource(R.drawable.card_mastercard);
            else if (validate(creditCard, DINERS_CLUB))
                imageView.setImageResource(R.drawable.card_dinner);
            else if (validate(creditCard, AMEX))
                imageView.setImageResource(R.drawable.card_amex);
            else if (validate(creditCard, CARTE_BLANCHE))
                imageView.setImageResource(R.drawable.card_carte);
            else if (validate(creditCard, DISCOVER))
                imageView.setImageResource(R.drawable.card_discover);
            else if (validate(creditCard, ENROUTE))
                imageView.setImageResource(R.drawable.card_enroute);
            else if (validate(creditCard, JCB))
                imageView.setImageResource(R.drawable.card_jcb);
            else
                imageView.setImageResource(R.drawable.card_default);
        } catch (Exception ex) {
            ex.printStackTrace();
            imageView.setImageResource(R.drawable.card_default);
        }
    }

    public static boolean validate(final String credCardNumber, final byte type) {
        String creditCard = credCardNumber.trim();
        boolean applyAlgo = false;
        switch (type) {
            case VISA:
                // VISA credit cards has length 13 - 15
                // VISA credit cards starts with prefix 4
                if (creditCard.startsWith("4")) {
                    applyAlgo = true;
                }
                break;
            case MASTERCARD:
                // MASTERCARD has length 16
                // MASTER card starts with 51, 52, 53, 54 or 55
                int prefix = Integer.parseInt(creditCard.substring(0, 2));
                if (prefix >= 51 && prefix <= 55) {
                    applyAlgo = true;
                }
                break;
            case AMEX:
                // AMEX has length 15
                // AMEX has prefix 34 or 37
                if ((creditCard.startsWith("34") || creditCard
                        .startsWith("37"))) {
                    applyAlgo = true;
                }
                break;
            case DINERS_CLUB:
            case CARTE_BLANCHE:
                // DINERSCLUB or CARTEBLANCHE has length 14
                // DINERSCLUB or CARTEBLANCHE has prefix 300, 301, 302, 303, 304,
                // 305 36 or 38
                try{
                    prefix = Integer.parseInt(creditCard.substring(0, 3));
                }catch(Exception ex){
                    prefix=0;
                }
                if ((prefix >= 300 && prefix <= 305)
                        || creditCard.startsWith("36")
                        || creditCard.startsWith("38")) {
                    applyAlgo = true;
                }
                break;
            case DISCOVER:
                // DISCOVER card has length of 16
                // DISCOVER card starts with 6011
                if (creditCard.startsWith("6011")) {
                    applyAlgo = true;
                }
                break;
            case ENROUTE:
                // ENROUTE card has length of 16
                // ENROUTE card starts with 2014 or 2149
                if ((creditCard.startsWith("2014") || creditCard
                        .startsWith("2149"))) {
                    applyAlgo = true;
                }
                break;
            case JCB:
                // JCB card has length of 16 or 15
                // JCB card with length 16 starts with 3
                // JCB card with length 15 starts with 2131 or 1800
                if ((creditCard.startsWith("3"))
                        || (creditCard.length() <= 15 && (creditCard
                        .startsWith("2131") || creditCard
                        .startsWith("1800")))) {
                    applyAlgo = true;
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
//        if (applyAlgo) {
//            return validate(creditCard);
//        }
        return applyAlgo;
    }

    public static boolean validate(String creditCard) {

        int sum = 0;
        int length = creditCard.length();
        for (int i = 0; i < creditCard.length(); i++) {
            if (0 == (i % 2)) {
                sum += creditCard.charAt(length - i - 1) - '0';
            } else {
                sum += sumDigits((creditCard.charAt(length - i - 1) - '0') * 2);
            }
        }
        return 0 == (sum % 10);
    }

    private static int sumDigits(int i) {
        return (i % 10) + (i / 10);
    }


    public static String getVersionCode(){
        String versionName = "";
        try{
            PackageInfo pInfo = MyApplication.getContext().getPackageManager().getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            versionName =pInfo.versionName;
        }catch(Exception ex){

        }
        return versionName;
    }

    public static void onServerHeaderIssue(final Activity activity, String errorCode){
        new ServerTimeUtil(activity).getServerTime();
        if(errorCode.equals(Constant.RELOGIN_ERROR_CODE)){
            activity.finishAffinity();

            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("isServerDenied",true);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else if(errorCode.equals(Constant.UPDATE_THE_APPLICATION)){
            PromeetsDialog.show(activity, "New version of the Promeets is available on google play, Please update.", new PromeetsDialog.OnOKListener() {
                @Override
                public void onOKListener() {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.promeets.android"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finishAffinity();
                    } catch (android.content.ActivityNotFoundException exception) {

                    }
                }
            });
        }
    }

    public static String getDeviceId(){

        String android_id = Settings.Secure.getString(MyApplication.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;

//        final TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//
//        final String tmDevice, tmSerial, androidId;
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//
//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
//
//        return deviceUuid.toString();
        //return System.currentTimeMillis()+"";
        //return "shashank123";
    }

    static ServiceDetailResp serviceDetailResp;
    public static void setPreviewObj(ServiceDetailResp serviceDetailResp){
        Utility.serviceDetailResp = serviceDetailResp;
    }

    public static ServiceDetailResp getServiceDetailResp() {
        return serviceDetailResp;
    }

    public static int px2dp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        int dp = (int) (px / density + 0.5f);
        return dp;
    }

    public static int unreadOneSignal() {
        int result = 0;
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
                    if (pojo.readFlag == 0)
                        result++;
                }
            }
        }
        return result;
    }

    public static Bitmap getThumbFromVideo(String videoPath) throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public static String extractYTId(String ytUrl) {
        String vId = "";
        Pattern pattern = Pattern.compile("(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*");
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.find()){
            vId = matcher.group();
        }
        return vId;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
