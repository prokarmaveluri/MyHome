package com.prokarma.myhome.features.fad.details.booking;

import android.support.annotation.Nullable;

import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentAvailableTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;
import com.prokarma.myhome.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kwelsh on 11/14/17.
 */

public class AppointmentManager {
    private static AppointmentManager ourInstance;
    private static ArrayList<AppointmentMonthDetails> appointmentDetailsList;

    public static AppointmentManager getInstance() {
        if(ourInstance == null){
            ourInstance = new AppointmentManager();
        }
        return ourInstance;
    }

    public static ArrayList<AppointmentMonthDetails> getAppointmentDetailsList() {
        return appointmentDetailsList;
    }

    public static void setAppointmentDetailsList(ArrayList<AppointmentMonthDetails> appointmentDetailsList) {
        AppointmentManager.appointmentDetailsList = appointmentDetailsList;
    }

    public void initializeAppointmentDetailsList(boolean forceInitialization) {
        if (forceInitialization || appointmentDetailsList == null) {
            appointmentDetailsList = new ArrayList<>();
        }
    }

    public void clearAppointmentDetails() {
        appointmentDetailsList = null;
    }

    public void addMonthsAppointmentDetails(AppointmentMonthDetails monthDetails) {
        if(monthDetails != null && !isDateCached(monthDetails.getFromDate())){
            appointmentDetailsList.add(monthDetails);
        }
    }

    public AppointmentTimeSlots getAppointmentTimeSlots() {
        AppointmentTimeSlots totalAppointmentDetails = new AppointmentTimeSlots();

        if (appointmentDetailsList != null && appointmentDetailsList.get(0) != null) {
            totalAppointmentDetails.setJsonapi(appointmentDetailsList.get(0).getAppointmentTimeSlots().getJsonapi());
            totalAppointmentDetails.setData(appointmentDetailsList.get(0).getAppointmentTimeSlots().getData());

            totalAppointmentDetails.getData().get(0).getAttributes().setAvailableTimes(combinedTimeSlots());
        }

        return totalAppointmentDetails;
    }

    public int getNumberOfMonths() {
        return appointmentDetailsList != null ? appointmentDetailsList.size() : 0;
    }

    public ArrayList<AppointmentType> getAppointmentTypes() {
        return appointmentDetailsList != null ? appointmentDetailsList.get(0).getAppointmentTimeSlots().getData().get(0).getAttributes().getAppointmentTypes() : new ArrayList<AppointmentType>();
    }

    public boolean isDateCached(Date date){
        if(appointmentDetailsList != null && !appointmentDetailsList.isEmpty()){

            for (AppointmentMonthDetails monthDetails : appointmentDetailsList) {
                if(DateUtil.isDateAfterOrEqual(date, monthDetails.getFromDate()) && DateUtil.isDateBeforeOrEqual(date, monthDetails.getToDate())){
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean hasAvailabilityForMonth(Date date){
        AppointmentTimeSlots appointmentTimeSlots = getAppointmentTimeSlotsForMonth(date);

        return appointmentTimeSlots != null && !appointmentTimeSlots.getData().get(0).getAttributes().getAvailableTimes().isEmpty();
    }

    @Nullable
    public AppointmentTimeSlots getAppointmentTimeSlotsForMonth(Date date){
        for (AppointmentMonthDetails monthDetails : appointmentDetailsList) {
            if(DateUtil.isDateAfterOrEqual(date, monthDetails.getFromDate()) && DateUtil.isDateBeforeOrEqual(date, monthDetails.getToDate())){
                return monthDetails.getAppointmentTimeSlots();
            }
        }

        return null;
    }

    private ArrayList<AppointmentAvailableTime> combinedTimeSlots() {
        Set<AppointmentAvailableTime> totalAvailableTimes = new HashSet<>();

        for (AppointmentMonthDetails monthDetails : appointmentDetailsList) {
            if (monthDetails != null && monthDetails.getAppointmentTimeSlots() != null && monthDetails.getAppointmentTimeSlots().getData().get(0).getAttributes() != null && monthDetails.getAppointmentTimeSlots().getData().get(0).getAttributes().getAvailableTimes() != null) {
                totalAvailableTimes.addAll(monthDetails.getAppointmentTimeSlots().getData().get(0).getAttributes().getAvailableTimes());
            }
        }

        return new ArrayList<>(totalAvailableTimes);
    }
}
