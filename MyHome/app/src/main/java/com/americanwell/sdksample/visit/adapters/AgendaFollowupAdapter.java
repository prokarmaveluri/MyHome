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

import com.americanwell.sdk.entity.visit.AgendaItemFollowUp;
import com.prokarma.myhome.R;

import java.util.List;

/**
 * Sub Adapter used for controlling Agenda Followup data in the RangedAdapter
 */
public class AgendaFollowupAdapter extends BaseSubAdapter<VisitReportHolder> {

    List<AgendaItemFollowUp> mAgendaItems;

    AgendaFollowupAdapter(List<AgendaItemFollowUp> agendaItems) {
        mAgendaItems = agendaItems;
    }

    @Override
    int getCount() {
        return mAgendaItems != null ? mAgendaItems.size() : 0;
    }

    @Override
    int getItemViewType(int position) {
        return VisitAdapterConstants.TYPE_AGENDA_ITEM_FOLLOW_UP;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TextView(context));
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TextView) holder.itemView).setText(mAgendaItems.get(position).getDescription());
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_AGENDA_ITEM_FOLLOW_UP};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_AGENDA_ITEM_FOLLOW_UP;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_follow_up;
    }
}
