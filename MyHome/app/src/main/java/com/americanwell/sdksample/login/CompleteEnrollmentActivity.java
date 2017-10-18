/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.manager.ValidationConstants;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.SampleNamedNothingSelectedSpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * This activity is displayed when the user has not completed all the necessary fields for
 * enrollment. It is used as a means to collect the final data.
 */
@RequiresPresenter(CompleteEnrollmentPresenter.class)
public class CompleteEnrollmentActivity extends BaseLoginActivity<CompleteEnrollmentPresenter> {
    @BindView(R.id.email_address_edit_text)
    EditText emailAddressEditText;
    @BindView(R.id.email_confirm_edit_text)
    EditText emailConfirmEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.email_address_layout)
    TextInputLayout emailAddressLayout;
    @BindView(R.id.email_confirm_layout)
    TextInputLayout emailConfirmLayout;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.state_spinner)
    Spinner stateSpinner;
    @BindView(R.id.country_spinner)
    Spinner countrySpinner;
    @BindView(R.id.service_agreement_check_box)
    CheckBox serviceAgreementCheckBox;
    @BindView(R.id.service_agreement_check_box_layout)
    TextInputLayout serviceAgreementCheckBoxLayout;
    @BindView(R.id.service_agreement_link)
    View serviceAgreementLink;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private AlertDialog serviceAgreementDialog;

    @BindString(R.string.registration_email_confirm_error)
    String emailConfirmError;
    @BindString(R.string.validationreason_field_invalid_format)
    String emailInvalidFormatError;

    private static final String EXTRA_AUTHENTICATION = "authentication";
    private static final String EXTRA_COMPLETE_ENROLLMENT_EMAIL = "completeEnrollmentEmail";
    private static final String EXTRA_COMPLETE_ENROLLMENT_LOCATION = "completeEnrollmentLocation";

    private SampleNamedNothingSelectedSpinnerAdapter stateAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter countryAdapter;

    public static Intent makeIntent(@NonNull final Context context,
                                    @NonNull Authentication authentication,
                                    String completeEnrollmentEmail,
                                    @NonNull State completeEnrollmentLocation) {
        final Intent intent = new Intent(context, CompleteEnrollmentActivity.class);
        intent.putExtra(EXTRA_AUTHENTICATION, authentication);
        intent.putExtra(EXTRA_COMPLETE_ENROLLMENT_EMAIL, completeEnrollmentEmail);
        intent.putExtra(EXTRA_COMPLETE_ENROLLMENT_LOCATION, completeEnrollmentLocation);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // the BaseSampleNucleusActivity handles a lot of the boilerplate stuff
        // each implementing activity has to, at a minimum, have these three lines:
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this); // each activity has to do this independently
        setContentView(R.layout.activity_complete_enrollment);

        getPresenter().setAuthentication(
                (Authentication) getIntent().getParcelableExtra(EXTRA_AUTHENTICATION));
        String email = getIntent().getStringExtra(EXTRA_COMPLETE_ENROLLMENT_EMAIL);
        getPresenter().setEmail(email);
        getPresenter().setCompleteEnrollmentState(
                (State) getIntent().getParcelableExtra(EXTRA_COMPLETE_ENROLLMENT_LOCATION));


        if (!TextUtils.isEmpty(email)) {
            emailAddressEditText.setText(email);
        }
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(serviceAgreementDialog);
    }

    @OnClick(R.id.fab)
    public void completeEnrollment() {
        getPresenter().completeEnrollment();
    }


    // presenter uses this to trigger validation - this time in the dialog fragment
    public void setCompleteEnrollmentServiceAgreementAccepted(boolean acceptServiceAgreement) {
        setTextViewError(!acceptServiceAgreement,
                serviceAgreementCheckBoxLayout,
                R.string.complete_enrollment_service_agreement);
    }

    public void setEmail(final String value) {
        emailAddressEditText.setText(value);
    }

    @OnTextChanged(value = R.id.email_address_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onEmailChanged(final CharSequence value) {
        getPresenter().setEmail(value.toString().trim());
    }

    public void setConfirmEmail(final String value) {
        emailConfirmEditText.setText(value);
    }

    @OnTextChanged(value = R.id.email_confirm_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onConfirmEmailChanged(final CharSequence value) {
        getPresenter().setConfirmEmail(value.toString().trim());
    }

    public void setHasConfirmEmailError(final boolean hasError) {
        emailConfirmLayout.setError(hasError ? emailConfirmError : null);
    }

    public void setHasEmailFormatError(final boolean hasError) {
        emailAddressLayout.setError(hasError ? emailInvalidFormatError : null);
    }

    public void setPassword(final String value) {
        passwordEditText.setText(value);
    }

    @OnTextChanged(value = R.id.password_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPasswordChanged(final CharSequence value) {
        getPresenter().setPassword(value.toString().trim());
    }

    @OnItemSelected(R.id.state_spinner)
    public void onStateSelected(final int position) {
        getPresenter().setCompleteEnrollmentState((State) stateSpinner.getItemAtPosition(position));
    }

    @OnItemSelected(R.id.country_spinner)
    public void onCountrySelected(final int position) {
        getPresenter().setCompleteEnrollmentCountry((Country) countrySpinner.getItemAtPosition(position));
    }

    @OnCheckedChanged(R.id.service_agreement_check_box)
    public void onDisclaimerChecked(boolean isChecked) {
        getPresenter().setCompleteEnrollmentAcceptDisclaimer(isChecked);
    }

    @OnClick(R.id.service_agreement_link)
    public void showServiceAgreement() {
        getPresenter().getServiceAgreement();
    }

    public void setFabVisibility(boolean isVisible) {
        fab.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setServiceAgreement(final String serviceAgreement) {
        @SuppressLint("InflateParams")
        final WebView disclaimerWebView =
                (WebView) getLayoutInflater().inflate(R.layout.dialog_disclaimer, null);
        disclaimerWebView.loadData(serviceAgreement, "text/html;charset=UTF-8", "UTF-8");
        serviceAgreementDialog = new AlertDialog.Builder(this)
                .setView(disclaimerWebView)
                .show();
    }

    public void showMultiCountrySpinner(final boolean showSpinner) {
        countrySpinner.setVisibility(showSpinner ? View.VISIBLE : View.GONE);

        if (showSpinner) {
            // until populated, set to empty list and disable
            populateStateSpinner(new ArrayList<State>());
            stateSpinner.setEnabled(false);

            countryAdapter = populateAdapter(
                    getPresenter().getSupportedCountries(), R.layout.spinner_row_unselected_country);
            countrySpinner.setAdapter(countryAdapter);
        }
        else {
            // only one Country supported
            Country defaultCountry = getPresenter().getSupportedCountries().get(0);
            populateStateSpinner(getPresenter().getEnrollmentStates(defaultCountry));
        }
    }

    public void populateStateSpinner(final List<State> states) {
        stateAdapter = populateAdapter(states, R.layout.spinner_row_unselected_state, true);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setEnabled(true);
    }

    public void setState(final State state) {
        if (state != null) {
            stateSpinner.setSelection(stateAdapter.getPosition(state));
        }
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_PASSWORD, passwordLayout);
        views.put(ValidationConstants.VALIDATION_EMAIL, emailAddressLayout);
        return views;
    }
}
