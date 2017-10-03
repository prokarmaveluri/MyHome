package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.MyAppointmentsRequest;
import com.prokarma.myhome.features.appointments.MyAppointmentsResponse;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.utils.CommonUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 10/2/17.
 */

public class AppointmentsTest {

    @Before
    public void setup() {

    }

    @Test
    public void getAppointments_Dev(){
        TestUtil.setDevEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.DEV_USER, TestConstants.DEV_PASSWORD);
        getAppointments(TestUtil.getLogin(loginRequest));
    }

    @Test
    public void getAppointments_Stage(){
        TestUtil.setStagingEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.STAGE_USER, TestConstants.STAGE_PASSWORD);
        getAppointments(TestUtil.getLogin(loginRequest));
    }

    @Test
    public void getAppointments_Prod(){
        TestUtil.setProdEnvironment();
        SignInRequest loginRequest = new SignInRequest(TestConstants.PROD_USER, TestConstants.PROD_PASSWORD);
        getAppointments(TestUtil.getLogin(loginRequest));
    }

    public List<Appointment> getAppointments(String bearerToken){
        Call<MyAppointmentsResponse> call = NetworkManager.getInstance().getMyAppointments(bearerToken, new MyAppointmentsRequest());

        try {
            Response<MyAppointmentsResponse> response = call.execute();

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertNotNull(response.body().getData());
            Assert.assertNotNull(response.body().getData().getUser());
            Assert.assertNotNull(response.body().getData().getUser().getAppointments());
            Assert.assertFalse(response.body().getData().getUser().getAppointments().isEmpty());

            Timber.d("Has Past Appointments: " + !CommonUtil.getPastAppointments(response.body().getData().getUser().getAppointments()).isEmpty());
            Timber.d("Has Future Appointments:" + !CommonUtil.getFutureAppointments(response.body().getData().getUser().getAppointments()).isEmpty());

            return response.body().getData().getUser().getAppointments();
        } catch (IOException e) {
            Assert.fail(e.toString());
            return null;
        }
    }


//    public void createAppointment(String bearerToken){
//        Call<CreateAppointmentResponse> call = NetworkManager.getInstance().createAppointment(bearerToken,
//                new CreateAppointmentRequest(
//                        "doctorName",
//                        "providerNpi",
//                        "officeName",
//                        "officePhone",
//                        bookingProfile,
//                        bookingAppointment,
//                        false,
//                        true));
//    }

}
