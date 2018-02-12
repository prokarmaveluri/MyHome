package com.prokarma.myhome.features.televisit.interfaces;

import com.americanwell.sdk.entity.consumer.Consumer;

/**
 * Created by kwelsh on 11/30/17.
 */

public interface AwsConsumer {
    void getConsumerComplete(Consumer consumer);

    void getConsumerFailed(String errorMessage);
}
