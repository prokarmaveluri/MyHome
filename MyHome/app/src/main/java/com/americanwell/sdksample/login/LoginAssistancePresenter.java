/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.consumer.RecoverEmailResponse;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.text.ParseException;

import icepick.State;
import rx.Observable;

/**
 * Presenter for LoginAssistanceActivity
 */
public class LoginAssistancePresenter extends BaseSampleNucleusRxPresenter<LoginAssistanceActivity> {

    private static final int RECOVER_EMAIL = 600;
    private static final int RESET_PASSWORD = 601;

    @State
    String email;
    @State
    String lastName;
    @State
    SDKLocalDate dob;

    @State
    Boolean isForgetEmail = null;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(RECOVER_EMAIL,
                new SampleRequestFunc0<SDKValidatedResponse<RecoverEmailResponse, SDKError>>(RECOVER_EMAIL) {
                    @Override
                    public Observable<SDKValidatedResponse<RecoverEmailResponse, SDKError>> go() {
                        return observableService.recoverEmail(
                                lastName,
                                dob);
                    }
                },
                new SampleValidatedResponseAction2<RecoverEmailResponse, SDKError, SDKValidatedResponse<RecoverEmailResponse, SDKError>>(RECOVER_EMAIL) {
                    @Override
                    public void onSuccess(LoginAssistanceActivity activity, RecoverEmailResponse response) {
                        activity.setEmailResponseStatus(response.getStatus(), response.getRedactedEmail());
                    }
                },
                new SampleFailureAction2(RECOVER_EMAIL)
        );

        restartableLatestCache(RESET_PASSWORD,
                new SampleRequestFunc0<SDKValidatedResponse<Void, SDKError>>(RESET_PASSWORD) {
                    @Override
                    public Observable<SDKValidatedResponse<Void, SDKError>> go() {
                        return observableService.resetPassword(
                                email,
                                lastName,
                                dob
                        );
                    }
                },
                new SampleValidatedResponseAction2<Void, SDKError, SDKValidatedResponse<Void, SDKError>>(RESET_PASSWORD) {
                    @Override
                    public void onSuccess(LoginAssistanceActivity activity, Void aVoid) {
                        activity.setResetPassword();
                    }
                },
                new SampleFailureAction2(RESET_PASSWORD)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void takeView(LoginAssistanceActivity loginAssistanceActivity) {
        super.takeView(loginAssistanceActivity);
        if (isForgetEmail != null) { // initial state this is null
            setForgetEmail(isForgetEmail);
        }
    }

    public void submit() {
        if (isForgetEmail) {
            start(RECOVER_EMAIL);
        }
        else {
            start(RESET_PASSWORD);
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDob(final String dob) {
        try {
            this.dob = SDKLocalDate.valueOf(dob, view.getString(R.string.dateFormat));
        }
        catch (ParseException e) {
            view.setError(e);
        }
    }

    public void setForgetEmail(boolean forgetEmail) {
        isForgetEmail = forgetEmail;
        if (forgetEmail) {
            view.showForgotEmailFields();
        }
        else {
            view.showForgotPasswordFields();
        }
    }
}


