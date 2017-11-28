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

import static com.americanwell.sdksample.visit.adapters.VisitAdapterConstants.TYPE_HIPAA_NOTICE_ADDITIONAL;

/**
 * Sub adapter for controlling additional HIPAA notice data
 * @since AWSDK 3.1
 */
public class AdditionalHipaaNoticeAdapter extends BaseSubAdapter<VisitReportHolder> {

    String additionalHipaaNotice;

    AdditionalHipaaNoticeAdapter(VisitReportDetail report) {
        additionalHipaaNotice = report.getAdditionalHipaaNoticeText();
    }

    @Override
    int getCount() {
        return TextUtils.isEmpty(additionalHipaaNotice) ? 0 : 1;
    }

    @Override
    int getItemViewType(int position) {
        return TYPE_HIPAA_NOTICE_ADDITIONAL;
    }

    @Override
    VisitReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new VisitReportHolder(new TitledDataView(context));    }

    @Override
    void onBindViewHolder(VisitReportHolder holder, int position) {
        ((TitledDataView)holder.itemView).setSubText(additionalHipaaNotice);
    }

    @Override
    int[] getViewTypes() {
        return new int[]{TYPE_HIPAA_NOTICE_ADDITIONAL};
    }

    @Override
    boolean isViewTypeSupported(int viewType) {
        return viewType == TYPE_HIPAA_NOTICE_ADDITIONAL;
    }

    @Override
    int getTitleRes() {
        return R.string.visit_reports_title_hipaa_notice_additional;
    }
}
