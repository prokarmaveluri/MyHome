package com.prokarma.myhome.features.profile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.entity.consumer.Consumer;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.preferences.ProviderResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/2/17.
 */

public class ProfileManager {
    private static Profile profile = null;
    public static ArrayList<Appointment> appointments = null;
    private static List<ProviderResponse> favoriteProviders = null;

    /**
     * Requests the Profile in the Singleton
     *
     * @return the Profile object stored in the Singleton
     */
    public static Profile getProfile() {
        return profile;
    }

    /**
     * Sets the Singleton to the given profile
     *
     * @param profile the Profile object to store as a singleton. Can be null to clear Singleton.
     */
    public static void setProfile(@Nullable Profile profile) {
        ProfileManager.profile = profile;
    }

    /**
     * Requests the Appointments in the Singleton
     *
     * @return the Appointments list stored in the Singleton
     */
    public static ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Sets the Singleton to the given appoinments
     *
     * @param appointments the Appointments object to store as a singleton. Can be null to clear Singleton.
     */
    public static void setAppointments(ArrayList<Appointment> appointments) {
        ProfileManager.appointments = appointments;
    }

    /**
     * Simply makes the network call for getting the Profile of the currently logged in user and loads it into Singleton
     */
    @SuppressWarnings("unused")
    public static void getProfileInfo() {
        String bearerToken = AuthManager.getInstance().getBearerToken();
        NetworkManager.getInstance().getProfile(bearerToken).enqueue(new Callback<ProfileGraphqlResponse>() {
            @Override
            public void onResponse(Call<ProfileGraphqlResponse> call, Response<ProfileGraphqlResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        Timber.d("ProfileManager.getProfile. Successful Response\n" + response);
                        ProfileManager.setProfile(response.body().getData().getUser());
                    } catch (NullPointerException ex) {
                        Timber.w(ex);
                    }
                } else {
                    Timber.e("ProfileManager.getProfile. Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<ProfileGraphqlResponse> call, Throwable t) {
                Timber.e("Something failed!\n" + t);
            }
        });
    }

    /**
     * Sends the profile information to the server to update the values
     *
     * @param bearer         the bearer token needed to provide authentication
     * @param updatedProfile the updated profile information being attempted
     */
    public static void updateProfile(@NonNull final String bearer, @NonNull final Profile updatedProfile, @Nullable final ProfileUpdateInterface profileUpdateInterface) {
        NetworkManager.getInstance().updateProfile(bearer, updatedProfile).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(updatedProfile);

                    /*
                    TODO Only comment this out if we need to update AmWell Profile when changing CIAM
                    if (AuthManager.getInstance().hasMyCare() && AwsManager.getInstance().isHasConsumer()) {
                        AwsManager.getInstance().getAWSDK().getNewAddress();

                        ConsumerUpdate update = AwsManager.getInstance().getAWSDK().getConsumerManager().getNewConsumerUpdate(AwsManager.getInstance().getPatient());

                        if (EnviHandler.isAttemptMutualAuth()) {
                            //Not sure what to put here. We don't have users password....
                        } else {
                            update.setPassword(EnviHandler.getAmwellPassword());
                        }

                        //Comment this back in once login works
                        update.setEmail(updatedProfile.email);
                        update.setFirstName(updatedProfile.firstName);
                        update.setLastName(updatedProfile.lastName);
                        update.setGender(updatedProfile.gender);
                        update.setPhone(CommonUtil.stripPhoneNumber(updatedProfile.phoneNumber));
                        update.setDob(DateUtil.convertReadabletoDob(updatedProfile.dateOfBirth));

                        com.americanwell.sdk.entity.Address userAddress = AwsManager.getInstance().getAWSDK().getNewAddress();

                        if (updatedProfile.address == null) {
                            updatedProfile.address = new Address();
                        }

                        userAddress.setAddress1(updatedProfile.address.line1);
                        userAddress.setAddress2(updatedProfile.address.line2);
                        userAddress.setCity(updatedProfile.address.city);
                        userAddress.setState(AwsManager.getInstance().getState(updatedProfile.address.stateOrProvince));
                        userAddress.setZipCode(updatedProfile.address.zipCode);

                        update.setAddress(userAddress);

                        AwsNetworkManager.getInstance().updateConsumer(update, new AwsUpdateConsumer() {
                            @Override
                            public void updateConsumerComplete(Consumer consumer) {
                                AwsManager.getInstance().setConsumer(consumer);

                                if(AwsManager.getInstance().isPatientMainConsumer()){
                                    AwsManager.getInstance().setPatient(consumer);
                                }
                            }

                            @Override
                            public void updateConsumerFailed(String errorMessage) {

                            }
                        });
                    }
                    */

                    if (profileUpdateInterface != null) {
                        profileUpdateInterface.profileUpdateComplete(updatedProfile);
                    }
                } else {
                    Timber.e("ProfileManager. updateProfile. Response, but not successful?\n" + response);

                    if (profileUpdateInterface != null) {
                        profileUpdateInterface.profileUpdateFailed(response != null ? response.message() : "Response is null");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("Something failed!\n" + t);

                if (profileUpdateInterface != null) {
                    profileUpdateInterface.profileUpdateFailed(t.getMessage());
                }
            }
        });
    }

    public static Profile getProfileFromConsumerObject(final Consumer consumer) {

        Profile profile = Profile.copy(getProfile());
        profile.firstName = consumer.getFirstName();
        profile.lastName = consumer.getLastName();
        profile.phoneNumber = consumer.getPhone();
        profile.gender = consumer.getGender();

        if (profile.address == null) {
            profile.address = new Address();
        }

        profile.address.line1 = consumer.getAddress().getAddress1();
        profile.address.line2 = consumer.getAddress().getAddress2();
        profile.address.city = consumer.getAddress().getCity();
        profile.address.stateOrProvince = consumer.getAddress().getState().getCode();
        profile.address.zipCode = consumer.getAddress().getZipCode();

        profile.dateOfBirth = DateUtil.convertReadableToUTC(DateUtil.convertDobtoReadable(consumer.getDob()));

        profile.preferredName = getProfile().preferredName;

        profile.insuranceProvider = getProfile().insuranceProvider;
        if (getProfile().insuranceProvider != null) {
            profile.insuranceProvider.insurancePlan = getProfile().insuranceProvider.insurancePlan;
            profile.insuranceProvider.memberNumber = getProfile().insuranceProvider.memberNumber;
            profile.insuranceProvider.groupNumber = getProfile().insuranceProvider.groupNumber;
        }

        return profile;
    }

    public static void updateProfileFromMcnData(final String bearer, final Consumer consumer) {
        final Profile profile = getProfileFromConsumerObject(consumer);

        NetworkManager.getInstance().updateProfile(bearer, profile).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Timber.d("updateProfileFromMcnData. Successful Response\n" + response);
                    ProfileManager.setProfile(profile);
                } else {
                    Timber.e("updateProfileFromMcnData. Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("updateProfileFromMcnData. Something failed!\n" + t);
            }
        });
    }

    public static void clearSessionData() {
        profile = null;
        appointments = null;
    }

    public static List<ProviderResponse> getFavoriteProviders() {
        return favoriteProviders;
    }

    public static void setFavoriteProviders(List<ProviderResponse> favoriteProviders) {
        ProfileManager.favoriteProviders = favoriteProviders;
    }
}
