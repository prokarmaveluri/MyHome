/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.services;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.americanwell.sdk.entity.Language;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.practice.OnDemandSpecialty;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.entity.provider.AvailableProviders;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for ServiceActivity
 */
public class ServicePresenter extends BaseSampleNucleusRxPresenter<ServiceActivity> {

    private static final String LOG_TAG = ServicePresenter.class.getName();

    private static final int GET_SERVICE = 1010;
    private static final int FIND_PROVIDERS = 1011;
    private static final int GET_SPECIALTIES = 1012;
    private static final int GET_FIRST_AVAILABLE_VISIT_CONTEXT = 1013;
    private static final int FIND_PAST_PROVIDERS = 1014;
    private static final int FIND_FUTURE_AVAILABLE_PROVIDERS = 1015;

    @State
    PracticeInfo serviceInfo;
    @State
    Practice service;
    @State
    ArrayList<ProviderInfo> providers;
    @State
    ArrayList<ProviderInfo> pastProviders;
    @State
    ArrayList<OnDemandSpecialty> specialties;
    @State
    OnDemandSpecialty specialty;
    @State
    Language languageSpoken;
    @State
    VisitContext visitContext;
    @State
    boolean showAllProviders = true;
    @State
    AvailableProviders futureAvailableProviders;
    @State
    Date appointmentsDate;
    @State
    boolean showFirstAvailable;
    @State
    int currentPage = 0;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_SERVICE,
                new SampleRequestFunc0<SDKResponse<Practice, SDKError>>(GET_SERVICE) {
                    @Override
                    public Observable<SDKResponse<Practice, SDKError>> go() {
                        return observableService.getService(serviceInfo);
                    }
                },
                new SampleResponseAction2<Practice, SDKError, SDKResponse<Practice, SDKError>>(GET_SERVICE) {
                    @Override
                    public void onSuccess(ServiceActivity serviceActivity, Practice practice) {
                        DefaultLogger.d(LOG_TAG, "retrieved service, now getting providers and specialties");
                        stop(GET_SERVICE);
                        setService(practice);
                        if (practice.isShowAvailableNow()) {
                            start(FIND_PROVIDERS);
                            start(FIND_PAST_PROVIDERS);
                            start(GET_SPECIALTIES);
                        }
                        if (practice.isShowScheduling()) {
                            start(FIND_FUTURE_AVAILABLE_PROVIDERS);
                        }
                    }
                },
                new SampleFailureAction2(GET_SERVICE)
        );

        restartableLatestCache(FIND_PROVIDERS,
                new SampleRequestFunc0<SDKResponse<List<ProviderInfo>, SDKError>>(FIND_PROVIDERS) {
                    @Override
                    public Observable<SDKResponse<List<ProviderInfo>, SDKError>> go() {
                        return observableService.findProviders(
                                consumer,
                                service,
                                languageSpoken
                        );
                    }
                },
                new SampleResponseAction2<List<ProviderInfo>, SDKError, SDKResponse<List<ProviderInfo>, SDKError>>(FIND_PROVIDERS) {
                    @Override
                    public void onSuccess(ServiceActivity serviceActivity, List<ProviderInfo> providerInfos) {
                        DefaultLogger.d(LOG_TAG, "retrieved providers");
                        setProviders((ArrayList<ProviderInfo>) providerInfos);
                    }
                },
                new SampleFailureAction2(FIND_PROVIDERS)
        );

        restartableLatestCache(FIND_PAST_PROVIDERS,
                new SampleRequestFunc0<SDKResponse<List<Provider>, SDKError>>(FIND_PAST_PROVIDERS) {
                    @Override
                    public Observable<SDKResponse<List<Provider>, SDKError>> go() {
                        return observableService.getPastProviders(
                                consumer,
                                null
                        );
                    }
                },
                new SampleResponseAction2<List<Provider>, SDKError, SDKResponse<List<Provider>, SDKError>>(FIND_PROVIDERS) {
                    @Override
                    public void onSuccess(ServiceActivity serviceActivity, List<Provider> pastProviders) {
                        DefaultLogger.d(LOG_TAG, "retrieved past providers");
                        setPastProviders((ArrayList<Provider>) pastProviders);
                    }
                },
                new SampleFailureAction2(FIND_PROVIDERS)
        );

        restartableLatestCache(GET_SPECIALTIES,
                new SampleRequestFunc0<SDKResponse<List<OnDemandSpecialty>, SDKError>>(GET_SPECIALTIES) {
                    @Override
                    public Observable<SDKResponse<List<OnDemandSpecialty>, SDKError>> go() {
                        return observableService.getSpecialties(
                                consumer,
                                service
                        );
                    }
                },
                new SampleResponseAction2<List<OnDemandSpecialty>, SDKError, SDKResponse<List<OnDemandSpecialty>, SDKError>>(GET_SPECIALTIES) {
                    @Override
                    public void onSuccess(ServiceActivity serviceActivity, List<OnDemandSpecialty> onDemandSpecialties) {
                        DefaultLogger.d(LOG_TAG, "retrieved specialties");
                        setSpecialties((ArrayList<OnDemandSpecialty>) onDemandSpecialties);
                    }
                },
                new SampleFailureAction2(GET_SPECIALTIES)
        );

        restartableLatestCache(GET_FIRST_AVAILABLE_VISIT_CONTEXT,
                new SampleRequestFunc0<SDKResponse<VisitContext, SDKError>>(GET_FIRST_AVAILABLE_VISIT_CONTEXT) {
                    @Override
                    public Observable<SDKResponse<VisitContext, SDKError>> go() {
                        return observableService.getVisitContext(consumer, specialty);
                    }
                },
                new SampleResponseAction2<VisitContext, SDKError, SDKResponse<VisitContext, SDKError>>(GET_FIRST_AVAILABLE_VISIT_CONTEXT) {
                    @Override
                    public void onSuccess(ServiceActivity serviceActivity, VisitContext visitContext) {
                        stop(GET_FIRST_AVAILABLE_VISIT_CONTEXT);
                        setVisitContext(visitContext);
                    }
                },
                new SampleFailureAction2(GET_FIRST_AVAILABLE_VISIT_CONTEXT)
        );

        restartableLatestCache(FIND_FUTURE_AVAILABLE_PROVIDERS,
                new SampleRequestFunc0<SDKResponse<AvailableProviders, SDKError>>(FIND_FUTURE_AVAILABLE_PROVIDERS) {
                    @Override
                    public Observable<SDKResponse<AvailableProviders, SDKError>> go() {
                        return observableService.findAvailableProviders(consumer, service, appointmentsDate, languageSpoken);
                    }
                },
                new SampleResponseAction2<AvailableProviders, SDKError, SDKResponse<AvailableProviders, SDKError>>(FIND_FUTURE_AVAILABLE_PROVIDERS) {
                    @Override
                    public void onSuccess(ServiceActivity serviceActivity, AvailableProviders availableProviders) {
                        stop(FIND_FUTURE_AVAILABLE_PROVIDERS);
                        setFutureAvailableProviders(availableProviders);
                    }
                },
                new SampleFailureAction2(FIND_FUTURE_AVAILABLE_PROVIDERS)
        );

    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(ServiceActivity view) {
        super.onTakeView(view);
        start(GET_SERVICE); // for this activity, refresh often
    }

    // this gets called when the activity first gets created
    public void setServiceInfo(final PracticeInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    private void setService(final Practice service) {
        this.service = service;
        view.setTitle(service.getName());
        view.setPracticeInfo(service.getWelcomeMessage());
    }

    private void setPastProviders(final ArrayList<Provider> pastProviders) {
        ArrayList<ProviderInfo> finalList = new ArrayList<>();

        for (ProviderInfo provider : pastProviders) {
            PracticeInfo practice = provider.getPracticeInfo();
            if (practice != null && service.equals(practice)) {
                finalList.add(provider);
            }
        }

        if (!finalList.isEmpty()) {
            this.pastProviders = finalList;
            if (view != null) {
                view.invalidateOptionsMenu();
            }
        }
    }

    private void setProviders(final ArrayList<ProviderInfo> providers) {
        this.providers = providers;
        if (view != null && showAllProviders) {
            view.setAvailableProvidersListItems(providers);
        }
    }

    private void setSpecialties(final ArrayList<OnDemandSpecialty> specialties) {
        this.specialties = specialties;
        if (view != null && specialties != null && specialties.size() > 0) {
            showFirstAvailable = true;
        }
        view.setShowFirstAvailable(showFirstAvailable);
    }

    public boolean showFirstAvailable() {
        return showFirstAvailable;
    }

    public void refreshService() {
        service = null;
        providers = null;
        specialties = null;
        start(GET_SERVICE);
    }

    public List<ProviderInfo> getProviders() {
        return this.providers;
    }

    public List<ProviderInfo> getPastProviders() {
        return this.pastProviders;
    }

    public List<OnDemandSpecialty> getSpecialties() {
        return this.specialties;
    }

    public void getFirstAvailableVisitContext() {
        this.specialty = specialties.get(0);
        start(GET_FIRST_AVAILABLE_VISIT_CONTEXT);
    }

    public List<Language> getLanguages() {
        return awsdk.getLanguages();
    }

    public Language getLanguageSpoken() {
        return languageSpoken;
    }

    public void setLanguageSpoken(final Language languageSpoken) {
        this.languageSpoken = languageSpoken;
        if (service.isShowAvailableNow()) {
            start(FIND_PROVIDERS);
        }
        if (service.isShowScheduling()) {
            start(FIND_FUTURE_AVAILABLE_PROVIDERS);
        }
    }

    // once we've fetched the visit context, tell the view
    public void setVisitContext(final VisitContext visitContext) {
        this.visitContext = visitContext;
        view.setVisitContext(visitContext);
    }

    public void changeProviderType() {
        showAllProviders = !showAllProviders;
        if (showAllProviders) {
            view.setAvailableProvidersListItems(providers);
            view.setAvailableProvidersListHeader(R.string.other_providers);
        }
        else {
            view.setAvailableProvidersListItems(pastProviders);
            view.setAvailableProvidersListHeader(R.string.past_providers);
        }
    }

    public boolean getShowAllProviders() {
        return showAllProviders;
    }

    // there's a slight crossing of responsibilities here in that we're passing UI componentry into the presenter, but it's a necessary evil
    public void loadProviderImage(final ProviderInfo providerInfo,
                                  final ImageView imageView,
                                  final Drawable placeHolder) {
        // check image exists
        if (providerInfo.hasImage()) {
            // preferred method for loading provider image
            awsdk.getPracticeProvidersManager()
                    .newImageLoader(providerInfo, imageView, ProviderImageSize.SMALL)
                    .placeholder(placeHolder)
                    .build()
                    .load();
        }
    }

    public void setFutureAvailableProviders(AvailableProviders futureAvailableProviders) {
        this.futureAvailableProviders = futureAvailableProviders;
        if (futureAvailableProviders.getDate() != null) { // if this comes back null, remember the date we TRIED to use
            appointmentsDate = futureAvailableProviders.getDate();
        }
        view.setFutureAvailableProviders(futureAvailableProviders);
    }

    public AvailableProviders getFutureAvailableProviders() {
        return futureAvailableProviders;
    }

    public Date getAppointmentsDate() {
        return appointmentsDate;
    }

    public void setAppointmentsDate(final Date date) {
        appointmentsDate = date;
        start(FIND_FUTURE_AVAILABLE_PROVIDERS);
    }

    // in this sample implementation we're making the assumption that we're always going to show
    // at least one of the pages (tabs).  so we're only asking about "available now" from the view
    public boolean isShowAvailableNow() {
        return service.isShowAvailableNow();
    }

    public int getPageCount() {
        // so we have separate flags for showing each possible page (tab) in the ui
        // if we're showing both, return page count of 2 otherwise 1
        return (service == null ||
                (service.isShowAvailableNow() && service.isShowScheduling())) ? 2 : 1;
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

}
