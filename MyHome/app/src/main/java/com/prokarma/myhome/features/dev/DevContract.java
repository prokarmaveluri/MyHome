package com.prokarma.myhome.features.dev;

import android.content.Context;

import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.profile.Profile;

/**
 * Created by kwelsh on 1/31/18.
 */

public class DevContract {
    interface View {
        void showBearerToken(String bearerToken);

        void showAmWellToken(String amWellToken);

        void showProfile(String profile);

        void showLocation(String location);

        void showHockeyId(String hockeyId);

        void showMapsKey(String mapsKey);

        void showBuildType(String buildType);
    }

    interface Presenter {
        void onApiButtonPressed();

        String requestingBearerToken();

        String requestingAmWellToken();

        String requestingProfile();

        String requestingLocation();

        String requestingHockeyId();

        String requestingMapsKey(Context context);

        String requestingBuildType();
    }

    interface Interactor {
        String getBearerToken();

        String getAmWellToken();

        Profile getProfile();

        LocationResponse getLocation();

        String getHockeyId();

        String getMapsKey(Context context);

        String getBuildType();
    }

    interface InteractorOutput {

    }

    interface Router {
        void goToApiScreen();
    }
}
