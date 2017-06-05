package com.dignityhealth.myhome.features.profile;

import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/2/17.
 */

public class ProfileManager {
    private static Profile profile = null;

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
     * Simply makes the network call for getting the Profile of the currently logged in user and loads it into Singleton
     */
    public static void queryProfile() {
        String bearerToken = AuthManager.getInstance().getBearerToken();
        NetworkManager.getInstance().getProfile(bearerToken).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(response.body());
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }
}
