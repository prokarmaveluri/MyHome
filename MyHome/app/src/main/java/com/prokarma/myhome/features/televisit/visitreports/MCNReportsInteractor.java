package com.prokarma.myhome.features.televisit.visitreports;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.features.televisit.AwsNetworkManager;
import com.prokarma.myhome.features.televisit.interfaces.AwsGetVisitReports;

import java.util.List;

/**
 * Created by veluri on 2/8/18.
 */

public class MCNReportsInteractor implements MCNReportsContract.Interactor, AwsGetVisitReports {
    final MCNReportsContract.InteractorOutput output;

    public MCNReportsInteractor(MCNReportsContract.InteractorOutput output) {
        this.output = output;
    }

    @Override
    public void getVisitReports() {
        AwsNetworkManager.getInstance().getVisitReports(this);
    }

    @Override
    public void getVisitReportsComplete(List<VisitReport> reports) {
        output.receivedVisitReports(reports, null);
    }

    @Override
    public void getVisitReportsFailed(String errorMessage) {
        output.receivedVisitReports(null, errorMessage);
    }
}