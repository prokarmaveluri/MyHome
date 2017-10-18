/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.internal.logging.DefaultLogger;
import com.americanwell.sdksample.BaseSampleNucleusActivity;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.NonScrollListView;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to fetch and display the visitReport summary (aka wrap-up) after a visitReport has
 * completed
 */
@RequiresPresenter(VisitSummaryPresenter.class)
public class VisitSummaryActivity extends BaseSampleNucleusActivity<VisitSummaryPresenter> {

    private static final String LOG_TAG = VisitSummaryActivity.class.getName();

    private static final String EXTRA_VISIT = "visitReport";

    @BindView(R.id.visit_summary_summary)
    TextView summaryText;
    @BindView(R.id.visit_summary_charge)
    TextView chargeText;
    @BindView(R.id.visit_summary_pharmacy)
    TextView pharmacyText;
    @BindView(R.id.visit_summary_feedback_question)
    TextView feedbackQuestionText;
    @BindView(R.id.visit_summary_feedback_answers)
    NonScrollListView feedbackAnswersList;
    @BindView(R.id.visit_summary_email_list)
    NonScrollListView summaryEmailList;
    @BindView(R.id.consumer_ratings_provider_label)
    View consumerRatingLabelProvider;
    @BindView(R.id.consumer_ratingbar_provider)
    RatingBar consumerRatingBarProvider;
    @BindView(R.id.consumer_ratingbar_visit)
    RatingBar consumerRatingBarVisit;
    @BindView(R.id.visit_summary_provider_notes_layout)
    View providerNotesLayout;
    @BindView(R.id.visit_summary_provider_notes_text)
    TextView providerNotesText;
    @BindView(R.id.visit_summary_diagnoses_layout)
    View diagnosesLayout;
    @BindView(R.id.visit_summary_diagnoses_text)
    TextView diagnosesText;
    @BindView(R.id.visit_summary_prescriptions_layout)
    View prescriptionsLayout;
    @BindView(R.id.visit_summary_prescriptions_text)
    TextView prescriptionsText;
    @BindView(R.id.visit_summary_followup_layout)
    View followupLayout;
    @BindView(R.id.visit_summary_followup_text)
    TextView followupText;
    @BindView(R.id.visit_summary_deferred_billilng)
    TextView deferredBillingText;
    @BindView(R.id.visit_summary_accept_hipaa_layout)
    View acceptHipaaLayout;
    @BindView(R.id.visit_summary_accept_hipaa_notice)
    CheckBox acceptHipaaNotice;
    @BindView(R.id.visit_summary_accept_hipaa_link)
    TextView acceptHipaaLink;

    @BindString(R.string.validation_generic_title)
    String genericTitle;
    @BindString(R.string.consumer_ratings_validation)
    String ratingsValidation;
    @BindString(R.string.visit_summary_accept_hipaa_validation)
    String hipaaValidation;

    private ArrayAdapter<String> feedbackAnswersAdapter;
    private ArrayAdapter<String> summaryEmailsAdapter;

    public static Intent makeIntent(final Context context, final Visit visit) {
        final Intent intent = new Intent(context, VisitSummaryActivity.class);
        intent.putExtra(EXTRA_VISIT, visit);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_visit_summary);

