package com.prokarma.myhome.features.fad.details.booking;

import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;

import java.util.Date;

/**
 * Created by kwelsh on 11/14/17.
 */

public class AppointmentMonthDetails {
    private Date fromDate;
    private Date toDate;
    private AppointmentTimeSlots appointmentTimeSlots;

    public AppointmentMonthDetails(Date fromDate, Date toDate, AppointmentTimeSlots appointmentTimeSlots) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.appointmentTimeSlots = appointmentTimeSlots;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public AppointmentTimeSlots getAppointmentTimeSlots() {
        return appointmentTimeSlots;
    }

    public void setAppointmentTimeSlots(AppointmentTimeSlots appointmentTimeSlots) {
        this.appointmentTimeSlots = appointmentTimeSlots;
    }
}
