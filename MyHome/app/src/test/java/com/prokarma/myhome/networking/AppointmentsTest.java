package com.prokarma.myhome.networking;

import com.prokarma.myhome.entities.Appointment;
import com.prokarma.myhome.entities.MyAppointmentsRequest;
import com.prokarma.myhome.entities.MyAppointmentsResponse;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 10/2/17.
 */

@RunWith(Parameterized.class)
public class AppointmentsTest {

    @Parameterized.Parameter
    public EnviHandler.EnvType environment;

    @Parameterized.Parameters
    public static Object[] environments() {
        return new Object[]{
                EnviHandler.EnvType.DEV,
                EnviHandler.EnvType.STAGE,
                EnviHandler.EnvType.PROD,
        };
    }

    @Rule
    public Timeout glocalTimeout = Timeout.seconds(20);

    @Test
    public void getAppointments(){
        SignInRequest loginRequest = null;

        switch (environment) {
            case DEV:
                TestUtil.setDevEnvironment();
                loginRequest = new SignInRequest(TestConstants.DEV_USER, TestConstants.DEV_PASSWORD);
                break;
            case STAGE:
                TestUtil.setStagingEnvironment();
                loginRequest = new SignInRequest(TestConstants.STAGE_USER, TestConstants.STAGE_PASSWORD);
                break;
            case PROD:
                TestUtil.setProdEnvironment();
                loginRequest = new SignInRequest(TestConstants.PROD_USER, TestConstants.PROD_PASSWORD);
                break;
        }

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
            //Assert.assertFalse(response.body().getData().getUser().getAppointments().isEmpty());

            Timber.d("Has Past Appointments: " + !CommonUtil.getPastAppointments(response.body().getData().getUser().getAppointments()).isEmpty());
            Timber.d("Has Future Appointments:" + !CommonUtil.getFutureAppointments(response.body().getData().getUser().getAppointments()).isEmpty());

            return response.body().getData().getUser().getAppointments();
        } catch (IOException e) {
            Assert.fail(e.toString());
            return null;
        }
    }
}
