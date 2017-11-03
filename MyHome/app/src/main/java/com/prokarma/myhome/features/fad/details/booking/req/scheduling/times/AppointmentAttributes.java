package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentAttributes {

    @SerializedName("registration-url")
    @Expose
    private String registrationUrl;
    @SerializedName("provider")
    @Expose
    private AppointmentProvider provider;
    @SerializedName("service")
    @Expose
    private AppointmentService service;
    @SerializedName("facility")
    @Expose
    private AppointmentFacility facility;
    @SerializedName("location")
    @Expose
    private AppointmentLocation location;
    @SerializedName("available-times")
    @Expose
    private List<AppointmentAvailableTime> availableTimes = null;
    @SerializedName("appointment-types")
    @Expose
    private List<AppointmentType> appointmentTypes = null;
    @SerializedName("has-availability")
    @Expose
    private Boolean hasAvailability;
    @SerializedName("next-available-times")
    @Expose
    private List<AppointmentNextAvailableTime> nextAvailableTimes = null;

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }

    public AppointmentProvider getProvider() {
        return provider;
    }

    public void setProvider(AppointmentProvider provider) {
        this.provider = provider;
    }

    public AppointmentService getService() {
        return service;
    }

    public void setService(AppointmentService service) {
        this.service = service;
    }

    public AppointmentFacility getFacility() {
        return facility;
    }

    public void setFacility(AppointmentFacility facility) {
        this.facility = facility;
    }

    public AppointmentLocation getLocation() {
        return location;
    }

    public void setLocation(AppointmentLocation location) {
        this.location = location;
    }

    public List<AppointmentAvailableTime> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<AppointmentAvailableTime> availableTimes) {
        this.availableTimes = availableTimes;
    }

    public List<AppointmentType> getAppointmentTypes() {
        return appointmentTypes;
    }

    public void setAppointmentTypes(List<AppointmentType> appointmentTypes) {
        this.appointmentTypes = appointmentTypes;
    }

    public Boolean getHasAvailability() {
        return hasAvailability;
    }

    public void setHasAvailability(Boolean hasAvailability) {
        this.hasAvailability = hasAvailability;
    }

    public List<AppointmentNextAvailableTime> getNextAvailableTimes() {
        return nextAvailableTimes;
    }

    public void setNextAvailableTimes(List<AppointmentNextAvailableTime> nextAvailableTimes) {
        this.nextAvailableTimes = nextAvailableTimes;
    }
}
