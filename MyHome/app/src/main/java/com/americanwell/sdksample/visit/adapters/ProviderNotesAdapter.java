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
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prokarma.myhome.R;

/**
 * Sub Adapter used for displaying Provider notes in the RangedAdapter
 */
public class ProviderNotesAdapter extends BaseSubAdapter<VisitReportHolder> {

    Spanned mNotes;

    ProviderNotesAdapter(Spanned notes) {
        mNotes = notes;
    }

    @Override
    int getCount() {
        return mNotes != null ? 1 : 0;
    }

    @Override
    int getItemViewType(int position) {
        return VisitAdapterConstants.TYPE_PROVIDER_NOTES;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TextView(context));
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TextView) holder.itemView).setText(mNotes);
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_PROVIDER_NOTES};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_PROVIDER_NOTES;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_notes;
    }
}
