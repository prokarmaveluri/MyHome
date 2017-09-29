package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Assert;

import java.io.IOException;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by kwelsh on 9/28/17.
 */

public class TestUtil {

    public static void setDevEnvironment() {
        EnviHandler.initEnv(EnviHandler.EnvType.DEV);
        NetworkManager.getInstance().initService();
    }

    public static void setStagingEnvironment() {
        EnviHandler.initEnv(EnviHandler.EnvType.STAGE);
        NetworkManager.getInstance().initService();
    }

    public static void setProdEnvironment() {
        EnviHandler.initEnv(EnviHandler.EnvType.PROD);
        NetworkManager.getInstance().initService();
    }

    public static String getLogin(SignInRequest loginRequest) {
        Assert.assertNotNull(loginRequest);
        Call<SignInResponse> call = NetworkManager.getInstance().signIn(loginRequest);
        try {
            Response<SignInResponse> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            SignInResponse loginResponse = response.body();
            Assert.assertNotNull(loginResponse);
            Assert.assertNotNull(loginResponse.result);
            Assert.assertNotNull(loginResponse.result.getAccessToken());
            return loginResponse.result.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
            return null;
        }
    }

    /**
     * Returns a random integer intended to be dummy data for a zipcode
     *
     * @return random zipcode value
     */
    public static int getRandomZipCode() {
        Random r = new Random();
        int low = 00501;
        int high = 99999;
        return r.nextInt(high - low) + low;
    }
}
