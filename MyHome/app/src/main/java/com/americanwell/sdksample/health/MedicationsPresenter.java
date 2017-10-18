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
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.ArrayList;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for MedicationsActivity
 */
public class MedicationsPresenter extends BaseSampleNucleusRxPresenter<MedicationsActivity> {

    private static final int GET_MEDICATIONS = 330;
    private static final int UPDATE_MEDICATIONS = 331;

    @State
    ArrayList<Medication> medications;
    @State
    ArrayList<Medication> selectedMedications = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_MEDICATIONS,
                new SampleRequestFunc0<SDKResponse<List<Medication>, SDKError>>(GET_MEDICATIONS) {
                    @Override
                    public Observable<SDKResponse<List<Medication>, SDKError>> go() {
                        return observableService.getMedications(consumer);
                    }
                },
                new SampleResponseAction2<List<Medication>, SDKError, SDKResponse<List<Medication>, SDKError>>(GET_MEDICATIONS) {
                    @Override
                    public void onSuccess(MedicationsActivity medicationsActivity, List<Medication> medications) {
                        stop(GET_MEDICATIONS);
                        setMedications(medications);
                        clearAndAddSelectedMedications(medications);
                    }
                },
                new SampleFailureAction2(GET_MEDICATIONS)
        );

        restartableLatestCache(UPDATE_MEDICATIONS,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(UPDATE_MEDICATIONS) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.updateMedications(
                                consumer,
                                new ArrayList<>(selectedMedications)
                        );
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(UPDATE_MEDICATIONS) {
                    @Override
                    public void onSuccess(MedicationsActivity medicationsActivity, Void aVoid) {
                        view.setMedicationsUpdated();
                    }
                },
                new SampleFailureAction2(UPDATE_MEDICATIONS)
        );
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(MedicationsActivity view) {
        super.onTakeView(view);
        if (medications == null) {
            start(GET_MEDICATIONS);
        }
        else {
            setMedications(medications);
        }
    }

    public void setMedications(final List<Medication> medications) {
        this.medications = (ArrayList<Medication>) medications;
        view.setMedications(medications);
    }

    public void clearAndAddSelectedMedications(final List<Medication> medications) {
        selectedMedications.clear();
        selectedMedications.addAll(medications);
    }

    public void toggleMedicationSelection(final Medication medication) {
        if (selectedMedications.contains(medication)) {
            selectedMedications.remove(medication);
        }
        else {
            selectedMedications.add(medication);
        }
    }

    public boolean isSelected(final Medication medication) {
        return selectedMedications.contains(medication);
    }

    public void addMedication(final Medication medication) {
        if (!medications.contains(medication)) {
            medications.add(medication);
            selectedMedications.add(medication);
        }
        if (view != null) {
            view.setMedications(medications);
        }
    }

    public void updateMedications() {
        start(UPDATE_MEDICATIONS);
    }
}
