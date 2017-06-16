package com.dignityhealth.myhome.features.profile;

import com.dignityhealth.myhome.features.appointments.Appointment;
import com.dignityhealth.myhome.features.appointments.AppointmentResponse;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;

import java.util.ArrayList;

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
    public static void setProfile(Profile profile) {
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
}
