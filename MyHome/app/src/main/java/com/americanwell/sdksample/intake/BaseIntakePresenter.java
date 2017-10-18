/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.intake;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKSuggestion;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;

import icepick.State;

/**
 * Base implementation of presenter for intake.
 * provides visitcontext handling
 */
public abstract class BaseIntakePresenter<V extends BaseSampleNucleusActivity> extends BaseSampleNucleusRxPresenter<V> {

    @State
    protected VisitContext visitContext;

    public VisitContext getVisitContext() {
        return visitContext;
    }

    public void setVisitContext(final VisitContext visitContext) {
        this.visitContext = visitContext;
    }

    // This method is used to determine if the error should result in the user being returned to the home screen.
    public boolean isGoHomeError(final SDKError error) {
        boolean isError = false;
        if (error.getSDKResponseSuggestion().getSDKSuggestion().equals(SDKSuggestion.PROVIDER_UNAVAILABLE)) {
            isError = true;
        }
        return isError;
    }
}
