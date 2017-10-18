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
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.registration.RegistrationActivity;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * A login screen that offers login via email/password.
 * <p/>
 * <p/>
 * As this is the launch activity, this one will have the most comments about the patterns used
 * across the sample app.
 * <p/>
 * The sample app is using a third-party MVP library called Nucleus.  This was chosen for it's
 * relative simplicity and it's native support for RXAndroid.  There are, of course, no specific
 * dependencies of the AWSDK on Nucleus or RXAndroid.
 */
@RequiresPresenter(LoginPresenter.class)
// This annotation is required by Nucleus to associate the Activity with it's Presenter
public class LoginActivity extends BaseLoginActivity<LoginPresenter> {


    // the Bind annotation is provided by ButterKnife to simplify connecting the UI with the code
    @BindView(R.id.email)
    EditText mEmailView;
    @BindView(R.id.login_email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.login_form)
    View mLoginFormView;
    @BindView(R.id.email_sign_in_button)
    Button signInButton;
    @BindView(R.id.register_button)
    Button registerButton;
    @BindView(R.id.remember_me_check_box)
    Switch rememberMeCheckBox;
    @BindView(R.id.login_assistance)
    View loginAssistance;

    // these fields are dev-only helpers, which allow overriding of the server url before login
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        SampleApplication.getActivityComponent().inject(this); // each activity has to do this independently
        setContentView(R.layout.activity_login_awsdk);

        if (savedInstanceState == null) {
            // initialize
            getPresenter().setBaseServiceUrl(getString(R.string.awsdkurl));
            getPresenter().setSDKKey(getString(R.string.awsdkkey));
            getPresenter().getEmail();
        }
        // it's possible to launch the app from a link with some params in the URI
        // for example, a TelehealthNow invitation to an Appointment
        getPresenter().setLaunchUri(getIntent().getData()); // we want to always do this because the app could potentially be
        // re-launched from an email link when it's already running and
        // we'll want to update the launch uri
    }

    @Override
    protected void onResume() {
        super.onResume();
        login();
    }

    // This is an example of a Butterknife annotation for an action handler
    // in this case, we're providing the implementation for the "submit" button on the
    // keyboard for login from the password field
    @OnEditorAction(R.id.password)
    public boolean onPasswordEditorAction(int id) {
        if ((id & EditorInfo.IME_MASK_ACTION) > 0) {
            login();
            return true;
        }
        return false;
    }

    // onclick handler for the sign in button
    // this simply makes a call to the presenter to do the heavy lifting
//    @OnClick(R.id.email_sign_in_button)
    public void login() {
        setEmail("cmajji@mailinator.com");
        setPassword("Pass123*");
        getPresenter().login();
    }

    // onclick handler for the "need help logging in" link
    @OnClick(R.id.login_assistance)
    public void onLoginAssistanceClick() {
        getPresenter().loginAssistance();
    }

    // onclick handler for the registration (aka enrollment) button
    @OnClick(R.id.register_button)
    public void onRegisterClick() {
        getPresenter().register();
    }

    // once the presenter has initialized for registration, this will be called to start the next activity
    public void setGoToRegistration() {
        startActivity(RegistrationActivity.makeIntent(this));
    }

    // once the presenter has initialized for login assistance, this will be called to start the next activity
    public void setGoToLoginAssistance() {
        startActivity(LoginAssistanceActivity.makeIntent(this));
    }

    /**
     * the presenter will call this upon fetching a stored email
     *
     * @param email
     */
    public void setEmail(final String email) {
        mEmailView.setText(email);
    }

    // The set of OnTextChanged listeners will keep the data in the presenter up-to-date with what's
    // going on in the UI
    @OnTextChanged(value = R.id.email, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onEmailChanged(final CharSequence value) {
        getPresenter().setEmail(value.toString().trim());
    }

    public void setPassword(final String password) {
        mPasswordView.setText(password);
    }

    @OnTextChanged(value = R.id.password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPasswordChanged(final CharSequence value) {
        getPresenter().setPassword(value.toString().trim());
    }


    // the presenter will call this to set the state of the "remember me" switch
    public void setRememberMe(final boolean rememberMe) {
        rememberMeCheckBox.setChecked(rememberMe);
        if (rememberMe) {
            mPasswordView.requestFocus();
        }
    }

    @OnCheckedChanged(R.id.remember_me_check_box)
    public void onRememberMeClick(boolean isChecked) {
        getPresenter().setRememberMe(isChecked);
    }

    // presenter uses this to trigger validation
    public void setEmailMissing(boolean bMissing) {
        setTextViewError(bMissing, emailLayout, R.string.login_email_address_warning);
    }

    // presenter uses this to trigger validation
    public void setPasswordMissing(boolean bMissing) {
        setTextViewError(bMissing, passwordLayout, R.string.login_email_password_warning);
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

