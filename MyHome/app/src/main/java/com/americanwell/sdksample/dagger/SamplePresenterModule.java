/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.dagger;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.PracticeProvidersManager;
import com.americanwell.sdk.manager.SecureMessageManager;
import com.americanwell.sdk.manager.VisitManager;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.ObservableService;
import com.americanwell.sdksample.util.FileUtils;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.StateUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Sample Dagger 2 Module for Presenters
 */
@Module
public class SamplePresenterModule {
    SampleApplication mApp;

    public SamplePresenterModule(SampleApplication app) {
        mApp = app;
    }

    @Provides
    @Singleton
    SampleApplication provideSampleApplication() {
        return mApp;
    }

    @Provides
    @Singleton
    AWSDK provideAWSDK() {
        return mApp.getAWSDK();
    }

    @Provides
    @Singleton
    ConsumerManager provideConsumerManager() {
        return mApp.getAWSDK().getConsumerManager();
    }

    @Provides
    @Singleton
    PracticeProvidersManager providePracticeProvidersManager() {
        return mApp.getAWSDK().getPracticeProvidersManager();
    }

    @Provides
    @Singleton
    SecureMessageManager provideSecureMessageManager() {
        return mApp.getAWSDK().getSecureMessageManager();
    }

    @Provides
    @Singleton
    VisitManager provideVisitManager() {
        return mApp.getAWSDK().getVisitManager();
    }

    @Provides
    @Singleton
    ObservableService provideObservableFactory() {
        return new ObservableService(mApp.getAWSDK());
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApp.getContext());
    }

    @Provides
    @Singleton
    StateUtils providesStateUtils() {
        return new StateUtils();
    }

    @Provides
    @Singleton
    FileUtils provideFileUtils() {
        return new FileUtils();
    }

    @Provides
    LocaleUtils provideLocaleUtils() {
        return new LocaleUtils(
                mApp.getAWSDK().getPreferredLocale(),
                mApp.getContext().getResources()
        );
    }

}
