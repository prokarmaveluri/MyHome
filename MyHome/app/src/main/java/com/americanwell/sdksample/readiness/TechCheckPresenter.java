/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.readiness;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.AppointmentReadiness;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.ArrayList;

import rx.Observable;

/**
 * Presenter for TechCheckActivity
 */

public class TechCheckPresenter extends BaseSampleNucleusRxPresenter<TechCheckActivity> {

    private static final int UPDATE_APPOINTMENT_READINESS = 910;

    private static final String LOG_TAG = TechCheckPresenter.class.getName();

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        restartableLatestCache(UPDATE_APPOINTMENT_READINESS,
                new SampleRequestFunc0<SDKResponse<AppointmentReadiness, SDKError>>(UPDATE_APPOINTMENT_READINESS) {
                    @Override
                    public Observable<SDKResponse<AppointmentReadiness, SDKError>> go() {
                        return observableService.updateAppointmentReadiness(
                                consumer, null, true
                        );
                    }
                },
                new SampleResponseAction2<AppointmentReadiness, SDKError, SDKResponse<AppointmentReadiness,
                        SDKError>>(UPDATE_APPOINTMENT_READINESS) {
                    @Override
                    public void onSuccess(TechCheckActivity techCheckActivity,
                                          AppointmentReadiness appointmentReadiness) {
                        stop(UPDATE_APPOINTMENT_READINESS);
                        techCheckActivity.finish(Activity.RESULT_OK);
                    }

                    @Override
                    public void onError(TechCheckActivity techCheckActivity, SDKError sdkError) {
                        super.onError(techCheckActivity, sdkError);
                        Toast.makeText(techCheckActivity, R.string.tech_check_request_error,
                                Toast.LENGTH_SHORT).show();
                        techCheckActivity.finish(Activity.RESULT_CANCELED);
                    }
                },
                new SampleFailureAction2(UPDATE_APPOINTMENT_READINESS)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);

    }

    public void setWrapUp() {
        if (hasPermissions()) {
            setTechCheckPassed();
        }
        else {
            view.finish(Activity.RESULT_OK);
        }
    }

    private void setTechCheckPassed() {
        start(UPDATE_APPOINTMENT_READINESS);
    }

    void updateData() {
        if (hasPermissions()) {
            view.setHasPermissions();
        }
        else {
            view.setMissingPermission();
        }
    }

    private boolean hasPermissions() {
        ArrayList<String> missingPermissions = new ArrayList<>();

        String[] requiredPermissions = awsdk.getRequiredPermissions();

        if (requiredPermissions.length != 0) {
            for (String requiredPermission : requiredPermissions) {
                if (ContextCompat.checkSelfPermission(view, requiredPermission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(requiredPermission);
                }
            }
        }

        return missingPermissions.isEmpty();
    }
}
