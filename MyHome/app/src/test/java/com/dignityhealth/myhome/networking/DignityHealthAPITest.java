package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.login.LoginRequest;
import com.dignityhealth.myhome.features.login.LoginResponse;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionRequest;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionResponse;
import com.dignityhealth.myhome.networking.auth.AuthManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/4/17.
 * Tests for DignityHealth APIs
 */

public class DignityHealthAPITest {
    private static final String STATIC_BEARER_TOKEN = "insert_bearer_token_here...";
    private static final String CURRENT_BEARER_TOKEN = AuthManager.getInstance().getBearerToken();
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("cmajji@gmail.com", "Password123*", new LoginRequest.Options(true, true));

    @Before
    public void setup(){
        NetworkManager.getInstance().initService();
    }

    @Test
    public void getLogin_Success() {
        Call<LoginResponse> call = NetworkManager.getInstance().login(LOGIN_REQUEST);
        try {
            Response<LoginResponse> response = call.execute();
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            LoginResponse loginResponse = response.body();
            Assert.assertNotNull(loginResponse);

            //Set up session token
            AuthManager.getInstance().setSessionToken(response.body().getSessionToken());
            getSessionId(response.body().getSessionToken());

            //TODO Get Bearer Token
            //fetchIdToken(response.body().getSessionToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getProfile_Success() {
        Assert.assertTrue(AuthManager.getInstance().getBearerToken() != null
                && !AuthManager.getInstance().getBearerToken().isEmpty()); //Check if bearer token in singleton is assigned or not
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

    public void getSessionId(String sessionToken) {
        CreateSessionRequest request = new CreateSessionRequest(sessionToken);
        NetworkManager.getInstance().createSession(request).enqueue(new Callback<CreateSessionResponse>() {
            @Override
            public void onResponse(Call<CreateSessionResponse> call, Response<CreateSessionResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("Session Id: " + response.body().getId());
                    AuthManager.getInstance().setIdTokenForSignOut(response.body().getId());
                }
            }

            @Override
            public void onFailure(Call<CreateSessionResponse> call, Throwable t) {
                Timber.i("Session Id: Failed" );
                AuthManager.getInstance().setIdTokenForSignOut(null);
            }
        });
    }
}
