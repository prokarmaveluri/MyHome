package com.prokarma.myhome.features.fad.details.booking;

import com.prokarma.myhome.features.fad.details.ProviderDetailsOffice;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResult;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;
import com.prokarma.myhome.features.profile.Profile;

import java.util.Date;

/**
 * Created by kwelsh on 7/17/17.
 */

public class BookingManager {
    private static ProviderDetailsResult bookingProvider;
    private static ProviderDetailsOffice bookingOffice;
    private static AppointmentTimeSlots bookingOfficeAppointmentDetails;
    private static AppointmentType bookingAppointmentType;
    private static Profile bookingProfile;
    private static AppointmentTime bookingAppointment;
    private static Date bookingDate;
    private static boolean isBookingForMe;
    private static String scheduleId;

    public static ProviderDetailsResult getBookingProvider() {
        return bookingProvider;
    }

    public static void setBookingProvider(ProviderDetailsResult bookingProvider) {
        BookingManager.bookingProvider = bookingProvider;
    }

    public static ProviderDetailsOffice getBookingOffice() {
        return bookingOffice;
    }

    public static void setBookingOffice(ProviderDetailsOffice bookingOffice) {
        BookingManager.bookingOffice = bookingOffice;
    }

    public static Profile getBookingProfile() {
        return bookingProfile;
    }

    public static void setBookingProfile(Profile bookingProfile) {
        BookingManager.bookingProfile = bookingProfile;
    }

    public static AppointmentTime getBookingAppointment() {
        return bookingAppointment;
    }

    public static void setBookingAppointment(AppointmentTime bookingAppointment) {
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

    public static AppointmentTimeSlots getBookingOfficeAppointmentDetails() {
        return bookingOfficeAppointmentDetails;
    }

    public static void setBookingOfficeAppointmentDetails(AppointmentTimeSlots bookingOfficeAppointmentDetails) {
        BookingManager.bookingOfficeAppointmentDetails = bookingOfficeAppointmentDetails;
    }

    public static AppointmentType getBookingAppointmentType() {
        return bookingAppointmentType;
    }

    public static void setBookingAppointmentType(AppointmentType bookingAppointmentType) {
        BookingManager.bookingAppointmentType = bookingAppointmentType;
    }

    public static String getScheduleId() {
        return scheduleId;
    }

    public static void setScheduleId(String scheduleId) {
        BookingManager.scheduleId = scheduleId;
    }

    public static void clearBookingData(boolean keepProvider) {
        if (!keepProvider) {
            bookingProvider = null;
            scheduleId = null;
            bookingOffice = null;
            bookingOfficeAppointmentDetails = null;
        }

        bookingProfile = null;
        bookingAppointment = null;
        bookingDate = null;
        isBookingForMe = true;
        bookingAppointmentType = null;
    }
}
