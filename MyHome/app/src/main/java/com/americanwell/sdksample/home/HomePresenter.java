/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.home;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.consumer.AppointmentReadiness;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerType;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.exception.UnsupportedLocaleException;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;
import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import icepick.State;
import rx.Observable;

/**
 * Presenter for HomeActivity
 */
public class HomePresenter extends BaseSampleNucleusRxPresenter<HomeActivity> {

    public static final String LOG_TAG = HomePresenter.class.getName();

    private static final int GET_SERVICES = 400;
    private static final int ADD_SERVICE_KEY = 401;
    private static final int UPDATE_CONSUMER = 402;
    private static final int GET_APPOINTMENT_READINESS = 403;

    // fyi - practice = service
    // the app refers to them as services
    @State
    ArrayList<PracticeInfo> services;
    @State
    String serviceKey;
    @State
    com.americanwell.sdk.entity.State legalResidence;
    @State
    Consumer newDependent;
    @State
    AppointmentReadiness appointmentReadiness;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // fetch the list of services available
        restartableLatestCache(GET_SERVICES,
                new SampleRequestFunc0<SDKResponse<List<PracticeInfo>, SDKError>>(GET_SERVICES) {
                    @Override
                    public Observable<SDKResponse<List<PracticeInfo>, SDKError>> go() {
                        return observableService.getPractices(consumer);
                    }
                },
                new SampleResponseAction2<List<PracticeInfo>, SDKError, SDKResponse<List<PracticeInfo>, SDKError>>(GET_SERVICES) {
                    @Override
                    public void onSuccess(HomeActivity activity, List<PracticeInfo> practices) {
                        setServices((ArrayList<PracticeInfo>) practices);
                    }
                },
                new SampleFailureAction2(GET_SERVICES)
        );

        // add the given service key
        restartableLatestCache(ADD_SERVICE_KEY,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(ADD_SERVICE_KEY) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        if (serviceKey != null) {
                            return observableService.addServiceKey(
                                    consumer,
                                    serviceKey
                            );
                        }
                        return null;
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(ADD_SERVICE_KEY) {
                    @Override
                    public void onSuccess(HomeActivity servicesActivity, Void aVoid) {
                        stop(ADD_SERVICE_KEY);
                        setServiceKeyAdded();
                    }
                },
                new SampleFailureAction2(ADD_SERVICE_KEY));

        // if we've updated dependent demographics, we actually need to refetch the parent consumer
        // and match the updated dependent with the collection member in the updated parent
        // if you're not working with the dependent as a subordinate of the parent like this sample
        // code is, you can certainly work with the returned dependent directly
        // in this case, it's better to fetch a new parent because we cannot update the dependent
        // inline in the parent consumer object.
        restartableLatestCache(UPDATE_CONSUMER,
                new SampleRequestFunc0<SDKValidatedResponse<Consumer, SDKPasswordError>>(UPDATE_CONSUMER) {
                    @Override
                    public Observable<SDKValidatedResponse<Consumer, SDKPasswordError>> go() {
                        // calling updateconsumer here to get a new instance of the consumer,
                        // but not making any changes to it.
                        final ConsumerUpdate consumerUpdate = awsdk.getConsumerManager().getNewConsumerUpdate(loginConsumer);
                        return observableService.updateConsumer(consumerUpdate);
                    }
                },
                new SampleValidatedPasswordResponseAction2<Consumer, SDKPasswordError, SDKValidatedResponse<Consumer, SDKPasswordError>>(UPDATE_CONSUMER) {
                    @Override
                    public void onSuccess(HomeActivity activity, Consumer consumer) {
                        loginConsumer = consumer; // update the login consumer (parent)
                        stateUtils.setLoginConsumer(consumer);
                        if (newDependent != null) { // this should always be non null
                            for (final Consumer dependent : consumer.getDependents()) {
                                if (dependent.equals(newDependent)) { // match the dependent
                                    setConsumer(dependent); // set it
                                }
                            }
                        }
                        newDependent = null; // clear out the temporary dependent now that we have it worked out
                        stop(UPDATE_CONSUMER);
                    }
                },
                new SampleFailureAction2(UPDATE_CONSUMER)
        );


        restartableLatestCache(GET_APPOINTMENT_READINESS,
                new SampleRequestFunc0<SDKResponse<AppointmentReadiness, SDKError>>(GET_APPOINTMENT_READINESS) {
                    @Override
                    public Observable<SDKResponse<AppointmentReadiness, SDKError>> go() {
                        return observableService.getAppointmentReadiness(consumer);
                    }
                },
                new SampleResponseAction2<AppointmentReadiness, SDKError, SDKResponse<AppointmentReadiness,
                        SDKError>>(GET_APPOINTMENT_READINESS) {
                    @Override
                    public void onSuccess(HomeActivity homeActivity,
                                          AppointmentReadiness appointmentReadiness) {
                        HomePresenter.this.appointmentReadiness = appointmentReadiness;
                        stop(GET_APPOINTMENT_READINESS);
                        setNavigationUpdate();
                    }
                },
                new SampleFailureAction2(GET_APPOINTMENT_READINESS)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }


