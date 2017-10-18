/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.insurance.HealthPlan;
import com.americanwell.sdk.entity.insurance.Relationship;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdk.manager.ValidationReason;
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
 * Activity for updating consumer's insurance information
 */
@RequiresPresenter(UpdateInsurancePresenter.class)
public class UpdateInsuranceActivity extends BaseSampleNucleusActivity<UpdateInsurancePresenter> {

    @BindView(R.id.health_plan_label)
    View healthPlanLabel;
    @BindView(R.id.health_plan_spinner)
    Spinner healthPlanSpinner;
    @BindView(R.id.no_insurance_checkbox)
    Switch noInsuranceCheckBox;

    @BindView(R.id.subscriber_id_layout)
    TextInputLayout subscriberIDLayout;
    @BindView(R.id.subscriber_id_edit_text)
    EditText subscriberIDEditText;

    @BindView(R.id.subscriber_suffix_layout)
    TextInputLayout subscriberSuffixLayout;
    @BindView(R.id.subscriber_suffix_edit_text)
    EditText subscriberSuffixEditText;

    @BindView(R.id.relationship_label)
    View relationshipLabel;
    @BindView(R.id.relationship_spinner)
    Spinner relationshipSpinner;

    @BindView(R.id.primary_subscriber_first_name_layout)
    TextInputLayout primarySubscriberFirstNameLayout;
    @BindView(R.id.primary_subscriber_first_name_edit_text)
    EditText primarySubscriberFirstNameEditText;

    @BindView(R.id.primary_subscriber_last_name_layout)
    TextInputLayout primarySubscriberLastNameLayout;
    @BindView(R.id.primary_subscriber_last_name_edit_text)
    EditText primarySubscriberLastNameEditText;

    @BindView(R.id.primary_subscriber_date_of_birth_layout)
    TextInputLayout primarySubscriberDateOfBirthLayout;
    @BindView(R.id.primary_subscriber_date_of_birth_edit_text)
    EditText primarySubscriberDateOfBirthEditText;
    @BindView(R.id.card_image)
    ImageView cardImage;

    @BindString(R.string.update_insurance_feedhp_error_title)
    String hpErrorTitle;
    @BindString(R.string.update_insurance_clear_info)
    String clearInsuranceMessage;
    @BindString(R.string.update_insurance_button_save)
    String buttonSave;
    @BindString(R.string.app_cancel)
    String buttonCancel;

