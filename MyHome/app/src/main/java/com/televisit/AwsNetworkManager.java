package com.televisit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.manager.SDKCallback;
import com.televisit.interfaces.AwsConsumer;
import com.televisit.interfaces.AwsInitialization;
import com.televisit.interfaces.AwsPharmacyUpdate;
import com.televisit.interfaces.AwsUserAuthentication;

import java.util.HashMap;
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

    public void getConsumer(@NonNull final Authentication authentication) {
        getConsumer(authentication, null);
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
                                awsConsumer.consumerComplete(consumer);
                            }
                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setConsumer(null);
                            AwsManager.getInstance().setDependent(null);
                            AwsManager.getInstance().setHasConsumer(false);

                            if (awsConsumer != null) {
                                awsConsumer.consumerFailed(sdkError.getMessage());
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
                            awsConsumer.consumerFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }


    public void updateConsumerPharmacy(@NonNull final Consumer patient, @Nullable final Pharmacy pharmacy) {
        updateConsumerPharmacy(patient, pharmacy, null);
    }

    public void updateConsumerPharmacy(@NonNull final Consumer patient, @Nullable final Pharmacy pharmacy, final AwsPharmacyUpdate awsPharmacyUpdate) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateConsumerPharmacy(
                patient,
                pharmacy,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setConsumerPharmacy(pharmacy);

                            if (awsPharmacyUpdate != null) {
                                awsPharmacyUpdate.pharmacyUpdateComplete(pharmacy);
                            }

                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);
                            AwsManager.getInstance().setConsumerPharmacy(null);

                            if (awsPharmacyUpdate != null) {
                                awsPharmacyUpdate.pharmacyUpdateFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setConsumerPharmacy(null);

                        if (awsPharmacyUpdate != null) {
                            awsPharmacyUpdate.pharmacyUpdateFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }
}
