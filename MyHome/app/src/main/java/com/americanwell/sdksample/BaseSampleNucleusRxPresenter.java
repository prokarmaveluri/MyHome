/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.ConsumerInitiatedIVRStatus;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.manager.PracticeProvidersManager;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdksample.rx.ObservableService;
import com.americanwell.sdksample.rx.SDKIVRResponse;
import com.americanwell.sdksample.rx.SDKMatchmakerResponse;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKStartConferenceResponse;
import com.americanwell.sdksample.rx.SDKStartVisitResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;
import com.americanwell.sdksample.rx.SDKVisitTransferResponse;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.StateUtils;
import com.livefront.bridge.Bridge;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;

/**
 * Base Nucleus Rx Presenter class for all Presenters
 */
public abstract class BaseSampleNucleusRxPresenter<V extends BaseSampleNucleusActivity> extends RxPresenter<V> {

    @Inject
    protected AWSDK awsdk;
    @Inject
    protected ObservableService observableService;
    @Inject
    protected StateUtils stateUtils;
    @Inject
    protected LocaleUtils localeUtils;

    @icepick.State
    protected Consumer consumer;
    @icepick.State
    protected Consumer loginConsumer;

    protected V view; // make the view generally available.
    // it's not always available to make sure to check for nulls
    // unless you can be certain it will be populated at the point in the
    // lifecycle you expect

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        // restore the awsdk instance state directly into the dagger component so the injection won't fail
        // without this, it can error injecting the managers in certain circumstances
        SampleApplication.getPresenterComponent().awsdk().restoreInstanceState(savedState);
        injectPresenter();
        Bridge.restoreInstanceState(this, savedState);
        if (loginConsumer == null) {
            loginConsumer = stateUtils.getLoginConsumer();
        }
        else {
            stateUtils.setLoginConsumer(loginConsumer);
        }
        if (consumer == null) {
            consumer = stateUtils.getConsumer(); // all presenters have access to the current and logged in consumer
        }
        else {
            stateUtils.setConsumer(consumer);
        }
    }

    // each presenter must override this - even though the override code is always exactly the same
    // we need the specific "this" instance
    protected abstract void injectPresenter();

    @Override
    public void onSave(@NonNull Bundle state) {
        super.onSave(state);
        awsdk.saveInstanceState(state); // the AWSDK instance can have it's state restored here
        // without this you may get "sdk initialization is missing" errors
        // if the app is destroyed and recreated
        Bridge.saveInstanceState(this, state);
    }

    @Override
    public void onTakeView(V view) {
        this.view = view; // here's where we grab the view (activity) and make it available.
        super.onTakeView(view);
    }

    protected boolean shouldBeInitialized() {
        return true;
    }

    protected boolean shouldHaveConsumer() {
        return true;
    }

    // Returns a list of "valid" (legal residence) states for a given country
    public List<State> getEnrollmentStates(@NonNull final Country country) {
        return awsdk.getConsumerManager().getEnrollmentStates(country);
    }

    // Return a list of states for a given country
    public List<State> getStates(@NonNull final Country country) {
        return awsdk.getConsumerManager().getStates(country);
    }

    // return a List of system supported countries
    public List<Country> getSupportedCountries() {
        return awsdk.getSupportedCountries();
    }

    // return if system supports multi-country
    public boolean isMultiCountry() {
        return awsdk.getConfiguration().isMultiCountry();
    }

    // return a List of valid payment states
    public List<State> getValidPaymentMethodStates(final Country country) {
        return awsdk.getConsumerManager().getValidPaymentMethodStates(country);
    }

    // try not to use this, it's a special case for getting access to the manager in ProviderView
    public PracticeProvidersManager getPracticeProvidersManager() {
        return awsdk.getPracticeProvidersManager();
    }

    // get the preferred locale
    public Locale getPreferredLocale() {
        return awsdk.getPreferredLocale();
    }

    // formats a time stamp using preferred Locale
    public String formatTimeStamp(long timeStamp) {
        return localeUtils.formatTimeStamp(timeStamp);
    }

    // below are helper classes for the Nucleus Rx request calls
    // these are part of the RxJava / Nucleus implementation being used to handle the asynchronous network
    // requests.
    // the wrappers found in the "rx" package will place the various responses inside SDKResponse classes (or derivatives thereof).
    // These methods below provide common handling for the responses and errors

    // This extends Func0 and is used to provide common handling for all request submissions
    // all it really does is provide base implementation to always turn on the "busy" view when a
    // request starts
    public abstract class SampleRequestFunc0<R extends SDKResponse> implements Func0<Observable<R>> {
        protected Integer requestId;

        public SampleRequestFunc0(int requestId) {
            this.requestId = requestId;
        }

        @Override
        public Observable<R> call() {
            DefaultLogger.d(this.getClass().getName(), "calling request. requestId = " + requestId);
            if (view != null) { // if the request is submitted before onTakeView() - view will be null - and spinner won't appear
                view.setSomethingIsBusy(true);  // launch the progress dialog or spinner, etc
            }
            return go();
        }

        public abstract Observable<R> go();
    }

    // this is a sample Action2 for successful responses in the simple "SDKResponse" case
    // turns the "busy" view off, handles a "graceful" server-provided error, if exists, otherwise
    // calls the custom "onSuccess" handler in the presenter
    public abstract class SampleResponseAction2<C, E extends SDKError, R extends SDKResponse<C, E>>
            implements Action2<V, R> {
        protected Integer requestId;

        public SampleResponseAction2(int requestId) {
            this.requestId = requestId;
        }

        @Override
        public void call(V v, R r) {
            v.setSomethingIsBusy(false);
            if (r.getError() != null) {
                DefaultLogger.d(this.getClass().getName(), "handling error response. requestId = " + requestId);
                onError(v, r.getError());
            }
            else {
                DefaultLogger.d(this.getClass().getName(), "handling success response. requestId = " + requestId);
                onSuccess(v, r.getResult());
            }
        }

        public abstract void onSuccess(V v, C c);

        public void onError(V v, E e) {
            if (requestId != null) {
                stop(requestId); // stop the request on error so we don't restart it.
                // this may not necessarily be the behavior you want in a production app
                // if you want to retry things, you may not want to stop it in this may
                // for the context of this sample application, however, it's fine.
            }
            v.setError(e);
        }
    }

    // similar to above, but adds support for the SDKPasswordError
    public abstract class SamplePasswordResponseAction2<
            C,
            E extends SDKPasswordError,
            R extends SDKResponse<C, E>>
            extends SampleResponseAction2<C, E, R> {
        public SamplePasswordResponseAction2(int requestId) {
            super(requestId);
        }

        public void onError(V v, E e) {
            if (requestId != null) {
                stop(requestId); // stop the request on error so we don't restart it.
                // this may not necessarily be the behavior you want in a production app
                // if you want to retry things, you may not want to stop it in this may
                // for the context of this sample application, however, it's fine.
            }
            v.setPasswordError(e);
        }
    }

    // similar to above, but adds support for the SDKValidatedResponse
    public abstract class SampleValidatedResponseAction2<
            C,
            E extends SDKError,
            R extends SDKValidatedResponse<C, E>>
            extends SampleResponseAction2<C, E, R> {
        public SampleValidatedResponseAction2(int requestId) {
            super(requestId);
        }

        @Override
        public void call(V v, R r) {
            if (r.getValidationReasons() != null) {
                DefaultLogger.d(this.getClass().getName(), "handling validation. requestId = " + requestId);
                onValidationFailures(v, r.getValidationReasons());
            }
            else {
                super.call(v, r);
            }
        }

        public void onValidationFailures(V v, Map<String, ValidationReason> reasons) {
            if (requestId != null) {
                stop(requestId); // stop the request on error so we don't restart it.
                // this may not necessarily be the behavior you want in a production app
                // if you want to retry things, you may not want to stop it in this may
                // for the context of this sample application, however, it's fine.
            }
            v.setSomethingIsBusy(false); // turn off spinner
            v.setValidationReasons(reasons); // pass the validation reasons to the UI
        }
    }

    // similar to above but adds support for the SDKPasswordError
    public abstract class SampleValidatedPasswordResponseAction2<
            C,
            E extends SDKPasswordError,
            R extends SDKValidatedResponse<C, E>>
            extends SampleValidatedResponseAction2<C, E, R> {
        public SampleValidatedPasswordResponseAction2(int requestId) {
            super(requestId);
        }

        public void onError(V v, E e) {
            if (requestId != null) {
                stop(requestId); // stop the request on error so we don't restart it.
                // this may not necessarily be the behavior you want in a production app
                // if you want to retry things, you may not want to stop it in this may
                // for the context of this sample application, however, it's fine.
            }
            v.setPasswordError(e);
        }
    }

    // similar to above but adds support for the SDKStartVisitResponse
    public abstract class SampleStartVisitResponseAction2<
            C,
            E extends SDKError,
            R extends SDKStartVisitResponse<C, E>>
            extends SampleValidatedResponseAction2<C, E, R> {
        public SampleStartVisitResponseAction2(int requestId) {
            super(requestId);
        }

        @Override
        public void call(V v, R r) {
            if (r.getIntent() != null) {
                DefaultLogger.d(this.getClass().getName(), "handling intent. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onProviderEntered(v, r.getIntent());
            }
            else if (r.getVisitEndReason() != null) {
                DefaultLogger.d(this.getClass().getName(), "handling visit end response. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onWaitingRoomEnded(v, r.getVisitEndReason());
            }
            else if (r.getPatientsAheadOfYou() > -1) {
                DefaultLogger.d(this.getClass().getName(), "handling patients ahead count. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onPatientsAheadOfYouCountChanged(v, r.getPatientsAheadOfYou());
            }
            else if (r.isSuggestedTransfer()) { // since AWSDK 2.1
                DefaultLogger.d(this.getClass().getName(), "handling suggested transfer. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onSuggestedTransfer(v);
            }
            else if (r.getChatReport() != null) { // since AWSDK 2.1
                DefaultLogger.d(this.getClass().getName(), "handling chat report. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onChat(v, r.getChatReport());
            }
            else {
                super.call(v, r);
            }
        }

        @Override
        public void onSuccess(V v, C c) {
        }

        public abstract void onProviderEntered(V v, Intent intent);

        public abstract void onWaitingRoomEnded(V v, VisitEndReason visitEndReason);

        public abstract void onPatientsAheadOfYouCountChanged(V v, int count);

        public abstract void onSuggestedTransfer(V v); // since AWSDK 2.1

        public abstract void onChat(V v, ChatReport chatReport); // since AWSDK 2.1
    }

    // similar to above but adds support for the SDKMatchmakerResponse
    public abstract class SampleMatchmakerResponseAction2<
            C,
            E extends SDKError,
            R extends SDKMatchmakerResponse<C, E>>
            extends SampleResponseAction2<C, E, R> {
        public SampleMatchmakerResponseAction2(int requestId) {
            super(requestId);
        }

        @Override
        public void call(V v, R r) {
            if (r.getProvider() != null) {
                DefaultLogger.d(this.getClass().getName(), "handling provider accepted response. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onProviderAccepted(v, r.getProvider());
            }
            else if (r.isProviderListExhausted()) {
                DefaultLogger.d(this.getClass().getName(), "handling provider exhausted response. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onProviderListExhausted(v);
            }
            else if (r.isRequestGone()) {
                DefaultLogger.d(this.getClass().getName(), "handling request gone response. requestId = " + requestId);
                v.setSomethingIsBusy(false);
                onRequestGone(v);
            }
            else {
                super.call(v, r);
            }
        }

        @Override
        public void onSuccess(V v, C c) {
        }

        public abstract void onProviderAccepted(V v, Provider provider);

        public abstract void onProviderListExhausted(V v);

        public abstract void onRequestGone(V v);
    }

    public abstract class SampleStartConferenceResponseAction2<
            C,
            E extends SDKError,
            R extends SDKStartConferenceResponse<C, E>>
            extends SampleResponseAction2<C, E, R> {
        public SampleStartConferenceResponseAction2(int requestId) {
            super(requestId);
        }

        @Override
        public void call(V v, R r) {
            view.setSomethingIsBusy(false);
            if (r.getIntent() != null) {
                onConferenceReady(v, r.getIntent());
            }
            else if (r.isConferenceCanceled()) {
                onConferenceCanceled(v);
            }
            else if (r.isConferenceDisabled()) {
                onConferenceDisabled(v);
            }
            else if (r.isConferenceEnded()) {
                onConferenceEnded(v);
            }
            else {
                super.call(v, r);
            }
        }

        @Override
        public void onSuccess(V v, C c) {
        }

        public abstract void onConferenceReady(V v, Intent intent);

        public abstract void onConferenceCanceled(V v);

        public abstract void onConferenceDisabled(V v);

        public abstract void onConferenceEnded(V v);
    }


    // this provides common failure handling
    public class SampleFailureAction2 implements Action2<V, Throwable> {
        protected Integer requestId;

        public SampleFailureAction2(int requestId) {
            this.requestId = requestId;
        }

        @Override
        public void call(V v, Throwable throwable) {
            DefaultLogger.d(this.getClass().getName(), "handling failure response. requestId = " + requestId);
            v.setSomethingIsBusy(false);
            onFailure(v, throwable);
        }

        public void onFailure(final V v, final Throwable throwable) { // override this if you want to do something different
            v.onFailure(throwable); // tell the activity about the failure, in the case of the sample app, we have common failure handling
        }
    }

    // similar to above, but adds support for the SDKVisitTransferResponse
    public abstract class SampleVisitTransferResponseAction2<
            C,
            E extends SDKError,
            R extends SDKVisitTransferResponse<C, E>>
            extends SampleResponseAction2<C, E, R> {
        public SampleVisitTransferResponseAction2(int requestId) {
            super(requestId);
        }

        @Override
        public void call(V v, R r) {
            if (r.getVisitRedirect() != null) {
                onVisitTransfer(r.getVisitRedirect());
            }
            else if (r.getVisitContext() != null) {
                onNewVisitContext(r.getVisitContext());
            }
            else if (r.isProviderUnavailable() == true) {
                onProviderUnavailable();
            }
            else if (r.getNewVisit() != null) {
                onStartNewVisit(r.getNewVisit());
            }
            else {
                super.call(v, r);
            }
        }

        @Override
        public void onSuccess(V v, C c) {
            // no-op default behavior
        }

        public abstract void onVisitTransfer(Visit visit);

        public abstract void onProviderUnavailable();

        public abstract void onStartNewVisit(Visit visit);

        public abstract void onNewVisitContext(VisitContext visitContext);
    }

    // similar to above but adds support for the SDKStartVisitResponse
    public abstract class SampleIVRResponseAction2<
            C,
            E extends SDKError,
            R extends SDKIVRResponse<C, E>>
            extends SampleValidatedResponseAction2<C, E, R> {
        public SampleIVRResponseAction2(int requestId) {
            super(requestId);
        }

        @Override
        public void call(V v, R r) {
            ConsumerInitiatedIVRStatus status = r.getStatus();
            int retryCount = r.getRetryCount();
            onUpdate(v, status, retryCount);
        }

        @Override
        public void onSuccess(V v, C c) {
        }

        public abstract void onUpdate(V v, ConsumerInitiatedIVRStatus status, int retryCount);
    }
}
