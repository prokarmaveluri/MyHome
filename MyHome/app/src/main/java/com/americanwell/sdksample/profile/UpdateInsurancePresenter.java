/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKErrorReason;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.consumer.ConsumerType;
import com.americanwell.sdk.entity.insurance.HealthPlan;
import com.americanwell.sdk.entity.insurance.Relationship;
import com.americanwell.sdk.entity.insurance.Subscription;
import com.americanwell.sdk.entity.insurance.SubscriptionUpdateRequest;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for UpdateInsuranceActivity
 */
public class UpdateInsurancePresenter extends BaseSampleNucleusRxPresenter<UpdateInsuranceActivity> {

    private final static String LOG_TAG = UpdateInsurancePresenter.class.getName();
    private final static int UPDATE_SUBSCRIPTION = 852;

    @Inject
    ConsumerManager consumerManager;

    @State
    ArrayList<Relationship> relationships;
    @State
    ArrayList<HealthPlan> healthPlans;

    @State
    boolean hasInsurance;
    @State
    Relationship relationship;
    @State
    HealthPlan healthPlan;
    @State
    String subscriberId;
    @State
    String subscriberSuffix;
    @State
    String primarySubscriberFirstName;
    @State
    String primarySubscriberLastName;
    @State
    String primarySubscriberDob;
    @State
    SubscriptionUpdateRequest subscriptionUpdateRequest;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(UPDATE_SUBSCRIPTION,
                new SampleRequestFunc0<SDKValidatedResponse<Void, SDKError>>(UPDATE_SUBSCRIPTION) {
                    @Override
                    public Observable<SDKValidatedResponse<Void, SDKError>> go() {
                        return observableService.updateSubscription(subscriptionUpdateRequest);
                    }
                },
                new SampleValidatedResponseAction2<Void, SDKError, SDKValidatedResponse<Void, SDKError>>(UPDATE_SUBSCRIPTION) {
                    @Override
                    public void onSuccess(UpdateInsuranceActivity updateInsuranceActivity, Void aVoid) {
                        view.setInsuranceUpdated();
                    }

                    @Override
                    public void onError(UpdateInsuranceActivity updateInsuranceActivity, SDKError sdkError) {
                        // special handling for eligibility errors - allow "save anyway" option
                        if (sdkError.getSDKErrorReason() == SDKErrorReason.VALIDATION_BAD_ELIG_INFO ||
                                sdkError.getSDKErrorReason() == SDKErrorReason.VALIDATION_ELIG_EXCEPTION) {
                            view.setEligibilityError(sdkError);
                        }
                        else {
                            super.onError(updateInsuranceActivity, sdkError);
                        }
                    }
                },
                new SampleFailureAction2(UPDATE_SUBSCRIPTION)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(UpdateInsuranceActivity view) {
        super.onTakeView(view);

        init();

        if (healthPlans == null) {
            healthPlans = (ArrayList<HealthPlan>) consumerManager.getHealthPlans();
        }
        setHealthPlans(healthPlans);

        if (relationships == null) {
            relationships = (ArrayList<Relationship>) consumerManager.getRelationships();
        }
        setRelationships(relationships);

        view.setHasInsurance(hasInsurance);
        setNoInsurance(!hasInsurance);
        view.setSubscriberId(subscriberId);
        view.setSubscriberSuffix(subscriberSuffix);
        view.setPrimarySubscriberFirstName(primarySubscriberFirstName);
        view.setPrimarySubscriberLastName(primarySubscriberLastName);
        view.setPrimarySubscriberDob(primarySubscriberDob);

        view.setEnabled(!consumer.getConsumerType().equals(ConsumerType.HP));
    }

    public void setHealthPlans(final ArrayList<HealthPlan> healthPlans) {
        this.healthPlans = healthPlans;
        view.setHealthPlans(healthPlans);
        view.setHealthPlan(healthPlan);
    }

    public void setRelationships(final ArrayList<Relationship> relationships) {
        if (consumer.isDependent()) {
            this.relationships = new ArrayList<>();
            for (final Relationship relationship : relationships) {
                if (relationship.isValidForMinor()) {
                    this.relationships.add(relationship);
                }
            }
        }
        else {
            this.relationships = relationships;
        }
        view.setRelationships(this.relationships);
        view.setRelationship(relationship);
    }

    public void setNoInsurance(final boolean noInsurance) {
        this.hasInsurance = !noInsurance;
        if (noInsurance) {
            view.showDataFields(false);
            view.setCardImage(null);
        }
        else {
            view.setSubscriberId(subscriberId);
            view.setSubscriberSuffix(subscriberSuffix);
            view.setHasHealthPlans(true);
            view.setHasRelationships(true);
            if (relationships != null) // don't do this if we haven't fetched relationships yet
            {
                view.setRelationship(relationship);
            }
            if (healthPlans != null) // don't do this if we haven't fetched health plans yet
            {
                view.setHealthPlan(healthPlan);
            }
            view.setPrimarySubscriberFirstName(primarySubscriberFirstName);
            view.setPrimarySubscriberLastName(primarySubscriberLastName);
            view.setPrimarySubscriberDob(primarySubscriberDob);
            view.showDataFields(true);
        }
    }

    public void setRelationship(final Relationship relationship) {
        this.relationship = relationship;

        if (relationship == null || relationship.isPrimarySubscriber()) {
            view.showPrimaryFields(false);
        }
        else {
            view.setPrimarySubscriberFirstName(primarySubscriberFirstName);
            view.setPrimarySubscriberLastName(primarySubscriberLastName);
            view.setPrimarySubscriberDob(primarySubscriberDob);
            view.showPrimaryFields(true);
        }
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public void setSubscriberSuffix(String subscriberSuffix) {
        this.subscriberSuffix = subscriberSuffix;
    }

    public void setPrimarySubscriberFirstName(String primarySubscriberFirstName) {
        this.primarySubscriberFirstName = primarySubscriberFirstName;
    }

    public void setPrimarySubscriberLastName(String primarySubscriberLastName) {
        this.primarySubscriberLastName = primarySubscriberLastName;
    }

    public void setPrimarySubscriberDob(String primarySubscriberDob) {
        this.primarySubscriberDob = primarySubscriberDob;
    }

    public void setHealthPlan(HealthPlan healthPlan) {
        this.healthPlan = healthPlan;
    }

    public void init() {
        final Subscription subscription = consumer.getSubscription();
        if (subscription == null) {
            hasInsurance = false;
        }
        else {
            hasInsurance = true;
            relationship = subscription.getRelationship();
            healthPlan = subscription.getHealthPlan();
            subscriberId = subscription.getSubscriberId();
            subscriberSuffix = subscription.getSubscriberSuffix();
            primarySubscriberFirstName = subscription.getPrimarySubscriberFirstName();
            primarySubscriberLastName = subscription.getPrimarySubscriberLastName();
            try {
                primarySubscriberDob = subscription.getPrimarySubscriberDateOfBirth() != null ? subscription
                        .getPrimarySubscriberDateOfBirth().toString(view.getString(R.string.dateFormat)) : null;
            }
            catch (ParseException e) {
                DefaultLogger.e(LOG_TAG, "error parsing stored primary subscriber dob", e);
            }
        }
    }

    public void updateSubscription() {
        subscriptionUpdateRequest = consumerManager.getNewSubscriptionUpdateRequest(consumer, false);
        // If there's insurance information, then set it on the request object
        if (hasInsurance) {
            // this is an example of providing a custom validation, rather than using the validation
            // framework provided in the base implementation
            if (healthPlan != null && healthPlan.isFeedControlled()) {
                view.setFeedHpError(healthPlan.getName());
                return;
            }
            final Subscription subscription = subscriptionUpdateRequest.getSubscription();
            subscription.setHealthPlan(healthPlan);
            subscription.setSubscriberId(subscriberId);
            subscription.setSubscriberSuffix(subscriberSuffix);
            subscription.setRelationship(relationship);
            if (relationship != null && !relationship.isPrimarySubscriber()) {
                subscription.setPrimarySubscriberFirstName(primarySubscriberFirstName);
                subscription.setPrimarySubscriberLastName(primarySubscriberLastName);
                try {
                    subscription.setPrimarySubscriberDateOfBirth(SDKLocalDate.valueOf(primarySubscriberDob, view
                            .getString(R.string.dateFormat)));
                }
                catch (ParseException e) {
                    view.setError(e);
                    return;
                }
            }

            Map<String, ValidationReason> errors = new HashMap<>();
            awsdk.getConsumerManager().validateSubscriptionUpdateRequest(subscriptionUpdateRequest, errors);
            if (errors.isEmpty()) {
                start(UPDATE_SUBSCRIPTION);
            }
            else {
                view.setValidationReasons(errors);
                String field = "subscriptionUpdateRequest.subscription" + ".subscriberId";

                ValidationReason reason = errors.get(field);
                if (reason != null && reason == ValidationReason.FIELD_INVALID_FORMAT) {
                    String errorString = subscription.getHealthPlan().getPayerInfo().getSubscriberIdPatternMsgKey();
                    view.setSubscriberIDError(errorString);
                }
            }
            // create empty map
            // call validatesubscription()
            // if map empty continue otherwise do following and stop
            // custom handling for subscription id invalid
            // if map contains key/value (id / invalid) - get error string from payerinfo
            // set the string to a new method in the activity that setError() on subscriber id
            // send the rest to setvalidationreasons()
        }
        // We don't want to display a confirmation message if no data will be deleted so we check these two
        // primary fields to see if any exists first.
        else if (healthPlan != null && !TextUtils.isEmpty(subscriberId)) {
            view.verifyRemoveSubscription();
        }
    }

    public void removeSubscription() {
        subscriptionUpdateRequest = consumerManager.getNewSubscriptionUpdateRequest(consumer, false);
        start(UPDATE_SUBSCRIPTION);
    }

    // there's a slight crossing of responsibilities here in that we're passing UI componentry into the presenter, but it's a necessary evil
    public void loadHealthPlanCardImage(final HealthPlan healthPlan, final ImageView imageView) {

        // check has image
        if (healthPlan.hasCardImage()) {
            awsdk.getConsumerManager()
                    .newImageLoader(healthPlan, imageView)
                    .build()
                    .load();
        }
    }

    // this assumes you got through updateSubscription() above and received an eligibility error
    // and in the UI the consumer said to ignore that error and save anyway
    // we just toggle the elig checks and resubmit
    public void resubmitSubscriptionIgnoreEligibilityErrors() {
        subscriptionUpdateRequest.setIgnoreEligibilityChecks(true);
        start(UPDATE_SUBSCRIPTION);
    }
}