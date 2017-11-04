package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAttributes implements Parcelable {

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
    private ArrayList<AppointmentType> appointmentTypes = null;
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

    public ArrayList<AppointmentType> getAppointmentTypes() {
        return appointmentTypes;
    }

    public void setAppointmentTypes(ArrayList<AppointmentType> appointmentTypes) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.registrationUrl);
        dest.writeParcelable(this.provider, flags);
        dest.writeParcelable(this.service, flags);
        dest.writeParcelable(this.facility, flags);
        dest.writeParcelable(this.location, flags);
        dest.writeList(this.availableTimes);
        dest.writeTypedList(this.appointmentTypes);
        dest.writeValue(this.hasAvailability);
        dest.writeTypedList(this.nextAvailableTimes);
    }

    public AppointmentAttributes() {
    }

    protected AppointmentAttributes(Parcel in) {
        this.registrationUrl = in.readString();
        this.provider = in.readParcelable(AppointmentProvider.class.getClassLoader());
        this.service = in.readParcelable(AppointmentService.class.getClassLoader());
        this.facility = in.readParcelable(AppointmentFacility.class.getClassLoader());
        this.location = in.readParcelable(AppointmentLocation.class.getClassLoader());
        this.availableTimes = new ArrayList<AppointmentAvailableTime>();
        in.readList(this.availableTimes, AppointmentAvailableTime.class.getClassLoader());
        this.appointmentTypes = in.createTypedArrayList(AppointmentType.CREATOR);
        this.hasAvailability = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.nextAvailableTimes = in.createTypedArrayList(AppointmentNextAvailableTime.CREATOR);
    }

    public static final Parcelable.Creator<AppointmentAttributes> CREATOR = new Parcelable.Creator<AppointmentAttributes>() {
        @Override
        public AppointmentAttributes createFromParcel(Parcel source) {
            return new AppointmentAttributes(source);
        }

        @Override
        public AppointmentAttributes[] newArray(int size) {
            return new AppointmentAttributes[size];
        }
    };
}
