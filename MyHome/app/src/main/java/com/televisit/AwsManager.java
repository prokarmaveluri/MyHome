package com.televisit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.AWSDKFactory;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.logging.AWSDKLogger;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.BuildConfig;
import com.televisit.interfaces.AwsConsumer;
import com.televisit.interfaces.AwsInitialization;
import com.televisit.interfaces.AwsUserAuthentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_AUDIO;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_DEFAULT;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_MEDIA;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_NETWORKING;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_PERMISSIONS;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_VIDEO;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_VISIT;

/**
 * Created by cmajji on 10/31/17.
 */

public class AwsManager {
    private static final AwsManager ourInstance = new AwsManager();
    private static AWSDK awsdk = null;

    private List<Allergy> allergies;
    private List<Condition> conditions;
    private List<Practice> practices;
    private List<Pharmacy> pharmacies;
    private List<Medication> medications;
    private Pharmacy consumerPharmacy;
    private Authentication authentication;
    private VisitContext visitContext;
    private Visit visit;
    private Consumer consumer;
    private boolean hasMedicationsFilledOut;
    private boolean hasAllergiesFilledOut;
    private boolean hasConditionsFilledOut;
    private boolean hasInitializedAwsdk;

    public static AwsManager getInstance() {
        return ourInstance;
    }

    private AwsManager() {
    }

    public void init(Context context) {
        try {
            this.awsdk = AWSDKFactory.getAWSDK(context);

            if (BuildConfig.REPORT_LOGS) {
                awsdk.getDefaultLogger().setPriority(3); // set log level to debug - Log.DEBUG
            } else {
                awsdk.getDefaultLogger().setPriority(6); // set log level to error - Log.ERROR
            }

            // Set the categories for the logs you want displayed. Setting this to null will allow all categories to be displayed.
            @AWSDKLogger.AWSDKLogCategory
            String[] categories = {LOG_CATEGORY_DEFAULT, LOG_CATEGORY_VIDEO, LOG_CATEGORY_AUDIO,
                    LOG_CATEGORY_PERMISSIONS, LOG_CATEGORY_MEDIA, LOG_CATEGORY_VISIT, LOG_CATEGORY_NETWORKING};
            awsdk.getDefaultLogger().setLogCategories(categories);
        } catch (AWSDKInstantiationException e) {
            Timber.e(e);
        }
    }

