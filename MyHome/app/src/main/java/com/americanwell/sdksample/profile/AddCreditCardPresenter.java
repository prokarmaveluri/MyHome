/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.os.Bundle;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKErrorReason;
import com.americanwell.sdk.entity.billing.CreatePaymentRequest;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for AddCreditCardActivity
 */
public class AddCreditCardPresenter extends BaseSampleNucleusRxPresenter<AddCreditCardActivity> {

    private static final int GET_PAYMENT_METHOD = 800;
    private static final int UPDATE_PAYMENT_METHOD = 801;

    @Inject
    ConsumerManager consumerManager;

    @State
    PaymentMethod paymentMethod;
    @State
    String nameOnCard;
    @State
    String cardNumber;
    @State
    int expirationMonth;
    @State
    int expirationYear;
    @State
    String cvvCode;
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
    String zipCode;
    @State
    CreatePaymentRequest createPaymentRequest;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_PAYMENT_METHOD,
                new SampleRequestFunc0<SDKResponse<PaymentMethod, SDKError>>(GET_PAYMENT_METHOD) {
                    @Override
                    public Observable<SDKResponse<PaymentMethod, SDKError>> go() {
                        return observableService.getPaymentMethod(consumer);
                    }
                },
                new SampleResponseAction2<PaymentMethod, SDKError, SDKResponse<PaymentMethod, SDKError>>(GET_PAYMENT_METHOD) {
                    @Override
                    public void onSuccess(AddCreditCardActivity activity, PaymentMethod paymentMethod) {
                        setPaymentMethod(paymentMethod);
                    }

                    @Override
                    public void onError(AddCreditCardActivity addCreditCardActivity, SDKError sdkError) {
                        if (sdkError.getSDKErrorReason() == SDKErrorReason.CREDIT_CARD_MISSING) {
                            addCreditCardActivity.setSomethingIsBusy(false); // make sure we decrement the spinner count
                            setPaymentMethod(null);
                        }
                        else {
                            super.onError(addCreditCardActivity, sdkError);
                        }
                    }
                },
                new SampleFailureAction2(GET_PAYMENT_METHOD)
        );

        restartableLatestCache(UPDATE_PAYMENT_METHOD,
                new SampleRequestFunc0<SDKValidatedResponse<PaymentMethod, SDKError>>(UPDATE_PAYMENT_METHOD) {
                    @Override
                    public Observable<SDKValidatedResponse<PaymentMethod, SDKError>> go() {
                        return observableService.updatePaymentMethod(
                                createPaymentRequest
                        );
                    }
                },
                new SampleValidatedResponseAction2<PaymentMethod, SDKError, SDKValidatedResponse<PaymentMethod, SDKError>>(UPDATE_PAYMENT_METHOD) {
                    @Override
                    public void onSuccess(AddCreditCardActivity addCreditCardActivity, PaymentMethod paymentMethod) {
                        view.setPaymentUpdated(paymentMethod);
                    }
                },
                new SampleFailureAction2(UPDATE_PAYMENT_METHOD)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(AddCreditCardActivity view) {
        super.onTakeView(view);
        if (paymentMethod == null) {
            start(GET_PAYMENT_METHOD);
        }
        else {
            setPaymentMethod(paymentMethod);
        }

        view.setNameOnCard(nameOnCard);
        view.setCardNumber(cardNumber);
        view.setExpirationMonth(expirationMonth);
        view.setExpirationYear(expirationYear);
        view.setCvvCode(cvvCode);
        view.setAddress1(address1);
        view.setAddress2(address2);
        view.setCity(city);
        view.setState(state);
        view.setCountry(country);
        view.setZipCode(zipCode);
        view.showMultiCountrySpinner(isMultiCountry());
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        view.setPaymentMethod(paymentMethod);
    }

    public void addCreditCard() {
        createPaymentRequest = consumerManager.getNewCreatePaymentRequest(consumer);
        createPaymentRequest.setNameOnCard(nameOnCard);
        createPaymentRequest.setCreditCardNumber(cardNumber);
        createPaymentRequest.setCreditCardMonth(expirationMonth);
        createPaymentRequest.setCreditCardYear(expirationYear);
        createPaymentRequest.setCreditCardSecCode(cvvCode);
        createPaymentRequest.setCreditCardZip(zipCode);

        final Address address = awsdk.getNewAddress();
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setZipCode(zipCode);

        createPaymentRequest.setAddress(address);
        start(UPDATE_PAYMENT_METHOD);
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public com.americanwell.sdk.entity.State getState() {
        return state;
    }

    public void setState(com.americanwell.sdk.entity.State state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCountry(Country country) {
        if (country != null && !country.equals(this.country)) {
            this.country = country;
            view.populateStateSpinner(getValidPaymentMethodStates(country));
        }
    }
}
