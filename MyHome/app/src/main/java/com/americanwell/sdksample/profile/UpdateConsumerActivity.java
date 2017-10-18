/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.SampleNamedNothingSelectedSpinnerAdapter;

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
 * Activity to update a Consumer's demographic information
 */
@RequiresPresenter(UpdateConsumerPresenter.class)
public class UpdateConsumerActivity extends BaseSampleNucleusActivity<UpdateConsumerPresenter> {

    @BindView(R.id.first_name_edit_text)
    EditText firstNameEditText;
    @BindView(R.id.first_name_layout)
    TextInputLayout firstNameLayout;
    @BindView(R.id.middle_initial_edit_text)
    EditText middleInitialEditText;
    @BindView(R.id.middle_initial_layout)
    TextInputLayout middleInitialLayout;
    @BindView(R.id.last_name_edit_text)
    EditText lastNameEditText;
    @BindView(R.id.last_name_layout)
    TextInputLayout lastNameLayout;
    @BindView(R.id.email_address_edit_text)
    EditText emailAddressEditText;
    @BindView(R.id.email_address_layout)
    TextInputLayout emailAddressLayout;
    @BindView(R.id.email_confirm_edit_text)
    EditText emailConfirmEditText;
    @BindView(R.id.email_confirm_layout)
    TextInputLayout emailConfirmLayout;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.date_of_birth_edit_text)
    EditText dateOfBirthEditText;
    @BindView(R.id.date_of_birth_layout)
    TextInputLayout dateOfBirthLayout;
    @BindView(R.id.phone_edit_text)
    EditText phoneEditText;
    @BindView(R.id.phone_layout)
    TextInputLayout phoneLayout;
    @BindView(R.id.gender_group)
    RadioGroup genderGroup;
    @BindView(R.id.male_radio_button)
    RadioButton maleRadioButton;
    @BindView(R.id.female_radio_button)
    RadioButton femaleRadioButton;
    @BindView(R.id.address_layout)
    LinearLayout addressLayout;
    @BindView(R.id.address1_edit_text)
    EditText address1EditText;
    @BindView(R.id.address1_layout)
    TextInputLayout address1Layout;
    @BindView(R.id.address2_edit_text)
    EditText address2EditText;
    @BindView(R.id.address2_layout)
    TextInputLayout address2Layout;
    @BindView(R.id.city_edit_text)
    EditText cityEditText;
    @BindView(R.id.city_layout)
    TextInputLayout cityLayout;
    @BindView(R.id.state_spinner)
    Spinner stateSpinner;
    @BindView(R.id.country_spinner)
    Spinner countrySpinner;
    @BindView(R.id.zip_edit_text)
    EditText zipEditText;
    @BindView(R.id.zip_layout)
    TextInputLayout zipLayout;
    @BindView(R.id.residence_state_spinner)
    Spinner stateResidenceSpinner;
    @BindView(R.id.residence_country_spinner)
    Spinner countryResidenceSpinner;

    @BindView(R.id.texts_enabled_check)
    CheckBox textsEnabledCheck;

    @BindString(R.string.update_consumer_email_confirm_error)
    String emailConfirmError;

    private SampleNamedNothingSelectedSpinnerAdapter stateAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter countryAdapter;

    private SampleNamedNothingSelectedSpinnerAdapter stateResidenceAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter countryResidenceAdapter;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, UpdateConsumerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_update_consumer);

        countryAdapter = populateAdapter(
                getPresenter().getSupportedCountries(), R.layout.spinner_row_unselected_country);
        countrySpinner.setAdapter(countryAdapter);

        countryResidenceAdapter = populateAdapter(
                getPresenter().getSupportedCountries(), R.layout.spinner_row_unselected_country);
        countryResidenceSpinner.setAdapter(countryResidenceAdapter);

        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        dateOfBirthEditText.setKeyListener(null);
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

    public void setEmailAddress(final String value) {
        emailAddressEditText.setText(value);
    }

    @OnTextChanged(value = R.id.email_address_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onEmailAddressChanged(final CharSequence value) {
        getPresenter().setEmailAddress(value.toString().trim());
    }

    public void showConfirmEmail(final boolean show) {
        emailConfirmLayout.setVisibility(show ? View.VISIBLE : View.GONE);
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
    public void onDobChanged(final CharSequence value) {
        getPresenter().setDobString(value.toString().trim());
    }

    public void setPhone(final String value) {
        phoneEditText.setText(value);
    }

    @OnTextChanged(value = R.id.phone_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPhoneChanged(final CharSequence value) {
        getPresenter().setPhone(sampleUtils.stripNonDigitCharacters(value.toString().trim()));
    }

    public void setCollectAddress(final boolean collect) {
        addressLayout.setVisibility(collect ? View.VISIBLE : View.GONE);
    }

    public void showMultiCountrySpinners(final boolean showSpinners) {
        countrySpinner.setVisibility(showSpinners ? View.VISIBLE : View.GONE);
        countryResidenceSpinner.setVisibility(showSpinners ? View.VISIBLE : View.GONE);
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

    @OnItemSelected(R.id.state_spinner)
    public void onStateSelected() {
        getPresenter().setState((State) stateSpinner.getSelectedItem());
    }

    @OnItemSelected(R.id.country_spinner)
    public void onCountrySelected() {
        getPresenter().setCountry((Country) countrySpinner.getSelectedItem());
    }


    @OnCheckedChanged(R.id.texts_enabled_check)
    public void onTextRemindersChanged(boolean isChecked) {
        getPresenter().setAppointmentReminderTextsEnabled(isChecked);
    }

    public void setAppointmentReminderTextsChecked(boolean isChecked) {
        textsEnabledCheck.setChecked(isChecked);
    }

    public void setState(final State state) {
        if (state != null) {
            stateSpinner.setSelection(stateAdapter.getPosition(state));
        }
    }

    public void populateStateSpinner(final List<State> states) {
        stateAdapter = populateAdapter(states, R.layout.spinner_row_unselected_state);
        stateSpinner.setAdapter(stateAdapter);
    }

    public void populateStateResidenceSpinner(final List<State> states) {
        stateResidenceAdapter = populateAdapter(states, R.layout.spinner_row_unselected_state);
        stateResidenceSpinner.setAdapter(stateResidenceAdapter);
    }

    public void setCountry(final Country country) {
        if (country != null) {
            countrySpinner.setSelection(countryAdapter.getPosition(country));
        }
    }

    @OnItemSelected(R.id.residence_state_spinner)
    public void onStateResidenceSelected() {
        getPresenter().setStateResidence((State) stateResidenceSpinner.getSelectedItem());
    }

    @OnItemSelected(R.id.residence_country_spinner)
    public void onCountryResidenceSelected() {
        getPresenter().setCountryResidence((Country) countryResidenceSpinner.getSelectedItem());
    }

    public void setStateResidence(final State state) {
        if (state != null) {
            stateResidenceSpinner.setSelection(stateResidenceAdapter.getPosition(state));
        }
    }

    public void setCountryResidence(final Country country) {
        if (country != null) {
            countryResidenceSpinner.setSelection(countryResidenceAdapter.getPosition(country));
        }
    }

    public void setZipCode(final String value) {
        zipEditText.setText(value);
    }

    @OnTextChanged(value = R.id.zip_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onZipChanged(final CharSequence value) {
        getPresenter().setZipcode(value.toString().trim());
    }

    @OnClick(R.id.fab)
    public void updateConsumer() {
        getPresenter().updateConsumer();
    }

    public void setConsumerUpdated(final String fullName) {
        Toast.makeText(this, getString(R.string.update_consumer_success, fullName), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_EMAIL, emailAddressLayout);
        views.put(ValidationConstants.VALIDATION_PHONE, phoneLayout);
        views.put(ValidationConstants.VALIDATION_ADDRESS1, address1Layout);
        views.put(ValidationConstants.VALIDATION_ADDRESS2, address2Layout);
        views.put(ValidationConstants.VALIDATION_ADDRESS_CITY, cityLayout);
        views.put(ValidationConstants.VALIDATION_ADDRESS_ZIPCODE, zipLayout);
        views.put(ValidationConstants.VALIDATION_FIRST_NAME, firstNameLayout);
        views.put(ValidationConstants.VALIDATION_MIDDLE_INITIAL, middleInitialLayout);
        views.put(ValidationConstants.VALIDATION_LAST_NAME, lastNameLayout);
        views.put(ValidationConstants.VALIDATION_DOB, dateOfBirthLayout);
        return views;
    }

    public void setFirstNameEnabled(boolean enabled) {
        firstNameEditText.setEnabled(enabled);
    }

    public void setMiddleInitialEnabled(boolean enabled) {
        middleInitialEditText.setEnabled(enabled);
    }

    public void setLastNameEnabled(boolean enabled) {
        lastNameEditText.setEnabled(enabled);
    }


    public void setGenderEnabled(boolean enabled) {
        genderGroup.setEnabled(enabled);
        for (int i = 0; i < genderGroup.getChildCount(); i++) {
            genderGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    public void setDobEnabled(boolean enabled) {
        dateOfBirthEditText.setEnabled(enabled);
    }

    public void setEmailAddressEnabled(boolean enabled) {
        emailAddressEditText.setEnabled(enabled);
    }

    public void setPhoneEnabled(boolean enabled) {
        phoneEditText.setEnabled(enabled);
    }

    public void setAddress1Enabled(boolean enabled) {
        address1EditText.setEnabled(enabled);
    }

    public void setAddress2Enabled(boolean enabled) {
        address2EditText.setEnabled(enabled);
    }

    public void setCityEnabled(boolean enabled) {
        cityEditText.setEnabled(enabled);
    }

    public void setStateEnabled(boolean enabled) {
        stateSpinner.setEnabled(enabled);
    }

    public void setZipEnabled(boolean enabled) {
        zipEditText.setEnabled(enabled);
    }
}
