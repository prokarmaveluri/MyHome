/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.americanwell.sdk.entity.consumer.RecoverEmailStatus;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for forgot username / forgot password
 */
@RequiresPresenter(LoginAssistancePresenter.class)
public class LoginAssistanceActivity extends BaseSampleNucleusActivity<LoginAssistancePresenter> {

    @BindView(R.id.forgot_email)
    View forgotEmail;
    @BindView(R.id.forgot_password)
    View forgotPassword;
    @BindView(R.id.forgot_email_prompt)
    View forgotEmailPrompt;
    @BindView(R.id.forgot_password_prompt)
    View forgotPasswordPrompt;
    @BindView(R.id.last_name_dob_section)
    View lastNameDobSection;
    @BindView(R.id.email_address_section)
    View emailSection;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.dob)
    EditText dob;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.response)
    TextView response;

    @BindString(R.string.login_assistance_check_email)
    String checkEmail;

    public static Intent makeIntent(final Context context) {
        return new Intent(context, LoginAssistanceActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_login_assistance);
        dob.setKeyListener(null);
    }

    @OnFocusChange(R.id.dob)
    public void onDobFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.isFocused()) {
                        datePickerDialog = localeUtils.showDatePicker(dob, LoginAssistanceActivity.this, false);
                    }
                }
            });
            datePickerDialog = localeUtils.showDatePicker(dob, LoginAssistanceActivity.this, false);
        }
        else {
            view.setOnClickListener(null);
        }
    }

    @OnClick(R.id.fab)
    public void submit() {
        getPresenter().submit();
    }

    @OnClick(R.id.forgot_email)
    public void onForgotEmailClick() {
        getPresenter().setForgetEmail(true);
    }

    public void showForgotEmailFields() {
        forgotEmail.setVisibility(View.GONE);
        forgotPassword.setVisibility(View.GONE);
        lastNameDobSection.setVisibility(View.VISIBLE);
        emailSection.setVisibility(View.GONE);
        forgotEmailPrompt.setVisibility(View.VISIBLE);
        forgotPasswordPrompt.setVisibility(View.GONE);
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
        }
        response.setVisibility(View.GONE);
    }

    @OnClick(R.id.forgot_password)
    public void onForgotPasswordClick() {
        getPresenter().setForgetEmail(false);
    }

    public void showForgotPasswordFields() {
        forgotEmail.setVisibility(View.GONE);
        forgotPassword.setVisibility(View.GONE);
        lastNameDobSection.setVisibility(View.VISIBLE);
        emailSection.setVisibility(View.VISIBLE);
        forgotEmailPrompt.setVisibility(View.GONE);
        forgotPasswordPrompt.setVisibility(View.VISIBLE);
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
        }
        response.setVisibility(View.GONE);
    }

    @OnTextChanged(value = R.id.last_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onLastNameChanged(final CharSequence value) {
        getPresenter().setLastName(value.toString().trim());
    }

    @OnTextChanged(value = R.id.email, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onEmailChanged(final CharSequence value) {
        getPresenter().setEmail(value.toString().trim());
    }

    @OnTextChanged(value = R.id.dob, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onDobChanged(final CharSequence value) {
        getPresenter().setDob(value.toString().trim());
    }


    public void setEmailResponseStatus(final RecoverEmailStatus recoverEmailStatus, final String redactedEmail) {
        String message =
                recoverEmailStatus.equals(RecoverEmailStatus.EMAIL_RECOVERED)
                        ? getString(R.string.login_assistance_redacted_email, redactedEmail)
                        : checkEmail;

        showResponse(message);
    }

    public void setResetPassword() {
        showResponse(checkEmail);
    }

    public void showResponse(final String message) {
        lastNameDobSection.setVisibility(View.GONE);
        emailSection.setVisibility(View.GONE);
        forgotEmailPrompt.setVisibility(View.GONE);
        forgotPasswordPrompt.setVisibility(View.GONE);
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }
        response.setVisibility(View.VISIBLE);
        response.setText(message);
    }

    @Override
    protected Map<String, View> getValidationViews() {
        Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_EMAIL, email);
        views.put(ValidationConstants.VALIDATION_LAST_NAME, lastName);
        views.put(ValidationConstants.VALIDATION_DOB, dob);
        return views;
    }

}
