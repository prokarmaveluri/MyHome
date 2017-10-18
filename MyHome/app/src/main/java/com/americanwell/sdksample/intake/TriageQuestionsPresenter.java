/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.intake;

import android.text.TextUtils;

import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.entity.visit.Topic;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.exception.FeatureNotEnabledException;
import com.americanwell.sdk.manager.VisitManager;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.SampleUtils;

import java.util.ArrayList;
import java.util.HashSet;

import javax.inject.Inject;

import icepick.State;

/**
 * Presenter for TriageQuestionsActivity
 */
public class TriageQuestionsPresenter extends BasePrivacyPolicyPresenter<TriageQuestionsActivity> {
    @Inject
    VisitManager visitManager;

    @State
    ArrayList<String> inviteEmails; // needs to be an ArrayList for parcelablilty

    @Override
    public void setVisitContext(final VisitContext visitContext) {
        super.setVisitContext(visitContext);
        setCallbackPhone(consumer.getPhone());
        for (LegalText legalText : visitContext.getLegalTexts()) {
            if (legalText.isRequired()) {
                // typically there's only one required legal text, which is the privacy policy
                setPrivacyPolicy(legalText.getLegalText());
            }
        }
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(TriageQuestionsActivity view) {
        super.onTakeView(view);
        view.setTriageQuestions(visitContext.showTriageQuestions(),
                visitContext.getTriageQuestions());
        view.setTopics(visitContext.getTopics());
        view.showTopicQuestion(awsdk.getConfiguration().otherTopicEnabled()
                || !visitContext.getTopics().isEmpty());
        view.showOtherTopic(awsdk.getConfiguration().otherTopicEnabled());
        view.setHasPrivacyPolicy(!visitContext.hasOnDemandSpecialty());
        view.setDependentInfoText(consumer.isDependent() ? consumer.getFullName() : null);
        view.setPhone(visitContext.getCallbackNumber());

        if (inviteEmails == null) {
            inviteEmails = new ArrayList<>();
        }
        setInviteEmails(awsdk.getConfiguration().isMultipleVideoParticipantsEnabled(), inviteEmails);
    }

    protected boolean isFirstAvailable() {
        return visitContext.hasOnDemandSpecialty();
    }

    public void setShareHealthSummary(final boolean share) {
        visitContext.setShareHealthSummary(share);
    }

    public void setOtherTopic(final String otherTopic) {
        visitContext.setOtherTopic(otherTopic);
    }

    public void toggleTopic(final Topic item) {
        item.setSelected(!item.isSelected());
    }

    public void setCallbackPhone(final String callbackPhone) {
        visitContext.setCallbackNumber(callbackPhone);
    }

    private void setInviteEmails(final boolean showInviteEmails, final ArrayList<String> inviteEmails) {
        this.inviteEmails = inviteEmails;
        view.setInviteEmails(showInviteEmails, inviteEmails);
        if (showInviteEmails) {
            setMaxInvitesReached();
        }
    }

    public void addInviteEmail(final String inviteEmail) {
        if (inviteEmail.length() > 0) {
            // invalid email
            if (!SampleUtils.isValidEmail(inviteEmail)) {
                view.setEmailWarning();
            }
            // email address already entered
            else if (isEmailInList(inviteEmail)) {
                view.setEmailExists();
            }
            else {
                inviteEmails.add(inviteEmail);
                view.addInviteEmail(inviteEmail);
                setMaxInvitesReached();
            }
        }
    }

    private void setMaxInvitesReached() {
        view.setMaxInvitesReached(inviteEmails.size() >= awsdk.getConfiguration().getMaxVideoInvites(),
                awsdk.getConfiguration().getMaxVideoInvites());
    }

    public void removeInviteEmail(final String inviteEmail) {
        inviteEmails.remove(inviteEmail);
        view.removeInviteEmail(inviteEmail);
        setMaxInvitesReached();
    }

    public boolean isEmailInList(final String email) {
        return TextUtils.isEmpty(email) || inviteEmails.contains(email);
    }

    public void validate() {
        boolean valid = true;

        if (!isEmailInList(view.getInviteEmail())) {
            view.showInviteEmailError();
            valid = false;
        }

        if (!validateVisitContext()) {
            valid = false;
        }

        if (awsdk.getConfiguration().isMultipleVideoParticipantsEnabled()) {
            try {
                visitContext.setGuestInvitationEmails(new HashSet<>(inviteEmails));
            }
            catch (FeatureNotEnabledException e) {
                view.setError(e);
                valid = false;
            }
        }

        if (valid) {
            view.setFieldsValid(isFirstAvailable());
        }
    }

    @Override
    protected boolean shouldShowPrivacyPolicyError() {
        return !isFirstAvailable();
    }

    @Override
    protected void setPrivacyPolicyError() {
        view.setPrivacyPolicyError();
    }
}

