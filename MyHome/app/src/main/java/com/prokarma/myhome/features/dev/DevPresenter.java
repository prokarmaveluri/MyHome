package com.prokarma.myhome.features.dev;

import android.content.Context;

import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.profile.Profile;

/**
 * Created by kwelsh on 1/31/18.
 */

public class DevPresenter implements DevContract.Presenter, DevContract.InteractorOutput {
    DevContract.View view;
    DevContract.Router router;
    DevContract.Interactor interactor;

    public DevPresenter(DevContract.View view) {
        this.view = view;
        this.router = new DevRouter((BaseFragment) view);
        this.interactor = new DevInteractor(this);
    }

    @Override
    public void onDestroy() {
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void onApiButtonPressed() {
        router.goToApiScreen();
    }

    @Override
    public void requestingBearerToken() {
        interactor.getBearerToken();
    }

    @Override
    public void requestingAmWellToken() {
        interactor.getAmWellToken();
    }

    @Override
    public void requestingProfile() {
        interactor.getProfile();
    }

    @Override
    public void requestingLocation() {
        interactor.getLocation();
    }

    @Override
    public void requestingHockeyId() {
        interactor.getHockeyId();
    }

    @Override
    public void requestingMapsKey(final Context context) {
        interactor.getMapsKey(context);
    }

    @Override
    public void requestingBuildType() {
        interactor.getBuildType();
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
