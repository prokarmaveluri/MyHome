/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.billing.PaymentMethod;
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
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to add a new credit card payment method
 * <p>
 * accessed directly via home menu or via a button during intake
 */
@RequiresPresenter(AddCreditCardPresenter.class)
public class AddCreditCardActivity extends BaseSampleNucleusActivity<AddCreditCardPresenter> {

    public static final int RESULT_CREDIT_CARD_ADDED = 1000;
    public static final String EXTRA_PAYMENT_METHOD = "paymentMethod";

    @BindView(R.id.current_payment_method_text)
    TextView currentPaymentMethodText;

    @BindView(R.id.name_on_card_edit_text)
    EditText nameOnCardEditText;
    @BindView(R.id.card_number_edit_text)
    EditText cardNumberEditText;
    @BindView(R.id.expiration_month_edit_text)
    EditText expirationMonthEditText;
    @BindView(R.id.expiration_year_edit_text)
    EditText expirationYearEditText;
    @BindView(R.id.cvv_edit_text)
    EditText cvvEditText;

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

    @BindString(R.string.add_credit_card_month_error)
    String ccMonthError;
    @BindString(R.string.add_credit_card_year_error)
    String ccYearError;

    private SampleNamedNothingSelectedSpinnerAdapter stateAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter countryAdapter;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, AddCreditCardActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_add_credit_card);

        countryAdapter = populateAdapter(
                getPresenter().getSupportedCountries(), R.layout.spinner_row_unselected_country);
        countrySpinner.setAdapter(countryAdapter);
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        if (paymentMethod != null) {
            currentPaymentMethodText.setText(paymentMethod.isExpired() ?
                    getString(R.string.add_credit_card_current_card_expired, paymentMethod.getLastDigits()) :
                    getString(R.string.add_credit_card_current_payment_method, paymentMethod.getLastDigits()));

            currentPaymentMethodText.setVisibility(View.VISIBLE);
        }
        else {
            currentPaymentMethodText.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.name_on_card_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onNameOnCardChanged(final CharSequence charSequence) {
        getPresenter().setNameOnCard(charSequence.toString());
    }

    public void setNameOnCard(final String value) {
        nameOnCardEditText.setText(value);
    }

    @OnTextChanged(value = R.id.card_number_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onCardNumberChanged(final CharSequence charSequence) {
        getPresenter().setCardNumber(charSequence.toString());
    }

    public void setCardNumber(final String value) {
        cardNumberEditText.setText(value);
    }

    @OnTextChanged(value = R.id.expiration_month_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onExpirationMonthChanged(final CharSequence charSequence) {
        try {
            final int value = Integer.valueOf(charSequence.toString()).intValue();
            getPresenter().setExpirationMonth(value);
        }
        catch (NumberFormatException e) {
            expirationMonthEditText.setError(ccMonthError);
            expirationMonthEditText.requestFocus();
        }
    }

    @SuppressLint("SetTextI18n")
    public void setExpirationMonth(final int value) {
        if (value > 0) {
            expirationMonthEditText.setText(Integer.valueOf(value).toString());
        }
    }

    @OnTextChanged(value = R.id.expiration_year_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onExpirationYearChanged(final CharSequence charSequence) {
        try {
            final int value = Integer.valueOf(charSequence.toString());
            getPresenter().setExpirationYear(value);
        }
        catch (NumberFormatException e) {
            expirationYearEditText.setError(ccYearError);
            expirationYearEditText.requestFocus();
        }
    }

    @SuppressLint("SetTextI18n")
    public void setExpirationYear(final int value) {
        if (value > 0) {
            expirationYearEditText.setText(Integer.valueOf(value).toString());
        }
    }

    @OnTextChanged(value = R.id.cvv_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onCvvCodeChanged(final CharSequence charSequence) {
        getPresenter().setCvvCode(charSequence.toString());
    }

    public void setCvvCode(final String value) {
        cvvEditText.setText(value);
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

    @OnItemSelected(R.id.country_spinner)
    public void onCountrySelected() {
        getPresenter().setCountry((Country)countrySpinner.getSelectedItem());
    }

    public void setState(final State state) {
        if (state != null) {
            stateSpinner.setSelection(stateAdapter.getPosition(state));
        }
    }

    public void setCountry(final Country country) {
        if (country != null) {
            countrySpinner.setSelection(countryAdapter.getPosition(country));
        }
    }

    @OnTextChanged(value = R.id.zip_code_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onZipCodeChanged(final CharSequence charSequence) {
        getPresenter().setZipCode(charSequence.toString());
    }

    public void setZipCode(final String value) {
        zipCodeEditText.setText(value);
    }

    @OnClick(R.id.fab)
    public void addCreditCard() {
        getPresenter().addCreditCard();
    }

    public void setPaymentUpdated(final PaymentMethod paymentMethod) {
        Toast.makeText(this, R.string.add_credit_card_added, Toast.LENGTH_SHORT).show();
        final Intent data = new Intent();
        data.putExtra(EXTRA_PAYMENT_METHOD, paymentMethod);
        setResult(RESULT_CREDIT_CARD_ADDED, data);  // this is used during intake
        finish();
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
            populateStateSpinner(getPresenter().getValidPaymentMethodStates(defaultCountry));
        }
    }

    public void populateStateSpinner(final List<State> states) {
        stateAdapter = populateAdapter(states, R.layout.spinner_row_unselected_state, true);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setEnabled(true);
    }

    @Override
    protected Map<String, View> getValidationViews() {
        Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_CPR_NAME_ON_CARD, nameOnCardEditText);
        views.put(ValidationConstants.VALIDATION_CPR_CREDIT_CARD_NUMBER, cardNumberEditText);
        views.put(ValidationConstants.VALIDATION_CPR_CREDIT_CARD_SEC_CODE, cvvEditText);
        views.put(ValidationConstants.VALIDATION_CPR_ADDRESS1, address1EditText);
        views.put(ValidationConstants.VALIDATION_CPR_ADDRESS2, address2EditText);
        views.put(ValidationConstants.VALIDATION_CPR_CITY, cityEditText);
        views.put(ValidationConstants.VALIDATION_CPR_ZIPCODE, zipCodeEditText);
        views.put(ValidationConstants.VALIDATION_CPR_MONTH, expirationMonthEditText);
        views.put(ValidationConstants.VALIDATION_CPR_YEAR, expirationYearEditText);
        return views;
    }
}
