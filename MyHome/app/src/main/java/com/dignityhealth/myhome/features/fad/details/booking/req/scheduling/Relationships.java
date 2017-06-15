package com.dignityhealth.myhome.features.fad.details.booking.req.scheduling;

/**
 * Created by kwelsh on 6/14/17.
 */

public class Relationships {
    private Schedule schedule;

    public Relationships(Schedule schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}

