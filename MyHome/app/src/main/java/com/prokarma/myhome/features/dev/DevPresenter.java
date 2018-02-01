package com.prokarma.myhome.features.dev;

import android.app.Activity;
import android.content.Context;

/**
 * Created by kwelsh on 1/31/18.
 */

public class DevPresenter implements DevContract.Presenter, DevContract.InteractorOutput {
    final DevContract.View view;
    final DevContract.Router router;
    final DevContract.Interactor interactor;

    public DevPresenter(DevContract.View view) {
        this.view = view;
        this.router = new DevRouter((Activity) view);
        this.interactor = new DevInteractor(this);
    }


    @Override
    public void onApiButtonPressed() {
        router.goToApiScreen();
    }

    @Override
    public String requestingBearerToken() {
        return interactor.getBearerToken() != null ? interactor.getBearerToken() : "Couldn't find Bearer Token";
    }

    @Override
    public String requestingAmWellToken() {
        return interactor.getAmWellToken() != null ? interactor.getAmWellToken() : "AmWell Token couldn't be found";
    }

    @Override
    public String requestingProfile() {
        return interactor.getProfile() != null ? interactor.getProfile().toString() : "Profile Not Retrieved Yet";
    }

    @Override
    public String requestingLocation() {
        return interactor.getLocation() != null ? interactor.getLocation().toString() : "Location Not Retrieved Yet";
    }

    @Override
    public String requestingHockeyId() {
        return interactor.getHockeyId();
    }

    @Override
    public String requestingMapsKey(final Context context) {
        return interactor.getMapsKey(context);
    }

    @Override
    public String requestingBuildType() {
        return interactor.getBuildType();
    }
}
