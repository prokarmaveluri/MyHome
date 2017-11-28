/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.dagger;

import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.FileUtils;
import com.americanwell.sdksample.util.LocaleUtils;
import com.americanwell.sdksample.util.SampleUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Sample Dagger 2 Module for Activites
 */
@Module
public class SampleActivityModule {

    SampleApplication mApp;

    public SampleActivityModule(SampleApplication app) {
        mApp = app;
    }

    @Provides
    @Singleton
    SampleUtils provideSampleUtils() {
        return new SampleUtils();
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
