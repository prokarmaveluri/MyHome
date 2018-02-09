package com.prokarma.myhome.features.televisit.interfaces;

import com.americanwell.sdk.entity.visit.VisitSummary;


/**
 * Created by kwelsh on 12/1/17.
 */

public interface AwsGetVisitSummary {
    void getVisitSummaryComplete(VisitSummary visitSummary);

    void getVisitSummaryFailed(String errorMessage);
}
