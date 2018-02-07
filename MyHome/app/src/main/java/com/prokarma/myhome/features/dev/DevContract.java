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
        void onCreate();

        void onDestroy();

        void onApiButtonPressed();
    }

    interface Interactor {
        void getBearerToken();

        void getAmWellToken();

        void getProfile();

        void getLocation();

        void getHockeyId();

        void getMapsKey(Context context);

        void getBuildType();
    }

    interface InteractorOutput {
        void receivedBearerToken(String bearerToken);

        void receivedAmWellToken(String amwellToken);

        void receivedProfile(Profile profile);

        void receivedLocation(LocationResponse location);

        void receivedHockeyId(String hockeyId);

        void receivedMapsKey(String mapsKey);

        void receivedBuildType(String buildType);
    }

    interface Router {
        void goToApiScreen();
    }
}
