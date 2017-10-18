/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.visit.TriageQuestion;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.visit.views.TriageQuestionView;

import java.util.List;

/**
 * Sub Adapter used for controlling Triage question data in the RangedAdapter
 */
public class TriageAdapter extends BaseSubAdapter<VisitReportHolder> {

    List<TriageQuestion> mTriageQuestions;

    TriageAdapter(VisitReportDetail report) {
        mTriageQuestions = report.getTriageQuestions();
    }

    @Override
    int getCount() {
        return mTriageQuestions != null ? mTriageQuestions.size() : 0;
    }

    @Override
    int getItemViewType(int position) {
        return VisitAdapterConstants.TYPE_TRIAGE_QA;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TriageQuestionView(context));
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TriageQuestionView) holder.itemView).setTriageQuestion(mTriageQuestions.get(position));
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_TRIAGE_QA};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_TRIAGE_QA;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_triage;
    }
}
