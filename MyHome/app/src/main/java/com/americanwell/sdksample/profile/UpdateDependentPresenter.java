/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.DependentUpdate;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;
import com.americanwell.sdksample.util.SampleUtils;

import java.text.ParseException;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;

/**
 * Presenter for UpdateDependentActivity
 *
 * @since AWSDK 2.2
 */
public class UpdateDependentPresenter extends BaseSampleNucleusRxPresenter<UpdateDependentActivity> {

    private static final int UPDATE_DEPENDENT = 860;

    @Inject
    ConsumerManager consumerManager;
    @State
    DependentUpdate dependentUpdate;
    @State
    String firstName;
    @State
    String middleInitial;
    @State
    String lastName;
    @State
    String dobString;
    @State
    Gender gender;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(UPDATE_DEPENDENT,
                new SampleRequestFunc0<SDKValidatedResponse<Consumer, SDKError>>(UPDATE_DEPENDENT) {
                    @Override
                    public Observable<SDKValidatedResponse<Consumer, SDKError>> go() {
                        return observableService.updateDependent(dependentUpdate);
                    }
                },
                new SampleValidatedResponseAction2<Consumer, SDKError, SDKValidatedResponse<Consumer, SDKError>>(UPDATE_DEPENDENT) {
                    @Override
                    public void onSuccess(UpdateDependentActivity updateDependentActivity, Consumer consumer) {
                        view.setDependentUpdated(consumer);
                    }
                },
                new SampleFailureAction2(UPDATE_DEPENDENT)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(UpdateDependentActivity view) {
        super.onTakeView(view);

        if (TextUtils.isEmpty(firstName)) {
            firstName = consumer.getFirstName();
        }
        view.setFirstName(firstName);

        view.setCollectMiddleInitial(awsdk.getConfiguration().isConsumerMiddleInitialCollected());

        if (TextUtils.isEmpty(middleInitial)) {
            middleInitial = consumer.getMiddleInitial();
        }
        view.setMiddleInitial(middleInitial);

        if (TextUtils.isEmpty(lastName)) {
            lastName = consumer.getLastName();
        }
        view.setLastName(lastName);

        if (TextUtils.isEmpty(dobString)) {
            try {
                dobString = consumer.getDob().toString(view.getString(R.string.dateFormat));
            }
            catch (ParseException e) {
                view.setError(e);
                return;
            }
        }
        view.setDob(dobString);

        if (gender == null) {
            gender = consumer.getGender();
        }
        view.setGender(gender);

        dependentUpdate = consumerManager.getNewDependentUpdate(consumer);
        view.setFirstNameEnabled(dependentUpdate.isEditable(ValidationConstants.VALIDATION_FIRST_NAME));
        view.setMiddleInitialEnabled(dependentUpdate.isEditable(ValidationConstants.VALIDATION_MIDDLE_INITIAL));
        view.setLastNameEnabled(dependentUpdate.isEditable(ValidationConstants.VALIDATION_LAST_NAME));
        view.setGenderEnabled(dependentUpdate.isEditable(ValidationConstants.VALIDATION_GENDER));
        view.setDobEnabled(dependentUpdate.isEditable(ValidationConstants.VALIDATION_DOB));
    }

    public void updateDependent() {

        if (SampleUtils.isDirty(consumer.getFirstName(), firstName)) {
            dependentUpdate.setFirstName(firstName);
        }

        if (SampleUtils.isDirty(consumer.getMiddleInitial(), middleInitial)) {
            // empty - no change, otherwise set
            dependentUpdate.setMiddleInitial(TextUtils.isEmpty(middleInitial) ? null : middleInitial);
        }
        // both empty - no change (null)
        else if (SampleUtils.bothEmpty(consumer.getMiddleInitial(), middleInitial)) {
            dependentUpdate.setMiddleInitial(null);
        }

        if (SampleUtils.isDirty(consumer.getLastName(), lastName)) {
            dependentUpdate.setLastName(lastName);
        }

        try {
            final String consumerDobString = consumer.getDob().toString(view.getString(R.string.dateFormat));
            if (SampleUtils.isDirty(consumerDobString, dobString)) {
                dependentUpdate.setDob(SDKLocalDate.valueOf(dobString, view.getString(R.string.dateFormat)));
            }
            // covers case when previous entry attempt was invalid
            else if (TextUtils.equals(consumerDobString, dobString)) {
                dependentUpdate.setDob(null);
            }
        }
        catch (ParseException e) {
            view.setError(e);
            return;
        }

        if (SampleUtils.isDirty(consumer.getGender().toString(), gender.toString())) {
            dependentUpdate.setGender(gender);
        }

        start(UPDATE_DEPENDENT);
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDobString(String dateOfBirth) {
        this.dobString = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

}
