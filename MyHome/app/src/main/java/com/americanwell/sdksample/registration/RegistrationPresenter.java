/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.registration;

import android.os.Bundle;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.entity.enrollment.ConsumerEnrollment;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.text.ParseException;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for RegistrationActivity
 */
public class RegistrationPresenter extends BaseSampleNucleusRxPresenter<RegistrationActivity> {

    private static final int ENROLL_CONSUMER = 900;
    private static final int GET_DISCLAIMER = 901;

    @Inject
    ConsumerManager consumerManager;

    @State
    ConsumerEnrollment consumerEnrollment;
    @State
    String firstName;
    @State
    String middleInitial;
    @State
    String lastName;
    @State
    String email;
    @State
    String confirmEmail;
    @State
    String password;
    @State
    com.americanwell.sdk.entity.State legalResidence;
    @State
    String dobString;
    @State
    Gender gender = Gender.MALE; // initialize
    @State
    boolean acceptedDisclaimer;
    @State
    String address1;
    @State
    String address2;
    @State
    String city;
    @State
    String zipCode;
    @State
    com.americanwell.sdk.entity.State state;
    @State
    Country country;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(ENROLL_CONSUMER,
                new SampleRequestFunc0<SDKValidatedResponse<Consumer, SDKPasswordError>>(ENROLL_CONSUMER) {
                    @Override
                    public Observable<SDKValidatedResponse<Consumer, SDKPasswordError>> go() {
                        return observableService.enrollConsumer(consumerEnrollment);
                    }
                },
                new SampleValidatedPasswordResponseAction2<Consumer, SDKPasswordError, SDKValidatedResponse<Consumer, SDKPasswordError>>(ENROLL_CONSUMER) {
                    @Override
                    public void onSuccess(RegistrationActivity registrationActivity, Consumer consumer) {
                        stateUtils.setLoginConsumer(consumer);
                        registrationActivity.setConsumerRegistered();
                    }
                },
                new SampleFailureAction2(ENROLL_CONSUMER));


        restartableLatestCache(GET_DISCLAIMER,
                new SampleRequestFunc0<SDKResponse<String, SDKError>>(GET_DISCLAIMER) {
                    @Override
                    public Observable<SDKResponse<String, SDKError>> go() {
                        return observableService.getEnrollmentDisclaimer();
                    }
                },
                new SampleResponseAction2<String, SDKError, SDKResponse<String, SDKError>>(GET_DISCLAIMER) {
                    @Override
                    public void onSuccess(RegistrationActivity registrationActivity, String s) {
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
    public void onTakeView(RegistrationActivity view) {
        super.onTakeView(view);
        view.setFirstName(firstName);
        view.setCollectMiddleInitial(awsdk.getConfiguration().isConsumerMiddleInitialCollected());
        view.setMiddleInitial(middleInitial);
        view.setLastName(lastName);
        view.setEmail(email);
        view.setPassword(password);
        view.setState(legalResidence);
        view.setDob(dobString);
        view.setGender(gender);
        view.setAcceptedDisclaimer(acceptedDisclaimer);
        view.setCollectAddress(awsdk.getConfiguration().isConsumerAddressRequired());
        view.showMultiCountrySpinner(isMultiCountry());
    }

    protected boolean shouldHaveConsumer() {
        return false;
    }

    public void register() {

        if (!email.equals(confirmEmail)) {
            view.setHasConfirmEmailError(true);
            return;
        }
        else {
            view.setHasConfirmEmailError(false);
        }

        consumerEnrollment = consumerManager.getNewConsumerEnrollment();
        consumerEnrollment.setFirstName(firstName);
        consumerEnrollment.setMiddleInitial(middleInitial);
        consumerEnrollment.setLastName(lastName);
        consumerEnrollment.setEmail(email);
        consumerEnrollment.setPassword(password);
        consumerEnrollment.setLegalResidence(legalResidence);
        try {
            consumerEnrollment.setDob(SDKLocalDate.valueOf(dobString, view.getString(R.string.dateFormat)));
        }
        catch (ParseException e) {
            view.setError(e);
            return;
        }
        consumerEnrollment.setGender(gender);
        consumerEnrollment.setConsumerAuthKey(email);
        consumerEnrollment.setAcceptedDisclaimer(acceptedDisclaimer);
        if (awsdk.getConfiguration().isConsumerAddressRequired()) {
            final Address address = awsdk.getNewAddress();
            address.setAddress1(address1);
            address.setAddress2(address2);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
            address.setCountry(country);
            consumerEnrollment.setAddress(address);
        }
        start(ENROLL_CONSUMER);
    }

    public void getServiceAgreement() {
        start(GET_DISCLAIMER);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLegalResidence(com.americanwell.sdk.entity.State legalResidence) {
        this.legalResidence = legalResidence;
    }

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setAcceptedDisclaimer(boolean acceptedDisclaimer) {
        this.acceptedDisclaimer = acceptedDisclaimer;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setState(com.americanwell.sdk.entity.State state) {
        this.state = state;
    }

    public void setCountry(Country country) {
        if (country != null && !country.equals(this.country)) {
            this.country = country;
            view.populateStateSpinner(getEnrollmentStates(country));
        }
    }
}
