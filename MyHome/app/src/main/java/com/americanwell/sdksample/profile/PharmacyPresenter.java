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
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.pharmacy.PharmacyType;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for PharmacyActivity
 */
public class PharmacyPresenter extends BaseSampleNucleusRxPresenter<PharmacyActivity> {

    private static int GET_PHARMACY = 830;
    private static int UPDATE_PHARMACY = 831;
    private static int GET_SHIPPING_ADDRESS = 832;
    private static int UPDATE_SHIPPING_ADDRESS = 833;

    @Inject
    ConsumerManager consumerManager;
    @State
    Pharmacy pharmacy;
    @State
    boolean userChangedPharmacy;
    @State
    Address shippingAddress;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_PHARMACY,
                new SampleRequestFunc0<SDKResponse<Pharmacy, SDKError>>(GET_PHARMACY) {
                    @Override
                    public Observable<SDKResponse<Pharmacy, SDKError>> go() {
                        return observableService.getPharmacy(consumer);
                    }
                },
                new SampleResponseAction2<Pharmacy, SDKError, SDKResponse<Pharmacy, SDKError>>(GET_PHARMACY) {
                    @Override
                    public void onSuccess(PharmacyActivity pharmacyActivity, Pharmacy pharmacy) {
                        stop(GET_PHARMACY);
                        setPharmacy(pharmacy, false);
                        if (pharmacy != null && pharmacy.getType().equals(PharmacyType.MailOrder)) {
                            start(GET_SHIPPING_ADDRESS);
                        }
                    }

                    @Override
                    public void onError(PharmacyActivity pharmacyActivity, SDKError err) {
                        // set to display no preferred pharmacies
                        view.setPharmacy(null);
                    }
                },
                new SampleFailureAction2(GET_PHARMACY)
        );

        restartableLatestCache(UPDATE_PHARMACY,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(UPDATE_PHARMACY) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.updatePharmacy(
                                consumer,
                                pharmacy
                        );
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(UPDATE_PHARMACY) {
                    @Override
                    public void onSuccess(PharmacyActivity pharmacyActivity, Void aVoid) {
                        view.setPharmacyUpdated(userChangedPharmacy);
                    }
                },
                new SampleFailureAction2(UPDATE_PHARMACY)
        );

        restartableLatestCache(GET_SHIPPING_ADDRESS,
                new SampleRequestFunc0<SDKResponse<Address, SDKError>>(GET_SHIPPING_ADDRESS) {
                    @Override
                    public Observable<SDKResponse<Address, SDKError>> go() {
                        return observableService.getShippingAddress(
                                consumer
                        );
                    }

                },
                new SampleResponseAction2<Address, SDKError, SDKResponse<Address, SDKError>>(GET_SHIPPING_ADDRESS) {
                    @Override
                    public void onSuccess(PharmacyActivity pharmacyActivity, Address address) {
                        stop(GET_SHIPPING_ADDRESS);
                        setShippingAddress(address);
                    }

                    @Override
                    public void onError(PharmacyActivity pharmacyActivity, SDKError err) {
                        // no-op - dont display error message associated with no shipping address
                    }

                },
                new SampleFailureAction2(GET_SHIPPING_ADDRESS)
        );

        restartableLatestCache(UPDATE_SHIPPING_ADDRESS,
                new SampleRequestFunc0<SDKValidatedResponse<Address, SDKError>>(UPDATE_SHIPPING_ADDRESS) {
                    @Override
                    public Observable<SDKValidatedResponse<Address, SDKError>> go() {
                        return observableService.updateShippingAddress(
                                consumer,
                                shippingAddress);
                    }
                },
                new SampleValidatedResponseAction2<Address, SDKError, SDKValidatedResponse<Address, SDKError>>(UPDATE_SHIPPING_ADDRESS) {
                    @Override
                    public void onSuccess(PharmacyActivity pharmacyActivity, Address address) {
                        setShippingAddress(address);
                    }
                },
                new SampleFailureAction2(UPDATE_SHIPPING_ADDRESS)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    private void setPharmacy(final Pharmacy pharmacy, final boolean user) {
        this.pharmacy = pharmacy;
        this.userChangedPharmacy = user;
        view.setPharmacy(pharmacy);
        final boolean mailOrder = pharmacy != null && pharmacy.getType().equals(PharmacyType.MailOrder);
        if (user || mailOrder) { // show it for mail order b/c the shipping address is editable
            view.setSaveEnabled(true);
        }
        view.setShowShippingAddress(mailOrder); // only show shipping address for mail order pharmacies
        view.showMultiCountrySpinner(isMultiCountry());
    }

    public void setPharmacy(final Pharmacy pharmacy) {
        setPharmacy(pharmacy, true);
    }

    @Override
    public void onTakeView(PharmacyActivity view) {
        super.onTakeView(view);
        if (pharmacy == null) {
            start(GET_PHARMACY);
        }
        else {
            setPharmacy(pharmacy);
        }

        if (shippingAddress != null) {
            setShippingAddress(shippingAddress);
        }
        else if (pharmacy != null && pharmacy.getType().equals(PharmacyType.MailOrder) && shippingAddress == null) {
            start(GET_SHIPPING_ADDRESS);
        }
    }

    public void updatePharmacy() {

        // this is an example of using the manager validation methods directly. most of the samples
        // in this app rely on the implicit validation of the methods... but here we want to avoid
        // submitting BEFORE then so we don't then go and call the pharmacy update
        if (pharmacy != null && pharmacy.getType().equals(PharmacyType.MailOrder)) {
            final Map<String, ValidationReason> errors = new HashMap<>();
            consumerManager.validateAddress(shippingAddress, errors);

            if (!errors.isEmpty()) {
                view.setValidationReasons(errors);
                return;
            }
            else {
                start(UPDATE_SHIPPING_ADDRESS);
            }
        }

        if (userChangedPharmacy) {
            start(UPDATE_PHARMACY);
        }
        else {
            view.setPharmacyUpdated(userChangedPharmacy);
        }
    }

    public void setShippingAddress(final Address address) {
        this.shippingAddress = address;
        if (shippingAddress != null) {
            view.setAddress1(shippingAddress.getAddress1());
            view.setAddress2(shippingAddress.getAddress2());
            view.setCity(shippingAddress.getCity());
            view.populateStateSpinner(getValidPaymentMethodStates(shippingAddress.getCountry()));
            view.setState(shippingAddress.getState());
            view.setCountry(shippingAddress.getCountry());
            view.setZipCode(shippingAddress.getZipCode());
        }
    }

    public void setAddress1(String address1) {
        getShippingAddress().setAddress1(address1);
    }

    public void setAddress2(String address2) {
        getShippingAddress().setAddress2(address2);
    }

    public void setCity(String city) {
        getShippingAddress().setCity(city);
    }

    public void setState(com.americanwell.sdk.entity.State state) {
        if (state != null) {
            getShippingAddress().setState(state);
        }
    }

    public void setCountry(final Country country) {
        if (country != null && !country.equals(getShippingAddress().getCountry())) {
            getShippingAddress().setCountry(country);
            view.populateStateSpinner(getValidPaymentMethodStates(country));
        }
    }

    public void setZipCode(String zipCode) {
        getShippingAddress().setZipCode(zipCode);
    }

    private Address getShippingAddress() {
        if (shippingAddress == null) {
            shippingAddress = awsdk.getNewAddress();
        }
        return shippingAddress;
    }

}
