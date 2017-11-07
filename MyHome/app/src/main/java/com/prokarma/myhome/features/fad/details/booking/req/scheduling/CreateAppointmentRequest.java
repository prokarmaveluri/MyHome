package com.prokarma.myhome.features.fad.details.booking.req.scheduling;

import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTime;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentType;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.networking.Jsonapi;

/**
 * Created by cmajji on 6/8/17.
 */

public class CreateAppointmentRequest {
    private Jsonapi jsonapi;
    private Data data;

    public CreateAppointmentRequest() {

    }

    public CreateAppointmentRequest(Jsonapi jsonapi, Data data) {
        this.jsonapi = jsonapi;
        this.data = data;
    }

    public CreateAppointmentRequest(String doctorName, String providerNpi, String officeName, String officePhone, Profile bookingProfile, AppointmentTime bookingAppointment, AppointmentType appointmentType, boolean isBookingForMe){
        setJsonapi(new Jsonapi("1.0"));

        //TODO Kevin, uncomment this out once you figure out the ScheduleId for the new Appointment Time API
//        AppointmentDetails appointmentDetails = new AppointmentDetails(bookingAppointment.ScheduleId, "schedules");
//        Schedule schedule = new Schedule(appointmentDetails);
//        Relationships relationships = new Relationships(schedule);
//
//        setData(new Data("visits", new Attributes(doctorName, providerNpi, officeName, officePhone, bookingProfile, bookingAppointment, appointmentType, isBookingForMe), relationships));
    }

    public Jsonapi getJsonapi() {
        return jsonapi;
    }

    public void setJsonapi(Jsonapi jsonapi) {
        this.jsonapi = jsonapi;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}