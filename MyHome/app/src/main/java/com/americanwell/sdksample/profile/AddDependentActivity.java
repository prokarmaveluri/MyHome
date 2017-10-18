/**
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
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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
 * Activity to add a dependent during intake
 */
@RequiresPresenter(AddDependentPresenter.class)
public class AddDependentActivity extends BaseSampleNucleusActivity<AddDependentPresenter> {

    @BindView(R.id.first_name_edit_text)
    EditText firstNameEditText;
    @BindView(R.id.middle_initial_layout)
    View middleInitialLayout;
    @BindView(R.id.middle_initial_edit_text)
    EditText middleInitialEditText;
    @BindView(R.id.last_name_edit_text)
    EditText lastNameEditText;
    @BindView(R.id.date_of_birth_edit_text)
    EditText dateOfBirthEditText;
    @BindView(R.id.male_radio_button)
    RadioButton maleRadioButton;
    @BindView(R.id.female_radio_button)
    RadioButton femaleRadioButton;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, AddDependentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_add_dependent);
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

    public void setDob(final String value) {
        dateOfBirthEditText.setText(value);
    }

    @OnTextChanged(value = R.id.date_of_birth_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onDobChanged(final CharSequence value) {
        getPresenter().setDobString(value.toString().trim());
    }

    public void setGender(@NonNull final Gender gender) {
        maleRadioButton.setChecked(gender == Gender.MALE);
        femaleRadioButton.setChecked(gender == Gender.FEMALE);
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

    @OnClick(R.id.fab)
    public void addDependent() {
        if (getCurrentFocus() != null) getCurrentFocus().clearFocus();
        getPresenter().enrollDependent();
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_FIRST_NAME, firstNameEditText);
        views.put(ValidationConstants.VALIDATION_MIDDLE_INITIAL, middleInitialEditText);
        views.put(ValidationConstants.VALIDATION_LAST_NAME, lastNameEditText);
        views.put(ValidationConstants.VALIDATION_DOB, dateOfBirthEditText);
        return views;
    }

    @OnClick(R.id.date_of_birth_edit_text)
    public void onDobClick() {
        if (dateOfBirthEditText.isFocused()) {
            datePickerDialog = localeUtils.showDatePicker(dateOfBirthEditText, this, false);
        }
    }

    @OnFocusChange(R.id.date_of_birth_edit_text)
    public void onDobFocusChange() {
        onDobClick();
    }

    // presenter calls this when dependent is added
    public void setDependentAdded(final String name) {
        setResult(RESULT_OK);
        Toast.makeText(this, getString(R.string.add_dependent_success, name), Toast.LENGTH_SHORT).show();
        finish();
    }
}
