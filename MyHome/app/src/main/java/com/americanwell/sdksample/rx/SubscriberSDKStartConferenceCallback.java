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
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.manager.StartConferenceCallback;

import rx.Subscriber;

/**
 * Extension of base callback to provide support for the StartConferenceCallback
 */
public class SubscriberSDKStartConferenceCallback
        extends SubscriberSDKCallback<SDKStartConferenceResponse, Void, SDKError>
        implements StartConferenceCallback {

    private static final String LOG_TAG = SubscriberSDKStartConferenceCallback.class.getName();

    public SubscriberSDKStartConferenceCallback(final Subscriber<SDKStartConferenceResponse> subscriber) {
        super(subscriber);
    }

    @Override
    public void onConferenceStarted(Intent intent) {
        final SDKStartConferenceResponse<Void, SDKError> response = new SDKStartConferenceResponse<>();
        response.setIntent(intent);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onConferenceEnded() {
        final SDKStartConferenceResponse<Void, SDKError> response = new SDKStartConferenceResponse<>();
        response.setConferenceEnded(true);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onConferenceDisabled() {
        final SDKStartConferenceResponse<Void, SDKError> response = new SDKStartConferenceResponse<>();
        response.setConferenceDisabled(true);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onConferenceCanceled() {
        final SDKStartConferenceResponse<Void, SDKError> response = new SDKStartConferenceResponse<>();
        response.setConferenceCanceled(true);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onPollFailure(@NonNull Throwable throwable) {
        // no-op - the sample app does not implement this method
        DefaultLogger.e(LOG_TAG, "onPollFailure", throwable);
    }

    @Override
    public SDKStartConferenceResponse getResponse(Void aVoid, SDKError sdkError) {
        return new SDKStartConferenceResponse<>(aVoid, sdkError);
    }

}
