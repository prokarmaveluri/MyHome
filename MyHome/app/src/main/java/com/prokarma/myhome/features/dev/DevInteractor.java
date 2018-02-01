package com.prokarma.myhome.features.dev;

import android.content.Context;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.FadManager;
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
    public void getBearerToken() {
        output.receivedBearerToken(AuthManager.getInstance().getBearerToken());
    }

    @Override
    public void getAmWellToken() {
        output.receivedAmWellToken(AuthManager.getInstance().getAmWellToken());
    }

    @Override
    public void getProfile() {
        output.receivedProfile(ProfileManager.getProfile());
    }

    @Override
    public void getLocation() {
        output.receivedLocation(FadManager.getInstance().getCurrentLocation());
    }

    @Override
    public void getHockeyId() {
        output.receivedHockeyId(BuildConfig.HOCKEY_ID);
    }

    @Override
    public void getMapsKey(final Context context) {
        output.receivedMapsKey(context.getString(R.string.google_maps_api_key));
    }

    @Override
    public void getBuildType() {
        output.receivedBuildType(BuildConfig.BUILD_TYPE);
    }
}
