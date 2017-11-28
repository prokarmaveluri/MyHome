/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.health;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for AddMedicationActivity
 */
public class AddMedicationPresenter extends BaseSampleNucleusRxPresenter<AddMedicationActivity> {

    private static final int SEARCH_MEDICATIONS = 300;

    @State
    String searchTerm;
    @State
    ArrayList<Medication> medications;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(SEARCH_MEDICATIONS,
                new SampleRequestFunc0<SDKValidatedResponse<List<Medication>, SDKError>>(SEARCH_MEDICATIONS) {
                    @Override
                    public Observable<SDKValidatedResponse<List<Medication>, SDKError>> go() {
                        return observableService.searchMedications(consumer, searchTerm);
                    }
                },
                new SampleValidatedResponseAction2<List<Medication>, SDKError, SDKValidatedResponse<List<Medication>, SDKError>>(SEARCH_MEDICATIONS) {
                    @Override
                    public void onSuccess(AddMedicationActivity addMedicationActivity, List<Medication> medications) {
                        setMedications((ArrayList<Medication>) medications);
                    }
                },
                new SampleFailureAction2(SEARCH_MEDICATIONS)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(AddMedicationActivity view) {
        super.onTakeView(view);
        if (medications != null) {
            setMedications(medications);
        }
        view.setSearchTerm(searchTerm);
    }


    public void searchMedications(final String searchTerm) {
        this.searchTerm = searchTerm;
        start(SEARCH_MEDICATIONS);
    }

    public void setMedications(final ArrayList<Medication> medications) {
        this.medications = medications;
        view.setMedications(medications);
        view.setEmptyMedications(medications.isEmpty(), searchTerm);
    }

}
