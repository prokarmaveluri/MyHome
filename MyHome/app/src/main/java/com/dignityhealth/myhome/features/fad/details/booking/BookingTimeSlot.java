package com.dignityhealth.myhome.features.fad.details.booking;

/**
 * Created by kwelsh on 5/24/17.
 * Simple Model to represent a time and if it was picked or not for booking an appointment
 */

public class BookingTimeSlot {
    public String time;
    public boolean isPicked;

    public BookingTimeSlot(String time, boolean isPicked) {
        this.time = time;
        this.isPicked = isPicked;
    }
}
