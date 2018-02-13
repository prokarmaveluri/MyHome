package com.prokarma.myhome.features.televisit.medications;

import android.support.annotation.NonNull;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsSearchMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdateMedications;

import java.util.List;
import java.util.Map;

/**
 * Created by veluri on 2/12/18.
 */

public class MCNMedicationsInteractor implements MCNMedicationsContract.Interactor, AwsGetMedications, AwsUpdateMedications, AwsSearchMedications {

    final MCNMedicationsContract.InteractorOutput output;

    public MCNMedicationsInteractor(MCNMedicationsContract.InteractorOutput output) {
        this.output = output;
    }

    @Override
    public void getMedications(final AwsGetMedications awsGetMedications) {

        AwsManager.getInstance().getAWSDK().getConsumerManager().getMedications(
                AwsManager.getInstance().getPatient(),
                new SDKCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setMedications(medications);

                            if (awsGetMedications != null) {
                                awsGetMedications.getMedicationsComplete(medications);
                            }
                        } else {
                            if (awsGetMedications != null) {
                                awsGetMedications.getMedicationsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (awsGetMedications != null) {
                            awsGetMedications.getMedicationsFailed(throwable.getMessage());
                        }
                    }
                }
        );
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
    public void updateMedications(final AwsUpdateMedications awsUpdateMedications) {

        AwsManager.getInstance().getAWSDK().getConsumerManager().getMedications(
                AwsManager.getInstance().getPatient(),
                new SDKCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setMedications(medications);

                            if (awsUpdateMedications != null) {
                                awsUpdateMedications.updateMedicationsComplete(medications);
                            }
                        } else {
                            if (awsUpdateMedications != null) {
                                awsUpdateMedications.updateMedicationsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (awsUpdateMedications != null) {
                            awsUpdateMedications.updateMedicationsFailed(throwable.getMessage());
                        }
                    }
                }
        );
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
    public void searchMedications(final String searchText, final AwsSearchMedications awsSearchMedications) {

        AwsManager.getInstance().getAWSDK().getConsumerManager().searchMedications(
                AwsManager.getInstance().getPatient(),
                searchText,
                new SDKValidatedCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        if (sdkError == null) {
                            if (awsSearchMedications != null) {
                                awsSearchMedications.searchMedicationsComplete(medications);
                            }
                        } else {
                            if (awsSearchMedications != null) {
                                awsSearchMedications.searchMedicationsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        if (awsSearchMedications != null) {
                            awsSearchMedications.searchMedicationsFailed("");
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (awsSearchMedications != null) {
                            awsSearchMedications.searchMedicationsFailed(throwable.getMessage());
                        }
                    }
                }
        );
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