    @Override
    public void onTakeView(HomeActivity view) {
        super.onTakeView(view);
        setNavHeader();

        if (services == null) {
            getServices();
        }
        else {
            setServices(services);
        }

        // Make sure appointment readiness is enabled before we try and make the call for it
        if (awsdk.getConfiguration().isAppointmentReadinessEnabled()) {
            start(GET_APPOINTMENT_READINESS);
        }
    }

    public void addServiceKey(final String serviceKey) {
        this.serviceKey = serviceKey;
        start(ADD_SERVICE_KEY);
    }

    private void setServiceKeyAdded() {
        serviceKey = null; // clear out the stored key
        view.setServiceKeyAdded(); // tell the view
        start(GET_SERVICES); // after updating the service key, have to re-fetch the services
    }

    public List<Consumer> getConsumers() {
        final List<Consumer> consumers = new ArrayList<>();
        consumers.add(loginConsumer); // always put parent consumer first
        consumers.addAll(loginConsumer.getDependents()); // then add dependents if there are any
        return consumers;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(final Consumer consumer) {
        this.consumer = consumer;
        // if the consumer is switched, set it in the state
        // the "login consumer" never changes, that's always the parent
        // the "consumer" could be the parent or a child.
        stateUtils.setConsumer(consumer);
        setNavHeader();
    }

    public void setNavHeader() {
        if (consumer.isDependent()) {
            view.setNavHeaderText(consumer.getFullName() + " (" + loginConsumer.getFullName() + ")"); // put the consumer's name in the navigation drawer
        }
        else {
            view.setNavHeaderText(consumer.getFullName()); // put the consumer's name in the navigation drawer
        }
        view.setSwitchUserView(!loginConsumer.getDependents().isEmpty());
        view.setShowAddDependentMenuItem(!consumer.isDependent() && consumer.getConsumerType() != ConsumerType.HP); // dependents and HP users cannot add dependents
        view.setShowMenuItems(!consumer.isDependent());
        view.setShowInsuranceMenuItem(awsdk.getConfiguration().isConsumerHealthInsuranceCollected());
        view.setShowServiceKeyMenuItem(awsdk.getConfiguration().isServiceKeyCollected());
        view.setShowSetLocale(getSupportedLocales().size() > 1);
    }

    public boolean isCurrentConsumer(final Consumer consumer) {
        return this.consumer == consumer; // match instance
    }

    public void logOut() {
        awsdk.clearAuthentication(loginConsumer);
    }

    private void setServices(final ArrayList<PracticeInfo> services) {
        this.services = services;
        view.setServices(services);
    }

    public void getServices() {
        start(GET_SERVICES);
    }

    public void loadPracticeImage(final PracticeInfo practiceInfo,
                                  final ImageView imageView,
                                  final Callback picassoCallback,
                                  final boolean checkCache) {

        // check image exists
        if (practiceInfo.hasSmallLogo()) {
            // preferred method for loading image
            awsdk.getPracticeProvidersManager()
                    .newImageLoader(practiceInfo, imageView, false)
                    .callback(picassoCallback)
                    .checkCache(checkCache)
                    .build()
                    .load();
        }
        else {
            picassoCallback.onError();
        }
    }

    public void setConsumerUpdated() {
        // the only way we can get here is when the parent was already selected, so we need to update the consumer in memory
        loginConsumer = stateUtils.getLoginConsumer();
        setConsumer(loginConsumer);
        getServices();
    }

    public void setDependentUpdated(final Consumer newDependent) {
        // when we get an updated dependent, hang on to it and kick off a parent consumer update process
        this.newDependent = newDependent;
        start(UPDATE_CONSUMER);
    }

    public boolean isDependent() {
        return consumer.isDependent();
    }

    protected List<Locale> getSupportedLocales() {

        // this is the system/device (not application) Locale
        Locale sysLocale = getSystemLocale();
        List<Locale> supportedLocales = awsdk.getSupportedLocales();

        // add system/device Locale if not included to allow toggling
        if (!supportedLocales.contains(sysLocale)) {
            supportedLocales.add(sysLocale);
        }

        return supportedLocales;
    }

    protected void setPreferredLocale(Locale locale) {
        try {
            awsdk.setPreferredLocale(locale);
        }
        catch (UnsupportedLocaleException ule) {
            DefaultLogger.d(LOG_TAG, ule.getMessage());
        }
    }

    protected Locale getSystemLocale() {
        return Resources.getSystem().getConfiguration().locale;
    }

    public void setNavigationUpdate() {
        view.setShowTechCheck(showTechCheck());
    }

    boolean showTechCheck() {
        return awsdk.getConfiguration().isAppointmentReadinessEnabled() &&
                appointmentReadiness != null &&
                appointmentReadiness.isTechCheckEnabled() &&
                !appointmentReadiness.getTechCheckPassed();
    }
}
