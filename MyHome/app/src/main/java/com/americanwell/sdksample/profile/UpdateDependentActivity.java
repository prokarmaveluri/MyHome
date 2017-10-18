/*
 * Copyright 2017 American Well Systems
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
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to update a Dependent Consumer's demographic information
 *
 * @since AWSDK 2.2
 */
@RequiresPresenter(UpdateDependentPresenter.class)
public class UpdateDependentActivity extends BaseSampleNucleusActivity<UpdateDependentPresenter> {

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
    @BindView(R.id.date_of_birth_edit_text)
    EditText dateOfBirthEditText;
    @BindView(R.id.date_of_birth_layout)
    TextInputLayout dateOfBirthLayout;
    @BindView(R.id.gender_group)
    RadioGroup genderGroup;
    @BindView(R.id.male_radio_button)
    RadioButton maleRadioButton;
    @BindView(R.id.female_radio_button)
    RadioButton femaleRadioButton;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, UpdateDependentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_update_dependent);
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

    public void setDependentUpdated(final Consumer dependent) {
        Toast.makeText(this, getString(R.string.update_consumer_success, dependent.getFullName()), Toast.LENGTH_SHORT).show();
        final Intent data = new Intent();
        data.putExtra("dependent", dependent); // pass the dependent back to the home activity for processing
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
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

    @OnTextChanged(value = R.id.date_of_birth_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onDobChanged(final CharSequence value) {
        getPresenter().setDobString(value.toString().trim());
    }

    @OnClick(R.id.fab)
    public void updateDependent() {
        getPresenter().updateDependent();
    }

}
