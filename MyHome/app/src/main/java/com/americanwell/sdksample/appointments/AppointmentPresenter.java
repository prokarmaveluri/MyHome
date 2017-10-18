/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.appointments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdk.entity.visit.CheckInStatus;
import com.americanwell.sdk.entity.visit.Disposition;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.Date;

import icepick.State;
import rx.Observable;

/**
 * Presenter for AppointmentActivity
 */
public class AppointmentPresenter extends BaseSampleNucleusRxPresenter<AppointmentActivity> {

    private static final int GET_VISIT_CONTEXT = 100;

    @State
    Appointment appointment;
    @State
    VisitContext visitContext;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_VISIT_CONTEXT,
                new SampleRequestFunc0<SDKResponse<VisitContext, SDKError>>(GET_VISIT_CONTEXT) {
                    @Override
                    public Observable<SDKResponse<VisitContext, SDKError>> go() {
                        return observableService.getVisitContext(appointment);
                    }
                },
                new SampleResponseAction2<VisitContext, SDKError, SDKResponse<VisitContext, SDKError>>(GET_VISIT_CONTEXT) {
                    @Override
                    public void onSuccess(AppointmentActivity activity, VisitContext visitContext) {
                        stop(GET_VISIT_CONTEXT);
                        setVisitContext(visitContext);
                    }
                },
                new SampleFailureAction2(GET_VISIT_CONTEXT)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(AppointmentActivity view) {
        super.onTakeView(view);

        // set up the view from the appointment
        if (appointment != null) {
            if (appointment.getConsumerProxy() != null) { // if the visit is for a dependent
                view.setGreeting(appointment.getConsumerProxy().getFirstName());
                view.setDependent(appointment.getConsumer().getFullName());
            }
            else {
                view.setGreeting(appointment.getConsumer().getFirstName());
                view.setDependent(null);
            }

            final Provider provider = appointment.getAssignedProvider();
            if (provider != null) {
                view.setProviderImage(provider);
                view.setWith(provider.getFullName());
                view.setSpecialty(provider.getSpecialty().getName());
            }
            else {
                view.setNoProviderImage();
                view.setWith(null);
                view.setSpecialty(null);
            }

            Date start = null;
            if (appointment.getSchedule() != null) { // if we still have a schedule object...
                start = new Date(appointment.getSchedule().getScheduledStartTime()); // .. get the start time
            }
            view.setStart(start);

            view.setNotes(appointment.getNoteToPatient());

            handleDisposition(appointment.getDisposition(), appointment.getCheckInStatus());
        }
    }

    // set the appointment
    public void setAppointment(final Appointment appointment) {
        this.appointment = appointment;
    }

    private void handleDisposition(final Disposition disposition, final CheckInStatus checkInStatus) {
        if (disposition.isCanceled()) {
            view.setCanceled();
        }
        else if (disposition.isResolved() || disposition.isProviderWrapUp()) {
            view.setResolved();
        }
        else if (disposition.isInProgress()) {
            view.setInProgress();
        }
        else if (disposition.isExpired()
                || checkInStatus == CheckInStatus.LATE) {
            view.setLate();
        }
        else if (disposition == Disposition.Scheduled) {
            if (checkInStatus == CheckInStatus.NO_PROVIDER) {
                view.setNoProviders(appointment.getSpecialty());
            }
            else if (checkInStatus == CheckInStatus.EARLY) {
                view.setEarly(awsdk.getConfiguration().getScheduledVisitMarginMs() / (60 * 1000));
            }
            else if (checkInStatus == CheckInStatus.ON_TIME) {
                view.setOnTime();
            }
        }
    }

    public void getVisitContext() {
        start(GET_VISIT_CONTEXT);
    }

    // once we've fetched the visit context, tell the view
    public void setVisitContext(final VisitContext visitContext) {
        this.visitContext = visitContext;
        view.setVisitContext(visitContext);
    }

    // there's a slight crossing of responsibilities here in that we're passing UI componentry into the presenter, but it's a necessary evil
    public void loadProviderImage(final ProviderInfo providerInfo,
                                  final ImageView imageView,
                                  final Drawable placeHolder) {

        // check image exists
        if (providerInfo.hasImage()) {
            awsdk.getPracticeProvidersManager()
                    .newImageLoader(providerInfo, imageView, ProviderImageSize.EXTRA_EXTRA_LARGE)
                    .placeholder(placeHolder)
                    .build()
                    .load();
        }
        else {
            imageView.setImageDrawable(placeHolder);
        }
    }

}

