/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;
import com.americanwell.sdksample.util.SampleUtils;

import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for UpdateConsumerActivity
 *
 * @since AWSDK 1.0
 */
public class UpdateConsumerPresenter extends BaseSampleNucleusRxPresenter<UpdateConsumerActivity> {

    private static final int UPDATE_CONSUMER = 840;

    @Inject
    ConsumerManager consumerManager;
    @State
    ConsumerUpdate consumerUpdate;
    @State
    String firstName;
    @State
    String middleInitial;
    @State
    String lastName;
    @State
    String emailAddress;
    @State
    Boolean emailDirty = false;
    @State
    String confirmEmail;
    @State
    String password;
    @State
    String dobString;
    @State
    String phone;
    @State
    Gender gender;
    @State
    String address1;
    @State
    String address2;
    @State
    String city;
    @State
    com.americanwell.sdk.entity.State state;
    @State
    Country country;
    @State
    String zipcode;
    @State
    com.americanwell.sdk.entity.State stateResidence;
    @State
    Country countryResidence;

    @State
    boolean appointmentReminderTextsEnabled;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(UPDATE_CONSUMER,
                new SampleRequestFunc0<SDKValidatedResponse<Consumer, SDKPasswordError>>(UPDATE_CONSUMER) {
                    @Override
                    public Observable<SDKValidatedResponse<Consumer, SDKPasswordError>> go() {
                        return observableService.updateConsumer(consumerUpdate);
                    }
                },
                new SampleValidatedPasswordResponseAction2<Consumer, SDKPasswordError, SDKValidatedResponse<Consumer, SDKPasswordError>>(UPDATE_CONSUMER) {
                    @Override
                    public void onSuccess(UpdateConsumerActivity updateConsumerActivity, Consumer consumer) {
                        stateUtils.setLoginConsumer(consumer); // update consumer in memory
                        view.setConsumerUpdated(consumer.getFullName());
                    }
                },
                new SampleFailureAction2(UPDATE_CONSUMER)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(UpdateConsumerActivity view) {
        super.onTakeView(view);

        if (TextUtils.isEmpty(firstName)) {
            firstName = consumer.getFirstName();
        }
        view.setFirstName(firstName);

        view.setCollectMiddleInitial(awsdk.getConfiguration().isConsumerMiddleInitialCollected());

        if (TextUtils.isEmpty(middleInitial)) {
            middleInitial = consumer.getMiddleInitial();
        }
        view.setMiddleInitial(middleInitial);

        if (TextUtils.isEmpty(lastName)) {
            lastName = consumer.getLastName();
        }
        view.setLastName(lastName);

        if (TextUtils.isEmpty(emailAddress)) {
            emailAddress = consumer.getEmail();
        }
        view.setEmailAddress(emailAddress);

        if (TextUtils.isEmpty(dobString)) {
            try {
                dobString = consumer.getDob().toString(view.getString(R.string.dateFormat));
            }
            catch (ParseException e) {
                view.setError(e);
                return;
            }
        }
        view.setDob(dobString);

        if (TextUtils.isEmpty(phone)) {
            phone = consumer.getPhone();
        }
        view.setPhone(phone);

        if (gender == null) {
            gender = consumer.getGender();
        }
        view.setGender(gender);

        view.setCollectAddress(awsdk.getConfiguration().isConsumerAddressRequired());
        view.showMultiCountrySpinners(isMultiCountry());

        Address address = consumer.getAddress();

        if (TextUtils.isEmpty(address1) && address != null) {
            address1 = address.getAddress1();
        }
        view.setAddress1(address1);

        if (TextUtils.isEmpty(address2) && address != null) {
            address2 = address.getAddress2();
        }
        view.setAddress2(address2);

        if (TextUtils.isEmpty(city) && address != null) {
            city = address.getCity();
        }
        view.setCity(city);

        if (state == null && address != null) {
            state = address.getState();
        }

        // country needs to be populated to get states
        if (country == null) {
            country = (address == null) ? getSupportedCountries().get(0) : address.getCountry();
        }

        // populate state spinner after country determined
        view.populateStateSpinner(getStates());
        view.setState(state);
        view.setCountry(country);

        if (TextUtils.isEmpty(zipcode) && address != null) {
            zipcode = address.getZipCode();
        }
        view.setZipCode(zipcode);

        if (stateResidence == null) {
            stateResidence = consumer.getLegalResidence();
        }

        if (countryResidence == null) {
            countryResidence = consumer.getLegalResidence().getCountry();
        }

        appointmentReminderTextsEnabled = consumer.isAppointmentReminderTextsEnabled();
        view.setAppointmentReminderTextsChecked(appointmentReminderTextsEnabled);

        // populate state spinner after residence country determined
        view.populateStateResidenceSpinner(getEnrollmentStates());
        view.setStateResidence(stateResidence);
        view.setCountryResidence(countryResidence);

        view.showConfirmEmail(emailDirty);

        consumerUpdate = consumerManager.getNewConsumerUpdate(consumer);
        view.setFirstNameEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_FIRST_NAME));
        view.setMiddleInitialEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_MIDDLE_INITIAL));
        view.setLastNameEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_LAST_NAME));
        view.setGenderEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_GENDER));
        view.setDobEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_DOB));
        view.setEmailAddressEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_EMAIL));
        view.setPhoneEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_PHONE));
        view.setAddress1Enabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_ADDRESS1));
        view.setAddress2Enabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_ADDRESS2));
        view.setCityEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_CITY));
        view.setStateEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_STATE));
        // country ??
        view.setZipEnabled(consumerUpdate.isEditable(ValidationConstants.VALIDATION_ZIPCODE));

        // state residence ??
        // country residence ??
    }

    public void updateConsumer() {

        if (emailDirty && !emailAddress.equals(confirmEmail)) {
            view.setHasConfirmEmailError(true);
            return;
        }
        else {
            view.setHasConfirmEmailError(false);
        }

        if (SampleUtils.isDirty(consumer.getFirstName(), firstName)) {
            consumerUpdate.setFirstName(firstName);
        }

        if (SampleUtils.isDirty(consumer.getMiddleInitial(), middleInitial)) {
            // empty - no change, otherwise set
            consumerUpdate.setMiddleInitial(TextUtils.isEmpty(middleInitial) ? null : middleInitial);
        }
        // both empty - no change
        else if (SampleUtils.bothEmpty(consumer.getMiddleInitial(), middleInitial)) {
            consumerUpdate.setMiddleInitial(null);
        }

        if (SampleUtils.isDirty(consumer.getMiddleInitial(), middleInitial)) {
            consumerUpdate.setMiddleInitial(middleInitial);
        }

        if (SampleUtils.isDirty(consumer.getLastName(), lastName)) {
            consumerUpdate.setLastName(lastName);
        }

        if (SampleUtils.isDirty(consumer.getEmail(), emailAddress)) {
            consumerUpdate.setEmail(emailAddress);
            consumerUpdate.setConsumerAuthKey(emailAddress);
        }

        if (!TextUtils.isEmpty(password)) {
            consumerUpdate.setPassword(password);
        }

        try {
            final String consumerDobString = consumer.getDob().toString(view.getString(R.string.dateFormat));
            if (SampleUtils.isDirty(consumerDobString, dobString)) {
                consumerUpdate.setDob(SDKLocalDate.valueOf(dobString, view.getString(R.string.dateFormat)));
            }
            // covers case when previous entry attempt was invalid
            else if (TextUtils.equals(consumerDobString, dobString)) {
                consumerUpdate.setDob(null);
            }
        }
        catch (ParseException e) {
            view.setError(e);
            return;
        }

        if (SampleUtils.isDirty(consumer.getGender().toString(), gender.toString())) {
            consumerUpdate.setGender(gender);
        }

        if (SampleUtils.isDirty(consumer.getPhone(), phone)) {
            consumerUpdate.setPhone(phone);
        }

        if (consumer.isAppointmentReminderTextsEnabled() != appointmentReminderTextsEnabled) {
            consumerUpdate.setAppointmentReminderTextsEnabled(appointmentReminderTextsEnabled);
        }

        Address address = null;

        if (!TextUtils.isEmpty(address1)
                || !TextUtils.isEmpty(address2)
                || !TextUtils.isEmpty(city)
                || state != null
                || !TextUtils.isEmpty(zipcode)) {
            address = awsdk.getNewAddress();
            address.setAddress1(address1);
            address.setAddress2(address2);
            address.setCity(city);
            address.setState(state);
            // not needed as Country is tied to State, but adding for clarity
            address.setCountry(country);
            address.setZipCode(zipcode);
        }

        if (consumer.getAddress() != null &&
                address != null &&
                !consumer.getAddress().equals(address)) {
            consumerUpdate.setAddress(address);
        }

        if (consumer.getLegalResidence() != null &&
                stateResidence != null &&
                !consumer.getLegalResidence().equals(stateResidence)) {
            consumerUpdate.setLegalResidence(stateResidence);
        }

        start(UPDATE_CONSUMER);
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        view.showConfirmEmail(emailDirty = !emailAddress.equals(consumer.getEmail()));
    }

    public void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDobString(String dateOfBirth) {
        this.dobString = dateOfBirth;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public void setState(com.americanwell.sdk.entity.State state) {
        this.state = state;
    }

    public void setCountry(Country country) {
        if (!country.equals(this.country)) {
            this.country = country;
            view.populateStateSpinner(getStates());
        }
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setStateResidence(com.americanwell.sdk.entity.State stateResidence) {
        this.stateResidence = stateResidence;
    }

    public void setCountryResidence(Country countryResidence) {
        if (!countryResidence.equals(this.countryResidence)) {
            this.countryResidence = countryResidence;
            view.populateStateResidenceSpinner(getEnrollmentStates());
        }
    }

    public void setAppointmentReminderTextsEnabled(boolean appointmentReminderTextsEnabled) {
        this.appointmentReminderTextsEnabled = appointmentReminderTextsEnabled;
    }

    protected List<com.americanwell.sdk.entity.State> getStates() {
        return getStates(country);
    }

    protected List<com.americanwell.sdk.entity.State> getEnrollmentStates() {
        return getEnrollmentStates(countryResidence);
    }

}
