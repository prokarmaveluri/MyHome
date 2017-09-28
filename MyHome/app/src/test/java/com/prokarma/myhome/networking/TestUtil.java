package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;

import org.junit.Assert;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by kwelsh on 9/28/17.
 */

public class TestUtil {

    public static String getLogin(SignInRequest loginRequest){
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

    public static Profile getProfile(String bearerToken) {
        Assert.assertTrue(bearerToken != null && !bearerToken.isEmpty());
        Call<ProfileGraphqlResponse> call = NetworkManager.getInstance().getProfile(bearerToken);
        try {
            Response<ProfileGraphqlResponse> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Profile profile = response.body().getData().getUser();
            Assert.assertNotNull(profile);
            return profile;
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
            return null;
        }
    }
}
