package com.promeets.android.util;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.promeets.android.activity.BaseActivity;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shashank Shekhar on 30-01-2017.
 */

public class LocationHandlerUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static GoogleApiClient mGoogleApiClient;

    private BaseActivity mBaseActivity;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;

    private LocationHandlerUtil(BaseActivity baseActivity) {
        // Create an instance of GoogleAPIClient.
        this.mBaseActivity = baseActivity;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(baseActivity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
    }

    ;

    private static LocationHandlerUtil instance;

    public static synchronized LocationHandlerUtil getInstance(BaseActivity baseActivity) {
        if (instance == null)
            instance = new LocationHandlerUtil(baseActivity);
        if (mGoogleApiClient.isConnected() == false)
            mGoogleApiClient.connect();
        return instance;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public String getLastKnownCity() throws IOException, SecurityException {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.d("Last Known location", mLastLocation + "");
        if (mLastLocation != null) {
            Geocoder geocoder = new Geocoder(mBaseActivity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    1);

            Log.d("Address", addresses + "");
            Log.d("Address", "First return");
            if (addresses == null || addresses.size() == 0)
                return "";
            else {
                Address address = addresses.get(0);
                Log.d("CityName", address.getLocality());
                return address.getLocality().replace(" ", "%20");
            }
        }
        return "";
    }

    public Location getLastKnownLocation() throws IOException, SecurityException {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.e("Last Known location", mLastLocation + "");
        if (mLastLocation != null) {
            return  mLastLocation;
        }
        return null;
    }
}