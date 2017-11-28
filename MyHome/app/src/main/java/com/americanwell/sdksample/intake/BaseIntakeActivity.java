/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.intake;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdksample.BaseSampleNucleusActivity;

/**
 * Base activity for all intake activities
 * provides some handling for visit context
 * each activity STILL must implement a static makeIntent() that takes visitcontext as a param
 * and sets it as an extra on the intent
 */
public abstract class BaseIntakeActivity<T extends BaseIntakePresenter> extends BaseSampleNucleusActivity<T> {

    protected static final String EXTRA_VISIT_CONTEXT = "visitContext";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getPresenter().setVisitContext((VisitContext) getIntent().getParcelableExtra(EXTRA_VISIT_CONTEXT));
        }
    }

    // Because after a transfer we are re-launching this activity with the flag
    // CLEAR_TOP we expect the activity to have already been created and this call to re-initialize
    // the visit. See Intent#FLAG_ACTIVITY_CLEAR_TOP for more information
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().setVisitContext((VisitContext) getIntent().getParcelableExtra(EXTRA_VISIT_CONTEXT));
    }

    @Override
    public void setError(@NonNull final SDKError error) {
        // here we are overriding the normal error handling to determine if the error deems the
        // necessity for the user to be returned to the home screen
        if (getPresenter().isGoHomeError(error)) {
            setNothingIsBusy();
            sampleUtils.handleError(this, error, goHomeClickListener());
        }
        else {
            super.setError(error);
        }
    }

}

