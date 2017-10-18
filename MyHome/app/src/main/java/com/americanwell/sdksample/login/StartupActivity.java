/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.login;

import android.content.Intent;
import android.os.Bundle;

import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * This activity is in charge of directing the user to consumer {@link LoginActivity} or the guest
 * {@link GuestLoginActivity}
 */
@RequiresPresenter(StartupPresenter.class)
public class StartupActivity extends NucleusAppCompatActivity<StartupPresenter> {

    private static final String LOG_TAG = StartupActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        getPresenter().setLaunchUri(getIntent().getData());

        boolean useThirdPartAuth = getResources().getBoolean(R.bool.use_third_party_auth);
        Intent intent;
        if (!getPresenter().hasVideoInvitation()) {
            if (useThirdPartAuth) {
                DefaultLogger.d(LOG_TAG, "going to third party login activity");
                intent = new Intent(this, ThirdPartyLoginActivity.class);
            }
            else {
                DefaultLogger.d(LOG_TAG, "going to standard login activity");
                intent = new Intent(this, LoginActivity.class);
            }
        }
        else {
            DefaultLogger.d(LOG_TAG, "going to guest login activity");
            intent = new Intent(this, GuestLoginActivity.class);
        }
        intent.setData(getIntent().getData());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
