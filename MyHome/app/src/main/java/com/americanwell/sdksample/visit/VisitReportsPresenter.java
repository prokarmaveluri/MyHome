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

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdksample.BaseSampleNucleusRxPresenter;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.rx.SDKResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import icepick.State;
import rx.Observable;

/**
 * Presenter for VisitReportsActivity
 */
public class VisitReportsPresenter extends BaseSampleNucleusRxPresenter<VisitReportsActivity> {

    private static final int GET_VISIT_REPORTS = 1100;

    @State
    ArrayList<VisitReport> visitReports;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableLatestCache(GET_VISIT_REPORTS,
                new SampleRequestFunc0<SDKResponse<List<VisitReport>, SDKError>>(GET_VISIT_REPORTS) {
                    @Override
                    public Observable<SDKResponse<List<VisitReport>, SDKError>> go() {
                        return observableService.getVisitReports(consumer);
                    }
                },
                new SampleResponseAction2<List<VisitReport>, SDKError, SDKResponse<List<VisitReport>, SDKError>>(GET_VISIT_REPORTS) {
                    @Override
                    public void onSuccess(VisitReportsActivity visitReportsActivity, List<VisitReport> visitReports) {
                        Comparator<VisitReport> reverseComparator =
                                Collections.reverseOrder(new VisitReportComparator());
                        Collections.sort(visitReports, reverseComparator);
                        setVisitReports((ArrayList<VisitReport>) visitReports);
                    }
                },
                new SampleFailureAction2(GET_VISIT_REPORTS));
    }

    @Override
    protected void injectPresenter() {
        SampleApplication.getPresenterComponent().inject(this);
    }

    @Override
    public void onTakeView(VisitReportsActivity view) {
        super.onTakeView(view);
        if (visitReports == null) {
            getVisitReports();
        }
        else {
            setVisitReports(visitReports);
        }
    }

    public void getVisitReports() {
        start(GET_VISIT_REPORTS);
    }


    private void setVisitReports(final ArrayList<VisitReport> visitReports) {
        this.visitReports = visitReports;
        view.setListItems(visitReports);
        view.setTitleCount(visitReports.size());
    }

    public static class VisitReportComparator implements Comparator<VisitReport> {
        @Override
        public int compare(VisitReport lhs, VisitReport rhs) {
            return lhs.getDate().toDate().compareTo(rhs.getDate().toDate());
        }
    }

}
