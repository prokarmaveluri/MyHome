package com.prokarma.myhome.features.profile;

import android.support.annotation.Nullable;

import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.AppointmentResponse;
import com.prokarma.myhome.features.preferences.MySavedDoctorsResponse;
import com.prokarma.myhome.networking.NetworkManager;
import com.prokarma.myhome.networking.auth.AuthManager;

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
    private static List<MySavedDoctorsResponse.FavoriteProvider> favoriteProviders = null;

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
    public static void getProfileInfo() {
        String bearerToken = AuthManager.getInstance().getBearerToken();
        NetworkManager.getInstance().getProfile(bearerToken).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(response.body().result);
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }

    /**
     * Sends the profile information to the server to update the values
     *
     * @param bearer         the bearer token needed to provide authentication
     * @param updatedProfile the updated profile information being attempted
     */
    public static void updateProfile(final String bearer, final Profile updatedProfile) {
        NetworkManager.getInstance().updateProfile(bearer, updatedProfile).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(updatedProfile);
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("Something failed! :/");
            }
        });
    }

    public static void getAppointmentInfo() {
        String bearerToken = AuthManager.getInstance().getBearerToken();
        NetworkManager.getInstance().getAppointments(bearerToken).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        AppointmentResponse result = response.body();
                        setAppointments(result.result.appointments);
                    }catch (NullPointerException ex){
                    }
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }

    public static void clearSessionData() {
        profile = null;
        appointments = null;
    }

    public static List<MySavedDoctorsResponse.FavoriteProvider> getFavoriteProviders() {
        return favoriteProviders;
    }

    public static void setFavoriteProviders(List<MySavedDoctorsResponse.FavoriteProvider> favoriteProviders) {
        ProfileManager.favoriteProviders = favoriteProviders;
    }
}
