/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.cost;

import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKErrorReason;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.intake.BaseIntakePresenter;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import icepick.State;
import rx.Observable;

/**
 * Presenter for VisitCostActivity
 */
public class VisitCostPresenter extends BaseIntakePresenter<VisitCostActivity> {

    private static final int CREATE_VISIT = 210;
    private static final int APPLY_COUPON = 211;
    private static final int GET_PAYMENT_METHOD = 212;

    @State
    Double expectedCost;
    @State
    String couponCode;
    @State
    Visit visit;
    @State
    PaymentMethod paymentMethod;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(CREATE_VISIT,
                new SampleRequestFunc0<SDKValidatedResponse<Visit, SDKError>>(CREATE_VISIT) {
                    @Override
                    public Observable<SDKValidatedResponse<Visit, SDKError>> go() {
                        return observableService.createOrUpdateVisit(visitContext);
                    }
                },
                new SampleValidatedResponseAction2<Visit, SDKError, SDKValidatedResponse<Visit, SDKError>>(CREATE_VISIT) {
                    @Override
                    public void onSuccess(VisitCostActivity activity, Visit visit) {
                        setVisit(visit);
                        if (couponCode == null) {
                            setCouponCode(visitContext.getProposedCouponCode());
                            view.setCouponCode(visitContext.getProposedCouponCode());
                        }
                        // if the visit was returned successfully but there were eligibility errors on the server side,
                        // notify the user - these errors are not blocking... just putting up an informational dialog
                        view.setEligibilityError(visit.getVisitCost().getEligibilityError());
                        getPaymentMethod();
                    }

                    @Override
                    public void onError(VisitCostActivity visitCostActivity, SDKError sdkError) {
                        stop(CREATE_VISIT);
                        visitCostActivity.handleFatalError(sdkError); // special case - kill intake on error here
                    }
                },
                new SampleFailureAction2(CREATE_VISIT) {
                    @Override
                    public void onFailure(VisitCostActivity visitCostActivity, Throwable throwable) {
                        stop(CREATE_VISIT);
                        visitCostActivity.handleFatalError(throwable); // special case - kill intake on error here
                    }
                }
        );

        restartableLatestCache(APPLY_COUPON,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(APPLY_COUPON) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.applyCouponCode(visit, couponCode);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(APPLY_COUPON) {
                    @Override
                    public void onSuccess(VisitCostActivity activity, Void aVoid) {
                        setExpectedCost(visit.getVisitCost().getExpectedConsumerCopayCost());
                    }
                },
                new SampleFailureAction2(APPLY_COUPON)
        );

        restartableLatestCache(GET_PAYMENT_METHOD,
                new SampleRequestFunc0<SDKResponse<PaymentMethod, SDKError>>(GET_PAYMENT_METHOD) {
                    @Override
                    public Observable<SDKResponse<PaymentMethod, SDKError>> go() {
                        return observableService.getPaymentMethod(visit);
                    }
                },
                new SampleResponseAction2<PaymentMethod, SDKError, SDKResponse<PaymentMethod, SDKError>>(GET_PAYMENT_METHOD) {
                    @Override
                    public void onSuccess(VisitCostActivity activity, PaymentMethod paymentMethod) {
                        setPaymentMethod(paymentMethod);
                    }

                    @Override
                    public void onError(VisitCostActivity visitCostActivity, SDKError sdkError) {
                        if (sdkError.getSDKErrorReason() == SDKErrorReason.CREDIT_CARD_MISSING) {
                            visitCostActivity.setSomethingIsBusy(false); // make sure we decrement the spinner count
                            setPaymentMethod(null);
                        }
                        else {
                            super.onError(visitCostActivity, sdkError);
                        }
                    }
                },
                new SampleFailureAction2(GET_PAYMENT_METHOD)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(VisitCostActivity view) {
        super.onTakeView(view);
        if (isUnsubscribed(CREATE_VISIT)) {
            if (visit == null) {
                start(CREATE_VISIT);
            }
        }
        else {
            setVisit(visit);
        }

        if (couponCode == null) {
            // if the visit has a proposed coupon code, prefill with it.
            if (visit != null) {
                setCouponCode(visit.getVisitCost().getProposedCouponCode());
                view.setCouponCode(visit.getVisitCost().getProposedCouponCode());
            }
        }
        else {
            view.setCouponCode(couponCode);
        }

        setExpectedCost(expectedCost);

        if (paymentMethod == null) {
            getPaymentMethod();
        }
        else {
            setPaymentMethod(paymentMethod);
        }
    }

    private void setVisit(final Visit visit) {
        this.visit = visit;
        if (visit != null) {
            view.setRequiresPayment(requiresPayment());
            if (visit.getVisitCost().isDeferredBillingInEffect()) { // if deferred billing is in effect, show the prompt
                view.setDeferredBillingPrompt(visit.getVisitCost().getDeferredBillingText());
                view.setShowCostSummary(false);
            }
            else {
                view.setShowCostSummary(true);
                view.setSummary(localeUtils.formatCurrency(
                        visit.getVisitCost().getExpectedConsumerCopayCost(),
                        awsdk.getConfiguration().getCurrencyCode()));
            }
            // this is important.  if you do not test for "can apply coupon code" you may get an IllegalArgumentException
            // attempting to apply in an invalid case.
            view.setShowCouponCode(
                    visit.getVisitCost().canApplyCouponCode() &&
                    !visit.getVisitCost().isFree()
            );
        }
    }

    public void applyCoupon() {
        if (!TextUtils.isEmpty(couponCode)) {
            start(APPLY_COUPON);
        }
        else {
            view.showNoCouponEntered();
        }
    }

    public void setCouponCode(final String couponCode) {
        this.couponCode = couponCode;
    }

    public boolean requiresPayment() {
        return visit.requiresPaymentMethod();
    }

    public Visit getVisit() {
        return visit;
    }

    public void setExpectedCost(final Double expectedCost) {
        if (expectedCost != null) {
            this.expectedCost = expectedCost;

            view.setSummary(localeUtils.formatCurrency(
                    expectedCost,
                    awsdk.getConfiguration().getCurrencyCode()));

            view.setCouponApplied(); // you can only apply one coupon to a Visit, so disable the UI
            view.setRequiresPayment(requiresPayment());
        }
    }

    public boolean isCouponApplied() {
        return this.expectedCost != null;
    }

    public boolean hasCouponCode() {
        return !TextUtils.isEmpty(couponCode);
    }

    public void getPaymentMethod() {
        if (visit != null) { // cannot do this until we've created the visit
            start(GET_PAYMENT_METHOD);
        }
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        view.setPaymentMethod(hasPaymentMethod() ? paymentMethod : null);
    }

    public boolean hasPaymentMethod() {
        return paymentMethod != null &&
                !TextUtils.isEmpty(paymentMethod.getType()) &&
                !TextUtils.isEmpty(paymentMethod.getLastDigits());
    }

    public boolean canPay() {
        return !requiresPayment() || (hasPaymentMethod() && !paymentMethod.isExpired());
    }
}
