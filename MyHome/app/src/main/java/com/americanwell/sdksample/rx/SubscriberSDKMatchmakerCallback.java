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
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.MatchmakerCallback;

import rx.Subscriber;

/**
 * Extension of the base callback to provide support for the MatchmakerCallback
 */
public class SubscriberSDKMatchmakerCallback
        extends SubscriberSDKCallback<SDKMatchmakerResponse, Void, SDKError>
        implements MatchmakerCallback {

    public SubscriberSDKMatchmakerCallback(final Subscriber<SDKMatchmakerResponse> subscriber) {
        super(subscriber);
    }

    @Override
    public void onProviderFound(Provider provider, VisitContext visitContext) {
        final SDKMatchmakerResponse<Void, SDKError> response = new SDKMatchmakerResponse<>();
        response.setProvider(provider);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onProviderListExhausted() {
        final SDKMatchmakerResponse<Void, SDKError> response = new SDKMatchmakerResponse<>();
        response.setProviderListExhausted(true);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onRequestGone() {
        final SDKMatchmakerResponse<Void, SDKError> response = new SDKMatchmakerResponse<>();
        response.setRequestGone(true);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public SDKMatchmakerResponse getResponse(Void aVoid, SDKError sdkError) {
        return new SDKMatchmakerResponse<>(aVoid, sdkError);
    }

}
