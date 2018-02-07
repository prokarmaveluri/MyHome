package com.prokarma.myhome.features.dev;

import android.content.Context;
import android.view.View;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.profile.Profile;

/**
 * Created by kwelsh on 1/31/18.
 */

public class DevPresenter implements DevContract.Presenter, DevContract.InteractorOutput {
    Context context;
    DevContract.View view;
    DevContract.Router router;
    DevContract.Interactor interactor;

    public DevPresenter(final Context context, final BaseFragment fragment, final View view) {
        this.context = context;
        this.view = new DevView(view, this);
        this.router = new DevRouter(fragment);
        this.interactor = new DevInteractor(this);
    }

    @Override
    public void onCreate() {
        interactor.getBearerToken();
        interactor.getAmWellToken();
        interactor.getProfile();
        interactor.getLocation();
        interactor.getHockeyId();
        interactor.getMapsKey(context);
        interactor.getBuildType();
    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void onApiButtonPressed() {
        router.goToApiScreen();
    }

    @Override
    public void receivedBearerToken(String bearerToken) {
        view.showBearerToken(bearerToken != null ? bearerToken : "Couldn't find Bearer Token");
    }

    @Override
    public void receivedAmWellToken(String amwellToken) {
        view.showAmWellToken(amwellToken != null ? amwellToken : "AmWell Token couldn't be found");
    }

    @Override
    public void receivedProfile(Profile profile) {
        view.showProfile(profile != null ? profile.toString() : "Profile Not Retrieved Yet");
    }

    @Override
    public void receivedLocation(LocationResponse location) {
        view.showLocation(location != null ? location.toString() : "Location Not Retrieved Yet");
    }

    @Override
    public void receivedHockeyId(String hockeyId) {
        view.showHockeyId(hockeyId);
    }

    @Override
    public void receivedMapsKey(String mapsKey) {
        view.showMapsKey(mapsKey);
    }

    @Override
    public void receivedBuildType(String buildType) {
        view.showBuildType(buildType);
    }
}
