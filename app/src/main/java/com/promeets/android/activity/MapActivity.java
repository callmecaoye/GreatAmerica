package com.promeets.android.activity;

import android.location.Location;
import android.support.annotation.NonNull;
import android.os.Bundle;


import com.promeets.android.custom.PermissionSetting;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.R;
import com.promeets.android.util.LocationHandlerUtil;
import com.promeets.android.util.ScreenUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a general activity to show markers on the mapView
 *
 * Pass a list of EventLocationPOJO and set isSelect for marker style
 *
 * @source: pages with Map fragment
 *
 */

public class MapActivity extends BaseActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ArrayList<EventLocationPOJO> mList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    private PermissionSetting mSetting;

    @Override
    public void initElement() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mList = (ArrayList<EventLocationPOJO>) bundle.getSerializable("list");
        }
    }

    @Override
    public void registerListeners() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSetting = new PermissionSetting(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestLocPermission();
        mMap.setMaxZoomPreference(15);

        if (mList != null && mList.size() > 0) {
            for (EventLocationPOJO pojo : mList) {
                if (StringUtils.isEmpty(pojo.latitude) || StringUtils.isEmpty(pojo.longitude))
                    continue;
                LatLng loc = new LatLng(Double.valueOf(pojo.latitude), Double.valueOf(pojo.longitude));
                Marker marker;
                if (pojo.isSelected) {
                    marker = mMap.addMarker(new MarkerOptions().position(loc).title(pojo.location)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_primary)));
                } else {
                    marker = mMap.addMarker(new MarkerOptions().position(loc).title(pojo.location)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_gray)));

                }
                marker.setTag(pojo);
                markerList.add(marker);
                builder.include(marker.getPosition());
            }

            if (markerList != null && markerList.size() > 0) {
                final LatLngBounds bounds = builder.build();
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                                ScreenUtil.convertDpToPx(100, MapActivity.this)));
                    }
                });
            }
        } else {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    try {
                        Location cur = LocationHandlerUtil.getInstance(MapActivity.this).getLastKnownLocation();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cur.getLatitude(), cur.getLongitude()), 15));
                    } catch (Exception e) {
                        e.printStackTrace();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.483254, -122.174395), 9));
                    }
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    private void requestLocPermission() {
        AndPermission.with(this)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        try {
                            mMap.setMyLocationEnabled(true);
                        } catch (SecurityException e){
                            mSetting.showSetting(permissions);
                        } catch (Exception e) {
                            mSetting.showSetting(permissions);
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {

                    }
                })
                .start();
    }
}
