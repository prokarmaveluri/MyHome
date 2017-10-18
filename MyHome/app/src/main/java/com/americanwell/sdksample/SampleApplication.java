/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.AWSDKFactory;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.logging.AWSDKLogger;
import com.americanwell.sdksample.dagger.DaggerSampleActivityComponent;
import com.americanwell.sdksample.dagger.DaggerSamplePresenterComponent;
import com.americanwell.sdksample.dagger.SampleActivityComponent;
import com.americanwell.sdksample.dagger.SampleActivityModule;
import com.americanwell.sdksample.dagger.SamplePresenterComponent;
import com.americanwell.sdksample.dagger.SamplePresenterModule;
import com.facebook.stetho.Stetho;
import com.livefront.bridge.Bridge;
import com.livefront.bridge.SavedStateHandler;

import icepick.Icepick;

import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_AUDIO;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_DEFAULT;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_MEDIA;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_NETWORKING;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_PERMISSIONS;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_VIDEO;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_VISIT;

/**
 * Sample Application class
 * provides a concrete implementation of AWSDKApplication
 * also acts as static container for Dagger component injection classes
 */
public class SampleApplication {

    private static SampleApplication INSTNCE = new SampleApplication();
    private final static String LOG_TAG = SampleApplication.class.getName();

    private static SamplePresenterComponent mPresenterComponent; // Dagger component for presenters
    private static SampleActivityComponent mActivityComponent; // Dagger component for activities

    private static AWSDK awsdk = null;
    private static Context context = null;

    private SampleApplication() {

    }

    public static SampleApplication getInstance() {
        return INSTNCE;
    }

    public void initVisit(Context context) {
        Stetho.initializeWithDefaults(context); // http://facebook.github.io/stetho/

        // https://github.com/livefront/bridge
        // The use of the "Bridge" library is to offer a simple way to persist activity state to SharedPreferences
        // rather than in memory.  We've found that in certain circumstances, the saved state Bundles can be very
        // large and cause TransactionTooLargeExceptions.  The use of Bridge avoids this.
        // However, for a production-quality application please note that any data that is persisted to the device
        // should be done so with security in mind.  SharedPreferences is NOT a secure storage mechanism without the
        // use of additional encryption.
        Bridge.initialize(context, new SavedStateHandler() {
            @Override
            public void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {
                Icepick.saveInstanceState(target, state);
            }

            @Override
            public void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {
                Icepick.restoreInstanceState(target, state);
            }
        });

        try {
            awsdk = AWSDKFactory.getAWSDK(context);
            awsdk.getDefaultLogger().setPriority(Log.DEBUG); // set log level to debug

            // Set the categories for the logs you want displayed. Setting this to null will allow all categories to be displayed.
            @AWSDKLogger.AWSDKLogCategory
            String[] categories = {LOG_CATEGORY_DEFAULT, LOG_CATEGORY_VIDEO, LOG_CATEGORY_AUDIO,
                    LOG_CATEGORY_PERMISSIONS, LOG_CATEGORY_MEDIA, LOG_CATEGORY_VISIT, LOG_CATEGORY_NETWORKING};
            awsdk.getDefaultLogger().setLogCategories(categories);

            // here's a good place to add additional loggers.  see SampleCrashlyticsLogger for an example
            mPresenterComponent = DaggerSamplePresenterComponent
                    .builder()
                    .samplePresenterModule(new SamplePresenterModule(this))
                    .build();

            mActivityComponent = DaggerSampleActivityComponent
                    .builder()
                    .sampleActivityModule(new SampleActivityModule(this))
                    .build();
        } catch (AWSDKInstantiationException e) {
            Log.e(LOG_TAG, "Unable to instantiate AWSDK instance", e);//using android log here b/c defaultlogger won't be available
        }
    }

    /**
     * get the presenter component.  each presenter must get this and call an inject() method
     *
     * @return
     */
    public static SamplePresenterComponent getPresenterComponent() {
        return mPresenterComponent;
    }

    /**
     * get the activity component.  each activity must get this and call an inject() method
     *
     * @return
     */
    public static SampleActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    /**
     * This should be the only place you get the {@link AWSDK} instance object.
     *
     * @return
     */
    public AWSDK getAWSDK() {
        return awsdk;
    }

    public Context getContext() {
        return context;
    }

    public static void setContext(Context ctx) {
        context = ctx;
    }
}
