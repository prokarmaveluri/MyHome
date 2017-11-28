/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.login;

import android.os.Bundle;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import rx.Observable;

/**
 * Presenter for OutstandingDisclaimerActivity
 *
 * @since AWSDK 3.0.1
 */
public class OutstandingDisclaimerPresenter extends BaseLoginPresenter<OutstandingDisclaimerActivity> {

    private static final int ACCEPT_OUTSTANDING_DISCLAIMER = 640;

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(ACCEPT_OUTSTANDING_DISCLAIMER,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(ACCEPT_OUTSTANDING_DISCLAIMER) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.acceptOutstandingDisclaimer(authentication);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(ACCEPT_OUTSTANDING_DISCLAIMER) {
                    @Override
                    public void onSuccess(OutstandingDisclaimerActivity outstandingDisclaimerActivity, Void aVoid) {
                        start(CONSUMER);
                    }
                },
                new SampleFailureAction2(ACCEPT_OUTSTANDING_DISCLAIMER)
        );

    }

    @Override
    public void onTakeView(OutstandingDisclaimerActivity view) {
        super.onTakeView(view);
        // using the "spanned" variation which will automatically handle the HTML in the legal text
        if (authentication.getOutstandingDisclaimer() != null) {
            view.setOutstandingDisclaimer(authentication.getOutstandingDisclaimer().getLegalTextSpanned());
        }
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public void acceptOutstandingDisclamer() {
        start(ACCEPT_OUTSTANDING_DISCLAIMER);
    }
}
