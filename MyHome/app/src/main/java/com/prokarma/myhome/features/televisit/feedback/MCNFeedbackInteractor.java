package com.prokarma.myhome.features.televisit.feedback;

import com.americanwell.sdk.entity.visit.ConsumerFeedbackQuestion;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.prokarma.myhome.features.televisit.AwsNetworkManager;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetVisitSummary;
import com.prokarma.myhome.features.televisit.interfaces.AwsSendVisitFeedback;
import com.prokarma.myhome.features.televisit.interfaces.AwsSendVisitRating;

/**
 * Created by veluri on 2/16/18.
 */

public class MCNFeedbackInteractor implements MCNFeedbackContract.Interactor, AwsGetVisitSummary, AwsSendVisitRating, AwsSendVisitFeedback {
    final MCNFeedbackContract.InteractorOutput output;

    public MCNFeedbackInteractor(MCNFeedbackContract.InteractorOutput output) {
        this.output = output;
    }

    @Override
    public void sendVisitRatings(Visit visit, int providerRating, int experienceRating) {
        AwsNetworkManager.getInstance().sendVisitRatings(visit, providerRating, experienceRating, this);
    }

    @Override
    public void sendVisitRatingComplete() {
        output.sendVisitRatingComplete();
    }

    @Override
    public void sendVisitRatingFailed(String errorMessage) {
        output.sendVisitRatingFailed(errorMessage);
    }

    @Override
    public void sendVisitFeedback(Visit visit, ConsumerFeedbackQuestion question) {
        AwsNetworkManager.getInstance().sendVisitFeedback(visit, question, this);
    }

    @Override
    public void sendVisitFeedbackComplete() {
        output.sendVisitFeedbackComplete();
    }

    @Override
    public void sendVisitFeedbackFailed(String errorMessage) {
        output.sendVisitFeedbackFailed(errorMessage);
    }

    @Override
    public void getVisitSummary(Visit visit) {
        AwsNetworkManager.getInstance().getVisitSummary(visit, this);
    }

    @Override
    public void getVisitSummaryComplete(VisitSummary visitSummary) {
        output.getVisitSummaryComplete(visitSummary);
    }

    @Override
    public void getVisitSummaryFailed(String errorMessage) {
        output.getVisitSummaryFailed(errorMessage);
    }
}