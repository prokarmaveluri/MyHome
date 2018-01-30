package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by kwelsh on 9/28/17.
 * Tests for DignityHealth APIs
 */

@RunWith(Parameterized.class)
public class ProfileTest {
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
    public void getProfile() {
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

        getProfile(TestUtil.getLogin(loginRequest));
    }

    @Test
    public void updateProfile() {
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

        String bearerToken = TestUtil.getLogin(loginRequest);

        Profile profile = getProfile(bearerToken);
        updateProfile(bearerToken, profile);
    }

    public Profile getProfile(String bearerToken) {
        Assert.assertTrue(bearerToken != null && !bearerToken.isEmpty());
        Call<ProfileGraphqlResponse> call = NetworkManager.getInstance().getProfile(bearerToken);

        try {
            Response<ProfileGraphqlResponse> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertNotNull(response.body().getData());
            Assert.assertNotNull(response.body().getData().getUser());

            Profile profile = response.body().getData().getUser();
            Assert.assertNotNull(profile);
            return profile;
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
            return null;
        }
    }

    /**
     * Update the zipcode of the profile and check if the update succeeds.
     *
     * @param bearerToken
     * @param profile
     */
    public void updateProfile(String bearerToken, Profile profile) {
        Assert.assertTrue(bearerToken != null && !bearerToken.isEmpty());
        String randomZipCode = String.valueOf(TestUtil.getRandomZipCode()).trim();
        profile.setZipCode(randomZipCode);

        Call<Void> call = NetworkManager.getInstance().updateProfile(bearerToken, profile);

        try {
            Response<Void> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());

            Profile updatedProfile = getProfile(bearerToken);
            Assert.assertEquals(updatedProfile.address.zipCode, randomZipCode);
        } catch (IOException e) {
            Assert.fail(e.toString());
        }
    }

}
