/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for collection of Vitals data
 */
@RequiresPresenter(VitalsPresenter.class)
public class VitalsActivity extends BaseSampleNucleusActivity<VitalsPresenter> {

    @BindView(R.id.systolic_blood_pressure_edit_text)
    EditText systolicBloodPressureEditText;
    @BindView(R.id.diastolic_blood_pressure_edit_text)
    EditText diastolicBloodPressureEditText;
    @BindView(R.id.body_temperature_edit_text)
    EditText bodyTemperatureEditText;
    @BindView(R.id.weight_edit_text)
    EditText weightEditText;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, VitalsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_vitals);
    }

    @OnTextChanged(value = R.id.systolic_blood_pressure_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSystolicChanged(final CharSequence value) {
        getPresenter().setSystolic(value.toString());
    }

    public void setSystolic(final String value) {
        systolicBloodPressureEditText.setText(value);
    }

    @OnTextChanged(value = R.id.diastolic_blood_pressure_edit_text,
                   callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onDiastolicChanged(final CharSequence value) {
        getPresenter().setDiastolic(value.toString());
    }

    public void setDiastolic(final String value) {
        diastolicBloodPressureEditText.setText(value);
    }

    @OnTextChanged(value = R.id.body_temperature_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onBodyTemperatureChanged(final CharSequence value) {
        getPresenter().setTemperature(value.toString());
    }

    public void setBodyTemperature(final String value) {
        bodyTemperatureEditText.setText(value);
    }

    @OnTextChanged(value = R.id.weight_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onWeightChanged(final CharSequence value) {
        getPresenter().setWeight(value.toString());
    }

    public void setWeight(final String value) {
        weightEditText.setText(value);
    }

    @OnClick(R.id.fab)
    public void submitVitals() {
        getPresenter().submitVitals();
    }

    public void setVitalsUpdated(boolean showToast) {
        if (showToast) {
            Toast.makeText(this, R.string.vitals_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_VITALS_DIASTOLIC, diastolicBloodPressureEditText);
        views.put(ValidationConstants.VALIDATION_VITALS_SYSTOLIC, systolicBloodPressureEditText);
        views.put(ValidationConstants.VALIDATION_VITALS_TEMPERATURE, bodyTemperatureEditText);
        views.put(ValidationConstants.VALIDATION_VITALS_WEIGHT, weightEditText);
        return views;
    }
}
