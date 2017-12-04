package com.televisit.interfaces;

/**
 * Created by kwelsh on 11/30/17.
 */

public interface AwsSendVisitFeedback {
    void sendVisitFeedbackComplete();

    void sendVisitFeedbackFailed(String errorMessage);
}
