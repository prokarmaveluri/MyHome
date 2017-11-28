/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.registration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
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
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for new user registration (enrollment)
 */
@RequiresPresenter(RegistrationPresenter.class)
public class RegistrationActivity extends BaseSampleNucleusActivity<RegistrationPresenter> {

    @BindView(R.id.first_name_edit_text)
    EditText firstNameEditText;
    @BindView(R.id.middle_initial_edit_text)
    EditText middleInitialEditText;
    @BindView(R.id.last_name_edit_text)
    EditText lastNameEditText;
    @BindView(R.id.email_address_edit_text)
    EditText emailAddressEditText;
    @BindView(R.id.email_confirm_edit_text)
    EditText emailConfirmEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.state_spinner)
    Spinner stateSpinner;
    @BindView(R.id.country_spinner)
    Spinner countrySpinner;
    @BindView(R.id.date_of_birth_edit_text)
    EditText dateOfBirthEditText;
    @BindView(R.id.male_radio_button)
    RadioButton maleRadioButton;
    @BindView(R.id.female_radio_button)
    RadioButton femaleRadioButton;
    @BindView(R.id.service_agreement_link)
    TextView serviceAgreementLink;
    @BindView(R.id.service_agreement_check_box)
    CheckBox serviceAgreementCheckBox;
    @BindView(R.id.address1_edit_text)
    EditText address1EditText;
    @BindView(R.id.address2_edit_text)
    EditText address2EditText;
    @BindView(R.id.city_edit_text)
    EditText cityEditText;
    @BindView(R.id.zip_code_edit_text)
    EditText zipCodeEditText;

    @BindView(R.id.first_name_layout)
    TextInputLayout firstNameLayout;
    @BindView(R.id.middle_initial_layout)
    TextInputLayout middleInitialLayout;
    @BindView(R.id.last_name_layout)
    TextInputLayout lastNameLayout;
    @BindView(R.id.email_address_layout)
    TextInputLayout emailAddressLayout;
    @BindView(R.id.email_confirm_layout)
    TextInputLayout emailConfirmLayout;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.date_of_birth_layout)
    TextInputLayout dateOfBirthLayout;
    @BindView(R.id.address1_layout)
    TextInputLayout address1Layout;
    @BindView(R.id.address2_layout)
    TextInputLayout address2Layout;
    @BindView(R.id.city_layout)
    TextInputLayout cityLayout;
    @BindView(R.id.zip_code_layout)
    TextInputLayout zipCodeLayout;

    @BindString(R.string.registration_email_confirm_error)
    String emailConfirmError;

    private AlertDialog serviceAgreementDialog;

    private SampleNamedNothingSelectedSpinnerAdapter stateAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter countryAdapter;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_registration);

        dateOfBirthEditText.setKeyListener(null);
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(serviceAgreementDialog);
    }

    public void setFirstName(final String value) {
        firstNameEditText.setText(value);
    }

    @OnTextChanged(value = R.id.first_name_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onFirstNameChanged(final CharSequence value) {
        getPresenter().setFirstName(value.toString().trim());
    }

    public void setCollectMiddleInitial(final boolean collect) {
        middleInitialLayout.setVisibility(collect ? View.VISIBLE : View.GONE);
    }

    public void setMiddleInitial(final String value) {
        middleInitialEditText.setText(value);
    }

    @OnTextChanged(value = R.id.middle_initial_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onMiddleInitialChanged(final CharSequence value) {
        getPresenter().setMiddleInitial(value.toString().trim());
    }

    public void setLastName(final String value) {
        lastNameEditText.setText(value);
    }

    @OnTextChanged(value = R.id.last_name_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onLastNameChanged(final CharSequence value) {
        getPresenter().setLastName(value.toString().trim());
    }

    public void setEmail(final String value) {
        emailAddressEditText.setText(value);
    }

    @OnTextChanged(value = R.id.email_address_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onEmailChanged(final CharSequence value) {
        getPresenter().setEmail(value.toString());
    }

    public void setConfirmEmail(final String value) {
        emailConfirmEditText.setText(value);
    }

    @OnTextChanged(value = R.id.email_confirm_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onConfirmEmailChanged(final CharSequence value) {
        getPresenter().setConfirmEmail(value.toString());
    }

    public void setHasConfirmEmailError(final boolean hasError) {
        emailConfirmLayout.setError(hasError ? emailConfirmError : null);
    }

    public void setPassword(final String value) {
        passwordEditText.setText(value);
    }

    @OnTextChanged(value = R.id.password_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPasswordChanged(final CharSequence value) {
        getPresenter().setPassword(value.toString().trim());
    }

    public void setState(final State state) {
        if (state != null) {
            stateSpinner.setSelection(stateAdapter.getPosition(state));
        }
    }

    @OnItemSelected(R.id.state_spinner)
    public void onStateSelected() {
        getPresenter().setLegalResidence((State) stateSpinner.getSelectedItem());
        getPresenter().setState((State) stateSpinner.getSelectedItem());
    }

    @OnItemSelected(R.id.country_spinner)
    public void onCountrySelected() {
        getPresenter().setCountry((Country)countrySpinner.getSelectedItem());
    }

    public void setDob(final String value) {
        dateOfBirthEditText.setText(value);
    }

    @OnFocusChange(R.id.date_of_birth_edit_text)
    public void onDobFocusChange(final boolean hasFocus) {
        if (hasFocus) {
            datePickerDialog = localeUtils.showDatePicker(dateOfBirthEditText, this, false);
        }
    }

    @OnClick(R.id.date_of_birth_edit_text)
    public void onDobClick(final View view) {
        if (view.isFocused()) {
            datePickerDialog = localeUtils.showDatePicker(dateOfBirthEditText, this, false);
        }
    }

    @OnTextChanged(value = R.id.date_of_birth_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onDobTextChanged(final CharSequence value) {
        getPresenter().setDobString(value.toString().trim());
    }

    public void setGender(final Gender gender) {
        if (gender != null) {
            maleRadioButton.setChecked(gender == Gender.MALE);
            femaleRadioButton.setChecked(gender == Gender.FEMALE);
        }
    }

    @OnCheckedChanged(R.id.male_radio_button)
    public void onMaleChecked(boolean isChecked) {
        if (isChecked) {
            getPresenter().setGender(Gender.MALE);
        }
    }

    @OnCheckedChanged(R.id.female_radio_button)
    public void onFemaleChecked(boolean isChecked) {
        if (isChecked) {
            getPresenter().setGender(Gender.FEMALE);
        }
    }

    @OnCheckedChanged(R.id.service_agreement_check_box)
    public void onAcceptDisclaimerChecked(boolean isChecked) {
        getPresenter().setAcceptedDisclaimer(isChecked);
    }

    public void setAcceptedDisclaimer(boolean accepted) {
        serviceAgreementCheckBox.setChecked(accepted);
    }

    @OnClick(R.id.service_agreement_link)
    public void showServiceAgreement() {
        getPresenter().getServiceAgreement();
    }

    public void setServiceAgreement(final String serviceAgreement) {
        @SuppressLint("InflateParams")
        final WebView disclaimerWebView = (WebView) getLayoutInflater().inflate(R.layout.dialog_disclaimer, null);
        disclaimerWebView.loadData(serviceAgreement, "text/html;charset=UTF-8", "UTF-8");
        serviceAgreementDialog = new AlertDialog.Builder(this)
                .setView(disclaimerWebView)
                .show();
    }

    public void setCollectAddress(final boolean collect) {
        final int visibility = collect ? View.VISIBLE : View.GONE;
        address1Layout.setVisibility(visibility);
        address2Layout.setVisibility(visibility);
        cityLayout.setVisibility(visibility);
        zipCodeLayout.setVisibility(visibility);
    }

    public void setAddress1(final String value) {
        address1EditText.setText(value);
    }

    @OnTextChanged(value = R.id.address1_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAddress1Changed(final CharSequence value) {
        getPresenter().setAddress1(value.toString().trim());
    }

    public void setAddress2(final String value) {
        address2EditText.setText(value);
    }

    @OnTextChanged(value = R.id.address2_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAddress2Changed(final CharSequence value) {
        getPresenter().setAddress2(value.toString().trim());
    }

    public void setCity(final String value) {
        cityEditText.setText(value);
    }

    @OnTextChanged(value = R.id.city_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onCityChanged(final CharSequence value) {
        getPresenter().setCity(value.toString().trim());
    }

    public void setZipCode(final String value) {
        zipCodeEditText.setText(value);
    }

    @OnTextChanged(value = R.id.zip_code_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onZipCodeChanged(final CharSequence value) {
        getPresenter().setZipCode(value.toString().trim());
    }

    /**
     * presenter will call this when a consumer is enrolled
     */
    public void setConsumerRegistered() {
        goHome();
    }

    @OnClick(R.id.fab)
    public void submit() {
        getPresenter().register();
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

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_PASSWORD, passwordLayout);
        views.put(ValidationConstants.VALIDATION_EMAIL, emailAddressLayout);
        views.put(ValidationConstants.VALIDATION_FIRST_NAME, firstNameLayout);
        views.put(ValidationConstants.VALIDATION_MIDDLE_INITIAL, middleInitialLayout);
        views.put(ValidationConstants.VALIDATION_LAST_NAME, lastNameLayout);
        views.put(ValidationConstants.VALIDATION_DOB, dateOfBirthLayout);
        views.put(ValidationConstants.VALIDATION_ADDRESS_ADDRESS1, address1Layout);
        views.put(ValidationConstants.VALIDATION_ADDRESS_ADDRESS2, address2Layout);
        views.put(ValidationConstants.VALIDATION_ADDRESS_CITY, cityLayout);
        views.put(ValidationConstants.VALIDATION_ADDRESS_STATE, zipCodeLayout);
        return views;
    }

}
