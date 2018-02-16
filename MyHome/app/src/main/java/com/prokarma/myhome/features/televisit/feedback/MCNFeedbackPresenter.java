package com.prokarma.myhome.features.televisit.feedback;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.americanwell.sdk.entity.visit.VisitSummary;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.CommonUtil;

import timber.log.Timber;

/**
 * Created by veluri on 2/16/18.
 */

public class MCNFeedbackPresenter implements MCNFeedbackContract.Presenter, MCNFeedbackContract.InteractorOutput {
    Activity activity;
    Context context;
    MCNFeedbackContract.View view;
    MCNFeedbackContract.Router router;
    MCNFeedbackContract.Interactor interactor;

    public MCNFeedbackPresenter(final Context context, final Activity activity, final BaseFragment fragment, final View view) {
        this.context = context;
        this.activity = activity;
        this.view = new MCNFeedbackView(view, this);
        this.router = new MCNFeedbackRouter(fragment);
        this.interactor = new MCNFeedbackInteractor(this);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onActivityCreated() {
        interactor.getVisitSummary(AwsManager.getInstance().getVisit());
    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void getVisitSummaryComplete(VisitSummary visitSummary) {
        view.getVisitSummaryComplete(visitSummary);
    }

    @Override
    public void getVisitSummaryFailed(String errorMessage) {
        view.getVisitSummaryFailed(errorMessage);
    }

    @Override
    public void sendVisitRatingComplete() {
        sendFeedback();
    }

    @Override
    public void sendVisitRatingFailed(String errorMessage) {
        CommonUtil.showToast(context, context.getString(R.string.feedback_ratings_submission_failed));
        sendFeedback();
    }

    @Override
    public void sendVisitFeedbackComplete() {
        ((MCNFeedbackView) view).hideLoading();
        CommonUtil.showToast(context, context.getString(R.string.feedback_completed));
        router.goToMcnDashboard();
    }

    @Override
    public void sendVisitFeedbackFailed(String errorMessage) {
        ((MCNFeedbackView) view).hideLoading();
        CommonUtil.showToast(context, context.getString(R.string.feedback_answers_submission_failed));
        router.goToMcnDashboard();
    }

    @Override
    public void saveFeedback() {

        MCNFeedbackView mcnview = ((MCNFeedbackView) view);

        if (mcnview.visitSummary == null) {

            Timber.e("sendVisitFeedback visitSummary is null. ");
            CommonUtil.showToast(context, context.getString(R.string.feedback_answers_submission_failed));

            router.goToMcnDashboard();
            return;

        } else if (mcnview.visitSummary.getConsumerFeedbackQuestion() == null) {

            Timber.e("sendVisitFeedback visitSummary.getConsumerFeedbackQuestion() is null. ");
            CommonUtil.showToast(context, context.getString(R.string.feedback_answers_submission_failed));

            router.goToMcnDashboard();
            return;

        } else if (mcnview.visitSummary != null && mcnview.visitSummary.getConsumerFeedbackQuestion() != null) {
            mcnview.visitSummary.getConsumerFeedbackQuestion().setQuestionAnswer(mcnview.getQuestionAnswer());
        }


        mcnview.showLoading();

        String answer = ((MCNFeedbackView) view).getQuestionAnswer();

        if (answer == null || answer.isEmpty()) {

            mcnview.hideLoading();
            CommonUtil.showToast(context, context.getString(R.string.feedback_choose_answer));

        } else if (mcnview.providerRatingValue == 0 && mcnview.experienceRatingValue == 0) {

            sendFeedback();

        } else {
            sendRatings();
        }
    }

    private void sendRatings() {
        interactor.sendVisitRatings(AwsManager.getInstance().getVisit(), ((MCNFeedbackView) view).providerRatingValue, ((MCNFeedbackView) view).experienceRatingValue);
    }

    private void sendFeedback() {
        interactor.sendVisitFeedback(AwsManager.getInstance().getVisit(), ((MCNFeedbackView) view).visitSummary.getConsumerFeedbackQuestion());
    }
}
