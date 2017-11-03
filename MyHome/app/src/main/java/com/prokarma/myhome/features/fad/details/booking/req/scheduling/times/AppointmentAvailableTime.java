package com.prokarma.myhome.features.fad.details.booking.req.scheduling.times;

/**
 * Created by kwelsh on 11/3/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentAvailableTime {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("times")
    @Expose
    private List<AppointmentTime> times = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<AppointmentTime> getTimes() {
        return times;
    }

    public void setTimes(List<AppointmentTime> times) {
        this.times = times;
    }
}
