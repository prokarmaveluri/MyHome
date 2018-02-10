package com.prokarma.myhome.features.televisit.interfaces;

import com.americanwell.sdk.entity.health.Medication;

import java.util.List;

/**
 * Created by kwelsh on 12/1/17.
 */

public interface AwsGetMedications {
    void getMedicationsComplete(List<Medication> medications);
    void getMedicationsFailed(String errorMessage);
}
