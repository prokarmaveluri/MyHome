package com.dignityhealth.myhome.features.fad.details.booking;

import com.dignityhealth.myhome.features.fad.Appointment;

/**
 * Created by kwelsh on 5/25/17.
 */

public interface BookingDateHeaderInterface {
    void onTimeSelected(Appointment appointment);
    void onBackArrowClicked();
    void onFrontArrowClicked();
    void onMonthHeaderClicked();
}
