/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.location.Location;
import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for FindPharmacyActivity
 */
public class FindPharmacyPresenter extends LocationListPresenter<FindPharmacyActivity> {

    private static final int FIND_PHARMACIES = 820;
    private static final int FIND_PHARMACIES_NEAR_ME = 821;

    @State
    String query;
    @State
    ArrayList<Pharmacy> pharmacies;

    @State
    boolean locationFailed = false;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(FIND_PHARMACIES,
                new SampleRequestFunc0<SDKValidatedResponse<List<Pharmacy>, SDKError>>(FIND_PHARMACIES) {
                    @Override
                    public Observable<SDKValidatedResponse<List<Pharmacy>, SDKError>> go() {
                        return observableService.findPharmaciesByZip(
                                consumer,
                                query
                        );
                    }
                },
                new SampleValidatedResponseAction2<List<Pharmacy>, SDKError, SDKValidatedResponse<List<Pharmacy>, SDKError>>(FIND_PHARMACIES) {
                    @Override
                    public void onSuccess(FindPharmacyActivity findPharmacyActivity, List<Pharmacy> pharmacies) {
                        view.setListHeader(query);
                        setPharmacies((ArrayList<Pharmacy>) pharmacies);
                        stop(FIND_PHARMACIES);
                    }
                },
                new SampleFailureAction2(FIND_PHARMACIES)
        );

        restartableLatestCache(FIND_PHARMACIES_NEAR_ME,
                new SampleRequestFunc0<SDKResponse<List<Pharmacy>, SDKError>>(FIND_PHARMACIES_NEAR_ME) {
                    @Override
                    public Observable<SDKResponse<List<Pharmacy>, SDKError>> go() {
                        Location location = getLocation();
                        float longitude = (float) location.getLongitude();
                        float latitude = (float) location.getLatitude();
                        return observableService.findPharmaciesNearMe(
                                consumer,
                                latitude,
                                longitude,
                                10,
                                false
                        );
                    }
                },
                new SampleResponseAction2<List<Pharmacy>, SDKError, SDKResponse<List<Pharmacy>, SDKError>>(FIND_PHARMACIES_NEAR_ME) {
                    @Override
                    public void onSuccess(FindPharmacyActivity findPharmacyActivity, List<Pharmacy> pharmacies) {
                        view.setListHeader(view.getString(R.string.pharmacy_search_current_location));
                        setPharmacies((ArrayList<Pharmacy>) pharmacies);
                        stop(FIND_PHARMACIES_NEAR_ME);
                    }
                },
                new SampleFailureAction2(FIND_PHARMACIES_NEAR_ME)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(FindPharmacyActivity view) {
        super.onTakeView(view);
        if (pharmacies != null) {
            setPharmacies(pharmacies); // if we've already fetched some, use them
        }
        view.setQuery(query);
    }

    public void findPharmacies(final String query) {
        if (isUnsubscribed(FIND_PHARMACIES_NEAR_ME)) {
            this.query = query;
            start(FIND_PHARMACIES);
        }
    }

    public void setPharmacies(final ArrayList<Pharmacy> pharmacies) {
        this.pharmacies = pharmacies;
        view.setPharmacies(pharmacies);
        view.setEmptyPharmacies(pharmacies.isEmpty(), query);
    }

    public void requestPharmaciesByLocation() {
        if (isUnsubscribed(FIND_PHARMACIES)) {
            start(FIND_PHARMACIES_NEAR_ME);
        }
    }

    @Override
    public void initLocationListener() {
        super.initLocationListener();
        view.invalidateOptionsMenu();
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        view.invalidateOptionsMenu();
    }

    public void setLocationFailed(boolean locationFailed) {
        this.locationFailed = locationFailed;
    }

    public boolean getLocationFailed() {
        return locationFailed;
    }
}
