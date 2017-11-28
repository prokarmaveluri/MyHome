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
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.ValidationReason;

import java.util.Map;

import rx.Subscriber;

/**
 * Extension of base callback to add support for validation
 */
public class SubscriberSDKValidatedCallback<T, E extends SDKError>
        extends SubscriberSDKCallback<SDKValidatedResponse, T, E>
        implements SDKValidatedCallback<T, E> {

    public SubscriberSDKValidatedCallback(final Subscriber<SDKValidatedResponse> subscriber) {
        super(subscriber);
    }

    @Override
    public SDKValidatedResponse getResponse(T t, E e) {
        return new SDKValidatedResponse<>(t, e, null);
    }

    @Override
    public void onValidationFailure(Map<String, ValidationReason> validationReasons) {
        subscriber.onNext(new SDKValidatedResponse<T, E>(null, null, validationReasons));
        subscriber.onCompleted();
    }

}