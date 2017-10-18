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

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.visit.Appointment;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for ThirdPartyLoginActivity
 * <p>
 * As with the ThirdPartyLoginActivity, this Presenter will provide comments to explain the
 * patterns used across the Presenters in the sample app
 *
 * @since AWSDK 2.1
 */
public class ThirdPartyLoginPresenter extends BaseLoginPresenter<ThirdPartyLoginActivity> {

    // the IDs represent the set of requests that are submitted by this presenter
    // the int values are intended to be unique across the entire application, but that is mostly
    // for logging purposes.  the request id is logged, so uniqueness is quite helpful there.
    // however, technically, they only need to be unique per class.
    private static final int CONSUMER = 632;
    private static final int GET_APPOINTMENT = 633;

    // constants
    private static final String PREF_SAVED_AUTH_TOKEN = "saved_auth_token";

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
    String authToken;

    @State
    boolean rememberMe;
    @State
    com.americanwell.sdk.entity.State state; // had to use fully qualified class name to avoid conflict with icepick.State
    @State
    Authentication authentication;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // initialize and authenticate
        restartableLatestCache(INITIALIZE_FOR_LOGIN,
                // all calls use the sample func0 implementation which provides uniform handling
                // for all requests
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(INITIALIZE_FOR_LOGIN) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        // the sample app wraps every SDK call with an Rx Observable and provides
                        // an adapter infrastructure to handle that.  the SDK does not support Rx
                        // natively.  The ObservableService is where all the wrapper methods live.
                        // Nucleus provides native Rx handling, and the app takes advantage of this
                        // and the automated handling of the asynchronous calls inside the quirky
                        // Android lifecycle

                        // this call (and the others) does the basic initialization for the SDK
                        // this is a prerequisite for all subsequent calls.
                        return observableService.initializeSdk(baseServiceUrl, sdkKey, launchUri);
                    }
                },
                // all calls use this action2 implementation, or one of it's derivatives, to provide
                // uniform response handling. this handling is for both success and graceful error response
                // handling
                // the action2 implementation has default handling for the error cases, but the onSuccess()
                // handler must be custom implemented for each call
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(INITIALIZE_FOR_LOGIN) {
                    @Override
                    public void onSuccess(ThirdPartyLoginActivity activity, Void aVoid) {
                        // in this case, once we've initialized, now we can authenticate
                        start(AUTHENTICATE);
                    }
                },
                // all of these calls use this failure implementation
                // it provides common handling for failure cases
                new SampleFailureAction2(INITIALIZE_FOR_LOGIN));

        // authenticate
        restartableLatestCache(AUTHENTICATE,
                new SampleRequestFunc0<SDKResponse<Authentication, SDKError>>(AUTHENTICATE) {
                    @Override
                    public Observable<SDKResponse<Authentication, SDKError>> go() {
                        return observableService.thirdPartyAuthenticate(authToken);
                    }
                },
                new SampleResponseAction2<Authentication, SDKError, SDKResponse<Authentication, SDKError>>(AUTHENTICATE) {
                    @Override
                    public void onSuccess(ThirdPartyLoginActivity activity, Authentication a) {
                        handleAuthenticationSuccess(a);
                    }
                },
                new SampleFailureAction2(AUTHENTICATE));

        // fetch consumer
        restartableLatestCache(CONSUMER,
                new SampleRequestFunc0<SDKResponse<Consumer, SDKError>>(CONSUMER) {
                    @Override
                    public Observable<SDKResponse<Consumer, SDKError>> go() {
                        // fetch the consumer that's been authenticated
                        // the sdk requires the fetched consumer object for most, if not all,
                        // authenticated operations
                        return observableService.getConsumer(authentication);
                    }
                },
                new SampleResponseAction2<Consumer, SDKError, SDKResponse<Consumer, SDKError>>(CONSUMER) {
                    @Override
                    public void onSuccess(ThirdPartyLoginActivity activity, Consumer c) {
                        setConsumer(c);
                    }
                },
                new SampleFailureAction2(CONSUMER));

        // if launching with an appointment id param, fetch the appointment
        restartableLatestCache(GET_APPOINTMENT,
                new SampleRequestFunc0<SDKResponse<Appointment, SDKError>>(GET_APPOINTMENT) {
                    @Override
                    public Observable<SDKResponse<Appointment, SDKError>> go() {
                        // if we've determined that we have an appointment id in our launch params
                        // we fetch that appointment here.
                        // we pass in the launch params to the call, which will be used to extract
                        // the appointment id
                        return observableService.getAppointment(consumer, awsdk.getLaunchParams());
                    }
                },
                new SampleResponseAction2<Appointment, SDKError, SDKResponse<Appointment, SDKError>>(GET_APPOINTMENT) {
                    @Override
                    public void onSuccess(ThirdPartyLoginActivity activity, Appointment appointment) {
                        stop(GET_APPOINTMENT);
                        activity.setAppointment(appointment);
                    }
                },
                new SampleFailureAction2(GET_APPOINTMENT));
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
    public void onTakeView(ThirdPartyLoginActivity view) {
        super.onTakeView(view);
        final boolean showServerFields = view.getResources().getBoolean(R.bool.sdksample_show_server_fields_on_login);
        view.setAuthToken(authToken);
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
        if (validateLogin()) {
            start(INITIALIZE_FOR_LOGIN);
        }
    }

    private boolean validateLogin() {
        boolean authTokenMissing = TextUtils.isEmpty(authToken);
        view.setAuthTokenMissing(authTokenMissing);
        return !authTokenMissing;
    }

    // the sdk is stateless, so there's not much to do for "logout".
    // there is the ability to "clear authentication" for a Consumer
    // which means if that consumer is retained but not authenticated, it cannot
    // be used for sdk calls without re-authenticating
    public void logout() {
        if (consumer != null) {
            awsdk.clearAuthentication(consumer);
        }
        if (awsdk.isInitialized()) {
            awsdk.getVisitManager().abandonGuestConference();
        }
    }

    // get auth token
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
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
