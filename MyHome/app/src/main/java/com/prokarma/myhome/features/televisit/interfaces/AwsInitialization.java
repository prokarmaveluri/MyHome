package com.prokarma.myhome.features.televisit.interfaces;

/**
 * Created by kwelsh on 11/30/17.
 */

public interface AwsInitialization {
    void initializationComplete();

    void initializationFailed(String errorMessage);
}
