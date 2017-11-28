/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLaunchParams;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.rx.SDKResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * BasePresenter for use with login and enrollment. This presenter contains methods for logging in
 * a user as well as retrieving an appointment from the server.
 */
public abstract class BaseLoginPresenter<V extends BaseLoginActivity>
        extends BaseSampleNucleusRxPresenter<V> {

    private static final String LOG_TAG = BaseLoginPresenter.class.getName();

    protected static final int INITIALIZE_FOR_LOGIN = 610;
    protected static final int AUTHENTICATE = 611;
    protected static final int CONSUMER = 612;
    private static final int GET_APPOINTMENT = 616;

    // constants
    protected static final String PREF_SAVED_USERNAME = "saved_username";

    // the Inject annotation is provided by dagger2.  see SamplePresenterComponent and SamplePresenterModule
    @Inject
    SharedPreferences sharedPreferences;

    @State
    String email;
    @State
    String password;

    @State
    boolean rememberMe;

    @State
    Authentication authentication;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        // if launching with an appointment id param, fetch the appointment
        restartableLatestCache(GET_APPOINTMENT,
                new SampleRequestFunc0<SDKResponse<Appointment, SDKError>>(GET_APPOINTMENT) {
                    @Override
                    public Observable<SDKResponse<Appointment, SDKError>> go() {
                        // if we've determined that we have an appointment id in our launch params
                        // we fetch that appointment here.
                        // we pass in the launch params to the call, which will be used to extract
                        // the appointment id
                        return observableService.getAppointment(consumer, awsdk.getLaunchParams());
                    }
                },
                new SampleResponseAction2<Appointment, SDKError, SDKResponse<Appointment, SDKError>>(GET_APPOINTMENT) {
                    @Override
                    public void onSuccess(V activity, Appointment appointment) {
                        stop(GET_APPOINTMENT);
                        activity.setAppointment(appointment);
                    }
                },
                new SampleFailureAction2(GET_APPOINTMENT));

        // fetch consumer
        restartableLatestCache(CONSUMER,
                new SampleRequestFunc0<SDKResponse<Consumer, SDKError>>(CONSUMER) {
                    @Override
                    public Observable<SDKResponse<Consumer, SDKError>> go() {
                        // fetch the consumer that's been authenticated
                        // the sdk requires the fetched consumer object for most, if not all,
                        // authenticated operations
                        return observableService.getConsumer(authentication);
                    }
                },
                new SampleResponseAction2<Consumer, SDKError, SDKResponse<Consumer, SDKError>>(CONSUMER) {
                    @Override
                    public void onSuccess(V activity, Consumer c) {
                        setConsumer(c);
                    }
                },
                new SampleFailureAction2(CONSUMER));
    }

    // when we've successfully fetched the consumer, we do a few things...
    // * store it in this instance
    // * store the email address if rememberMe is true
    // * set the consumer in stateUtils for use in all subsequent calls - this is important
    // * check for an appointment and kick off that process if one is found
    protected void setConsumer(final Consumer consumer) {
        this.consumer = consumer;
        if (rememberMe) {
            storeEmail(email);
        }
        else {
            storeEmail(null);
        }
        stateUtils.setLoginConsumer(consumer);
        if (awsdk.getLaunchParams().hasFeature(SDKLaunchParams.SDKLaunchFeature.APPOINTMENT)) {
            start(GET_APPOINTMENT);
        }
        else {
            view.setConsumerRetrieved();
        }
    }

    // if we've decided to "remember me" this will store the email
    // it will also clear it out if "email" is empty or null
    private void storeEmail(final String email) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (TextUtils.isEmpty(email)) {
            editor.remove(PREF_SAVED_USERNAME);
        }
        else {
            editor.putString(PREF_SAVED_USERNAME, email);
        }
        editor.apply();
    }

    // the sdk is stateless, so there's not much to do for "logout".
    // there is the ability to "clear authentication" for a Consumer
    // which means if that consumer is retained but not authenticated, it cannot
    // be used for sdk calls without re-authenticating
    public void logout() {
        if (consumer != null) {
            awsdk.clearAuthentication(consumer);
        }
        if (awsdk.isInitialized()) {
            awsdk.getVisitManager().abandonGuestConference();
        }
    }

    // getters
    public com.americanwell.sdk.entity.State getAuthenticationLegalResidence() {
        return authentication.getConsumerInfo().getLegalResidence();
    }

    public String getAuthenticationEmail() {
        return authentication.getConsumerInfo().getEmail();
    }

    protected void handleAuthenticationSuccess(final Authentication a) {
        // if we've successfully authenticated, we are handed an Authentication instance
        authentication = a;
        // here is where we test to see if the consumer needs to complete the enrollment process
        // if they do not, they will not be able to log in and proceed.
        if (a.needsToCompleteEnrollment()) {
            DefaultLogger.d(LOG_TAG, "needs to complete enrollment");
            com.americanwell.sdk.entity.State completeEnrollmentLocation = getAuthenticationLegalResidence();
            String email = getAuthenticationEmail();
            view.setNeedsToCompleteEnrollment(a, email,
                    completeEnrollmentLocation);
            // make sure these calls to not restart
            stop(INITIALIZE_FOR_LOGIN);
            stop(AUTHENTICATE);
        }
        else if (a.getOutstandingDisclaimer() != null) {
            // if the Authentication includes an outstanding disclaimer, but the
            // Consumer does NOT need to complete enrollment, we need to direct
            // them to a view of the updated disclaimer to accept and then
            // continue the login process
            // this is NOT enforced by the server, and if your workflow does not
            // depend on Disclaimer acceptance, you can skip this step (this is
            // not common).
            // if the Consumer DOES need to complete enrollment, the flow there
            // should show the enrollment disclaimer and provide a mechanism to
            // accept it there.
            DefaultLogger.d(LOG_TAG, "needs to accept outstanding disclaimer");
            view.setNeedsToAcceptOustandingDisclaimer(a);

            stop(INITIALIZE_FOR_LOGIN);
            stop(AUTHENTICATE);
        }
        else {
            DefaultLogger.d(LOG_TAG, "fetching consumer");
            // if we are able to continue, fetch the consumer
            start(CONSUMER);
        }
    }

}
