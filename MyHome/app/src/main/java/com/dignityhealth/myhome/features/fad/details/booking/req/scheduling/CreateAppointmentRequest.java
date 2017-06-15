package com.dignityhealth.myhome.features.fad.details.booking.req.scheduling;

import com.dignityhealth.myhome.features.fad.Appointment;
import com.dignityhealth.myhome.features.fad.Office;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;
import com.dignityhealth.myhome.features.profile.Profile;

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

    public CreateAppointmentRequest(ProviderDetailsResponse providerDetailsResponse, Office currentOffice, Profile bookingProfile, Appointment bookingAppointment, boolean isNewPatient, boolean isBookingForMe){
        setJsonapi(new Jsonapi("1.0"));

        AppointmentDetails appointmentDetails = new AppointmentDetails(bookingAppointment.ScheduleId, "schedules");
        Schedule schedule = new Schedule(appointmentDetails);
        Relationships relationships = new Relationships(schedule);

        setData(new Data("visits", new Attributes(providerDetailsResponse, currentOffice, bookingProfile, bookingAppointment, isNewPatient, isBookingForMe), relationships));
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