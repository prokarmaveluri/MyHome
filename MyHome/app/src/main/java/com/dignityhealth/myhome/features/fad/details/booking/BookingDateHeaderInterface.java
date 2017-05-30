package com.dignityhealth.myhome.features.fad.details.booking;

import com.dignityhealth.myhome.features.fad.Appointment;

import java.util.Date;

/**
 * Created by kwelsh on 5/25/17.
 */

public interface BookingDateHeaderInterface {
    void onTimeSelected(Appointment appointment);
    void onBackArrowClicked();
    void onFrontArrowClicked();
    void onMonthHeaderClicked();
    void onDateChanged(Date date);
}
