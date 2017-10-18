/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.visit;

import android.os.Bundle;

import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.entity.visit.VisitReportDetail;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import icepick.State;
import rx.Observable;

/**
 * Presenter for VisitReportDetailActivity
 */
public class VisitReportDetailPresenter extends BaseSampleNucleusRxPresenter<VisitReportDetailActivity> {

    private static final int GET_VISIT_REPORT_DETAIL = 1110;
    private static final int GET_ATTACHMENT = 1101;

    @State
    VisitReport visitReport;
    @State
    VisitReportDetail visitReportDetail;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_ATTACHMENT,
                new SampleRequestFunc0<SDKResponse<FileAttachment, SDKError>>(GET_ATTACHMENT) {
                    @Override
                    public Observable<SDKResponse<FileAttachment, SDKError>> go() {
                        return observableService.getVisitReportAttachment(consumer, visitReport);
                    }
                },
                new SampleResponseAction2<FileAttachment, SDKError, SDKResponse<FileAttachment, SDKError>>(GET_ATTACHMENT) {
                    @Override
                    public void onSuccess(VisitReportDetailActivity visitReportDetailActivity, FileAttachment fileAttachment) {
                        visitReportDetailActivity.setAttachment(fileAttachment);
                        stop(GET_ATTACHMENT); // make sure we don't restart this when returning from viewer
                    }
                },
                new SampleFailureAction2(GET_ATTACHMENT));


        restartableLatestCache(GET_VISIT_REPORT_DETAIL,
                new SampleRequestFunc0<SDKResponse<VisitReportDetail, SDKError>>(GET_VISIT_REPORT_DETAIL) {
                    @Override
                    public Observable<SDKResponse<VisitReportDetail, SDKError>> go() {
                        return observableService.getVisitReportDetail(consumer, visitReport);
                    }
                },
                new SampleResponseAction2<VisitReportDetail, SDKError, SDKResponse<VisitReportDetail, SDKError>>(GET_VISIT_REPORT_DETAIL) {
                    @Override
                    public void onSuccess(VisitReportDetailActivity visitSummaryActivity, VisitReportDetail visitDetailReport) {
                        setVisitReportDetail(visitDetailReport);
                    }
                },
                new SampleFailureAction2(GET_VISIT_REPORT_DETAIL)
        );

    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(VisitReportDetailActivity view) {
        super.onTakeView(view);
        if (visitReportDetail == null) {
            start(GET_VISIT_REPORT_DETAIL);
        }
        else {
            displayVisitDetails();
        }
    }

    public void setVisitReport(VisitReport visit) {
        this.visitReport = visit;
    }

    private void setVisitReportDetail(VisitReportDetail visitReport) {
        this.visitReportDetail = visitReport;
        displayVisitDetails();
    }

    private void displayVisitDetails() {
        if (visitReportDetail != null) {
            view.setVisitReportDetail(
                    visitReportDetail,
                    awsdk.getPreferredLocale(),
                    awsdk.getConfiguration().getCurrencyCode());
        }
    }

    public void viewPDF() {
        start(GET_ATTACHMENT);
    }
}
