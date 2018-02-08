package com.televisit.interfaces;

import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.visit.VisitReport;

import java.util.List;

/**
 * Created by veluri on 2/8/18.
 */

public interface AwsGetVisitReports {
    void getVisitReportsComplete(List<VisitReport> reports);

    void getVisitReportsFailed(String errorMessage);
}
