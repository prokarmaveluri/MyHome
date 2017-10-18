/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.rx;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.manager.StartVisitCallback;
import com.americanwell.sdk.manager.ValidationReason;

import java.util.Map;

import rx.Subscriber;

/**
 * Extension of the base callback to provide support for the StartVisitCallback
 */
public class SubscriberSDKStartVisitCallback
        extends SubscriberSDKCallback<SDKStartVisitResponse, Void, SDKError>
        implements StartVisitCallback {

    private static final String LOG_TAG = SubscriberSDKStartVisitCallback.class.getName();

    public SubscriberSDKStartVisitCallback(final Subscriber<SDKStartVisitResponse> subscriber) {
        super(subscriber);
    }

    @Override
    public void onProviderEntered(Intent videoVisitIntent) {
        final SDKStartVisitResponse<Void, SDKError> response = new SDKStartVisitResponse<>();
        response.setIntent(videoVisitIntent);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onStartVisitEnded(VisitEndReason visitEndReason) {
        final SDKStartVisitResponse<Void, SDKError> response = new SDKStartVisitResponse<>();
        response.setVisitEndReason(visitEndReason);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onPatientsAheadOfYouCountChanged(int count) {
        final SDKStartVisitResponse<Void, SDKError> response = new SDKStartVisitResponse<>();
        response.setPatientsAheadOfYou(count);
        subscriber.onNext(response); // this one is repeatable - aka - polling.  so don't call onCompleted()
    }

    @Override
    public void onSuggestedTransfer() {
        final SDKStartVisitResponse<Void, SDKError> response = new SDKStartVisitResponse<>();
        response.setSuggestedTransfer(true);
        subscriber.onNext(response); // this one is repeatable - aka - polling.  so don't call onCompleted()
    }

    @Override
    public void onChat(ChatReport chatReport) {
        final SDKStartVisitResponse<Void, SDKError> response = new SDKStartVisitResponse<>();
        response.setChatReport(chatReport);
        subscriber.onNext(response); // this one is repeatable - aka - polling.  so don't call onCompleted()
    }

    @Override
    public void onPollFailure(@NonNull Throwable throwable) {
        // no-op - the sample app does not implement this method
        DefaultLogger.e(LOG_TAG, "onPollFailure", throwable);
    }

    @Override
    public void onValidationFailure(Map<String, ValidationReason> validationReasons) {
        final SDKStartVisitResponse<Void, SDKError> response = new SDKStartVisitResponse<>(null, null, validationReasons);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public SDKStartVisitResponse getResponse(Void aVoid, SDKError sdkError) {
        return new SDKStartVisitResponse<>(aVoid, sdkError, null);
    }
}
