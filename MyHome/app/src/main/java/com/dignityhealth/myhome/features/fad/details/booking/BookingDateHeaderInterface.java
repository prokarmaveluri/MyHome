package com.dignityhealth.myhome.features.fad.details.booking;

/**
 * Created by kwelsh on 5/25/17.
 */

public interface BookingDateHeaderInterface {
    void onTimeSelected(BookingTimeSlot bookingTimeSlot);
    void onBackArrowClicked();
    void onFrontArrowClicked();
    void onMonthHeaderClicked();
}
