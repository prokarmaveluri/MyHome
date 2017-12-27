package com.prokarma.myhome.features.fad.details.booking;

import com.prokarma.myhome.features.fad.details.ProviderDetailsAddress;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResult;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;
import com.prokarma.myhome.features.profile.Profile;

import java.util.Date;

/**
 * Created by kwelsh on 7/17/17.
 */

public class BookingManager {
    private static ProviderDetailsResult bookingProvider;
    private static ProviderDetailsAddress bookingLocation;
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

    public static ProviderDetailsAddress getBookingLocation() {
        return bookingLocation;
    }

    public static void setBookingLocation(ProviderDetailsAddress bookingLocation) {
        BookingManager.bookingLocation = bookingLocation;
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
            bookingLocation = null;
        }

        bookingProfile = null;
        bookingAppointment = null;
        bookingDate = null;
        isBookingForMe = true;
        bookingAppointmentType = null;
    }
}
