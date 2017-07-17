package com.prokarma.myhome.features.fad.details.booking;

import com.prokarma.myhome.features.fad.Appointment;
import com.prokarma.myhome.features.fad.Office;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.profile.Profile;

import java.util.Date;

/**
 * Created by kwelsh on 7/17/17.
 */

public class BookingManager {
    private static ProviderDetailsResponse bookingProvider;
    private static Office bookingOffice;
    private static Profile bookingProfile;
    private static Appointment bookingAppointment;
    private static Date bookingDate;
    private static boolean isBookingForMe;
    private static boolean isNewPatient;

    public static ProviderDetailsResponse getBookingProvider() {
        return bookingProvider;
    }

    public static void setBookingProvider(ProviderDetailsResponse bookingProvider) {
        BookingManager.bookingProvider = bookingProvider;
    }

    public static Office getBookingOffice() {
        return bookingOffice;
    }

    public static void setBookingOffice(Office bookingOffice) {
        BookingManager.bookingOffice = bookingOffice;
    }

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

    public static void clearBookingData(boolean keepProvider) {
        if (!keepProvider) {
            bookingProvider = null;
            bookingOffice = null;
        }

        bookingProfile = null;
        bookingAppointment = null;
        bookingDate = null;
        isBookingForMe = true;
        isNewPatient = false;
    }
}
