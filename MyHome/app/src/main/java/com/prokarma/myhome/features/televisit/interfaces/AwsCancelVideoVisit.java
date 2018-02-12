package com.prokarma.myhome.features.televisit.interfaces;

/**
 * Created by kwelsh on 12/1/17.
 */

public interface AwsCancelVideoVisit {
    void cancelVideoVisitComplete();
    void cancelVideoVisitFailed(String errorMessage);
}
