package com.televisit.interfaces;

/**
 * Created by kwelsh on 11/30/17.
 */

public interface AwsSendVisitRating {
    void sendVisitRatingComplete();

    void sendVisitRatingFailed(String errorMessage);
}
