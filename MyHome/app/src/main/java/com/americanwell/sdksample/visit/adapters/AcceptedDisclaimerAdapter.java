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
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.prokarma.myhome.R;

import java.util.List;

/**
 * Sub Adapter used for controlling Accepted Disclaimer data in the RangedAdapter
 */
public class AcceptedDisclaimerAdapter extends BaseSubAdapter<VisitReportHolder> {

    List<String> mAcceptedDisclaimers;

    AcceptedDisclaimerAdapter(VisitReportDetail report) {
        mAcceptedDisclaimers = report.getAcceptedDisclaimers();
    }

    @Override
    int getCount() {
        return mAcceptedDisclaimers != null ? mAcceptedDisclaimers.size() : 0;
    }

    @Override
    int getItemViewType(int position) {
        return VisitAdapterConstants.TYPE_ACCEPTED_DISCLAIMERS;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TextView(context));
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TextView) holder.itemView).setText(mAcceptedDisclaimers.get(position));
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_ACCEPTED_DISCLAIMERS};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_ACCEPTED_DISCLAIMERS;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_accepted_disclaimers;
    }
}
