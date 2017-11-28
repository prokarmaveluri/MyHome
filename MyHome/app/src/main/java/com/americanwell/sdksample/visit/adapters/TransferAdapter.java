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
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.provider.ProviderType;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdksample.visit.views.VisitReportTransferView;

/**
 * Sub Adapter used for displaying Transfer data in the RangedAdapter
 */
public class TransferAdapter extends BaseSubAdapter<VisitReportHolder> {

    private VisitReportDetail mReport;
    String mProvider;
    String mNotes;
    String mSpecialty;

    TransferAdapter(VisitReportDetail visitReportDetail) {
        mReport = visitReportDetail;
        mProvider = visitReportDetail.getTransferredFromProviderName();
        mNotes = visitReportDetail.getTransferNote();

        ProviderType providerType = visitReportDetail.getTransferredFromSpecialty();
        mSpecialty = null;
        if (providerType != null) {
            mSpecialty = providerType.getName();
        }

        setHasTitle(false);
    }

    @Override
    int getCount() {
        return 1;
    }

    @Override
    int getItemViewType(int position) {
        return VisitAdapterConstants.TYPE_TRANSFER;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = new VisitReportTransferView(context);
        return new VisitReportHolder(view);
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        VisitReportTransferView transfer = ((VisitReportTransferView) holder.itemView);
        transfer.setProvider(mProvider, mSpecialty);
        transfer.setNotes(mNotes);
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_TRANSFER};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_TRANSFER;
    }

    @Override
    int getTitleRes() {
        return 0;
    }

}