        if (savedInstanceState == null) {
            getPresenter().setVisit((Visit) getIntent().getParcelableExtra(EXTRA_VISIT));
        }
        feedbackAnswersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice);
        feedbackAnswersList.setAdapter(feedbackAnswersAdapter);
        feedbackAnswersList.setFocusable(false);
        summaryEmailsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked);
        summaryEmailList.setAdapter(summaryEmailsAdapter);
        summaryEmailList.setFocusable(false);
        DefaultLogger.d(LOG_TAG, "initialized lists and adapters");

        consumerRatingBarProvider.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                getPresenter().setProviderRating(Float.valueOf(rating).intValue());
            }
        });

        consumerRatingBarVisit.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                getPresenter().setVisitRating(Float.valueOf(rating).intValue());
            }
        });
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }

    public void setShowProviderRating(final boolean show) {
        consumerRatingBarProvider.setVisibility(show ? View.VISIBLE : View.GONE);
        consumerRatingLabelProvider.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setProviderRatingError() {
        setError(genericTitle, ratingsValidation);
    }

    public void setFeedbackQuestion(final String value) {
        feedbackQuestionText.setText(value);
    }

    public void setFeedbackResponseOptions(final List<String> options) {
        feedbackAnswersAdapter.clear();
        feedbackAnswersAdapter.addAll(options);
    }

    @OnItemClick(R.id.visit_summary_feedback_answers)
    public void onFeedbackAnswerClick(int pos) {
        getPresenter().setFeedbackAnswer(feedbackAnswersAdapter.getItem(pos));
    }

    public void setFeedbackAnswer(final String answer) {
        if (!TextUtils.isEmpty(answer)) {
            int pos = feedbackAnswersAdapter.getPosition(answer);
            feedbackAnswersList.setSelection(pos);
        }
    }

    public void setProviderName(final String value) {
        summaryText.setText(getString(R.string.visit_summary_summary, value));
    }

    public void setExpectedConsumerCopayCost(String copayCost) {
        chargeText.setText(getString(R.string.visit_summary_charge, copayCost));
    }

    public void setShowCostSummary(final boolean showCostSummary) {
        chargeText.setVisibility(showCostSummary ? View.VISIBLE : View.GONE);
    }

    public void setSummaryEmails(final List<String> emails) {
        summaryEmailsAdapter.clear();
        summaryEmailsAdapter.addAll(emails);
    }

    @OnItemClick(R.id.visit_summary_email_list)
    public void onEmailSelected(int position) {
        final String email = summaryEmailsAdapter.getItem(position);
        getPresenter().setEmailSelected(email, summaryEmailList.isItemChecked(position));
    }

    public void setPharmacy(final Pharmacy pharmacy) {
        if (pharmacy != null) {
            pharmacyText.setText(sampleUtils.buildPharmacyDisplayText(
                    this, pharmacy, true, getPresenter().isMultiCountry()));
            pharmacyText.setVisibility(View.VISIBLE);
        }
        else {
            pharmacyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        //        We want the user to complete the form so we disable the back button
    }

    @OnClick(R.id.fab)
    public void next() {
        getPresenter().submit();
    }

    public void setSummarySubmitted() {
        Toast.makeText(this, R.string.visit_summary_submitted, Toast.LENGTH_SHORT).show();
        goHome();
    }

    public void setProviderNotes(final Spanned spanned) {
        if (spanned != null) {
            providerNotesText.setText(spanned);
            providerNotesLayout.setVisibility(View.VISIBLE);
        }
        else {
            providerNotesLayout.setVisibility(View.GONE);
        }
    }

    public void setDiagnoses(final String diagnoses) {
        if (TextUtils.isEmpty(diagnoses)) {
            diagnosesLayout.setVisibility(View.GONE);
        }
        else {
            diagnosesText.setText(diagnoses);
            diagnosesLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setPrescriptions(final String prescriptions) {
        if (TextUtils.isEmpty(prescriptions)) {
            prescriptionsLayout.setVisibility(View.GONE);
        }
        else {
            prescriptionsText.setText(prescriptions);
            prescriptionsLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setFollowup(final String followup) {
        if (TextUtils.isEmpty(followup)) {
            followupLayout.setVisibility(View.GONE);
        }
        else {
            followupText.setText(followup);
            followupLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setDeferredBillingPrompt(final String prompt) {
        if (TextUtils.isEmpty(prompt)) {
            deferredBillingText.setVisibility(View.GONE);
        }
        else {
            deferredBillingText.setVisibility(View.VISIBLE);
            deferredBillingText.setText(prompt);
        }
    }

    public void showAcceptHipaaNotice(boolean show) { // since AWSDK 3.1
        acceptHipaaLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @OnCheckedChanged(R.id.visit_summary_accept_hipaa_notice) // since AWSDK 3.1
    public void onAcceptHipaaNotice(boolean checked) {
        getPresenter().setHipaaNoticeAccepted(checked);
    }

    public void setHipaaNotAccepted() { // since AWSDK 3.1
        setError(genericTitle, hipaaValidation);
    }

    @OnClick(R.id.visit_summary_accept_hipaa_link)
    public void showHipaaNotice() {
        @SuppressLint("InflateParams")
        final WebView disclaimerWebView = (WebView) getLayoutInflater().inflate(R.layout.dialog_disclaimer, null);
        disclaimerWebView.loadData(getPresenter().getHipaaNotice(), "text/html;charset=UTF-8", "UTF-8");
        new AlertDialog.Builder(this)
                .setView(disclaimerWebView)
                .show();
    }
}
