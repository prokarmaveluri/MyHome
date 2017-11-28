/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.visit.AgendaItemFollowUp;
import com.americanwell.sdk.entity.visit.ChatItem;
import com.americanwell.sdk.entity.visit.PostVisitFollowUpItem;
import com.americanwell.sdk.entity.visit.ProviderEntries;
import com.americanwell.sdk.entity.visit.Topic;
import com.americanwell.sdk.entity.visit.TriageQuestion;
import com.americanwell.sdk.entity.visit.VisitDiagnosis;
import com.americanwell.sdk.entity.visit.VisitProcedure;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdk.entity.visit.VisitRx;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.visit.views.VisitReportSubHeaderView;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * SubClass of {@link RangedAdapter} used specifically for displaying {@link VisitReportDetail}
 * data.
 */
public class VisitReportAdapter extends RangedAdapter<VisitReportHolder> {
    private VisitReportDetail mReport;

    public void setVisitReportDetail(VisitReportDetail report, Locale locale, String currencyCode) {
        resetData();
        mReport = report;

        addAdapter(new HeaderAdapter(report, locale, currencyCode));

        if (!TextUtils.isEmpty(report.getTransferredFromProviderName())) {
            addAdapter(new TransferAdapter(report));
        }

        List<Topic> topics = report.getConsumerSummaryTopics();
        if (topics != null && topics.size() > 0) {
            addAdapter(new TopicsAdapter(report));
        }

        List<TriageQuestion> triageQuestions = report.getTriageQuestions();
        if (triageQuestions != null && triageQuestions.size() > 0) {
            addAdapter(new TriageAdapter(report));
        }

        List<String> acceptedDisclaimers = report.getAcceptedDisclaimers();
        if (acceptedDisclaimers != null && acceptedDisclaimers.size() > 0) {
            addAdapter(new AcceptedDisclaimerAdapter(report));
        }

        ProviderEntries providerEntries = report.getProviderEntries();

        if (providerEntries != null) {

            if (!TextUtils.isEmpty(providerEntries.getNotesSpanned())) {
                addAdapter(new ProviderNotesAdapter(providerEntries.getNotesSpanned()));
            }

            Set<VisitDiagnosis> diagnoses = providerEntries.getDiagnoses();
            Set<VisitProcedure> procedures = providerEntries.getProcedures();
            if ((diagnoses != null && diagnoses.size() > 0) || (procedures != null && procedures.size() > 0)) {
                addAdapter(new DiagnosesAndProceduresAdapter(providerEntries));
            }

            Set<VisitRx> prescriptions = providerEntries.getPrescriptions();
            if (prescriptions != null && prescriptions.size() > 0) {
                addAdapter(new PrescriptionAdapter(providerEntries, report.getPharmacy()));
            }

            List<AgendaItemFollowUp> followUps = providerEntries.getAgendaItemFollowUps();
            if (followUps != null && followUps.size() > 0) {
                addAdapter(new AgendaFollowupAdapter(providerEntries.getAgendaItemFollowUps()));
            }

            List<PostVisitFollowUpItem> pvFollowUps = providerEntries.getPostVisitFollowUpItems();
            if (pvFollowUps != null && pvFollowUps.size() > 0) {
                addAdapter(new PostVisitFollowUpAdapter(providerEntries.getPostVisitFollowUpItems()));
            }
        }

        List<ChatItem> chatItems = report.getChatReport().getChatItems();
        if (chatItems != null && chatItems.size() > 0) {
            addAdapter(new ChatAdapter(report));
        }

        if (!TextUtils.isEmpty(report.getHipaaNoticeText())) { // since AWSDK 3.1
            addAdapter(new HipaaNoticeAdapter(report));
        }

        if (!TextUtils.isEmpty(report.getAdditionalHipaaNoticeText())) { // since AWSDK 3.1
            addAdapter(new AdditionalHipaaNoticeAdapter(report));
        }

        notifyDataSetChanged();
    }

    @Override
    public VisitReportHolder getTitleViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = new VisitReportSubHeaderView(parent.getContext());
        view.setLayoutParams(params);
        return new VisitReportHolder(view);
    }

    @Override
    protected void bindTitleHolder(VisitReportHolder holder, BaseSubAdapter adapter) {
        int titleRes = adapter.getTitleRes();
        if (titleRes == 0) {
            titleRes = R.string.visit_reports_title_default;
        }
        String title = holder.itemView.getResources().getString(titleRes);
        ((VisitReportSubHeaderView) holder.itemView).setTitle(title);
    }
}

  
