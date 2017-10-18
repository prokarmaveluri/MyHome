/*
 * Copyright 2017 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.visit.views.TitledDataView;

import static com.americanwell.sdksample.visit.adapters.VisitAdapterConstants.TYPE_HIPAA_NOTICE;

/**
 * Sub adapter for controlling HIPAA notice data
 * @since AWSDK 3.1
 */
public class HipaaNoticeAdapter extends BaseSubAdapter<VisitReportHolder> {

    String hipaaNotice;

    HipaaNoticeAdapter(VisitReportDetail report) {
        hipaaNotice = report.getHipaaNoticeText();
    }

    @Override
    int getCount() {
        return TextUtils.isEmpty(hipaaNotice) ? 0 : 1;
    }

    @Override
    int getItemViewType(int position) {
        return TYPE_HIPAA_NOTICE;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TitledDataView(context));
    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TitledDataView)holder.itemView).setSubText(hipaaNotice);
    }

    @Override
    int[] getViewTypes() {
        return new int[]{TYPE_HIPAA_NOTICE};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == TYPE_HIPAA_NOTICE;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_hipaa_notice;
    }
}
