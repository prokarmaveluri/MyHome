/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.rx;

import android.support.annotation.NonNull;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.ConsumerInitiatedIVRStatus;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.manager.IVRCallback;
import com.americanwell.sdk.manager.ValidationReason;

import java.util.Map;

import rx.Subscriber;

/**
 * Extension of the base callback to provide support for the {@link IVRCallback}
 */
class SubscriberSDKIVRCallback extends SubscriberSDKCallback<SDKIVRResponse, Void, SDKError>
        implements IVRCallback {

    private static final String LOG_TAG = SubscriberSDKIVRCallback.class.getName();

    SubscriberSDKIVRCallback(Subscriber<SDKIVRResponse> subscriber) {
        super(subscriber);
    }

    @Override
    public void onUpdate(ConsumerInitiatedIVRStatus status, int retryCount) {
        final SDKIVRResponse<Void, SDKError> response = new SDKIVRResponse<>();
        response.setStatus(status);
        response.setRetryCount(retryCount);
        subscriber.onNext(response);
    }

    @Override
    public void onPollFailure(@NonNull Throwable throwable) {
        // no-op - the sample app does not implement this method
        DefaultLogger.e(LOG_TAG, "onPollFailure", throwable);
    }

    @Override
    public void onValidationFailure(Map<String, ValidationReason> validationReasons) {
        final SDKIVRResponse<Void, SDKError> response = new SDKIVRResponse<>(null, null,
                validationReasons);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }
}
