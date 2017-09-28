package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by kwelsh on 5/4/17.
 * Tests for DignityHealth APIs
 */

public class LoginTest {
    private static final String STATIC_BEARER_TOKEN = "insert_bearer_token_here...";

    @Before
    public void setup() {

    }

    @Test
    public void getLogin_Dev() {
        SignInRequest loginRequest = new SignInRequest("brett@mailinator.com", "Ap29bx1442@");
        EnviHandler.initEnv(EnviHandler.EnvType.DEV);
        NetworkManager.getInstance().initService();
        getLogin(loginRequest);
    }

//    @Test
//    public void getLogin_Test() {
//        SignInRequest loginRequest = new SignInRequest("sam@mailinator.com", "Ap29bx1442@");
//        EnviHandler.initEnv(EnviHandler.EnvType.TEST);
//        NetworkManager.getInstance().initService();
//        getLogin(loginRequest);
//    }

    @Test
    public void getLogin_Stage() {
        SignInRequest loginRequest = new SignInRequest("suji@mailinator.com", "Ap29bx1442@");
        EnviHandler.initEnv(EnviHandler.EnvType.STAGE);
        NetworkManager.getInstance().initService();
        getLogin(loginRequest);
    }

    @Test
    public void getLogin_Prod() {
        SignInRequest loginRequest = new SignInRequest("kutala@mailinator.com", "Ap29bx1442@");
        EnviHandler.initEnv(EnviHandler.EnvType.PROD);
        NetworkManager.getInstance().initService();
        getLogin(loginRequest);
    }

    public void getLogin(SignInRequest loginRequest){
        Call<SignInResponse> call = NetworkManager.getInstance().signIn(loginRequest);
        try {
            Response<SignInResponse> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            SignInResponse loginResponse = response.body();
            Assert.assertNotNull(loginResponse);
            Assert.assertNotNull(loginResponse.result);
            Assert.assertNotNull(loginResponse.result.getAccessToken());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    @Test
    public void getProfile_Success() {
        Assert.assertTrue(AuthManager.getInstance().getBearerToken() != null
                && !AuthManager.getInstance().getBearerToken().isEmpty()); //Check if bearer token in singleton is assigned or not
        Call<ProfileGraphqlResponse> call = NetworkManager.getInstance().getProfile(STATIC_BEARER_TOKEN);
        try {
            Response<ProfileGraphqlResponse> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Profile profile = response.body().getData().getUser();
            Assert.assertNotNull(profile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
