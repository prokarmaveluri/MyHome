/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.logging;

import android.support.annotation.Nullable;
import android.util.Log;

import com.americanwell.sdk.logging.AWSDKLogger;

/**
 * This is a sample {@link AWSDKLogger} which shows how to integrate
 * the American Well SDK logging mechanism with Crashlytics
 * <p>
 * The code below is commented out because we do not include Crashlytics support in the Sample
 * Application directly.  However, if you are a Crashlytics customer,  you can use their tools in
 * Android Studio to add Crashlytics support to your application.
 * <p>
 * Once their changes have been made to your Gradle scripts, application manifest, etc, you can
 * uncomment the lines below and use this logger.
 * <p>
 * To integrate the logger into your application, set the custom logger onto your AWSDK instance:
 * {@link com.americanwell.sdk.AWSDK#setCustomLogger(AWSDKLogger)} and provide "new
 * SampleCrashlyticsLogger()" as the param
 * <p>
 * And that's it.  This call will chain the SampleCrashlyticsLogger to the provided DefaultLogger
 * and any log messages will be posted to it, based on the value of {@link #setPriority(int)}
 */
public class SampleCrashlyticsLogger implements AWSDKLogger {

    private int priority = Log.INFO;

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setLogCategories(@Nullable @AWSDKLogCategory String[] logCategories) {
        // implement here
    }

    @Nullable
    @Override
    public String[] getLogCategories() {
        // implement here
        return null;
    }

    @Override
    public void log(int priority,
                    @Nullable String tag,
                    @Nullable String message,
                    @Nullable Throwable throwable) {
        log(priority, AWSDKLogger.LOG_CATEGORY_DEFAULT, tag, message, throwable);
    }

    @Override
    public void log(int priority, @Nullable @AWSDKLogCategory String category, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        if (this.priority <= priority) {
    /*
            Crashlytics.log(priority, tag, message);
            if (throwable != null) {
                Crashlytics.logException(throwable);
            }
    */
        }
    }

}