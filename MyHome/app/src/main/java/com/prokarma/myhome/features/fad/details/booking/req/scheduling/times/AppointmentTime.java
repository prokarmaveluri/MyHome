package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentTime {

    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("appointmentTypes")
    @Expose
    private List<AppointmentType> appointmentTypes = null;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<AppointmentType> getAppointmentTypes() {
        return appointmentTypes;
    }

    public void setAppointmentTypes(List<AppointmentType> appointmentTypes) {
        this.appointmentTypes = appointmentTypes;
    }
}
