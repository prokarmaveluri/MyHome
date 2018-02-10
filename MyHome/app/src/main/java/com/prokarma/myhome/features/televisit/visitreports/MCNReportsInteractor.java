package com.prokarma.myhome.features.televisit.visitreports;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.visitreports.ui.MCNReportsComparator;

import java.util.Collections;
import java.util.List;

/**
 * Created by veluri on 2/8/18.
 */

public class MCNReportsInteractor implements MCNReportsContract.Interactor {
    final MCNReportsContract.InteractorOutput output;

    public MCNReportsInteractor(MCNReportsContract.InteractorOutput output) {
        this.output = output;
    }

    @Override
    public void getVisitReports() {

        boolean scheduledOnly = false;
        AwsManager.getInstance().getAWSDK().getConsumerManager().getVisitReports(
                AwsManager.getInstance().getPatient(),
                null,
                scheduledOnly,
                new SDKCallback<List<VisitReport>, SDKError>() {
                    @Override
                    public void onResponse(List<VisitReport> visitReports, SDKError sdkError) {
                        if (sdkError == null) {
                            Collections.sort(visitReports, new MCNReportsComparator());
                            AwsManager.getInstance().setVisitReports(visitReports);

                            output.receivedVisitReports(visitReports, null);
                        } else {
                            output.receivedVisitReports(null, sdkError.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        output.receivedVisitReports(null, throwable.getMessage());
                    }
                }
        );
    }
}