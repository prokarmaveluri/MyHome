package com.televisit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.StartVisitCallback;
import com.televisit.interfaces.AwsCancelVideoVisit;
import com.televisit.interfaces.AwsConsumer;
import com.televisit.interfaces.AwsGetAllergies;
import com.televisit.interfaces.AwsGetConditions;
import com.televisit.interfaces.AwsGetMedications;
import com.televisit.interfaces.AwsGetPharmacy;
import com.televisit.interfaces.AwsInitialization;
import com.televisit.interfaces.AwsStartVideoVisit;
import com.televisit.interfaces.AwsUpdatePharmacy;
import com.televisit.interfaces.AwsUserAuthentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by kwelsh on 12/1/17.
 */

public class AwsNetworkManager {
    private static AwsNetworkManager instance;

    public static AwsNetworkManager getInstance() {

        if (null == instance) {
            instance = new AwsNetworkManager();
        }
        return instance;
    }


    public void initializeAwsdk(@NonNull final String baseServiceUrl, @NonNull final String clientKey, @Nullable final String launchUri) {
        initializeAwsdk(baseServiceUrl, clientKey, launchUri, null);
    }

    public void initializeAwsdk(@NonNull final String baseServiceUrl, @NonNull final String clientKey, @Nullable final String launchUri, @Nullable final AwsInitialization awsInitialization) {
        final Map<Integer, Object> initParams = new HashMap<>();
        initParams.put(AWSDK.InitParam.BaseServiceUrl, baseServiceUrl);
        initParams.put(AWSDK.InitParam.ApiKey, clientKey);
        initParams.put(AWSDK.InitParam.LaunchIntentData, launchUri);

        try {
            AwsManager.getInstance().getAWSDK().initialize(
                    initParams,
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null) {
                                AwsManager.getInstance().setHasInitializedAwsdk(true);

                                if (awsInitialization != null) {
                                    awsInitialization.initializationComplete();
                                }
                            } else {
                                Timber.e("Error + " + sdkError);
                                AwsManager.getInstance().setHasInitializedAwsdk(false);

                                if (awsInitialization != null) {
                                    awsInitialization.initializationFailed(sdkError.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            AwsManager.getInstance().setHasInitializedAwsdk(false);

                            if (awsInitialization != null) {
                                awsInitialization.initializationFailed(throwable.getMessage());
                            }
                        }
                    });
        } catch (AWSDKInitializationException e) {
            Timber.e(e);
            AwsManager.getInstance().setHasInitializedAwsdk(false);

            if (awsInitialization != null) {
                awsInitialization.initializationFailed(e.getMessage());
            }
        }
    }

    public void getUsersAuthentication(@NonNull final String username, @NonNull final String password) {
        getUsersAuthentication(username, password, null);
    }

