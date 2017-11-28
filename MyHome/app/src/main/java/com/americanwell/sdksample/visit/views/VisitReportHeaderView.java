/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.util.LocaleUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View for displaying view report header information
 */
public class VisitReportHeaderView extends LinearLayout {

    @Inject
    LocaleUtils localeUtils;

    @BindView(R.id.visit_report_practice)
    TextView practiceView;
    @BindView(R.id.visit_report_title)
    TextView titleView;
    @BindView(R.id.visit_report_schedule)
    TextView scheduleView;
    @BindView(R.id.visit_report_billing_summary)
    TextView summaryView;

    public VisitReportHeaderView(Context context) {
        super(context);
        init(context);
    }

    public VisitReportHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_visit_report_header, this, true);

        ButterKnife.bind(this);
        SampleApplication.getActivityComponent().inject(this);
    }

    public void setVisitReportDetail(VisitReportDetail report, Locale locale, String currencyCode) {
        setPractice(report.getPracticeName());
        setTitle(report.getTitle());
        setScheduleSummary(report, locale);
        setBillingSummary(report, currencyCode);
    }

    public void setPractice(@NonNull String practice) {
        practiceView.setText(practice);
    }

    public void setTitle(@NonNull String title) {
        titleView.setText(title);
    }

    public void setScheduleSummary(@NonNull VisitReportDetail report, Locale locale) {
        Date date = new Date(report.getSchedule().getActualStartTime());

        int minutes = report.getSchedule().getActualDurationMs() / 1000 / 60;
        String minutesString = getResources().getQuantityString(
                R.plurals.visit_reports_schedule_summary_minutes_plurals,
                minutes, minutes);

        String scheduleSummary = String.format(
                getResources().getString(R.string.visit_reports_schedule_summary),
                localeUtils.formatVisitSummaryDate(date),
                localeUtils.formatVisitSummaryTime(date),
                minutesString);

        scheduleView.setText(scheduleSummary);
    }

    public void setBillingSummary(@NonNull VisitReportDetail report, String currencyCode) {
        String cost = localeUtils.formatCurrency(report.getPaymentAmount(), currencyCode);

        boolean isDeferred = (report.getPaymentAmount() == 0 && report.getVisitCost().isDeferredBillingInEffect());
        String costText = (isDeferred)
                ? report.getVisitCost().getDeferredBillingWrapUpText()
                : String.format(getResources().getString(R.string.visit_reports_billing_summary_cost), cost);

        String coupon = "";
        if (!TextUtils.isEmpty(report.getCouponCode())) {
            coupon = String.format(getResources().getString(R.string.visit_reports_billing_summary_coupon),
                    report.getCouponCode());
        }
        String paymentType = "";
        if (!TextUtils.isEmpty(report.getPaymentType())) {
            paymentType = String.format(getResources().getString(R.string.visit_reports_billing_summary_type),
                    report.getPaymentType());
        }

        String summary = costText + coupon + paymentType;
        summaryView.setText(summary);
    }
}
