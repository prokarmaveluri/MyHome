package com.televisit.feedback;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.americanwell.sdk.entity.visit.ConsumerFeedbackQuestion;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.mycare.MyCareNowFragment;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsSendVisitFeedback;
import com.televisit.interfaces.AwsSendVisitRating;

import java.util.List;

/**
 * Created by kwelsh on 12/1/17.
 */

public class FeedbackFragment extends BaseFragment implements AwsSendVisitFeedback, AwsSendVisitRating {
    public static final String FEEDBACK_TAG = "feedback_tag";

    private LinearLayout feedbackLayout;
    private ProgressBar progressBar;
    private RatingBar providerRating;
    private RatingBar experienceRating;

    public FeedbackFragment() {
    }

    public static FeedbackFragment newInstance() {
        FeedbackFragment fragment = new FeedbackFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.feedback));
        View view = inflater.inflate(R.layout.visit_feedback, container, false);

        feedbackLayout = (LinearLayout) view.findViewById(R.id.feedback_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        providerRating = (RatingBar) view.findViewById(R.id.rate_provider);
        experienceRating = (RatingBar) view.findViewById(R.id.rate_experience);

        showLayout();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.visit_feedback_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:
                showLoading();
                AwsNetworkManager.getInstance().sendVisitRatings(AwsManager.getInstance().getVisit(), providerRating.getNumStars(), experienceRating.getNumStars(), this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLoading() {
        feedbackLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showLayout() {
        feedbackLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void goBackToDashboard() {
        getFragmentManager().popBackStack(MyCareNowFragment.MCN_DASHBOARD_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.VIDEO_VISIT_FEEDBACK;
    }

    @Override
    public void sendVisitRatingComplete() {
        //TODO Not sure how to send this feedback...
        AwsNetworkManager.getInstance().sendVisitFeedback(AwsManager.getInstance().getVisit(), new ConsumerFeedbackQuestion() {
            @Override
            public boolean isShow() {
                return false;
            }

            @Nullable
            @Override
            public String getQuestionText() {
                return null;
            }

            @NonNull
            @Override
            public List<String> getResponseOptions() {
                return null;
            }

            @Override
            public void setQuestionAnswer(@NonNull String s) {

            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }
        }, this);
    }

    @Override
    public void sendVisitRatingFailed(String errorMessage) {
        goBackToDashboard();
    }

    @Override
    public void sendVisitFeedbackComplete() {
        goBackToDashboard();
    }

    @Override
    public void sendVisitFeedbackFailed(String errorMessage) {
        goBackToDashboard();
    }
}
