/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for GuestLoginActivity
 *
 * @since AWSDK 2.0
 */
public class GuestLoginPresenter extends BaseSampleNucleusRxPresenter<GuestLoginActivity> {

    // the IDs represent the set of requests that are submitted by this presenter
    // the int values are intended to be unique across the entire application, but that is mostly
    // for logging purposes.  the request id is logged, so uniqueness is quite helpful there.
    // however, technically, they only need to be unique per class.
    private static final int INITIALIZE_FOR_GUEST = 620;

    // the Inject annotation is provided by dagger2.  see SamplePresenterComponent and SamplePresenterModule
    @Inject
    SharedPreferences sharedPreferences;

    // the State annotation is provided by IcePick and any member tagged with it will be automatically
    // persisted and restored when appropriate
    // all of the SDK data container entity classes are Parcelable
    @State
    String baseServiceUrl;
    @State
    String sdkKey;
    @State
    Uri launchUri;
    @State
    String email;
    @State
    String name;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // initialize and go to registration
        restartableLatestCache(INITIALIZE_FOR_GUEST,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(INITIALIZE_FOR_GUEST) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.initializeSdk(baseServiceUrl, sdkKey, launchUri);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(INITIALIZE_FOR_GUEST) {
                    @Override
                    public void onSuccess(GuestLoginActivity activity, Void aVoid) {
                        stop(INITIALIZE_FOR_GUEST);
                        activity.setGoToGuestWaitingRoom(name, email);
                    }
                },
                new SampleFailureAction2(INITIALIZE_FOR_GUEST));

    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    // onTakeView() is a Nucleus lifecycle event that is tied into the activity's onResume() lifecycle
    // however, it is "smart" and is not called on EVERY onResume(), but only when necessary.
    // for example, it is called on rotation, but not when the activity is popped back to the top of the
    // stack
    @Override
    public void onTakeView(GuestLoginActivity view) {
        super.onTakeView(view);
        final boolean showServerFields = view.getResources().getBoolean(R.bool.sdksample_show_server_fields_on_login);
        view.setEmail(email);
        view.setName(name);
        view.setShowServerUrl(showServerFields);
        view.setServerUrl(baseServiceUrl);
        view.setShowVersion(showServerFields);
        view.setShowSDKKey(showServerFields);
        view.setSDKKey(sdkKey);
    }

    protected boolean shouldBeInitialized() {
        return false;
    }

    protected boolean shouldHaveConsumer() {
        return false;
    }

    // initiate the login process
    public void login() {
        if (validateGuest()) {
            start(INITIALIZE_FOR_GUEST);
        }
    }

    // validation
    private boolean validateGuest() {
        boolean emailMissing = TextUtils.isEmpty(email);
        boolean nameMissing = TextUtils.isEmpty(name);
        view.setEmailMissing(emailMissing);
        view.setNameMissing(nameMissing);
        return !(emailMissing || nameMissing);
    }

    // the sdk is stateless, so there's not much to do for "logout".
    // there is the ability to "clear authentication" for a Consumer
    // which means if that consumer is retained but not authenticated, it cannot
    // be used for sdk calls without re-authenticating
    public void logout() {
        if (awsdk.isInitialized()) {
            awsdk.getVisitManager().abandonGuestConference();
        }
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setBaseServiceUrl(String baseServiceUrl) {
        this.baseServiceUrl = baseServiceUrl;
    }

    public void setSDKKey(String sdkKey) {
        this.sdkKey = sdkKey;
    }

    // take the intent's launch uri and tell the SDK instance about it
    // this can happen more than once, if the app is open but relaunched from an intent link somewhere
    public void setLaunchUri(Uri launchUri) {
        this.launchUri = launchUri;
        awsdk.updateLaunchParams(launchUri);
    }
}
