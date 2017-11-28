/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.List;

import rx.Observable;

/**
 * Presenter for AllergiesActivity
 */
public class AllergiesPresenter extends BaseHealthItemIntakePresenter<AllergiesActivity, Allergy> {

    private static int GET_ALLERGIES = 310;
    private static int UPDATE_ALLERGIES = 311;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_ALLERGIES,
                new SampleRequestFunc0<SDKResponse<List<Allergy>, SDKError>>(GET_ALLERGIES) {
                    @Override
                    public Observable<SDKResponse<List<Allergy>, SDKError>> go() {
                        return observableService.getAllergies(consumer);
                    }
                },
                new SampleResponseAction2<List<Allergy>, SDKError, SDKResponse<List<Allergy>, SDKError>>(GET_ALLERGIES) {
                    @Override
                    public void onSuccess(AllergiesActivity activity, List<Allergy> allergies) {
                        setItems(allergies);
                    }
                },
                new SampleFailureAction2(GET_ALLERGIES)
        );

        restartableLatestCache(UPDATE_ALLERGIES,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(UPDATE_ALLERGIES) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.updateAllergies(
                                consumer,
                                consumerHealthItems
                        );
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(UPDATE_ALLERGIES) {
                    @Override
                    public void onSuccess(AllergiesActivity activity, Void aVoid) {
                        view.setAllergiesSubmitted();
                    }
                },
                new SampleFailureAction2(UPDATE_ALLERGIES)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    public void startGetItems() {
        start(GET_ALLERGIES);
    }

    public void updateAllergies() {
        start(UPDATE_ALLERGIES);
    }

}
