/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.entity.enrollment.DependentEnrollment;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.text.ParseException;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for AddDependentActivity
 */
public class AddDependentPresenter extends BaseSampleNucleusRxPresenter<AddDependentActivity> {

    private static final int ADD_DEPENDENT = 810;

    @Inject
    ConsumerManager consumerManager;
    @State
    DependentEnrollment enrollment;
    @State
    String firstName;
    @State
    String middleInitial;
    @State
    String lastName;
    @State
    String dobString;
    @State
    Gender gender = Gender.MALE; // initialize to male

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(ADD_DEPENDENT,
                new SampleRequestFunc0<SDKValidatedResponse<Consumer, SDKError>>(ADD_DEPENDENT) {
                    @Override
                    public Observable<SDKValidatedResponse<Consumer, SDKError>> go() {
                        return observableService.enrollDependent(enrollment);
                    }
                },
                new SampleValidatedResponseAction2<Consumer, SDKError, SDKValidatedResponse<Consumer, SDKError>>(ADD_DEPENDENT) {
                    @Override
                    public void onSuccess(AddDependentActivity addDependentActivity, Consumer consumer) {
                        addDependentActivity.setDependentAdded(consumer.getFullName());
                    }
                },
                new SampleFailureAction2(ADD_DEPENDENT)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(AddDependentActivity view) {
        super.onTakeView(view);
        view.setFirstName(firstName);
        view.setCollectMiddleInitial(awsdk.getConfiguration().isConsumerMiddleInitialCollected());
        view.setMiddleInitial(middleInitial);
        view.setLastName(lastName);
        view.setGender(gender);
        view.setDob(dobString);
    }

    public void enrollDependent() {
        enrollment = consumerManager.getNewDependentEnrollment(consumer);
        enrollment.setFirstName(firstName);
        enrollment.setMiddleInitial(middleInitial);
        enrollment.setLastName(lastName);
        try {
            enrollment.setDob(SDKLocalDate.valueOf(dobString, view.getString(R.string.dateFormat)));
        }
        catch (ParseException e) {
            view.setError(e);
            return;
        }
        enrollment.setGender(gender);
        start(ADD_DEPENDENT);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
