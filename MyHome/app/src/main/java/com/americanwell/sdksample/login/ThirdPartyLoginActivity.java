/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.Appointment;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.appointments.AppointmentActivity;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;


/**
 * A login screen that offers login via authToken.
 */
@RequiresPresenter(ThirdPartyLoginPresenter.class)
// This annotation is required by Nucleus to associate the Activity with it's Presenter
public class ThirdPartyLoginActivity extends BaseLoginActivity<ThirdPartyLoginPresenter> {


    // the Bind annotation is provided by ButterKnife to simplify connecting the UI with the code
    @BindView(R.id.auth_token)
    EditText mAuthTokenView;
    @BindView(R.id.auth_token_layout)
    TextInputLayout authTokenLayout;
    @BindView(R.id.third_party_sign_in_button)
    Button signInButton;

    // these two fields are dev-only helpers, which allow overriding of the server url before login
    // this is not typically something you'd offer in a production-quality application
    @BindView(R.id.server_url_layout)
    View serverUrlLayout;
    @BindView(R.id.server_url_text)
    EditText serverUrlText;
    @BindView(R.id.sdk_key_layout)
    View sdkKeyLayout;
    @BindView(R.id.sdk_key_text)
    EditText sdkKeyText;
    @BindView(R.id.version)
    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // the BaseSampleNucleusActivity handles a lot of the boilerplate stuff
        // each implementing activity has to, at a minimum, have these three lines:
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this); // each activity has to do this independently
        setContentView(R.layout.activity_third_party_login);

        if (savedInstanceState == null) {
            // initialize
            getPresenter().setBaseServiceUrl(getString(R.string.awsdkurl));
            getPresenter().setSDKKey(getString(R.string.awsdkkey));
            // temp defaults
            getPresenter().setAuthToken(getString(R.string.third_party_auth_token));
        }

        // it's possible to launch the app from a link with some params in the URI
        // for example, a TelehealthNow invitation to an Appointment
        getPresenter().setLaunchUri(getIntent().getData()); // we want to always do this because the app could potentially be
        // re-launched from an email link when it's already running and
        // we'll want to update the launch uri
    }

    @Override
    public void onBackPressed() {
        getPresenter().logout(); // otherwise logout
        super.onBackPressed();
    }

    // This is an example of a Butterknife annotation for an action handler
    // in this case, we're providing the implementation for the "submit" button on the
    // keyboard for login from the password field
    @OnEditorAction(R.id.auth_token)
    public boolean onPasswordEditorAction(int id) {
        if ((id & EditorInfo.IME_MASK_ACTION) > 0) {
            login();
            return true;
        }
        return false;
    }

    // onclick handler for the sign in button
    // this simply makes a call to the presenter to do the heavy lifting
    @OnClick(R.id.third_party_sign_in_button)
    public void login() {
        getPresenter().login();
    }


    public void setAuthToken(final String authToken) {
        mAuthTokenView.setText(authToken);
    }

    // The set of OnTextChanged listeners will keep the data in the presenter up-to-date with what's
    // going on in the UI
    @OnTextChanged(value = R.id.auth_token, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAuthTokenChanged(final CharSequence value) {
        getPresenter().setAuthToken(value.toString().trim());
    }

    /**
     * presenter will call this when a consumer is fetched
     */
    public void setConsumerRetrieved() {
        goHome(); // ok, we're ready to start!
    }

    // if we're launching from a telehealth now URL, we fetch the appointment
    // once it's fetched, we set it here to move on.
    public void setAppointment(final Appointment appointment) {
        startActivity(AppointmentActivity.makeIntent(this, appointment, true));
        finish();
    }

    // presenter uses this to trigger validation
    public void setAuthTokenMissing(boolean bMissing) {
        setTextViewError(bMissing, authTokenLayout, R.string.login_auth_token_warning);
    }

    // developer util - tell the layout to show the server url field
    public void setShowServerUrl(boolean show) {
        serverUrlLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setServerUrl(final String serverUrl) {
        serverUrlText.setText(serverUrl);
    }

    @OnTextChanged(value = R.id.server_url_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onServerUrlChanged(final CharSequence value) {
        getPresenter().setBaseServiceUrl(value.toString().trim());
    }

    public void setShowVersion(boolean show) {
        version.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setShowSDKKey(boolean show) {
        sdkKeyLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setSDKKey(final String sdkKey) {
        sdkKeyText.setText(sdkKey);
    }

    @OnTextChanged(value = R.id.sdk_key_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSDKKeyChanged(final CharSequence value) {
        getPresenter().setSDKKey(value.toString().trim());
    }

}

