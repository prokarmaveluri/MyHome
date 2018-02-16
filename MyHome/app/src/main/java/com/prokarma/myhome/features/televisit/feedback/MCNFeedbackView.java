package com.prokarma.myhome.features.televisit.feedback;

import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.VisitSummary;
import com.prokarma.myhome.R;
import com.whinc.widget.ratingbar.RatingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by veluri on 2/16/18.
 */

public class MCNFeedbackView implements MCNFeedbackContract.View {

    private MCNFeedbackPresenter presenter;

    private LinearLayout feedbackLayout;
    private ProgressBar progressBar;
    private RatingBar providerRating;
    private RatingBar experienceRating;

    private TextView textQuestion3;
    private LinearLayout layoutCheckboxes;
    private String question1 = "";
    private String question2 = "";
    private String question3 = "";
    private List<String> question3Options = new ArrayList<String>();

    public VisitSummary visitSummary;
    public int providerRatingValue;
    public int experienceRatingValue;


    public MCNFeedbackView(final View view, final MCNFeedbackPresenter presenter) {
        this.presenter = presenter;

        feedbackLayout = (LinearLayout) view.findViewById(R.id.feedback_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        providerRating = (RatingBar) view.findViewById(R.id.rate_provider);
        experienceRating = (RatingBar) view.findViewById(R.id.rate_experience);

        layoutCheckboxes = (LinearLayout) view.findViewById(R.id.layout_checkboxes);
        textQuestion3 = (TextView) view.findViewById(R.id.text_question3);

        feedbackLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        question1 = presenter.context.getString(R.string.rate_your_provider);
        question2 = presenter.context.getString(R.string.rate_your_overall_experience);
        question3 = presenter.context.getString(R.string.if_you_had_not_used_my_care_now_today_where_would_you_have_gone_instead);

        question3Options.clear();
        question3Options.add(presenter.context.getString(R.string.emergency_room));
        question3Options.add(presenter.context.getString(R.string.urgent_care_center));
        question3Options.add(presenter.context.getString(R.string.retail_health_clinic));
        question3Options.add(presenter.context.getString(R.string.doctor_s_office));
        question3Options.add(presenter.context.getString(R.string.done_nothing));

        displayDynamicQuestionAnswer();

        addClickEventsToRatingBars();
    }

    @Override
    public void getVisitSummaryComplete(VisitSummary visitSummaryObject) {

        progressBar.setVisibility(View.GONE);

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
    }

    @Override
    public void getVisitSummaryFailed(String errorMessage) {
        progressBar.setVisibility(View.GONE);
    }



    public void showLoading() {
        feedbackLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        feedbackLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void displayDynamicQuestionAnswer() {

        textQuestion3.setText(question3);
        textQuestion3.setTextAppearance(presenter.context, R.style.tradeGothicLTStd_Dynamic20);

        layoutCheckboxes.removeAllViews();

        int i = 0;
        for (String questionAnswer : question3Options) {

            i = i + 1;

            CheckBox view = new CheckBox(presenter.context);
            view.setId(i + 1000);
            view.setText(questionAnswer);
            view.setContentDescription(questionAnswer);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT, 1f);
            llp.setMargins(0, 0, 0, 27);
            view.setLayoutParams(llp);
            view.setTextAppearance(presenter.context, R.style.tradeGothicLTStd_Dynamic18);
            view.setIncludeFontPadding(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setTextColor(presenter.context.getResources().getColor(R.color.db_charcoal_grey, presenter.context.getTheme()));
            } else {
                view.setTextColor(presenter.context.getResources().getColor(R.color.db_charcoal_grey));
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

    public String getQuestionAnswer() {
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
}