    /**
     * This should be the only place you get the {@link AWSDK} instance object.
     *
     * @return
     */
    public AWSDK getAWSDK() {
        return awsdk;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public List<Practice> getPractices() {
        return practices;
    }

    public void setPractices(List<Practice> practices) {
        this.practices = practices;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;

        setHasConditionsFilledOut(false);
        for (Condition condition : conditions) {
            if (condition.isCurrent()) {
                setHasConditionsFilledOut(true);
                break;
            }
        }
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;

        setHasAllergiesFilledOut(false);
        for (Allergy allergy : allergies) {
            if (allergy.isCurrent()) {
                setHasAllergiesFilledOut(true);
                break;
            }
        }
    }

    public List<Pharmacy> getPharmacies() {
        return pharmacies;
    }

    public void setPharmacies(List<Pharmacy> pharmacies) {
        this.pharmacies = pharmacies;
    }

    public Pharmacy getConsumerPharmacy() {
        return consumerPharmacy;
    }

    public void setConsumerPharmacy(Pharmacy consumerPharmacy) {
        this.consumerPharmacy = consumerPharmacy;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;

        if (medications != null && !medications.isEmpty()) {
            setHasMedicationsFilledOut(true);
        } else {
            setHasMedicationsFilledOut(false);
        }
    }

    public VisitContext getVisitContext() {
        return visitContext;
    }

    public void setVisitContext(VisitContext visitContext) {
        this.visitContext = visitContext;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public boolean isHasMedicationsFilledOut() {
        return hasMedicationsFilledOut;
    }

    public void setHasMedicationsFilledOut(boolean hasMedicationsFilledOut) {
        this.hasMedicationsFilledOut = hasMedicationsFilledOut;
    }

    public boolean isHasAllergiesFilledOut() {
        return hasAllergiesFilledOut;
    }

    public void setHasAllergiesFilledOut(boolean hasAllergiesFilledOut) {
        this.hasAllergiesFilledOut = hasAllergiesFilledOut;
    }

    public boolean isHasConditionsFilledOut() {
        return hasConditionsFilledOut;
    }

    public void setHasConditionsFilledOut(boolean hasConditionsFilledOut) {
        this.hasConditionsFilledOut = hasConditionsFilledOut;
    }

    public boolean isHasInitializedAwsdk() {
        return hasInitializedAwsdk;
    }

    public void setHasInitializedAwsdk(boolean hasInitializedAwsdk) {
        this.hasInitializedAwsdk = hasInitializedAwsdk;
    }

    public void authenticateUser(Authentication authentication) {
        this.awsdk.getConsumerManager().getConsumer(
                authentication,
                new SDKCallback<Consumer, SDKError>() {
                    @Override
                    public void onResponse(Consumer consumer, SDKError sdkError) {
                        if (sdkError == null) {
                            Timber.i("Authneticated User : " + consumer.getFullName());
                            AwsManager.getInstance().setConsumer(consumer);
                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setConsumer(null);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setConsumer(null);
                    }
                }
        );
    }

    public void getUsersAuthentication(@NonNull final String username, @NonNull final String password) {
        getUsersAuthentication(username, password, null);
    }

    public void getUsersAuthentication(@NonNull final String username, @NonNull final String password, @Nullable final AwsUserAuthentication awsUserAuthentication) {
        //techincally, the first parameter in this call is "legalResidence" https://sdk.americanwell.com/?page_id=7377
        awsdk.authenticate(
                username,
                password,
                username,
                new SDKCallback<Authentication, SDKError>() {
                    @Override
                    public void onResponse(Authentication authentication, SDKError sdkError) {
                        if (sdkError == null) {
                            Timber.i("Authentication : " + authentication);
                            AwsManager.getInstance().setAuthentication(authentication);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authenticationComplete(authentication);
                            }

                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setAuthentication(null);

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
        awsdk.authenticateMutual(
                amWellToken,
                new SDKCallback<Authentication, SDKError>() {
                    @Override
                    public void onResponse(Authentication authentication, SDKError sdkError) {
                        if (sdkError == null) {
                            Timber.i("Authentication : " + authentication);
                            AwsManager.getInstance().setAuthentication(authentication);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authenticationComplete(authentication);
                            }
                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setAuthentication(null);

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

                        if (awsUserAuthentication != null) {
                            awsUserAuthentication.authentciationFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void initializeAwsdk(@NonNull final String baseServiceUrl, @NonNull final String clientKey, @Nullable final String launchUri) {
        initializeAwsdk(baseServiceUrl, clientKey, launchUri, null);
    }

    public void initializeAwsdk(@NonNull final String baseServiceUrl, @NonNull final String clientKey, @Nullable final String launchUri, @Nullable final AwsInitialization awsInitialization) {
        final Map<AWSDK.InitParam, Object> initParams = new HashMap<>();
        initParams.put(AWSDK.InitParam.BaseServiceUrl, baseServiceUrl);
        initParams.put(AWSDK.InitParam.ApiKey, clientKey);
        initParams.put(AWSDK.InitParam.LaunchIntentData, launchUri);

        try {
            this.awsdk.initialize(
                    initParams,
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null) {
                                setHasInitializedAwsdk(true);

                                if (awsInitialization != null) {
                                    awsInitialization.initializationComplete();
                                }
                            } else {
                                Timber.e("Error + " + sdkError);
                                setHasInitializedAwsdk(false);

                                if (awsInitialization != null) {
                                    awsInitialization.initializationFailed(sdkError.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            setHasInitializedAwsdk(false);

                            if (awsInitialization != null) {
                                awsInitialization.initializationFailed(throwable.getMessage());
                            }
                        }
                    });
        } catch (AWSDKInitializationException e) {
            Timber.e(e);
            setHasInitializedAwsdk(false);

            if (awsInitialization != null) {
                awsInitialization.initializationFailed(e.getMessage());
            }
        }
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

                            if (awsConsumer != null) {
                                awsConsumer.consumerComplete(consumer);
                            }
                        } else {
                            Timber.e("Error + " + sdkError);
                            AwsManager.getInstance().setConsumer(null);

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

                        if (awsConsumer != null) {
                            awsConsumer.consumerFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

}
