package com.prokarma.myhome.features.fad.details.booking.req.scheduling;

/**
 * Created by kwelsh on 6/14/17.
 */

public class Schedule {
    private AppointmentDetails data;

    public Schedule(AppointmentDetails data) {
        this.data = data;
    }

    public AppointmentDetails getData() {
        return data;
    }

    public void setData(AppointmentDetails data) {
        this.data = data;
    }
}
