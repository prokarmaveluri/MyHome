package com.prokarma.myhome.features.televisit.interfaces;

import com.americanwell.sdk.entity.health.Medication;

import java.util.List;

/**
 * Created by veluri on 2/12/18.
 */

public interface AwsUpdateMedications {

    void updateMedicationsComplete(List<Medication> medications);

    void updateMedicationsFailed(String errorMessage);
}
