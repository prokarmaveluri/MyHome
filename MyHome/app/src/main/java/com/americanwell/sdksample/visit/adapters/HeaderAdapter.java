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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdksample.visit.views.VisitReportHeaderView;

import java.util.Locale;

/**
 * Sub Adapter used for displaying the main Visit Report header in the RangedAdapter
 */
public class HeaderAdapter extends BaseSubAdapter<VisitReportHolder> {

    private VisitReportDetail mReport;
    private Locale mLocale;
    private String mCurrencyCode;

    HeaderAdapter(VisitReportDetail visitReportDetail, Locale locale, String currencyCode) {
        mReport = visitReportDetail;
        mLocale = locale;
        mCurrencyCode = currencyCode;

        setHasTitle(false);
    }

    @Override
    int getCount() {
        return 1;
    }

    @Override
    int getItemViewType(int position) {
        return VisitAdapterConstants.TYPE_HEADER;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = new VisitReportHeaderView(context);
        view.setLayoutParams(params);

        return new VisitReportHolder(view);
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((VisitReportHeaderView) holder.itemView).setVisitReportDetail(mReport, mLocale, mCurrencyCode);
    }

    @Override
    int[] getViewTypes() {
        return new int[]{VisitAdapterConstants.TYPE_HEADER};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == VisitAdapterConstants.TYPE_HEADER;
    }

    @Override
    int getTitleRes() {
        return 0;
    }

}
