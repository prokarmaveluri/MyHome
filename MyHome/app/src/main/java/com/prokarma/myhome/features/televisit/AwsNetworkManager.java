package com.prokarma.myhome.features.televisit;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.americanwell.sdk.entity.consumer.DependentUpdate;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.ConsumerFeedbackQuestion;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.StartVisitCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.televisit.interfaces.AwsCancelVideoVisit;
import com.prokarma.myhome.features.televisit.interfaces.AwsConsumer;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetAllergies;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetConditions;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetPharmacy;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetVisitReports;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetVisitSummary;
import com.prokarma.myhome.features.televisit.interfaces.AwsInitialization;
import com.prokarma.myhome.features.televisit.interfaces.AwsSearchMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsSendVisitFeedback;
import com.prokarma.myhome.features.televisit.interfaces.AwsSendVisitRating;
import com.prokarma.myhome.features.televisit.interfaces.AwsStartVideoVisit;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdateConsumer;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdateDependent;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdateMedications;
import com.prokarma.myhome.features.televisit.interfaces.AwsUpdatePharmacy;
import com.prokarma.myhome.features.televisit.interfaces.AwsUserAuthentication;
import com.prokarma.myhome.features.televisit.visitreports.ui.MCNReportsComparator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by kwelsh on 12/1/17.
 */

public class AwsNetworkManager {
    private static AwsNetworkManager instance;
    private static final int ONGOING_VISIT_NOTIFICATION_ID = 12345;

    public static AwsNetworkManager getInstance() {

        if (null == instance) {
            instance = new AwsNetworkManager();
        }
        return instance;
    }

    public void initializeAwsdk(@NonNull final String baseServiceUrl, @NonNull final String clientKey, @Nullable final String launchUri) {
        initializeAwsdk(baseServiceUrl, clientKey, launchUri, null);
    }

