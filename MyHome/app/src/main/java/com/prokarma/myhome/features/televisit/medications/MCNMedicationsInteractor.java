package com.prokarma.myhome.features.televisit.medications;

import com.americanwell.sdk.entity.health.Medication;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.AwsNetworkManager;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsSearchMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdateMedications;

import java.util.List;

/**
 * Created by veluri on 2/12/18.
 */

public class MCNMedicationsInteractor implements MCNMedicationsContract.Interactor, AwsGetMedications, AwsUpdateMedications, AwsSearchMedications {

    final MCNMedicationsContract.InteractorOutput output;

    public MCNMedicationsInteractor(MCNMedicationsContract.InteractorOutput output) {
        this.output = output;
    }

    @Override
    public void getMedications() {
        AwsNetworkManager.getInstance().getMedications(AwsManager.getInstance().getPatient(), this);
    }

    @Override
    public void getMedicationsComplete(List<Medication> medications) {
        output.receivedMedications(medications, null);
    }

    @Override
    public void getMedicationsFailed(String errorMessage) {
        output.receivedMedications(null, errorMessage);
    }

    @Override
    public void updateMedications(List<Medication> medicationsToSave) {
        AwsNetworkManager.getInstance().updateMedications(AwsManager.getInstance().getPatient(), medicationsToSave, this);
    }

    @Override
    public void updateMedicationsComplete(List<Medication> medications) {
        output.receivedUpdateMedications(medications, null);
    }

    @Override
    public void updateMedicationsFailed(String errorMessage) {
        output.receivedUpdateMedications(null, errorMessage);
    }

    @Override
    public void searchMedications(final String searchText) {
        AwsNetworkManager.getInstance().searchMedications(AwsManager.getInstance().getPatient(), searchText, this);
    }

    @Override
    public void searchMedicationsComplete(List<Medication> medications) {
        output.receivedSearchMedications(medications, null);
    }

    @Override
    public void searchMedicationsFailed(String errorMessage) {
        output.receivedSearchMedications(null, errorMessage);
    }
}
