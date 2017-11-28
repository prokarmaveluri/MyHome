/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.services;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.RemindOptions;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.util.Date;

import icepick.State;
import rx.Observable;

/**
 * Presenter for ScheduleAppointmentActivity
 *
 * @since AWSDK 3.0
 */
public class ScheduleAppointmentPresenter extends BaseSampleNucleusRxPresenter<ScheduleAppointmentActivity> {

    private static final int SCHEDULE_APPOINTMENT = 1020;

    @State
    ProviderInfo providerInfo;
    @State
    Date appointmentDate;
    @State
    String callbackNumber;
    @State
    RemindOptions remindOptions;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(SCHEDULE_APPOINTMENT,
                new SampleRequestFunc0<SDKValidatedResponse<Void, SDKError>>(SCHEDULE_APPOINTMENT) {
                    @Override
                    public Observable<SDKValidatedResponse<Void, SDKError>> go() {
                        return observableService.scheduleAppointment(consumer, providerInfo, appointmentDate, callbackNumber, remindOptions);
                    }
                },
                new SampleValidatedResponseAction2<Void, SDKError, SDKValidatedResponse<Void, SDKError>>(SCHEDULE_APPOINTMENT) {
                    @Override
                    public void onSuccess(ScheduleAppointmentActivity providerActivity, Void aVoid) {
                        view.setAppointmentScheduled(providerInfo, appointmentDate);
                    }

                    @Override
                    public void onError(ScheduleAppointmentActivity scheduleAppointmentActivity, SDKError sdkError) {
                        // custom error handling here, we want to finish the activity if there's an error
                        stop(SCHEDULE_APPOINTMENT);
                        view.handleErrorAndFinish(sdkError);
                    }
                },
                new SampleFailureAction2(SCHEDULE_APPOINTMENT)
        );

    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(ScheduleAppointmentActivity view) {
        super.onTakeView(view);
        view.setProviderInfo(providerInfo);
        view.setAppointmentDate(appointmentDate);

        if (callbackNumber == null) {
            callbackNumber = consumer.getPhone();
        }

        view.setCallbackNumber(callbackNumber);

        if (remindOptions == null) {
            remindOptions = RemindOptions.NO_REMINDER;
        }

        view.setRemindOptions(remindOptions);
    }

    public void setProviderInfo(final ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }

    public void setAppointmentDate(final Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void scheduleAppointment() {
        start(SCHEDULE_APPOINTMENT);
    }

    public void setRemindOptions(final RemindOptions remindOptions) {
        this.remindOptions = remindOptions;
    }

    public void setCallbackNumber(final String callbackNumber) {
        this.callbackNumber = callbackNumber;
    }

}
