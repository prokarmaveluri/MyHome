/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.SDKLaunchParams;
import com.americanwell.sdksample.SampleApplication;
import com.livefront.bridge.Bridge;

import javax.inject.Inject;

import icepick.State;
import nucleus.presenter.Presenter;

/**
 * Presenter for StartupActivity
 *
 * @since AWSDK 2.1
 */
public class StartupPresenter extends Presenter<StartupActivity> {

    @Inject
    protected AWSDK awsdk;
    @State
    Uri launchUri;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        SampleApplication.getPresenterComponent().awsdk().restoreInstanceState(savedState);
        SampleApplication.getPresenterComponent().inject(this);
        Bridge.restoreInstanceState(this, savedState);
    }

    @Override
    public void onSave(@NonNull Bundle state) {
        super.onSave(state);
        awsdk.saveInstanceState(state);
        Bridge.saveInstanceState(this, state);
    }

    // take the intent's launch uri and tell the SDK instance about it
    // this can happen more than once, if the app is open but relaunched from an intent link somewhere
    public void setLaunchUri(Uri launchUri) {
        this.launchUri = launchUri;
        awsdk.updateLaunchParams(launchUri);
    }

    public boolean hasVideoInvitation() {
        return awsdk.getLaunchParams().hasFeature(SDKLaunchParams.SDKLaunchFeature.VIDEO_INVITATION);
    }
}