    private SampleNamedNothingSelectedSpinnerAdapter<HealthPlan> healthPlansAdapter;
    private SampleNamedNothingSelectedSpinnerAdapter<Relationship> relationshipsAdapter;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, UpdateInsuranceActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_update_insurance);
        primarySubscriberDateOfBirthEditText.setKeyListener(null);
    }

    public void setHasInsurance(final boolean hasInsurance) {
        noInsuranceCheckBox.setChecked(!hasInsurance);
    }

    public void setHealthPlans(final List<HealthPlan> healthPlans) {
        healthPlansAdapter = new SampleNamedNothingSelectedSpinnerAdapter<>(this, healthPlans, R.layout.spinner_row_unselected_hp);
        healthPlanSpinner.setAdapter(healthPlansAdapter);
    }

    public void setRelationships(final List<Relationship> relationships) {
        relationshipsAdapter = new SampleNamedNothingSelectedSpinnerAdapter<>(this, relationships, R.layout.spinner_row_unselected_relationship);
        relationshipSpinner.setAdapter(relationshipsAdapter);
    }

    public void setSubscriberId(final String subscriberId) {
        subscriberIDEditText.setText(subscriberId);
    }

    public void setSubscriberIDError(final String error) {
        String errorString = error;
        if (TextUtils.isEmpty(errorString)) {
            errorString = sampleUtils.getValidationReasonText(this, ValidationReason.FIELD_INVALID_FORMAT);
        }
        subscriberIDEditText.setError(errorString);
    }

    public void setSubscriberSuffix(final String subscriberSuffix) {
        subscriberSuffixEditText.setText(subscriberSuffix);
    }

    public void setShowSubscriberSuffix(final boolean show) {
        subscriberSuffixEditText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setHasHealthPlans(final boolean hasHealthPlans) {
        if (hasHealthPlans) {
            healthPlanSpinner.setAdapter(healthPlansAdapter);
        }
        else {
            healthPlanSpinner.setAdapter(null);
        }
    }

    public void setHasRelationships(final boolean hasRelationships) {
        if (hasRelationships) {
            relationshipSpinner.setAdapter(relationshipsAdapter);
        }
        else {
            relationshipSpinner.setAdapter(null);
        }
    }

    public void setPrimarySubscriberFirstName(final String firstName) {
        primarySubscriberFirstNameEditText.setText(firstName);
    }

    public void setPrimarySubscriberLastName(final String lastName) {
        primarySubscriberLastNameEditText.setText(lastName);
    }

    public void setPrimarySubscriberDob(final String dob) {
        primarySubscriberDateOfBirthEditText.setText(dob);
    }

    public void showDataFields(boolean show) {
        final int visibility = show ? View.VISIBLE : View.GONE;
        subscriberIDLayout.setVisibility(visibility);
        subscriberSuffixLayout.setVisibility(visibility);
        healthPlanSpinner.setVisibility(visibility);
        relationshipSpinner.setVisibility(visibility);
        healthPlanLabel.setVisibility(visibility);
        relationshipLabel.setVisibility(visibility);

        showPrimaryFields(show);
    }

    public void showPrimaryFields(boolean show) {
        final int visibility = show ? View.VISIBLE : View.GONE;
        primarySubscriberDateOfBirthLayout.setVisibility(visibility);
        primarySubscriberFirstNameLayout.setVisibility(visibility);
        primarySubscriberLastNameLayout.setVisibility(visibility);
    }

    @OnCheckedChanged(R.id.no_insurance_checkbox)
    public void checkNoInsurance(boolean isChecked) {
        getPresenter().setNoInsurance(isChecked);
    }

    @OnClick(R.id.primary_subscriber_date_of_birth_edit_text)
    public void onPrimaryDobClick() {
        if (primarySubscriberDateOfBirthEditText.isFocused() &&
                primarySubscriberDateOfBirthLayout.getVisibility() == View.VISIBLE) {
            datePickerDialog = localeUtils.showDatePicker(primarySubscriberDateOfBirthEditText, this, false);
        }
    }

    @OnFocusChange(R.id.primary_subscriber_date_of_birth_edit_text)
    public void onPrimaryDobFocus() {
        onPrimaryDobClick();
    }

    @OnItemSelected(R.id.relationship_spinner)
    public void onRelationshipSelected() {
        getPresenter().setRelationship((Relationship) relationshipSpinner.getSelectedItem());
    }

    public void setRelationship(final Relationship relationship) {
        if (relationship != null) {
            relationshipSpinner.setSelection(relationshipsAdapter.getPosition(relationship));
        }
    }

    @OnItemSelected(R.id.health_plan_spinner)
    public void onHealthPlanSelected() {
        final HealthPlan healthPlan = (HealthPlan) healthPlanSpinner.getSelectedItem();
        if (healthPlan != null) {
            getPresenter().setHealthPlan(healthPlan);
            setShowSubscriberSuffix(healthPlan.isUsesSuffix());
            setCardImage(healthPlan);
        }
    }

    public void setHealthPlan(final HealthPlan healthPlan) {
        if (healthPlan != null) {
            healthPlanSpinner.setSelection(healthPlansAdapter.getPosition(healthPlan));
            setShowSubscriberSuffix(healthPlan.isUsesSuffix());
            setCardImage(healthPlan);
        }
    }

    @OnTextChanged(value = R.id.subscriber_id_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSubscriberIdChanged(final CharSequence subscriberId) {
        getPresenter().setSubscriberId(subscriberId.toString());
    }

    @OnTextChanged(value = R.id.subscriber_suffix_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSubscriberSuffixChanged(final CharSequence suffix) {
        getPresenter().setSubscriberSuffix(suffix.toString());
    }

    @OnTextChanged(value = R.id.primary_subscriber_first_name_edit_text,
                   callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPrimaryFirstChanged(final CharSequence first) {
        getPresenter().setPrimarySubscriberFirstName(first.toString());
    }

    @OnTextChanged(value = R.id.primary_subscriber_last_name_edit_text,
                   callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPrimaryLastChanged(final CharSequence last) {
        getPresenter().setPrimarySubscriberLastName(last.toString());
    }

    @OnTextChanged(value = R.id.primary_subscriber_date_of_birth_edit_text,
                   callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPrimaryDobChanged(final CharSequence dob) {
        getPresenter().setPrimarySubscriberDob(dob.toString());
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_SUR_SUBSCRIBER_ID, subscriberIDEditText);
        views.put(ValidationConstants.VALIDATION_SUR_SUBSCRIBER_SUFFIX, subscriberSuffixEditText);
        views.put(ValidationConstants.VALIDATION_SUR_PRIMARY_FIRST_NAME, primarySubscriberFirstNameEditText);
        views.put(ValidationConstants.VALIDATION_SUR_PRIMARY_LAST_NAME, primarySubscriberLastNameEditText);
        views.put(ValidationConstants.VALIDATION_SUR_PRIMARY_DOB, primarySubscriberDateOfBirthEditText);
        return views;
    }


    @OnClick(R.id.fab)
    public void updateInsurance() {
        getPresenter().updateSubscription();
    }

    public void setInsuranceUpdated() {
        Toast.makeText(this, getString(R.string.update_insurance_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    // show an error if they've selected a feed-controlled health plan
    public void setFeedHpError(final String hpName) {
        setError(hpErrorTitle,
                getString(R.string.update_insurance_feedhp_error_message, hpName));
    }

    // if a consumer is a feed member (aka HP), they cannot edit or update their insurance
    public void setEnabled(boolean enabled) {
        healthPlanSpinner.setEnabled(enabled);
        noInsuranceCheckBox.setEnabled(enabled);
        subscriberIDEditText.setEnabled(enabled);
        subscriberSuffixEditText.setEnabled(enabled);
        relationshipSpinner.setEnabled(enabled);
        primarySubscriberFirstNameEditText.setEnabled(enabled);
        primarySubscriberLastNameEditText.setEnabled(enabled);
        primarySubscriberDateOfBirthEditText.setEnabled(enabled);
        if (fab != null) {
            fab.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public void setCardImage(final HealthPlan healthPlan) {
        if (healthPlan != null && healthPlan.hasCardImage()) {
            cardImage.setVisibility(View.VISIBLE);
            getPresenter().loadHealthPlanCardImage(
                    healthPlan,
                    cardImage
            );
        }
        else {
            cardImage.setVisibility(View.GONE);
        }
    }

    public void verifyRemoveSubscription() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setMessage(clearInsuranceMessage);
        alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE,
                getString(android.R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getPresenter().removeSubscription();
                    }
                });
        alertDialog.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void setEligibilityError(final SDKError sdkError) {
        setSomethingIsBusy(false);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(sampleUtils.getErrorTitle(this, sdkError));
        alertDialog.setMessage(sampleUtils.getErrorBody(this, sdkError));
        alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE,
                buttonSave,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getPresenter().resubmitSubscriptionIgnoreEligibilityErrors();
                    }
                });
        alertDialog.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                buttonCancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }
}
