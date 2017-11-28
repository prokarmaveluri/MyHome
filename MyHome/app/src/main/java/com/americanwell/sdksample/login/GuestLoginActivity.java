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

import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.visit.GuestWaitingRoomActivity;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * A login screen that offers guest login via email/name.
 *
 * @since AWSDK 2.0
 */
@RequiresPresenter(GuestLoginPresenter.class)
// This annotation is required by Nucleus to associate the Activity with it's Presenter
public class GuestLoginActivity extends BaseSampleNucleusActivity<GuestLoginPresenter> {


    // the Bind annotation is provided by ButterKnife to simplify connecting the UI with the code
    @BindView(R.id.email)
    EditText mEmailView;
    @BindView(R.id.email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.name)
    EditText mNameView;
    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.login_form)
    View mLoginFormView;
    @BindView(R.id.email_sign_in_button)
    Button signInButton;

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
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this); // each activity has to do this independently
        setContentView(R.layout.activity_guest_login);

        if (savedInstanceState == null) {
            // initialize
            getPresenter().setBaseServiceUrl(getString(R.string.awsdkurl));
            getPresenter().setSDKKey(getString(R.string.awsdkkey));
        }

        getPresenter().setLaunchUri(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        getPresenter().logout();
        super.onBackPressed();
    }

    @OnEditorAction(R.id.name)
    public boolean onNameEditorAction(int id) {
        if ((id & EditorInfo.IME_MASK_ACTION) > 0) {
            login();
            return true;
        }
        return false;
    }

    // onclick handler for the sign in button
    // this simply makes a call to the presenter to do the heavy lifting
    @OnClick(R.id.email_sign_in_button)
    public void login() {
        getPresenter().login();
    }

    public void setGoToGuestWaitingRoom(String name, String email) {
        startActivity(GuestWaitingRoomActivity.makeIntent(this, name, email));
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


    @OnTextChanged(value = R.id.name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onNameChanged(final CharSequence value) {
        getPresenter().setName(value.toString().trim());
    }

    public void setName(final String name) {
        mNameView.setText(name);
    }


    // presenter uses this to trigger validation
    public void setEmailMissing(boolean bMissing) {
        setTextViewError(bMissing, emailLayout, R.string.login_email_address_warning);
    }

    public void setNameMissing(boolean bMissing) {
        setTextViewError(bMissing, nameLayout, R.string.login_name_warning);
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

