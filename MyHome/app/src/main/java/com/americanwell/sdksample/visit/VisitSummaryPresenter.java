/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.os.Bundle;
import android.text.TextUtils;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.AgendaItemFollowUp;
import com.americanwell.sdk.entity.visit.ProviderEntries;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitDiagnosis;
import com.americanwell.sdk.entity.visit.VisitRx;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;
import com.americanwell.sdksample.rx.SDKValidatedResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import icepick.State;
import rx.Observable;

/**
 * Presenter for VisitSummaryActivity
 */
public class VisitSummaryPresenter extends BaseSampleNucleusRxPresenter<VisitSummaryActivity> {

    private static final int GET_SUMMARY = 1110;
    private static final int SEND_FEEDBACK = 1111;
    private static final int SEND_REPORT = 1112;
    private static final int SEND_RATINGS = 1113;

    @State
    Visit visit;
    @State
    VisitSummary visitSummary;
    @State
    String feedbackAnswer;
    @State
    ArrayList<String> selectedEmails = new ArrayList<>();
    @State
    Integer providerRating = 0;
    @State
    Integer visitRating = 0;
    @State
    boolean hipaaNoticeAccepted = false; // since AWSDK 3.1

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_SUMMARY,
                new SampleRequestFunc0<SDKResponse<VisitSummary, SDKError>>(GET_SUMMARY) {
                    @Override
                    public Observable<SDKResponse<VisitSummary, SDKError>> go() {
                        return observableService.getVisitSummary(visit);
                    }
                },
                new SampleResponseAction2<VisitSummary, SDKError, SDKResponse<VisitSummary, SDKError>>(GET_SUMMARY) {
                    @Override
                    public void onSuccess(VisitSummaryActivity visitSummaryActivity, VisitSummary visitSummary) {
                        setVisitSummary(visitSummary);
                    }
                },
                new SampleFailureAction2(GET_SUMMARY)
        );

        restartableLatestCache(SEND_FEEDBACK,
                new SampleRequestFunc0<SDKValidatedResponse<Void, SDKError>>(SEND_FEEDBACK) {
                    @Override
                    public Observable<SDKValidatedResponse<Void, SDKError>> go() {
                        return observableService.sendVisitFeedback(
                                visit,
                                visitSummary.getConsumerFeedbackQuestion()
                        );
                    }
                },
                new SampleValidatedResponseAction2<Void, SDKError, SDKValidatedResponse<Void, SDKError>>(SEND_FEEDBACK) {
                    @Override
                    public void onSuccess(VisitSummaryActivity visitSummaryActivity, Void aVoid) {
                        if (!selectedEmails.isEmpty()) {
                            start(SEND_REPORT);
                        }
                        else if (providerRating != null || visitRating != null) {
                            start(SEND_RATINGS);
                        }
                        else {
                            view.setSummarySubmitted();
                        }
                    }
                },
                new SampleFailureAction2(SEND_FEEDBACK)
        );

        restartableLatestCache(SEND_REPORT,
                new SampleRequestFunc0<SDKValidatedResponse<Void, SDKError>>(SEND_REPORT) {
                    @Override
                    public Observable<SDKValidatedResponse<Void, SDKError>> go() {
                        final Set<String> emails = new HashSet<>(selectedEmails);
                        return observableService.sendEmailSummary(visit, emails, hipaaNoticeAccepted);
                    }
                },
                new SampleValidatedResponseAction2<Void, SDKError, SDKValidatedResponse<Void, SDKError>>(SEND_REPORT) {
                    @Override
                    public void onSuccess(VisitSummaryActivity visitSummaryActivity, Void aVoid) {
                        if (providerRating != null || visitRating != null) {
                            start(SEND_RATINGS);
                        }
                        else {
                            view.setSummarySubmitted();
                        }
                    }
                },
                new SampleFailureAction2(SEND_REPORT)
        );

        restartableLatestCache(SEND_RATINGS,
                new SampleRequestFunc0<SDKResponse<Void, SDKError>>(SEND_RATINGS) {
                    @Override
                    public Observable<SDKResponse<Void, SDKError>> go() {
                        return observableService.sendRatings(visit, providerRating, visitRating);
                    }
                },
                new SampleResponseAction2<Void, SDKError, SDKResponse<Void, SDKError>>(SEND_RATINGS) {
                    @Override
                    public void onSuccess(VisitSummaryActivity visitSummaryActivity, Void aVoid) {
                        view.setSummarySubmitted();
                    }
                },
                new SampleFailureAction2(SEND_RATINGS)
        );

    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(VisitSummaryActivity view) {
        super.onTakeView(view);
        if (visitSummary == null) {
            start(GET_SUMMARY);
        }
        else {
            setVisitSummary(visitSummary);
        }
        view.setShowProviderRating(awsdk.getConfiguration().showProviderRating());
    }

    private void setVisitSummary(final VisitSummary visitSummary) {
        this.visitSummary = visitSummary;
        view.setProviderName(visitSummary.getAssignedProviderInfo().getFullName());
        view.setFeedbackQuestion(visitSummary.getConsumerFeedbackQuestion().getQuestionText());
        view.setFeedbackResponseOptions(visitSummary.getConsumerFeedbackQuestion().getResponseOptions());
        view.setFeedbackAnswer(feedbackAnswer);
        view.setPharmacy(visitSummary.getPharmacy());

        if (visit.getVisitCost().isDeferredBillingInEffect()) {
            view.setDeferredBillingPrompt(visit.getVisitCost().getDeferredBillingWrapUpText());
            view.setShowCostSummary(false);
        }
        else {
            view.setExpectedConsumerCopayCost(localeUtils.formatCurrency(
                    visitSummary.getVisitCost().getExpectedConsumerCopayCost(),
                    awsdk.getConfiguration().getCurrencyCode()));
            view.setShowCostSummary(true);
            view.setDeferredBillingPrompt(null);
        }

        setSummaryEmails();
        setProviderEntries(visitSummary.getProviderEntries());
    }

    private void setSummaryEmails() {
        final List<String> emails = new ArrayList<>();
        String email = visit.getConsumer().getEmail();
        if (!TextUtils.isEmpty(email)) {
            emails.add(email);
        }
        emails.addAll(visitSummary.getInviteEmails());
        view.setSummaryEmails(emails);
    }

    private void setProviderEntries(final ProviderEntries entries) {
        view.setProviderNotes(entries.getNotesSpanned());

        final StringBuilder diagnoses = new StringBuilder();
        if (entries.getDiagnoses() != null) {
            for (VisitDiagnosis visitDiagnosis : entries.getDiagnoses()) {
                diagnoses.append(visitDiagnosis.getName());
                diagnoses.append(". ");
            }
        }
        view.setDiagnoses(diagnoses.toString());

        final StringBuilder prescriptions = new StringBuilder();
        if (entries.getPrescriptions() != null) {
            for (VisitRx rx : entries.getPrescriptions()) {
                prescriptions.append(rx.getName());
                prescriptions.append(". ");
            }
        }
        view.setPrescriptions(prescriptions.toString());

        final StringBuilder followup = new StringBuilder();
        if (entries.getAgendaItemFollowUps() != null) {
            for (AgendaItemFollowUp agendaItemFollowUp : entries.getAgendaItemFollowUps()) {
                followup.append(agendaItemFollowUp.getDescription());
                followup.append(". ");
            }
        }
        view.setFollowup(followup.toString());
    }

    public void setEmailSelected(final String email, final boolean bSelected) {
        if (bSelected) {
            selectedEmails.add(email);
        }
        else {
            selectedEmails.remove(email);
        }
        view.showAcceptHipaaNotice(!selectedEmails.isEmpty());
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public void setFeedbackAnswer(final String answer) {
        this.feedbackAnswer = answer;
        visitSummary.getConsumerFeedbackQuestion().setQuestionAnswer(answer);
    }

    public void submit() {
        if (!validateRatings()) {
            view.setProviderRatingError();
        }
        else if (!validateHipaa()) { // since AWSDK 3.1
            view.setHipaaNotAccepted();
        }
        else {
            start(SEND_FEEDBACK);
        }
    }

    private boolean validateRatings() {
        return awsdk.getConfiguration().endVisitRatingsOptional() ||
                (visitRating > 0 &&
                        (!awsdk.getConfiguration().showProviderRating() || providerRating > 0));
    }

    private boolean validateHipaa() { // since AWSDK 3.1
        return selectedEmails.isEmpty() || hipaaNoticeAccepted;
    }

    public void setProviderRating(Integer providerRating) {
        this.providerRating = providerRating;
    }

    public void setVisitRating(Integer visitRating) {
        this.visitRating = visitRating;
    }

    public void setHipaaNoticeAccepted(boolean hipaaNoticeAccepted) {
        this.hipaaNoticeAccepted = hipaaNoticeAccepted;
    } // since AWSDK 3.1

    public String getHipaaNotice() {
        return visitSummary.getHipaaNoticeText() + "\n\n" + visitSummary.getAdditionalHipaaNoticeText();
    }
}
