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
import com.americanwell.sdk.entity.visit.Vitals;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for VitalsActivity
 */
public class VitalsPresenter extends BaseSampleNucleusRxPresenter<VitalsActivity> {

    private static final int UPDATE_VITALS = 340;

    @Inject
    ConsumerManager consumerManager;

    @State
    String systolic;
    @State
    String diastolic;
    @State
    String temperature;
    @State
    String weight;

    @State
    Vitals vitals;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // notice there's no "getVitals" here.
        // the sample app collects vitals outside the context of a visit, so vitals are collected
        // and uploaded.
        // if vitals collection is implemented as part of the intake process, a visit context can be provided
        // as well, which will allow for both storage and retrieval of vitals, but only within the context of that
        // visit

        restartableLatestCache(UPDATE_VITALS,
                new SampleRequestFunc0<SDKValidatedResponse<Void, SDKError>>(UPDATE_VITALS) {
                    @Override
                    public Observable<SDKValidatedResponse<Void, SDKError>> go() {
                        return observableService.updateVitals(
                                consumer,
                                vitals,
                                null
                        );
                    }
                },
                new SampleValidatedResponseAction2<Void, SDKError, SDKValidatedResponse<Void, SDKError>>(UPDATE_VITALS) {
                    @Override
                    public void onSuccess(VitalsActivity vitalsActivity, Void aVoid) {
                        view.setVitalsUpdated(true);
                    }
                },
                new SampleFailureAction2(UPDATE_VITALS)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(VitalsActivity view) {
        super.onTakeView(view);
        view.setSystolic(systolic);
        view.setDiastolic(diastolic);
        view.setBodyTemperature(temperature);
        view.setWeight(weight);
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void submitVitals() {

        if (systolic == null && diastolic == null && temperature == null && weight == null) {
            view.setVitalsUpdated(false);
            return;
        }

        vitals = consumerManager.getNewVitals();

        // Systolic blood pressure
        final String systolicBloodPressureText = systolic;
        if (systolicBloodPressureText.length() > 0) {
            vitals.setSystolic(Integer.parseInt(systolicBloodPressureText));
        }

        // Diastolic blood pressure
        final String diastolicBloodPressureText = diastolic;
        if (diastolicBloodPressureText.length() > 0) {
            vitals.setDiastolic(Integer.parseInt(diastolicBloodPressureText));
        }

        // Body temperature
        final String bodyTemperatureText = temperature;
        if (bodyTemperatureText.length() > 0) {
            vitals.setTemperature(Double.parseDouble(bodyTemperatureText));
        }

        // Weight
        final String weightText = weight;
        if (weightText.length() > 0) {
            vitals.setWeight(Integer.parseInt(weightText));
        }

        if (!vitals.isEmpty()) { // only update if we have data
            start(UPDATE_VITALS);
        }

    }
}
