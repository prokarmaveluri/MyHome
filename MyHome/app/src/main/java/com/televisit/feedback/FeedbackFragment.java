package com.televisit.feedback;

import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by kwelsh on 12/1/17.
 */

public class FeedbackFragment extends BaseFragment {
    public static final String FEEDBACK_TAG = "feedback_tag";

    private LinearLayout feedbackLayout;
    private ProgressBar progressBar;
    private RatingBar providerRating;
    private RatingBar experienceRating;

    private TextView textQuestion3;
    private RadioGroup radioGroup;
    private String question1 = "";
    private String question2 = "";
    private String question3 = "";
    private List<String> question3Options = new ArrayList<String>();
    private VisitSummary visitSummary;

    public FeedbackFragment() {
    }

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.feedback));
        View view = inflater.inflate(R.layout.visit_feedback, container, false);

        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.feedback));
        } else {
            getActivity().setTitle(getString(R.string.feedback));
        }

        feedbackLayout = (LinearLayout) view.findViewById(R.id.feedback_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        providerRating = (RatingBar) view.findViewById(R.id.rate_provider);
        experienceRating = (RatingBar) view.findViewById(R.id.rate_experience);

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        textQuestion3 = (TextView) view.findViewById(R.id.text_question3);

        showLayout();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        question1 = getContext().getString(R.string.rate_your_provider);
        question2 = getContext().getString(R.string.rate_your_overall_experience);
        question3 = getContext().getString(R.string.if_you_had_not_used_my_care_now_today_where_would_you_have_gone_instead);

        question3Options.add(getContext().getString(R.string.emergency_room));
        question3Options.add(getContext().getString(R.string.urgent_care_center));
        question3Options.add(getContext().getString(R.string.retail_health_clinic));
        question3Options.add(getContext().getString(R.string.doctor_s_office));
        question3Options.add(getContext().getString(R.string.done_nothing));

        getVisitSummary();
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

                String answer = getQuestionAnswer();

                Timber.d("feedback finish. ProviderRating = " + getProviderRating() + " out of " + providerRating.getNumStars());
                Timber.d("feedback finish. VisitRating = " + getVisitRating() + " out of " + experienceRating.getNumStars());
                Timber.d("feedback finish. answer = " + answer);

                if (answer == null || answer.isEmpty()) {

                    feedbackLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    CommonUtil.showToast(getActivity(),getString(R.string.feedback_choose_answer));
                } else if (getProviderRating() == 0 && getVisitRating() == 0) {

                    Timber.d("Feedback. User has not submitted both ProviderRating and VisitRating. ");

                    sendFeedback();

                } else {
                    sendRatings();
                }
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
        ((NavigationActivity) getActivity()).getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_NOW, null);
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.VIDEO_VISIT_FEEDBACK;
    }

    private void getVisitSummary() {

        AwsManager.getInstance().getAWSDK().getVisitManager().getVisitSummary(
                AwsManager.getInstance().getVisit(),
                new SDKCallback<VisitSummary, SDKError>() {
                    @Override
                    public void onResponse(VisitSummary visitSummaryObject, SDKError sdkError) {
                        if (sdkError == null) {
                            visitSummary = visitSummaryObject;
                            if (visitSummary != null
                                    && visitSummary.getConsumerFeedbackQuestion() != null
                                    && !visitSummary.getConsumerFeedbackQuestion().getQuestionText().isEmpty()
                                    && visitSummary.getConsumerFeedbackQuestion().getResponseOptions() != null
                                    && visitSummary.getConsumerFeedbackQuestion().getResponseOptions().size() > 0) {

                                question3 = visitSummary.getConsumerFeedbackQuestion().getQuestionText();

                                question3Options.clear();
                                question3Options = visitSummary.getConsumerFeedbackQuestion().getResponseOptions();

                                displayDynamicQuestionAnswer();
                            }
                        } else {
                            Timber.e("getVisitSummary failed! :/");
                            Timber.e("SDK Error: " + sdkError);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getVisitSummary failed! :/");
                        Timber.e("Throwable = " + throwable);
                    }
                }
        );
    }

    private void displayDynamicQuestionAnswer() {

        Timber.d("feedback. question3 = " + question3);

        textQuestion3.setText(question3);
        textQuestion3.setTextAppearance(this.getContext(), R.style.tradeGothicLTStd_Dynamic20);

        radioGroup.removeAllViews();

        int i = 0;
        for (String questionAnswer : question3Options) {

            i = i + 1;

            Timber.d("feedback. answer " + i + " = " + questionAnswer);

            RadioButton rbn = new RadioButton(this.getContext());
            rbn.setId(i + 1000);
            rbn.setText(questionAnswer);
            rbn.setLayoutParams(new LinearLayout.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT, 1f));
            rbn.setTextAppearance(this.getContext(), R.style.tradeGothicLTStd_Dynamic18);
            radioGroup.addView(rbn);
        }

        radioGroup.requestLayout();
    }

    private int getProviderRating() {
        return Math.round(providerRating.getRating());
    }

    private int getVisitRating() {
        return Math.round(experienceRating.getRating());
    }

    private String getQuestionAnswer() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            return "";
        } else {
            View radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            int radioButtonIndex = radioGroup.indexOfChild(radioButton);

            if (radioButtonIndex > -1 && radioButtonIndex < radioGroup.getChildCount()) {
                RadioButton optionSelected = (RadioButton) radioGroup.getChildAt(radioButtonIndex);

                Timber.d("feedback. answer selected = " + optionSelected.getText().toString());

                return optionSelected.getText().toString();
            }
        }
        return "";
    }

    private void sendRatings() {

        if (getProviderRating() == 0 && getVisitRating() == 0) {

            Timber.d("Feedback. User has not submitted both ProviderRating and VisitRating. ");

            sendFeedback();

        } else {
            AwsManager.getInstance().getAWSDK().getVisitManager().sendRatings(
                    AwsManager.getInstance().getVisit(),
                    getProviderRating(),
                    getVisitRating(),
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void voida, SDKError sdkError) {

                            if (sdkError == null) {
                                Timber.d("sendRatings succeeded! ");
                                sendFeedback();

                            } else {
                                Timber.e("sendRatings failed! :/");
                                Timber.e("SDK Error: " + sdkError);
                                sendFeedback();
                                CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_ratings_submission_failed));
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("sendRatings failed! :/");
                            Timber.e("Throwable = " + throwable);
                            sendFeedback();
                            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_ratings_submission_failed));
                        }
                    }
            );
        }
    }

    private void sendFeedback() {

        if (visitSummary == null) {
            Timber.e("sendVisitFeedback visitSummary is null. ");
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_answers_submission_failed));
            goBackToDashboard();
            return;
        } else if (visitSummary.getConsumerFeedbackQuestion() == null) {
            Timber.e("sendVisitFeedback visitSummary.getConsumerFeedbackQuestion() is null. ");
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_answers_submission_failed));
            goBackToDashboard();
            return;
        } else if (visitSummary != null && visitSummary.getConsumerFeedbackQuestion() != null) {
            visitSummary.getConsumerFeedbackQuestion().setQuestionAnswer(getQuestionAnswer());
        }

        AwsManager.getInstance().getAWSDK().getVisitManager().sendVisitFeedback(
                AwsManager.getInstance().getVisit(),
                visitSummary.getConsumerFeedbackQuestion(),
                new SDKValidatedCallback<Void, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {

                        Timber.e("sendVisitFeedback Validation failed! :/");
                        Timber.e("Map: " + map);
                        CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_answers_submission_failed));
                    }

                    @Override
                    public void onResponse(@Nullable Void aVoid, @Nullable SDKError sdkError) {

                        if (sdkError == null) {
                            Timber.d("sendVisitFeedback succeeded! ");
                            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_completed));
                            goBackToDashboard();

                        } else {
                            Timber.e("sendVisitFeedback failed! :/");
                            Timber.e("SDK Error: " + sdkError);
                            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_answers_submission_failed));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable throwable) {
                        Timber.e("sendVisitFeedback failed! :/");
                        Timber.e("Throwable = " + throwable);
                        CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_answers_submission_failed));
                    }
                }
        );
    }
}
