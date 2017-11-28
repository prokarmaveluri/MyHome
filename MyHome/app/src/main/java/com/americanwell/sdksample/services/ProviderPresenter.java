/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.services;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for ProviderActivity
 */
public class ProviderPresenter extends BaseSampleNucleusRxPresenter<ProviderActivity> {

    private static final int GET_PROVIDER = 1000;
    private static final int GET_VISIT_CONTEXT = 1001;
    private static final int GET_PROVIDER_AVAILABILITY = 1003;

    @State
    ProviderInfo providerInfo;
    @State
    Provider provider;
    @State
    VisitContext visitContext;
    @State
    ArrayList<Date> availableAppointments;
    @State
    Date appointmentDate;
    @State
    Date scheduleAppointmentOnThisDate;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // get the provider
        restartableLatestCache(GET_PROVIDER,
                new SampleRequestFunc0<SDKResponse<Provider, SDKError>>(GET_PROVIDER) {
                    @Override
                    public Observable<SDKResponse<Provider, SDKError>> go() {
                        return observableService.getProvider(consumer, providerInfo);
                    }
                },
                new SampleResponseAction2<Provider, SDKError, SDKResponse<Provider, SDKError>>(GET_PROVIDER) {
                    @Override
                    public void onSuccess(ProviderActivity activity, Provider provider) {
                        setProvider(provider);
                    }
                },
                new SampleFailureAction2(GET_PROVIDER)
        );

        restartableLatestCache(GET_VISIT_CONTEXT,
                new SampleRequestFunc0<SDKResponse<VisitContext, SDKError>>(GET_VISIT_CONTEXT) {
                    @Override
                    public Observable<SDKResponse<VisitContext, SDKError>> go() {
                        return observableService.getVisitContext(consumer, provider);
                    }
                },
                new SampleResponseAction2<VisitContext, SDKError, SDKResponse<VisitContext, SDKError>>(GET_VISIT_CONTEXT) {
                    @Override
                    public void onSuccess(ProviderActivity providerActivity, VisitContext visitContext) {
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
    public void onTakeView(ProviderActivity view) {
        super.onTakeView(view);
        if (provider == null) {
            start(GET_PROVIDER);
        }
        else {
            setProvider(provider);
        }
        view.setAvailableAppointments(availableAppointments);
        view.setAppointmentDate(appointmentDate);
    }

    public void setProviderInfo(final ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }

    public void setProvider(final Provider provider) {
        this.provider = provider;
        view.setProvider(provider);
    }

    public Provider getProvider() {
        return provider;
    }

    public void getVisitContext() {
        start(GET_VISIT_CONTEXT);
    }

    // once we've fetched the visit context, tell the view
    public void setVisitContext(final VisitContext visitContext) {
        this.visitContext = visitContext;
        view.setVisitContext(visitContext);
    }

    public void setAvailableAppointments(final List<Date> availableAppointments) {
        this.availableAppointments = (ArrayList<Date>) availableAppointments;
    }

    public List<Date> getAvailableAppointments() {
        return availableAppointments;
    }

    public void setAppointmentDate(final Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

}
