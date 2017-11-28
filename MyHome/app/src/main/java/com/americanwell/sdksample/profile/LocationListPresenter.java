/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.LocationListActivity;

import java.util.Date;
import java.util.List;

/**
 * Presenter for LocationListActivity
 */
public abstract class LocationListPresenter<V extends LocationListActivity> extends
        BaseSampleNucleusRxPresenter<V> implements LocationListener {
    public final static int PERMISSION_ACCESS_FINE_LOCATION = 55;
    private final static long FIFTEEN_MINUTES = 1000 * 60 * 15;
    private LocationManager locationManager;
    private Location mLocation;

    public void disableLocationUpdates() {
        try {
            getLocationManager().removeUpdates(this);
        }
        catch (SecurityException e) {
            DefaultLogger.w("AWSDK", "Missing Location Permissions");
        }
    }

    private LocationManager getLocationManager() {
        if (locationManager == null) {
            // Acquire a reference to the system Location Manager
            locationManager = (LocationManager) view.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }

    @SuppressWarnings("MissingPermission")
    public void initLocationListener() {
        if (hasPermission()) {
            getLastLocation();

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);

            String provider = getLocationManager().getBestProvider(criteria, true);

            long time = 0;
            float distance = 30;

            // Register the listener with the Location Manager to receive location updates
            getLocationManager().requestLocationUpdates(provider, time, distance, this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;
        long minTime = new Date().getTime() - FIFTEEN_MINUTES;
        List<String> matchingProviders = getLocationManager().getAllProviders();
        for (String provider : matchingProviders) {
            Location location = getLocationManager().getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    mLocation = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
                else if (time < minTime &&
                        bestAccuracy == Float.MAX_VALUE && time > bestTime) {
                    mLocation = location;
                    bestTime = time;
                }
            }
        }
    }

    protected boolean hasPermission() {
        boolean permission = true;

        boolean hasFineLocation = ActivityCompat.checkSelfPermission(view, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;

        boolean hasCourseLocation = ActivityCompat.checkSelfPermission(view, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;

        if (hasFineLocation &&
                hasCourseLocation) {
            ActivityCompat.requestPermissions(view, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            permission = false;
        }
        return permission;
    }

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Called when a new location is found by the location provider.
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
