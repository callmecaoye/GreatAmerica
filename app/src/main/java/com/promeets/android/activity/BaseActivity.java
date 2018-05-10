package com.promeets.android.activity;

import android.content.Context;
import android.content.Intent;
import com.promeets.android.fragment.AccountFragment;
import com.promeets.android.fragment.HomePageFragment;
import com.promeets.android.fragment.NotificationListFragment;
import com.promeets.android.listeners.OnScreenChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import com.promeets.android.util.PromeetsPreferenceUtil;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.promeets.android.fragment.EventFragment;
import com.promeets.android.fragment.ServiceSearchListFragment;
import com.promeets.android.Constant;
import com.promeets.android.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This is an abstract class as base activity
 *
 * Most activities except Chat module are extending from it
 *
 * Including initialization(Chat module),
 * defining activity life cycle
 * application themes(full screen with no TitleBar),
 * common utilities (start activity, check internet status, hide soft keyboard)
 * and Fragment container and navigation
 *
 */

public abstract class BaseActivity extends AppCompatActivity implements OnScreenChangeListener, Constant {

    public static String token = "";

    public Fragment fragment = null;

    public FragmentManager mFragmentManager;

    public PromeetsPreferenceUtil promeetsPreferenceUtil;

    public boolean isFinishActivity = false;

    private FragmentTransaction mFragmentTransaction;

    private int fragmentId;

    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";

    final DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onStart() {
        super.onStart();
        initElement();
        registerListeners();
    }

    public abstract void initElement();

    public abstract void registerListeners();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promeetsPreferenceUtil = new PromeetsPreferenceUtil();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
    }

    public int getWidth() {
        return displayMetrics.widthPixels;
    }

    public int getHeight(){ return displayMetrics.heightPixels; }

    public void setTransparentStatus(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    public void initFragment() {

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * This method played as a role of Fragment navigation
     *
     * Using Enum as fragmentName to decide which Fragment to be loaded
     *
     * @param containerId a full-screen layout to hold fragment
     * @param fragmentName enum SCREEN in OnScreenChangeListener
     * @param fragmentTag
     * @param addTobackStack back stack to restore fragment
     * @param data Bundle data for fragment initialization
     * @return
     */
    @Override
    public Fragment onScreenChange(int containerId, SCREEN fragmentName, String fragmentTag, boolean addTobackStack,Object data) {
        initFragment();
        Fragment fragment=null;

        switch (fragmentName) {
            // Tab1
            case HOME_SCREEN_FRAGMENT:
                fragment = mFragmentManager.findFragmentByTag(fragmentTag);
                if (fragment == null)
                    fragment = HomePageFragment.newInstance();
                break;
            // Tab 2
            case SERVICE_SEARCH_FRAGMENT:
                fragment = mFragmentManager.findFragmentByTag(fragmentTag);
                if (fragment == null)
                    fragment = ServiceSearchListFragment.newInstance();
                break;
            // Tab 3
            case EVENT_FRAGMENT:
                fragment = mFragmentManager.findFragmentByTag(fragmentTag);
                if (fragment == null)
                    fragment = EventFragment.newInstance();
                break;
            // Tab 4
            case NOTIFICATION_LIST_FRAGMENT:
                fragment = mFragmentManager.findFragmentByTag(fragmentTag);
                if (fragment == null)
                    fragment = NotificationListFragment.newInstance();
                break;
            // Tab 5
            case ACCOUNT_SCREEN_FRAGMENT:
                fragment = mFragmentManager.findFragmentByTag(fragmentTag);
                if (fragment == null)
                    fragment = AccountFragment.newInstance();
                break;
        }

        addFragment(containerId, fragment, fragmentTag);
        return fragment;
    }


    private void addFragment(final int containerId, Fragment fragment, String fragmentTag) {
        if(fragment instanceof DialogFragment){
            try{
                ((DialogFragment) fragment).show(mFragmentTransaction,fragment.getTag());
            }catch(Exception ex){

            }
            return;
        }

        /*if(this.fragment!=null) {
            mFragmentTransaction.remove(this.fragment);
            Log.d("Remove....", fragment + " Removed");
        }*/

        mFragmentTransaction.replace(containerId, fragment, fragmentTag);
        Log.d("Replaced....", fragment + " Replaced");
        /*if (addToBackStack)
            mFragmentTransaction.addToBackStack(fragment.getTag());*/
        mFragmentTransaction.commitAllowingStateLoss();
        mFragmentManager.executePendingTransactions();



        /*this.fragment = fragment;
        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Log.d("Back Change", "Back Change listner");
                int backCount = getSupportFragmentManager().getBackStackEntryCount();
                if (backCount == 0) {
                    // block where back has been pressed. since backstack is zero.
                }
                Fragment fragment = mFragmentManager.findFragmentById(containerId);
                fragmentId = Integer.valueOf(fragment.getTag());
            }
        });*/
    }

    public int getCurrentFragmentID(){
        return fragmentId;
    }

    /**
     * Common method to check internet connectivity status
     * @return
     */
    public Boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Common method to start activity with animation
     * @param activity
     */
    public final void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public final void enableView(View view) {
        view.setEnabled(true);
    }

    public final void disableView(View view) {
        view.setEnabled(false);
    }

    public boolean isSuccess(String value){
        return value != null && value.equals("200");
    }

    public Fragment getPreviousFragment(String TAG){
        initFragment();

        for(int entry = 0; entry < mFragmentManager.getBackStackEntryCount(); entry++){
            Log.i(TAG, "Found fragment: " + mFragmentManager.getBackStackEntryAt(entry).getId());
        }

        return mFragmentManager.findFragmentByTag(TAG);
    }
}

