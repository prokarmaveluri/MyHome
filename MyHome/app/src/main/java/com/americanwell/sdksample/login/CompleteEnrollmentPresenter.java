/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.internal.util.ValidationUtil;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.registration.RegistrationActivity;
import com.americanwell.sdksample.rx.SDKResponse;

import icepick.State;
import rx.Observable;

/**
 * Presenter for CompleteEnrollmentActivity
 */
public class CompleteEnrollmentPresenter extends BaseLoginPresenter<CompleteEnrollmentActivity> {

    private static final int COMPLETE_ENROLLMENT = 613;
    private static final int GET_DISCLAIMER = 619;

    @State
    com.americanwell.sdk.entity.State state; // had to use fully qualified class name to avoid conflict with icepick.State
    @State
    Authentication authentication;

    @State
    String email;
    @State
    String confirmEmail;
    @State
    String password;
    @State
    com.americanwell.sdk.entity.State completeEnrollmentState;
    @State
    Country completeEnrollmentCountry;
    @State
    boolean completeEnrollmentAcceptDisclaimer;

    private ValidationUtil validationUtil = new ValidationUtil();


    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        // complete enrollment
        restartableLatestCache(COMPLETE_ENROLLMENT,
                new SampleRequestFunc0<SDKResponse<Consumer, SDKPasswordError>>(COMPLETE_ENROLLMENT) {
                    @Override
                    public Observable<SDKResponse<Consumer, SDKPasswordError>> go() {
                        // if the authenticated consumer was not fully enrolled, we'll collect the missing
                        // information and present it here to update
                        return observableService.completeEnrollment(
                                authentication,
                                completeEnrollmentState,
                                email,
                                password);
                    }
                },
                new SamplePasswordResponseAction2<Consumer, SDKPasswordError, SDKResponse<Consumer, SDKPasswordError>>(COMPLETE_ENROLLMENT) {
                    @Override
                    public void onSuccess(CompleteEnrollmentActivity activity, Consumer c) {
                        // the successful completion of enrollment will provide us with the consumer object
                        setConsumer(c);
                    }
                },
                new SampleFailureAction2(COMPLETE_ENROLLMENT));


        restartableLatestCache(GET_DISCLAIMER,
                new SampleRequestFunc0<SDKResponse<String, SDKError>>(GET_DISCLAIMER) {
                    @Override
                    public Observable<SDKResponse<String, SDKError>> go() {
                        return observableService.getEnrollmentDisclaimer();
                    }
                },
                new SampleResponseAction2<String, SDKError, SDKResponse<String, SDKError>>(GET_DISCLAIMER) {
                    @Override
                    public void onSuccess(CompleteEnrollmentActivity activity, String s) {
                        view.setServiceAgreement(s);
                        stop(GET_DISCLAIMER);
                    }
                },
                new SampleFailureAction2(GET_DISCLAIMER));
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(CompleteEnrollmentActivity view) {
        super.onTakeView(view);

        // show multi-country before setting state
        view.showMultiCountrySpinner(isMultiCountry());
        view.setState(completeEnrollmentState);
    }

    // initiate the complete enrollment process
    public void completeEnrollment() {
        if (validateCompleteEnrollment(completeEnrollmentAcceptDisclaimer)) { // validate
            start(COMPLETE_ENROLLMENT);
        }
    }

    // they must accept the terms before they can proceed
    private boolean validateCompleteEnrollment(final boolean acceptServiceAgreement) {
        view.setCompleteEnrollmentServiceAgreementAccepted(acceptServiceAgreement);
        return acceptServiceAgreement;
    }

    public void setCompleteEnrollmentState(com.americanwell.sdk.entity.State completeEnrollmentState) {
        this.completeEnrollmentState = completeEnrollmentState;
        toggleFab();
    }

    public void setCompleteEnrollmentCountry(Country completeEnrollmentCountry) {
        if (completeEnrollmentCountry != null &&
                !completeEnrollmentCountry.equals(this.completeEnrollmentCountry)) {
            this.completeEnrollmentCountry = completeEnrollmentCountry;
            view.populateStateSpinner(getEnrollmentStates(completeEnrollmentCountry));
        }
    }

    public void setCompleteEnrollmentAcceptDisclaimer(boolean completeEnrollmentAcceptDisclaimer) {
        this.completeEnrollmentAcceptDisclaimer = completeEnrollmentAcceptDisclaimer;
        toggleFab();
    }

    private void toggleFab() {
        if (view != null) {
            boolean showFab = isFormComplete();
            view.setFabVisibility(showFab);
        }
    }

    private boolean isFormComplete() {
        return completeEnrollmentState != null
                && completeEnrollmentAcceptDisclaimer
                && !TextUtils.isEmpty(password)
                && isEmailSet();
    }

    private boolean isEmailSet() {

        boolean isEmailConfirmed = false;
        boolean isEmailValid = false;
        if (!TextUtils.isEmpty(email)) {
            // check emails match
            if (!email.equals(confirmEmail)) {
                view.setHasConfirmEmailError(true);
            }
            else {
                view.setHasConfirmEmailError(false);
                isEmailConfirmed = true;
            }
            // check email valid format
            if (!validationUtil.isEmailValid(email)) {
                view.setHasEmailFormatError(true);
            }
            else {
                view.setHasEmailFormatError(false);
                isEmailValid = true;
            }
        }

        return isEmailConfirmed && isEmailValid;
    }

    public void setEmail(String email) {
        this.email = email;
        toggleFab();
    }

    void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
        toggleFab();

    }

    public void setPassword(String password) {
        this.password = password;
        toggleFab();
    }

    public com.americanwell.sdk.entity.State getCompleteEnrollmentState() {
        return completeEnrollmentState;
    }

    public void getServiceAgreement() {
        start(GET_DISCLAIMER);
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
