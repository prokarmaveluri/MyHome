/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.americanwell.sdk.entity.Authentication;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;

import butterknife.BindView;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to display and provide option to accept an outstanding disclaimer, aka updated terms of use.
 *
 * @since AWSDK 3.0.1
 */
@RequiresPresenter(OutstandingDisclaimerPresenter.class)
public class OutstandingDisclaimerActivity extends BaseLoginActivity<OutstandingDisclaimerPresenter> {

    public final static String EXTRA_AUTHENTICATION = "awsdk_authentication";

    @BindView(R.id.outstanding_disclaimer_text)
    TextView outstandingDisclaimerText;

    public static Intent makeIntent(@NonNull final Context context,
                                    @NonNull final Authentication authentication) {
        final Intent intent = new Intent(context, OutstandingDisclaimerActivity.class);
        intent.putExtra(EXTRA_AUTHENTICATION, authentication);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_outstanding_disclaimer);
        getPresenter().setAuthentication(
                (Authentication) getIntent().getParcelableExtra(EXTRA_AUTHENTICATION));

    }

    public void setOutstandingDisclaimer(final Spanned outstandingDisclaimer) {
        outstandingDisclaimerText.setText(outstandingDisclaimer);
        outstandingDisclaimerText.setMovementMethod(new ScrollingMovementMethod());
    }

    @OnClick(R.id.fab)
    public void acceptOutstandingDisclaimer() {
        getPresenter().acceptOutstandingDisclamer();
    }

}