    public void initializeAwsdk(@NonNull String baseServiceUrl, @NonNull String clientKey, @Nullable final String launchUri, @Nullable final AwsInitialization awsInitialization) {

        //29260: app is  crashing when turn off location,microphone,and camera in settings>try to login after deactivating the permission to the app
        /*if (baseServiceUrl == null
                && clientKey == null) {
            Intent intentHome = new Intent(AuthManager.getInstance().getContext(), LoginActivity.class);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(AuthManager.getInstance().getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
            ActivityCompat.startActivity(AuthManager.getInstance().getContext(), intentHome, options.toBundle());
        }*/
        /*if (baseServiceUrl == null
                && clientKey == null
                && AppPreferences.getInstance().getPreference(Constants.ENV_AMWELL) != null
                && !AppPreferences.getInstance().getPreference(Constants.ENV_AMWELL).isEmpty()) {

            EnviHandler.initEnv(EnviHandler.EnvType.toEnvType(AppPreferences.getInstance().getPreference(Constants.ENV_MYHOME)));
            EnviHandler.initAmWellEnv(EnviHandler.AmWellEnvType.toAmWellEnvType(AppPreferences.getInstance().getPreference(Constants.ENV_AMWELL)));

            EnviHandler.setAttemptMutualAuth(AppPreferences.getInstance().getBooleanPreference(Constants.ENV_MUTUAL_AUTH));
            EnviHandler.setAmwellUsername(AppPreferences.getInstance().getPreference(Constants.ENV_AMWELL_USERNAME));
            EnviHandler.setAmwellPassword(AppPreferences.getInstance().getPreference(Constants.ENV_AMWELL_PASSWORD));

            baseServiceUrl = EnviHandler.AWSDK_URL;
            clientKey = EnviHandler.AWSDK_KEY;
        }*/

        Map<Integer, Object> initParams = new HashMap<>();
        initParams.put(AWSDK.InitParam.BaseServiceUrl, baseServiceUrl);
        initParams.put(AWSDK.InitParam.ApiKey, clientKey);
        initParams.put(AWSDK.InitParam.LaunchIntentData, launchUri);

        try {
            //29260: app is  crashing when turn off location,microphone,and camera in settings>try to login after deactivating the permission to the app
            //java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference
            if (AwsManager.getInstance().getAWSDK() == null) {
                return;
            }
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
                                Timber.e("AWSDK initialize. Error + " + sdkError);
                                AwsManager.getInstance().setHasInitializedAwsdk(false);

                                if (awsInitialization != null) {
                                    awsInitialization.initializationFailed(sdkError.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("AWSDK initialize. Something failed! :/");
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

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authenticationComplete(authentication);
                            }

                        } else {
                            Timber.e("getUsersAuthentication. Error + " + sdkError);
                            AwsManager.getInstance().setAuthentication(null);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authentciationFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getUsersAuthentication. Something failed! :/");
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
        AwsManager.getInstance().getAWSDK().authenticateMutual(
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
                            Timber.e("getUsersMutualAuthneticaion. Error + " + sdkError);
                            AwsManager.getInstance().setAuthentication(null);

                            if (awsUserAuthentication != null) {
                                awsUserAuthentication.authentciationFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getUsersMutualAuthneticaion. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setAuthentication(null);

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
                            AwsManager.getInstance().setPatient(null);
                            AwsManager.getInstance().setHasConsumer(true);

                            if (awsConsumer != null) {
                                awsConsumer.getConsumerComplete(consumer);
                            }
                        } else {
                            Timber.e("getConsumer. Error + " + sdkError);
                            AwsManager.getInstance().setConsumer(null);
                            AwsManager.getInstance().setPatient(null);
                            AwsManager.getInstance().setHasConsumer(false);

                            if (awsConsumer != null) {
                                awsConsumer.getConsumerFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getConsumer. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        AwsManager.getInstance().setConsumer(null);
                        AwsManager.getInstance().setPatient(null);
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
                            Timber.e("updatePharmacy. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);
                            AwsManager.getInstance().setConsumerPharmacy(null);

                            if (awsUpdatePharmacy != null) {
                                awsUpdatePharmacy.pharmacyUpdateFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("updatePharmacy. Something failed! :/");
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
                            Timber.e("getPharmacy. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetPharmacy != null) {
                                awsGetPharmacy.getPharmacyFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getPharmacy. Something failed! :/");
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
                            Timber.e("getMedications. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetMedications != null) {
                                awsGetMedications.getMedicationsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getMedications. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsGetMedications != null) {
                            awsGetMedications.getMedicationsFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void updateMedications(@NonNull final Consumer patient, final List<Medication> listToSave, @Nullable final AwsUpdateMedications awsUpdateMedications) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateMedications(
                patient,
                listToSave,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setMedications(listToSave);

                            if (awsUpdateMedications != null) {
                                awsUpdateMedications.updateMedicationsComplete(listToSave);
                            }
                        } else {
                            Timber.e("updateMedications. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsUpdateMedications != null) {
                                awsUpdateMedications.updateMedicationsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("updateMedications. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsUpdateMedications != null) {
                            awsUpdateMedications.updateMedicationsFailed(throwable.getMessage());
                        }
                    }
                });
    }

    public void searchMedications(@NonNull final Consumer patient, final String searchText, @Nullable final AwsSearchMedications awsSearchMedications) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().searchMedications(
                patient, searchText,
                new SDKValidatedCallback<List<Medication>, SDKError>() {
                    @Override
                    public void onResponse(List<Medication> medications, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setMedications(medications);

                            if (awsSearchMedications != null) {
                                awsSearchMedications.searchMedicationsComplete(medications);
                            }
                        } else {
                            Timber.e("searchMedications. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsSearchMedications != null) {
                                awsSearchMedications.searchMedicationsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("searchMedications. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsSearchMedications != null) {
                            awsSearchMedications.searchMedicationsFailed(throwable.getMessage());
                        }
                    }

                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        Timber.e("searchMedications. onValidationFailure! :/");

                        if (awsSearchMedications != null) {
                            awsSearchMedications.searchMedicationsFailed("validation failed");
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
                            Timber.e("getConditions. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetConditions != null) {
                                awsGetConditions.getConditionsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getConditions. Something failed! :/");
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
                            Timber.e("getAllergies. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetAllergies != null) {
                                awsGetAllergies.getAllergiesFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getAllergies. Something failed! :/");
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
                        Timber.w("startVideoVisit. onValidationFailure " + map);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onValidationFailure(map);
                        }
                    }

                    @Override
                    public void onProviderEntered(@NonNull Intent intent) {
                        Timber.d("startVideoVisit. onProviderEntered " + intent);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onProviderEntered(intent);
                        }
                    }

                    @Override
                    public void onStartVisitEnded(@NonNull String s) {
                        Timber.d("startVideoVisit. onStartVisitEnded " + s);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onStartVisitEnded(s);
                        }
                    }

                    @Override
                    public void onPatientsAheadOfYouCountChanged(int i) {
                        Timber.d("startVideoVisit. onPatientsAheadOfYouCountChanged " + i);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onPatientsAheadOfYouCountChanged(i);
                        }
                    }

                    @Override
                    public void onSuggestedTransfer() {
                        Timber.d("startVideoVisit. onSuggestedTransfer ");

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onSuggestedTransfer();
                        }
                    }

                    @Override
                    public void onChat(@NonNull ChatReport chatReport) {

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onChat(chatReport);
                        }
                    }

                    @Override
                    public void onPollFailure(@NonNull Throwable throwable) {
                        Timber.w("startVideoVisit. onPollFailure " + throwable);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onPollFailure(throwable);
                        }
                    }

                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onResponse(aVoid, sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.w("startVideoVisit. onFailure " + throwable);

                        if (awsStartVideoVisit != null) {
                            awsStartVideoVisit.onFailure(throwable);
                        }
                    }
                }
        );
    }

    public void cancelVideoVisit(@NonNull final Visit visit, @Nullable final AwsCancelVideoVisit awsCancelVideoVisit) {
        if (AwsManager.getInstance().getAWSDK() == null ||
                AwsManager.getInstance().getVisit() == null ||
                !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

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
                            Timber.e("cancelVideoVisit. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsCancelVideoVisit != null) {
                                awsCancelVideoVisit.cancelVideoVisitFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("cancelVideoVisit. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsCancelVideoVisit != null) {
                            awsCancelVideoVisit.cancelVideoVisitFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

    public void abandonVideoVisit() {
        if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        AwsManager.getInstance().getAWSDK().getVisitManager().abandonCurrentVisit();
    }

    public void createVisitNotification(Context context, Activity activity, Intent intent) {
        removeVideoVisitNotification(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // set up ongoing notification
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        builder.setSmallIcon(R.drawable.ic_local_hospital_white_18dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.video_console_ongoing_notification, AwsManager.getInstance().getVisit().getAssignedProvider().getFullName()))
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        notificationManager.notify(ONGOING_VISIT_NOTIFICATION_ID, builder.build());

        AwsManager.getInstance().setVisitOngoing(true);
    }

    public void removeVideoVisitNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ONGOING_VISIT_NOTIFICATION_ID);

        AwsManager.getInstance().setVisitOngoing(false);
    }

    public void getVisitSummary(@NonNull final Visit visit, @Nullable final AwsGetVisitSummary awsGetVisitSummary) {
        AwsManager.getInstance().getAWSDK().getVisitManager().getVisitSummary(
                visit,
                new SDKCallback<VisitSummary, SDKError>() {
                    @Override
                    public void onResponse(@Nullable VisitSummary visitSummary, @Nullable SDKError sdkError) {
                        if (sdkError == null) {
                            if (awsGetVisitSummary != null) {
                                awsGetVisitSummary.getVisitSummaryComplete(visitSummary);
                            }
                        } else {
                            Timber.e("getVisitSummary. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsGetVisitSummary != null) {
                                awsGetVisitSummary.getVisitSummaryFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable throwable) {
                        Timber.e("getVisitSummary. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsGetVisitSummary != null) {
                            awsGetVisitSummary.getVisitSummaryFailed(throwable.getMessage());
                        }
                    }
                }
        );

    }

    public void sendVisitRatings(@NonNull final Visit visit, final int providerRating, final int experienceRating, @Nullable final AwsSendVisitRating awsSendVisitRating) {
        AwsManager.getInstance().getAWSDK().getVisitManager().sendRatings(
                visit,
                providerRating,
                experienceRating,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(@Nullable Void aVoid, @Nullable SDKError sdkError) {
                        if (sdkError == null) {
                            if (awsSendVisitRating != null) {
                                awsSendVisitRating.sendVisitRatingComplete();
                            }
                        } else {
                            Timber.e("sendVisitRatings. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsSendVisitRating != null) {
                                awsSendVisitRating.sendVisitRatingFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable throwable) {
                        Timber.e("sendVisitRatings. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsSendVisitRating != null) {
                            awsSendVisitRating.sendVisitRatingFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

    public void sendVisitFeedback(@NonNull final Visit visit, @NonNull final ConsumerFeedbackQuestion consumerFeedbackQuestion, @Nullable final AwsSendVisitFeedback awsSendVisitFeedback) {
        AwsManager.getInstance().getAWSDK().getVisitManager().sendVisitFeedback(
                visit,
                consumerFeedbackQuestion,
                new SDKValidatedCallback<Void, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        Timber.e("sendVisitFeedback. onValidationFailure Something failed! :/");
                        Timber.e("Map: " + map);

                        if (awsSendVisitFeedback != null) {
                            awsSendVisitFeedback.sendVisitFeedbackFailed("Validation Failed");
                        }
                    }

                    @Override
                    public void onResponse(@Nullable Void aVoid, @Nullable SDKError sdkError) {
                        if (sdkError == null) {
                            if (awsSendVisitFeedback != null) {
                                awsSendVisitFeedback.sendVisitFeedbackComplete();
                            }
                        } else {
                            Timber.e("sendVisitFeedback. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (awsSendVisitFeedback != null) {
                                awsSendVisitFeedback.sendVisitFeedbackFailed(sdkError.getMessage());
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Throwable throwable) {
                        Timber.e("sendVisitFeedback. Something failed! :/");
                        Timber.e("Throwable = " + throwable);

                        if (awsSendVisitFeedback != null) {
                            awsSendVisitFeedback.sendVisitFeedbackFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }

    public void updateConsumer(@NonNull final ConsumerUpdate consumerUpdate, @Nullable final AwsUpdateConsumer awsUpdateConsumer) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateConsumer((ConsumerUpdate) consumerUpdate, new SDKValidatedCallback<Consumer, SDKPasswordError>() {
            @Override
            public void onValidationFailure(@NonNull Map<String, String> map) {
                Timber.e("updateConsumer. Something failed! :/");
                Timber.e("Map: " + map);

                if (awsUpdateConsumer != null) {
                    awsUpdateConsumer.updateConsumerFailed("Validation Failed");
                }
            }

            @Override
            public void onResponse(@Nullable Consumer consumer, @Nullable SDKPasswordError sdkPasswordError) {
                if (sdkPasswordError == null) {
                    if (awsUpdateConsumer != null) {
                        awsUpdateConsumer.updateConsumerComplete(consumer);
                    }
                } else {
                    Timber.e("updateConsumer. Something failed! :/");
                    Timber.e("SDK Error: " + sdkPasswordError);

                    if (awsUpdateConsumer != null) {
                        awsUpdateConsumer.updateConsumerFailed(sdkPasswordError.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                Timber.e("updateConsumer. Something failed! :/");
                Timber.e("Throwable = " + throwable);

                if (awsUpdateConsumer != null) {
                    awsUpdateConsumer.updateConsumerFailed(throwable.getMessage());
                }
            }
        });
    }

    public void updateDependent(@NonNull final DependentUpdate consumerUpdate, @Nullable final AwsUpdateDependent awsUpdateConsumer) {
        AwsManager.getInstance().getAWSDK().getConsumerManager().updateDependent((DependentUpdate) consumerUpdate, new SDKValidatedCallback<Consumer, SDKError>() {
            @Override
            public void onValidationFailure(@NonNull Map<String, String> map) {
                Timber.e("updateDependent. onValidationFailure. Something failed! :/");
                Timber.e("Map: " + map);

                if (awsUpdateConsumer != null) {
                    awsUpdateConsumer.updateDependentFailed("Validation Failed");
                }
            }

            @Override
            public void onResponse(@Nullable Consumer consumer, @Nullable SDKError sdkError) {
                if (sdkError == null) {
                    if (awsUpdateConsumer != null) {
                        awsUpdateConsumer.updateDependentComplete(consumer);
                    }
                } else {
                    Timber.e("updateDependent. Something failed! :/");
                    Timber.e("SDK Error: " + sdkError);

                    if (awsUpdateConsumer != null) {
                        awsUpdateConsumer.updateDependentFailed(sdkError.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                Timber.e("updateDependent. Something failed! :/");
                Timber.e("Throwable = " + throwable);

                if (awsUpdateConsumer != null) {
                    awsUpdateConsumer.updateDependentFailed(throwable.getMessage());
                }
            }
        });
    }

    public void getVisitReports(@Nullable final AwsGetVisitReports awsGetVisitReports) {

        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReports(
                AwsManager.getInstance().getPatient(),
                null, /* pull reports from date. pass null to fetch all reports irrespective of date. */
                false, /* scheduledOnly */
                new SDKCallback<List<VisitReport>, SDKError>() {
                    @Override
                    public void onResponse(List<VisitReport> visitReports, SDKError sdkError) {
                        if (sdkError == null) {
                            Collections.sort(visitReports, new MCNReportsComparator());
                            AwsManager.getInstance().setVisitReports(visitReports);

                            if (awsGetVisitReports != null) {
                                awsGetVisitReports.getVisitReportsComplete(visitReports);
                            }
                        } else {
                            if (awsGetVisitReports != null) {
                                awsGetVisitReports.getVisitReportsFailed(sdkError.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (awsGetVisitReports != null) {
                            awsGetVisitReports.getVisitReportsFailed(throwable.getMessage());
                        }
                    }
                }
        );
    }
}
