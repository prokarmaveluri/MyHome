package com.prokarma.myhome.features.dev;

import android.content.Context;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.auth.AuthManager;

/**
 * Created by kwelsh on 2/1/18.
 */

public class DevInteractor implements DevContract.Interactor {
    final DevContract.InteractorOutput output;

    public DevInteractor(DevContract.InteractorOutput output) {
        this.output = output;
    }


    @Override
    public String getBearerToken() {
        return AuthManager.getInstance().getBearerToken();
    }

    @Override
    public String getAmWellToken() {
        return AuthManager.getInstance().getAmWellToken();
    }

    @Override
    public Profile getProfile() {
        return ProfileManager.getProfile();
    }

    @Override
    public LocationResponse getLocation() {
        return FadManager.getInstance().getCurrentLocation();
    }

    @Override
    public String getHockeyId() {
        return BuildConfig.HOCKEY_ID;
    }

    @Override
    public String getMapsKey(final Context context) {
        return context.getString(R.string.google_maps_api_key);
    }

    @Override
    public String getBuildType() {
        return BuildConfig.BUILD_TYPE;
    }
}
