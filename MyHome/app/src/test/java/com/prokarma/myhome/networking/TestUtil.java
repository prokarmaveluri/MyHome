package com.prokarma.myhome.networking;

import android.content.Context;
import android.content.SharedPreferences;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Assert;
import org.mockito.Mockito;

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
        setSharedPreferences();
    }

    public static void setStagingEnvironment() {
        EnviHandler.initEnv(EnviHandler.EnvType.STAGE);
        NetworkManager.getInstance().initService();
        setSharedPreferences();
    }

    public static void setProdEnvironment() {
        EnviHandler.initEnv(EnviHandler.EnvType.PROD);
        NetworkManager.getInstance().initService();
        setSharedPreferences();
    }

    private static void setSharedPreferences(){
        final Context context = Mockito.mock(Context.class);
        final SharedPreferences sharedPrefs = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(AppPreferences.APP_PREFERENCES, Context.MODE_PRIVATE)).thenReturn(sharedPrefs);
        AuthManager.getInstance().setContext(context);
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
