/**
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
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdk.manager.VisitTransferCallback;

import java.util.Map;

import rx.Subscriber;

/**
 * Extension of the base callback to provide support for the {@link VisitTransferCallback}
 */
public class SubscriberSDKVisitTransferCallback
        extends SubscriberSDKCallback<SDKVisitTransferResponse, Void, SDKError>
        implements VisitTransferCallback {

    public SubscriberSDKVisitTransferCallback(final Subscriber<SDKVisitTransferResponse> subscriber) {
        super(subscriber);
    }

    @Override
    public void onVisitTransfer(@NonNull Visit visit) {
        final SDKVisitTransferResponse<VisitContext, SDKError> response
                = new SDKVisitTransferResponse<>();
        response.setVisitRedirect(visit);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onProviderUnavailable() {
        final SDKVisitTransferResponse<VisitContext, SDKError> response
                = new SDKVisitTransferResponse<>();
        response.setProviderUnavailable(true);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onStartNewVisit(@NonNull Visit visit) {
        final SDKVisitTransferResponse<VisitContext, SDKError> response
                = new SDKVisitTransferResponse<>();
        response.setNewVisit(visit);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onNewVisitContext(@NonNull VisitContext visitContext) {
        final SDKVisitTransferResponse<VisitContext, SDKError> response
                = new SDKVisitTransferResponse<>();
        response.setVisitContext(visitContext);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void onValidationFailure(Map<String, ValidationReason> validationReasons) {
        final SDKVisitTransferResponse<Void, SDKError> response
                = new SDKVisitTransferResponse<>(null, null, validationReasons);
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public SDKVisitTransferResponse<Void, SDKError> getResponse(Void aVoid, SDKError sdkError) {
        return new SDKVisitTransferResponse<>(aVoid, sdkError, null);
    }
}
