package com.prokarma.myhome.features.fad.details.booking;

/**
 * Created by kwelsh on 5/28/17.
 */

public interface BookingDoneInterface {
    void onBookingSuccess();
    void onBookingFailed(String errorMessage);
}
