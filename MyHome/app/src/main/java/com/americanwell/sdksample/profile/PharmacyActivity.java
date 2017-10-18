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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.SampleNamedNothingSelectedSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to display currently preferred pharmacy
 */
@RequiresPresenter(PharmacyPresenter.class)
public class PharmacyActivity extends BaseSampleNucleusActivity<PharmacyPresenter> {

    private static final int REQUEST_FIND_PHARMACY = 1000;

    @BindView(R.id.pharmacy_text_view)
    TextView pharmacyTextView;
    @BindView(R.id.find_pharmacy_button)
    Button findPharmacyButton;
    @BindView(R.id.address_layout)
    View addressLayout;
    @BindView(R.id.address1_edit_text)
    EditText address1EditText;
    @BindView(R.id.address2_edit_text)
    EditText address2EditText;
    @BindView(R.id.city_edit_text)
    EditText cityEditText;
    @BindView(R.id.state_spinner)
    Spinner stateSpinner;
    @BindView(R.id.country_spinner)
    Spinner countrySpinner;
    @BindView(R.id.zip_code_edit_text)
    EditText zipCodeEditText;

    private SampleNamedNothingSelectedSpinnerAdapter stateAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter countryAdapter;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, PharmacyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pharmacy);
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
    }

    @OnClick(R.id.fab)
    public void updatePharmacy() {
        getPresenter().updatePharmacy();
    }

    @OnClick(R.id.find_pharmacy_button)
    public void findPharmacy() {
        startActivityForResult(FindPharmacyActivity.makeIntent(this), REQUEST_FIND_PHARMACY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FIND_PHARMACY && resultCode == RESULT_OK) {
            final Pharmacy pharmacy = data.getParcelableExtra(FindPharmacyActivity.EXTRA_PHARMACY);
            getPresenter().setPharmacy(pharmacy);
        }
    }

    public void setPharmacy(final Pharmacy pharmacy) {
        if (pharmacy == null) {
            pharmacyTextView.setText(R.string.pharmacy_no_preferred_pharmacy);
        }
        else {
            final String builder = sampleUtils.buildPharmacyDisplayText(
                    this, pharmacy, true, getPresenter().isMultiCountry());
            pharmacyTextView.setText(builder);
        }
    }

    public void setSaveEnabled(final boolean enabled) {
        if (fab != null) {
            fab.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public void setPharmacyUpdated(boolean showToast) {
        if (showToast) {
            Toast.makeText(this, R.string.pharmacy_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @OnTextChanged(value = R.id.address1_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAddress1Changed(final CharSequence charSequence) {
        getPresenter().setAddress1(charSequence.toString());
    }

    public void setAddress1(final String value) {
        address1EditText.setText(value);
    }

    @OnTextChanged(value = R.id.address2_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAddress2Changed(final CharSequence charSequence) {
        getPresenter().setAddress2(charSequence.toString());
    }

    public void setAddress2(final String value) {
        address2EditText.setText(value);
    }

    @OnTextChanged(value = R.id.city_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onCityChanged(final CharSequence charSequence) {
        getPresenter().setCity(charSequence.toString());
    }

    public void setCity(final String value) {
        cityEditText.setText(value);
    }

    @OnItemSelected(R.id.state_spinner)
    public void onStateSelected() {
        getPresenter().setState((State) stateSpinner.getSelectedItem());
    }

    public void setState(@NonNull final State state) {
        stateSpinner.setSelection(stateAdapter.getPosition(state));
    }

    @OnItemSelected(R.id.country_spinner)
    public void onCountrySelected() {
        getPresenter().setCountry((Country)countrySpinner.getSelectedItem());
    }

    public void setCountry(final Country country) {
        if (country != null && countryAdapter != null) {
            countrySpinner.setSelection(countryAdapter.getPosition(country));
        }
    }

    public void populateStateSpinner(final List<State> states) {
        stateAdapter = populateAdapter(states, R.layout.spinner_row_unselected_state, true);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setEnabled(true);
    }

    @OnTextChanged(value = R.id.zip_code_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onZipCodeChanged(final CharSequence charSequence) {
        getPresenter().setZipCode(charSequence.toString());
    }

    public void setZipCode(final String value) {
        zipCodeEditText.setText(value);
    }

    public void setShowShippingAddress(boolean showShippingAddress) {
        addressLayout.setVisibility(showShippingAddress ? View.VISIBLE : View.GONE);
    }

    public void showMultiCountrySpinner(final boolean showSpinners) {
        countrySpinner.setVisibility(showSpinners ? View.VISIBLE : View.GONE);
    
        if (showSpinners) {
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


}
