package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.networking.auth.AuthManager;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by kwelsh on 5/4/17.
 * Tests for DignityHealth APIs
 */

public class DignityHealthAPITest {
    private static final String STATIC_BEARER_TOKEN = "Bearer " + "insert_bearer_token_here...";
    private static final String CURRENT_BEARER_TOKEN = "Bearer " + AuthManager.getBearerToken();

    @Test
    public void getProfile_Success() {
        NetworkManager.getInstance().initService();
        Assert.assertTrue(AuthManager.getBearerToken() != null && !AuthManager.getBearerToken().isEmpty()); //Check if bearer token in singleton is assigned or not
        Call<Profile> call = NetworkManager.getInstance().getProfile(CURRENT_BEARER_TOKEN);
        try {
            Response<Profile> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Profile profile = response.body();
            Assert.assertNotNull(profile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