    public void getUsersAuthentication(@NonNull final String username, @NonNull final String password, @Nullable final AwsUserAuthentication awsUserAuthentication) {
        //techincally, the first parameter in this call is "legalResidence" https://sdk.americanwell.com/?page_id=7377
        AwsManager.getInstance().getAWSDK().authenticate(
                username,
                password,
                username,
                new SDKCallback<Authentication, SDKError>() {
                    @Override
                    public void onResponse(Authentication authentication, SDKError sdkError) {
                        if (sdkError == null) {
                            Timber.i("Authentication : " + authentication);
                            AwsManager.getInstance().setAuthentication(authentication);
                            AwsManager.getInstance().setHasAuthenticated(true);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authenticationComplete(authentication);
                            }

                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setAuthentication(null);
                            AwsManager.getInstance().setHasAuthenticated(false);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authentciationFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setAuthentication(null);
                        AwsManager.getInstance().setHasAuthenticated(false);

                        if (awsUserAuthentication != null) {
                            awsUserAuthentication.authentciationFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void getUsersMutualAuthneticaion(@NonNull final String amWellToken) {
        getUsersMutualAuthneticaion(amWellToken, null);
    }

    public void getUsersMutualAuthneticaion(@NonNull final String amWellToken, @Nullable final AwsUserAuthentication awsUserAuthentication) {
        AwsManager.getInstance().getAWSDK().authenticateMutual(
                amWellToken,
                new SDKCallback<Authentication, SDKError>() {
                    @Override
                    public void onResponse(Authentication authentication, SDKError sdkError) {
                        if (sdkError == null) {
                            Timber.i("Authentication : " + authentication);
                            AwsManager.getInstance().setAuthentication(authentication);
                            AwsManager.getInstance().setHasAuthenticated(false);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authenticationComplete(authentication);
                            }
                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setAuthentication(null);
                            AwsManager.getInstance().setHasAuthenticated(false);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authentciationFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setAuthentication(null);
                        AwsManager.getInstance().setHasAuthenticated(false);

                        if (awsUserAuthentication != null) {
                            awsUserAuthentication.authentciationFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void getConsumer(@NonNull final Authentication authentication, @Nullable final AwsConsumer awsConsumer) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().getConsumer(
                authentication,
                new SDKCallback<Consumer, SDKError>() {
                    @Override
                    public void onResponse(Consumer consumer, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setConsumer(consumer);
                            AwsManager.getInstance().setDependent(null);
                            AwsManager.getInstance().setHasConsumer(true);

                            if (awsConsumer != null) {
                                awsConsumer.getConsumerComplete(consumer);
                            }
                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setConsumer(null);
                            AwsManager.getInstance().setDependent(null);
                            AwsManager.getInstance().setHasConsumer(false);

                            if (awsConsumer != null) {
                                awsConsumer.getConsumerFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setConsumer(null);
                        AwsManager.getInstance().setDependent(null);
                        AwsManager.getInstance().setHasConsumer(false);

                        if (awsConsumer != null) {
                            awsConsumer.getConsumerFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

    public void updatePharmacy(@NonNull final Consumer patient, @Nullable final Pharmacy pharmacy, final AwsUpdatePharmacy awsUpdatePharmacy) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateConsumerPharmacy(
                patient,
                pharmacy,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setConsumerPharmacy(pharmacy);

                            if (awsUpdatePharmacy != null) {
                                awsUpdatePharmacy.pharmacyUpdateComplete(pharmacy);
                            }

                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);
                            AwsManager.getInstance().setConsumerPharmacy(null);

                            if (awsUpdatePharmacy != null) {
                                awsUpdatePharmacy.pharmacyUpdateFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setConsumerPharmacy(null);

                        if (awsUpdatePharmacy != null) {
                            awsUpdatePharmacy.pharmacyUpdateFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

    public void getPharmacy(@NonNull final Consumer patient, @Nullable final AwsGetPharmacy awsGetPharmacy) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().getConsumerPharmacy(
                patient, new SDKCallback<Pharmacy, SDKError>() {
                    @Override
                    public void onResponse(Pharmacy pharmacy, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setConsumerPharmacy(pharmacy);

                            if (awsGetPharmacy != null) {
                                awsGetPharmacy.getPharmacyComplete(pharmacy);
                            }
                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetPharmacy != null) {
                                awsGetPharmacy.getPharmacyFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsGetPharmacy != null) {
                            awsGetPharmacy.getPharmacyFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void getMedications(@NonNull final Consumer patient, @Nullable final AwsGetMedications awsGetMedications) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().getMedications(
                patient, new SDKCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setMedications(medications);

                            if (awsGetMedications != null) {
                                awsGetMedications.getMedicationsComplete(medications);
                            }
                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetMedications != null) {
                                awsGetMedications.getMedicationsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsGetMedications != null) {
                            awsGetMedications.getMedicationsFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void getConditions(@NonNull final Consumer patient, @Nullable final AwsGetConditions awsGetConditions) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().getConditions(
                patient,
                new SDKCallback<List<Condition>, SDKError>() {
                    @Override
                    public void onResponse(List<Condition> conditions, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setConditions(conditions);

                            if (awsGetConditions != null) {
                                awsGetConditions.getConditionsComplete(conditions);
                            }
                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetConditions != null) {
                                awsGetConditions.getConditionsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsGetConditions != null) {
                            awsGetConditions.getConditionsFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

    public void getAllergies(@NonNull final Consumer patient, @Nullable final AwsGetAllergies awsGetAllergies) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().getAllergies(
                patient,
                new SDKCallback<List<Allergy>, SDKError>() {
                    @Override
                    public void onResponse(List<Allergy> allergies, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setAllergies(allergies);

                            if (awsGetAllergies != null) {
                                awsGetAllergies.getAllergiesComplete(allergies);
                            }
                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetAllergies != null) {
                                awsGetAllergies.getAllergiesFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsGetAllergies != null) {
                            awsGetAllergies.getAllergiesFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void startVideoVisit(@NonNull final Visit visit, @NonNull final Address location, @Nullable final Intent visitFinishedIntent, @Nullable final AwsStartVideoVisit awsStartVideoVisit) {
        AwsManager.getInstance().getAWSDK().getVisitManager().startVisit(
                visit,
                location,
                visitFinishedIntent,
                new StartVisitCallback() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        Timber.w("onValidationFailure " + map);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onValidationFailure(map);
                        }
                    }

                    @Override
                    public void onProviderEntered(@NonNull Intent intent) {
                        Timber.d("onProviderEntered " + intent);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onProviderEntered(intent);
                        }
                    }

                    @Override
                    public void onStartVisitEnded(@NonNull String s) {
                        Timber.d("onStartVisitEnded " + s);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onStartVisitEnded(s);
                        }
                    }

                    @Override
                    public void onPatientsAheadOfYouCountChanged(int i) {
                        Timber.d("onPatientsAheadOfYouCountChanged " + i);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onPatientsAheadOfYouCountChanged(i);
                        }
                    }

                    @Override
                    public void onSuggestedTransfer() {
                        Timber.d("onSuggestedTransfer ");

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onSuggestedTransfer();
                        }
                    }

                    @Override
                    public void onChat(@NonNull ChatReport chatReport) {
                        Timber.d("onChat " + chatReport);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onChat(chatReport);
                        }
                    }

                    @Override
                    public void onPollFailure(@NonNull Throwable throwable) {
                        Timber.w("onPollFailure " + throwable);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onPollFailure(throwable);
                        }
                    }

                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        Timber.d("onResponse " + aVoid + " " + sdkError);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onResponse(aVoid, sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.w("onFailure " + throwable);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onFailure(throwable);
                        }
                    }
                }
        );
    }

    public void cancelVideoVisit(@NonNull final Visit visit, @Nullable final AwsCancelVideoVisit awsCancelVideoVisit) {
        AwsManager.getInstance().getAWSDK().getVisitManager().cancelVisit(
                visit,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (sdkError == null) {
                            if (awsCancelVideoVisit != null) {
                                awsCancelVideoVisit.cancelVideoVisitComplete();
                            }

                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsCancelVideoVisit != null) {
                                awsCancelVideoVisit.cancelVideoVisitFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsCancelVideoVisit != null) {
                            awsCancelVideoVisit.cancelVideoVisitFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

}
