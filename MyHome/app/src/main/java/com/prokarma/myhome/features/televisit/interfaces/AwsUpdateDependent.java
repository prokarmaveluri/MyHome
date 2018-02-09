package com.prokarma.myhome.features.televisit.interfaces;

import com.americanwell.sdk.entity.consumer.Consumer;

/**
 * Created by kwelsh on 12/1/17.
 */

public interface AwsUpdateDependent {
    void updateDependentComplete(Consumer consumer);
    void updateDependentFailed(String errorMessage);
}
