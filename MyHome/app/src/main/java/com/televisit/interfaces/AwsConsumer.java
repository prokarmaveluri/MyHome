package com.televisit.interfaces;

import com.americanwell.sdk.entity.consumer.Consumer;

/**
 * Created by kwelsh on 11/30/17.
 */

public interface AwsConsumer {
    void consumerComplete(Consumer consumer);

    void consumerFailed(String errorMessage);
}
