package com.prokarma.myhome.features.fad.details.booking;

import com.prokarma.myhome.features.fad.Appointment;
import com.prokarma.myhome.features.profile.Profile;

import java.util.Date;

/**
 * Created by kwelsh on 7/17/17.
 */

public class BookingManager {
    private static Profile bookingProfile;
    private static Appointment bookingAppointment;
    private static Date bookingDate;
    private static boolean isBookingForMe;
    private static boolean isNewPatient;

    public static Profile getBookingProfile() {
        return bookingProfile;
    }

    public static void setBookingProfile(Profile bookingProfile) {
        BookingManager.bookingProfile = bookingProfile;
    }

    public static Appointment getBookingAppointment() {
        return bookingAppointment;
    }

    public static void setBookingAppointment(Appointment bookingAppointment) {
        BookingManager.bookingAppointment = bookingAppointment;
    }

    public static Date getBookingDate() {
        return bookingDate;
    }

    public static void setBookingDate(Date bookingDate) {
        BookingManager.bookingDate = bookingDate;
    }

    public static boolean isBookingForMe() {
        return isBookingForMe;
    }

    public static void setIsBookingForMe(boolean isBookingForMe) {
        BookingManager.isBookingForMe = isBookingForMe;
    }

    public static boolean isNewPatient() {
        return isNewPatient;
    }

    public static void setIsNewPatient(boolean isNewPatient) {
        BookingManager.isNewPatient = isNewPatient;
    }

    public static void clearBookingData() {
        bookingProfile = null;
        bookingAppointment = null;
        bookingDate = null;
        isBookingForMe = true;
        isNewPatient = false;
    }
}
