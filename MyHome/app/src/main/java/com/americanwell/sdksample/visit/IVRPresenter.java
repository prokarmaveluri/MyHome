/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.ConsumerInitiatedIVRStatus;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKIVRResponse;
import com.americanwell.sdksample.rx.SDKResponse;

import icepick.State;
import rx.Observable;

import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_IVR;

/**
 * Presenter for IVRActivity
 */
public class IVRPresenter extends BaseSampleNucleusRxPresenter<IVRActivity> {

    private static final String LOG_TAG = IVRPresenter.class.getName();

    private static final int IVR_MONITOR = 1120;
    private static final int IVR_RETRY = 1121;
    private static final int IVR_CANCEL = 1122;

    @State
    Visit visit;
    @State
    boolean started;

    @State
    ConsumerInitiatedIVRStatus currentStatus = null;

    @Override
    public void onCreate(Bundle savedState) {
        DefaultLogger.d(LOG_CATEGORY_IVR, LOG_TAG, "IVR Activity Started");
        SampleApplication.getPresenterComponent().inject(this);
        super.onCreate(savedState);

        restartableLatestCache(IVR_MONITOR,
                new SampleRequestFunc0<SDKIVRResponse<Void, SDKError>>(IVR_MONITOR) {
                    @Override
                    public Observable<SDKIVRResponse<Void, SDKError>> go() {
                        started = true;
                        return observableService.monitorIVRStatus(visit);
                    }
                },
                new SampleIVRResponseAction2<Void, SDKError, SDKIVRResponse<Void, SDKError>>
                        (IVR_MONITOR) {
                    @Override
                    public void onUpdate(IVRActivity activity,
                                         ConsumerInitiatedIVRStatus status,
                                         int retryCount) {
                        handleStatus(status);
                    }
                },
                new SampleFailureAction2(IVR_MONITOR)
        );

        restartableLatestCache(IVR_RETRY,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(IVR_RETRY) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.retryIVR(visit);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(IVR_RETRY) {
                    @Override
                    public void onSuccess(IVRActivity activity, Void aVoid) {
                        stop(IVR_RETRY);
                    }
                },
                new SampleFailureAction2(IVR_RETRY) {
                    @Override
                    public void onFailure(IVRActivity ivrActivity, Throwable throwable) {
                        super.onFailure(ivrActivity, throwable);
                        stop(IVR_RETRY);
                    }
                }
        );

        restartableLatestCache(IVR_CANCEL,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(IVR_CANCEL) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.cancelIVR(visit);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(IVR_CANCEL) {
                    @Override
                    public void onSuccess(IVRActivity activity, Void aVoid) {
                        stop(IVR_CANCEL);
                        DefaultLogger.d(LOG_CATEGORY_IVR, LOG_TAG, "IVR Cancelled: Success");
                        view.startSummaryActivity();
                    }
                },
                new SampleFailureAction2(IVR_CANCEL) {
                    @Override
                    public void onFailure(IVRActivity ivrActivity, Throwable throwable) {
                        super.onFailure(ivrActivity, throwable);
                        stop(IVR_CANCEL);
                        DefaultLogger.d(LOG_CATEGORY_IVR, LOG_TAG, "IVR Cancelled: Failed");
                        view.startSummaryActivity();
                    }
                }
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(IVRActivity view) {
        super.onTakeView(view);
        if (!started) {
            startIVRMonitor();
        }

        if (visit != null) {
            setIVRNumber();
        }
    }

    public void setVisit(@NonNull Visit visit) {
        this.visit = visit;
    }

    public Visit getVisit() {
        return visit;
    }

    private void startIVRMonitor() {
        start(IVR_MONITOR);
    }

    void retryIVR() {
        start(IVR_RETRY);
    }

    void cancelIVR() {
        start(IVR_CANCEL);
    }

    public String getProviderName() {
        return visit.getAssignedProvider().getFullName();
    }

    private void handleStatus(@NonNull ConsumerInitiatedIVRStatus status) {
        if (status != currentStatus) {
            DefaultLogger.d(LOG_CATEGORY_IVR, LOG_TAG, "New IVR Status = " + status);
            currentStatus = status;

            switch (status) {
                case IVR_REQUESTED:
                case IVR_DIALING_MEMBER:
                case IVR_DIALING_PROVIDER:
                    break;
                case IVR_TIMEOUT:
                case IVR_MEMBER_UNREACHABLE:
                case IVR_PROVIDER_UNREACHABLE:
                    if (shouldPromptRetry()) {
                        view.showPromptForRetry(getIVRNumber());
                    }
                    break;
                case IVR_CONNECTED:
                case IVR_MEMBER_FAILED:
                case IVR_PROVIDER_FAILED:
                default:
                    view.startSummaryActivity();
                    break;
            }
        }
    }

    private boolean shouldPromptRetry() {
        return isUnsubscribed(IVR_RETRY) && isUnsubscribed(IVR_CANCEL);
    }

    //IVR number should use the override number but if that was not provided then it should
    // default to the consumers account number.
    private void setIVRNumber() {
        view.setIvrNumber(getIVRNumber());
    }

    String getIVRNumber() {
        String numberText = visit.getOverridePhoneNumber();
        if (TextUtils.isEmpty(visit.getOverridePhoneNumber())
                && visit.getConsumer() != null
                && !TextUtils.isEmpty(visit.getConsumer().getPhone())) {
            numberText = visit.getConsumer().getPhone();
        }
        return numberText;
    }
}
