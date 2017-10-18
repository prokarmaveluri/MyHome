/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.rx;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.manager.SDKCallback;

import rx.Subscriber;

/**
 * This is the base "wrapper" callback to provide a bridge between the SDK standard
 * callbacks and an Rx Subscriber.
 */
public class SubscriberSDKCallback<R extends SDKResponse, T, E extends SDKError> implements SDKCallback<T, E> {

    protected Subscriber<R> subscriber;

    public SubscriberSDKCallback(final Subscriber<R> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onResponse(T t, E sdkError) {
        // we take the results of the response... good and bad and wrap them in
        // a response object
        subscriber.onNext(getResponse(t, sdkError));
        subscriber.onCompleted();
    }

    @Override
    public void onFailure(Throwable throwable) {
        // here we just pass along the throwable
        subscriber.onError(throwable);
    }

    @SuppressWarnings("unchecked")
    public R getResponse(T t, E sdkError) {
        return (R) new SDKResponse<>(t, sdkError);
    }
}