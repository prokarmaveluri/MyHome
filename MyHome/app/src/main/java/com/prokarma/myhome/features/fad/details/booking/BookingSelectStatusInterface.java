package com.prokarma.myhome.features.fad.details.booking;

import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;

/**
 * Created by kwelsh on 5/25/17.
 */

public interface BookingSelectStatusInterface {
    void onTypeSelected(AppointmentType appointmentType);
}
