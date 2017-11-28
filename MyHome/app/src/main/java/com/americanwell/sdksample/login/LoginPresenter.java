/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import icepick.State;
import rx.Observable;

/**
 * Presenter for LoginActivity
 * <p/>
 * As with the LoginActivity, this Presenter will provide comments to explain the patterns
 * used across the Presenters in the sample app
 */
public class LoginPresenter extends BaseLoginPresenter<LoginActivity> {

    private static final String LOG_TAG = LoginPresenter.class.getName();

    // the IDs represent the set of requests that are submitted by this presenter
    // the int values are intended to be unique across the entire application, but that is mostly
    // for logging purposes.  the request id is logged, so uniqueness is quite helpful there.
    // however, technically, they only need to be unique per class.
    private static final int INITIALIZE_FOR_ASSISTANCE = 614;
    private static final int INITIALIZE_FOR_REGISTER = 615;


    // the State annotation is provided by IcePick and any member tagged with it will be automatically
    // persisted and restored when appropriate
    // all of the SDK data container entity classes are Parcelable
    @State
    String baseServiceUrl;
    @State
    String sdkKey;
    @State
    Uri launchUri;


    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // there are 3 different initialize calls here... the only difference is what they do onSuccess()
        // it's possible to have one initialize that checks state and figures out what to do.
        // the reason for this is to avoid calling the initialize() upon activity launch, as it will take some time
        // and launch the progress indeterminate bar.  this way, the activity loads fast, and the initialization won't happen
        // until the user takes some action.

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
                    public void onSuccess(LoginActivity activity, Void aVoid) {
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
                        // the sample app always attempts to login with email and password
                        // in the sample we always set the consumer auth key equal to the email address
                        // this is a simplification for the sample, which may or may not be production quality
                        // it depends on the requirements of the individual customer.  keep that in mind.
                        // also note that there's an alternative authentication method that simply accepts the
                        // consumer auth key, once it's been assigned to a consumer on the server
                        // the sample app does not demonstrate that mechanism
                        return observableService.authenticate(email, password);
                    }
                },
                new SampleResponseAction2<Authentication, SDKError, SDKResponse<Authentication, SDKError>>(AUTHENTICATE) {
                    @Override
                    public void onSuccess(LoginActivity activity, Authentication a) {
                        // before we move on clear the password for security reasons
                        view.setPassword(null);
                        handleAuthenticationSuccess(a);
                    }
                },
                new SampleFailureAction2(AUTHENTICATE));


        // initialize and go to login assistance
        restartableLatestCache(INITIALIZE_FOR_ASSISTANCE,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(INITIALIZE_FOR_ASSISTANCE) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        // if the user clicks on "need help logging in" we still need to
                        // initialize the SDK to get access to the server
                        return observableService.initializeSdk(baseServiceUrl, sdkKey, launchUri);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(INITIALIZE_FOR_ASSISTANCE) {
                    @Override
                    public void onSuccess(LoginActivity activity, Void aVoid) {
                        stop(INITIALIZE_FOR_ASSISTANCE);
                        activity.setGoToLoginAssistance();
                    }
                },
                new SampleFailureAction2(INITIALIZE_FOR_ASSISTANCE));

        // initialize and go to registration
        restartableLatestCache(INITIALIZE_FOR_REGISTER,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(INITIALIZE_FOR_REGISTER) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        // same thing here, we initialize before we go to registration
                        return observableService.initializeSdk(baseServiceUrl, sdkKey, launchUri);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(INITIALIZE_FOR_REGISTER) {
                    @Override
                    public void onSuccess(LoginActivity activity, Void aVoid) {
                        stop(INITIALIZE_FOR_REGISTER);
                        activity.setGoToRegistration();
                    }
                },
                new SampleFailureAction2(INITIALIZE_FOR_REGISTER));


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
    public void onTakeView(LoginActivity view) {
        super.onTakeView(view);
        final boolean showServerFields = view.getResources().getBoolean(R.bool.sdksample_show_server_fields_on_login);
        view.setEmail(email);
        view.setPassword(password);
        view.setRememberMe(rememberMe);
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
        boolean emailMissing = TextUtils.isEmpty(email);
        boolean passwordMissing = TextUtils.isEmpty(password);
        view.setEmailMissing(emailMissing);
        view.setPasswordMissing(passwordMissing);
        return !(emailMissing || passwordMissing);
    }


    // Start the login assistance process
    public void loginAssistance() {
        start(INITIALIZE_FOR_ASSISTANCE);
    }

    // Start the registration process
    public void register() {
        start(INITIALIZE_FOR_REGISTER);
    }




    // if the "remember me" switch has been set to TRUE we will save the email (username)
    // to sharedpreferences
    // upon launch, we will fetch the email address.  if we fetch it we will also set the
    // switch to true... using the existence of the persisted email as a toggle
    public String getEmail() {
        if (email == null) {
            email = sharedPreferences.getString(PREF_SAVED_USERNAME, null);
            if (email != null) {
                setRememberMe(true);
            }
        }
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
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
