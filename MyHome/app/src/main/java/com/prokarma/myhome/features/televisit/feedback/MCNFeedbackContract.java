package com.prokarma.myhome.features.televisit.feedback;

import com.americanwell.sdk.entity.visit.ConsumerFeedbackQuestion;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.prokarma.myhome.app.BasePresenter;
import com.prokarma.myhome.app.BaseRouter;

/**
 * Created by veluri on 2/16/18.
 */

public class MCNFeedbackContract {
    public interface View {
        void getVisitSummaryComplete(VisitSummary visitSummary);

        void getVisitSummaryFailed(String errorMessage);
    }

    public interface Presenter extends BasePresenter {
        void onActivityCreated();

        void saveFeedback();
    }

    public interface Interactor {
        void getVisitSummary(Visit visit);

        void sendVisitRatings(Visit visit, int providerRating, int experienceRating);

        void sendVisitFeedback(Visit visit, ConsumerFeedbackQuestion question);
    }

    public interface InteractorOutput {

        void getVisitSummaryComplete(VisitSummary visitSummary);

        void getVisitSummaryFailed(String errorMessage);

        void sendVisitRatingComplete();

        void sendVisitRatingFailed(String errorMessage);

        void sendVisitFeedbackComplete();

        void sendVisitFeedbackFailed(String errorMessage);
    }

    public interface Router extends BaseRouter {
        //no other specific routing otherthan going back to MCN Home
    }
}
