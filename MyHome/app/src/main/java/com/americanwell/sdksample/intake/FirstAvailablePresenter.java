/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.intake;

import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKMatchmakerResponse;
import com.americanwell.sdksample.rx.SDKResponse;

import icepick.State;
import rx.Observable;

/**
 * Presenter for FirstAvailableActivity this demonstrates the use of the MatchmakerCallback,
 * which is different from the others in that it can represent a long-running asynchronous
 * process. The custom progress spinner on the activity is used to differentiate this.  the
 * inline spinner versus the popup shows that this may take a while.
 */
public class FirstAvailablePresenter extends BasePrivacyPolicyPresenter<FirstAvailableActivity> {

    private static final int START_MATCHMAKING = 510;
    private static final int CANCEL_MATCHMAKING = 511;

    @State
    Provider provider;
    @State
    boolean providerListExhausted = false;
    @State
    boolean requestGone = false;
    @State
    boolean canceled = false;
    @State
    boolean started;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(START_MATCHMAKING,
                new SampleRequestFunc0<SDKMatchmakerResponse<Void, SDKError>>(START_MATCHMAKING) {
                    @Override
                    public Observable<SDKMatchmakerResponse<Void, SDKError>> go() {
                        started = true;
                        return observableService.startMatchmaking(visitContext);
                    }
                },
                new SampleMatchmakerResponseAction2<Void, SDKError, SDKMatchmakerResponse<Void, SDKError>>(START_MATCHMAKING) {
                    @Override
                    public void onProviderAccepted(FirstAvailableActivity firstAvailableActivity, Provider provider) {
                        setProvider(provider);
                    }

                    @Override
                    public void onProviderListExhausted(FirstAvailableActivity firstAvailableActivity) {
                        setProviderListExhausted(true);
                    }

                    @Override
                    public void onRequestGone(FirstAvailableActivity firstAvailableActivity) {
                        setRequestGone(true);
                    }
                },
                new SampleFailureAction2(START_MATCHMAKING)
        );

        restartableLatestCache(CANCEL_MATCHMAKING,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(CANCEL_MATCHMAKING) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.cancelMatchmaking(visitContext);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(CANCEL_MATCHMAKING) {
                    @Override
                    public void onSuccess(FirstAvailableActivity firstAvailableActivity, Void aVoid) {
                        stop(START_MATCHMAKING);
                        setMatchmakingCanceled(true);
                    }
                },
                new SampleFailureAction2(CANCEL_MATCHMAKING)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(FirstAvailableActivity view) {
        super.onTakeView(view);
        showProvider();
        view.setRequestGone(requestGone);
        view.setProviderListExhausted(providerListExhausted);
        view.setMatchmakingCanceled(canceled);
        if (!started) {
            start(START_MATCHMAKING);
        }
    }

    public void cancelMatchmaking() {
        start(CANCEL_MATCHMAKING);
    }

    private void setProvider(final Provider provider) {
        this.provider = provider;
        if (provider.getLegalText() != null) {
            // if provider has legal text, use that for privacy policy
            setPrivacyPolicy(provider.getLegalText().getLegalText());
        }
        else {
            for (LegalText legalText : visitContext.getLegalTexts()) {
                // otherwise use the default legal text
                // typically there's only one required legal text, which is the privacy policy
                if (legalText.isRequired()) {
                    setPrivacyPolicy(legalText.getLegalText());
                }
            }
        }
        showProvider();
    }

    public void setProviderListExhausted(boolean providerListExhausted) {
        this.providerListExhausted = providerListExhausted;
        view.setProviderListExhausted(providerListExhausted);
    }

    public void setRequestGone(boolean requestGone) {
        this.requestGone = requestGone;
        view.setRequestGone(requestGone);
    }

    public void setMatchmakingCanceled(boolean canceled) {
        this.canceled = canceled;
        view.setMatchmakingCanceled(canceled);
    }

    private void showProvider() {
        if (provider != null && !TextUtils.isEmpty(getPrivacyPolicy())) {
            view.setProvider(provider);
            view.showPrivacySection(true);
        }
    }

    @Override
    protected void setPrivacyPolicyError() {
        view.setPrivacyPolicyError();
    }

    public void validate() {
        if (validateVisitContext()) {
            view.setFieldsValid();
        }
    }

    @Override
    protected boolean shouldShowPrivacyPolicyError() {
        return true;
    }
}
