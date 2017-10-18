/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample;

import android.content.pm.PackageManager;

import com.americanwell.sdksample.profile.LocationListPresenter;

/**
 * Activity used to request location changes from the device.
 */
public class LocationListActivity<P extends LocationListPresenter> extends BaseSampleNucleusActivity<P> {
    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().initLocationListener();
    }

    @Override
    protected void onPause() {
        getPresenter().disableLocationUpdates();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LocationListPresenter.PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPresenter().initLocationListener();
                }
                invalidateOptionsMenu();

                break;
        }
    }
}
