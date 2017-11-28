/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.appointments;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for AppointmentsActivity
 */
public class AppointmentsPresenter extends BaseSampleNucleusRxPresenter<AppointmentsActivity> {

    private static final int GET_APPOINTMENTS = 110;
    private static final int CANCEL_APPOINTMENT = 111;

    @State
    Appointment appointment;
    @State
    ArrayList<Appointment> appointments; // these are ArrayList vs just List b/c they need to be Parcelable

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_APPOINTMENTS,
                new SampleRequestFunc0<SDKResponse<List<Appointment>, SDKError>>(GET_APPOINTMENTS) {
                    @Override
                    public Observable<SDKResponse<List<Appointment>, SDKError>> go() {
                        return observableService.getAppointments(consumer);
                    }
                },
                new SampleResponseAction2<List<Appointment>, SDKError, SDKResponse<List<Appointment>, SDKError>>(GET_APPOINTMENTS) {
                    @Override
                    public void onSuccess(AppointmentsActivity appointmentsActivity, List<Appointment> appointments) {
                        setAppointments((ArrayList<Appointment>) appointments);
                    }
                },
                new SampleFailureAction2(GET_APPOINTMENTS));

        restartableLatestCache(CANCEL_APPOINTMENT,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(CANCEL_APPOINTMENT) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.cancelAppointment(consumer, appointment);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(CANCEL_APPOINTMENT) {
                    @Override
                    public void onSuccess(AppointmentsActivity appointmentsActivity, Void aVoid) {
                        appointmentsActivity.setAppointmentCanceled();
                        getAppointments();
                    }
                },
                new SampleFailureAction2(CANCEL_APPOINTMENT));
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(AppointmentsActivity view) {
        super.onTakeView(view);
        if (appointments == null) {
            getAppointments(); // if we haven't already fetched, fetch
        }
        else {
            setAppointments(appointments);
        }
    }

    public void getAppointments() {
        start(GET_APPOINTMENTS);
    }

    public void setAppointments(final ArrayList<Appointment> appointments) {
        this.appointments = appointments;
        view.setListItems(appointments);
        view.setTitleCount(appointments.size());
    }

    public void cancelAppointment(final Appointment appointment) {
        this.appointment = appointment;
        start(CANCEL_APPOINTMENT);
    }
}
