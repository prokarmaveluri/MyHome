package com.prokarma.myhome.features.televisit.feedback;

import android.os.Build;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.VisitSummary;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import com.whinc.widget.ratingbar.RatingBar;

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
    private int providerRatingValue;
    private int experienceRatingValue;

    private TextView textQuestion3;
    private LinearLayout layoutCheckboxes;
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

        layoutCheckboxes = (LinearLayout) view.findViewById(R.id.layout_checkboxes);
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

        question3Options.clear();
        question3Options.add(getContext().getString(R.string.emergency_room));
        question3Options.add(getContext().getString(R.string.urgent_care_center));
        question3Options.add(getContext().getString(R.string.retail_health_clinic));
        question3Options.add(getContext().getString(R.string.doctor_s_office));
        question3Options.add(getContext().getString(R.string.done_nothing));

        displayDynamicQuestionAnswer();

        addClickEventsToRatingBars();

        getVisitSummary();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.finish_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_FEEDBACK_SCREEN, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish:
                showLoading();

                String answer = getQuestionAnswer();

                if (answer == null || answer.isEmpty()) {

                    feedbackLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    CommonUtil.showToast(getActivity(), getString(R.string.feedback_choose_answer));
                } else if (providerRatingValue == 0 && experienceRatingValue == 0) {

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

        textQuestion3.setText(question3);
        textQuestion3.setTextAppearance(this.getContext(), R.style.tradeGothicLTStd_Dynamic20);

        layoutCheckboxes.removeAllViews();

        int i = 0;
        for (String questionAnswer : question3Options) {

            i = i + 1;

            CheckBox view = new CheckBox(this.getContext());
            view.setId(i + 1000);
            view.setText(questionAnswer);
            view.setContentDescription(questionAnswer);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT, 1f);
            llp.setMargins(0, 0, 0, 27);
            view.setLayoutParams(llp);
            view.setTextAppearance(this.getContext(), R.style.tradeGothicLTStd_Dynamic18);
            view.setIncludeFontPadding(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setTextColor(this.getContext().getResources().getColor(R.color.db_charcoal_grey, this.getContext().getTheme()));
            } else {
                view.setTextColor(this.getContext().getResources().getColor(R.color.db_charcoal_grey));
            }

            addClickEventsToAnswerCheckBox(view);

            layoutCheckboxes.addView(view);
        }
        layoutCheckboxes.requestLayout();
    }

    private void addClickEventsToAnswerCheckBox(CheckBox view) {

        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                if (!isChecked || layoutCheckboxes == null || layoutCheckboxes.getChildCount() == 0) {
                                                    return;
                                                }
                                                //unselect rest of all the checkboxes
                                                for (int i = 0; i < layoutCheckboxes.getChildCount(); i++) {
                                                    if (layoutCheckboxes.getChildAt(i) instanceof CheckBox) {
                                                        CheckBox chk = (CheckBox) layoutCheckboxes.getChildAt(i);
                                                        if (chk.isChecked() && chk.getId() != buttonView.getId()) {
                                                            chk.setChecked(false);
                                                        }
                                                    }
                                                }
                                            }
                                        }
        );
    }

    private void addClickEventsToRatingBars() {
        providerRating.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(RatingBar view, int preCount, int curCount) {
                providerRatingValue = curCount;
            }
        });
        experienceRating.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(RatingBar view, int preCount, int curCount) {
                experienceRatingValue = curCount;
            }
        });
    }

    private String getQuestionAnswer() {
        if (layoutCheckboxes == null || layoutCheckboxes.getChildCount() == 0) {
            return "";
        }
        for (int i = 0; i < layoutCheckboxes.getChildCount(); i++) {
            if (layoutCheckboxes.getChildAt(i) instanceof CheckBox) {
                CheckBox chk = (CheckBox) layoutCheckboxes.getChildAt(i);
                if (chk.isChecked()) {
                    return chk.getText().toString();
                }
            }
        }
        return "";
    }

    private void sendRatings() {

        if (providerRatingValue == 0 && experienceRatingValue == 0) {

            Timber.d("Feedback. User has not submitted both ProviderRating and VisitRating. ");

            sendFeedback();

        } else {
            AwsManager.getInstance().getAWSDK().getVisitManager().sendRatings(
                    AwsManager.getInstance().getVisit(),
                    providerRatingValue,
                    experienceRatingValue,
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void voida, SDKError sdkError) {

                            if (sdkError == null) {
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
                        CommonUtil.showToast(getActivity(), getActivity().getString(R.string.feedback_answers_submission_failed));
                    }

                    @Override
                    public void onResponse(@Nullable Void aVoid, @Nullable SDKError sdkError) {

                        if (sdkError == null) {
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
