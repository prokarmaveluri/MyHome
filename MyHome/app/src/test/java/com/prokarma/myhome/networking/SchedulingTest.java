package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.fad.Appointment;
import com.prokarma.myhome.features.fad.AppointmentType;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.profile.Address;
import com.prokarma.myhome.features.profile.InsuranceProvider;
import com.prokarma.myhome.features.profile.Profile;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by kwelsh on 10/3/17.
 */

public class SchedulingTest {

    @Before
    public void setup() {

    }

    @Test
    public void createAppointment_Dev(){
        TestUtil.setDevEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.DEV_USER, TestConstants.DEV_PASSWORD);
        createAppointment(TestUtil.getLogin(loginRequest));
    }

    @Test
    public void createAppointment_Stage(){
        TestUtil.setStagingEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.STAGE_USER, TestConstants.STAGE_PASSWORD);
        createAppointment(TestUtil.getLogin(loginRequest));
    }

    //TODO Uncomment the test annotation to test creating an appointment in prod. BE CAREFUL!
    //@Test
    public void createAppointment_Prod(){
        TestUtil.setProdEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.PROD_USER, TestConstants.PROD_PASSWORD);
        createAppointment(TestUtil.getLogin(loginRequest));
    }

    public void createAppointment(String bearerToken){
        Address bookingAddress = new Address("540 Trinity Lane N", "Apt 5301", "St.Petersburg", "FL", "33716", "US");
        InsuranceProvider insuranceProvider = new InsuranceProvider("", "AARP Medicare Complete", "12321", "131312", "616-826-1635", "aarp-medicare-complete");
        Profile bookingProfile = new Profile("Kevin", "C", "Welsh", "Nickname", "Male", "1988-12-12T00:00:00Z", bookingAddress, "1212121212", null, null, null, "", false, null, insuranceProvider, null, null, "draman@prokarma.com", "Sick", false, "", false);

        ArrayList<AppointmentType> appointmentTypes = new ArrayList<>();
        AppointmentType appointmentType = new AppointmentType("established-patient-1", "Established Patient Visit", "established-patient");
        appointmentTypes.add(appointmentType);
        Appointment bookingAppointment = new Appointment("2017-10-04T14:00:00-07:00", appointmentTypes, false, "test-location-1", "1755 Court St", "Redding", "CA", "96001", "40.5805", "-122.394", "https://gecb-test.inquickerstaging.com/schedule/2185?at=201710041400&schedule_id=2185", "f868a670-108c-4357-8e82-d57a5bd99989", "1755 Court Street Redding CA");

        Call<CreateAppointmentResponse> call = NetworkManager.getInstance().createAppointment(bearerToken,
                new CreateAppointmentRequest(
                        "doctorName",
                        "providerNpi",
                        "officeName",
                        "officePhone",
                        bookingProfile,
                        bookingAppointment,
                        false,
                        true));


    }
}
