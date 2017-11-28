/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.intake;

import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.manager.ValidationConstants;
import com.americanwell.sdk.manager.ValidationReason;
import com.americanwell.sdk.manager.VisitManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import icepick.State;

/**
 * base implementation for presenters that collect privacy policy
 */
public abstract class BasePrivacyPolicyPresenter<T extends BaseIntakeActivity> extends BaseIntakePresenter<T> {

    @Inject
    VisitManager visitManager;

    @State
    protected String privacyPolicy;

    public void setPrivacyPolicy(final String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setLegalTextsAccepted(boolean accepted) {
        for (LegalText legalText : visitContext.getLegalTexts()) {
            legalText.setAccepted(accepted);
        }
    }

    protected abstract boolean shouldShowPrivacyPolicyError();

    protected abstract void setPrivacyPolicyError();

    protected boolean validateVisitContext() {
        boolean valid = true;
        final Map<String, ValidationReason> errors = new ConcurrentHashMap<>();
        visitManager.validateVisitContext(visitContext, errors);
        if (!errors.isEmpty()) {

            // this is a little edge case... since our sample UI does not really expose the name / details of the
            // privacy policy legal text, the canned validation doesn't make much sense. so we're handling it above
            for (Map.Entry<String, ValidationReason> entry : errors.entrySet()) {
                if (entry.getKey().startsWith(ValidationConstants.VALIDATION_VC_LEGAL_TEXTS)) {
                    errors.remove(entry.getKey()); // remove the error from the map
                    if (shouldShowPrivacyPolicyError()) {
                        setPrivacyPolicyError(); // there should be a BasePrivacyPolicyActivity that has this field.
                        valid = false;
                    }
                }
            }

            if (!errors.isEmpty()) {
                view.setValidationReasons(errors);
                valid = false;
            }
        }
        return valid;
    }

}
