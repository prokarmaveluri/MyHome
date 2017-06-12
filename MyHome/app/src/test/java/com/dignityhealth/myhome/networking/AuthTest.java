package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.login.LoginRequest;
import com.dignityhealth.myhome.features.login.LoginResponse;
import com.dignityhealth.myhome.networking.auth.AuthManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by cmajji on 6/11/17.
 */

public class AuthTest {

    boolean isSuccess = false;

    @Before
    public void setup() {
        NetworkManager.getInstance().initService();
    }


    @Test
    public void TestLoginAPI() {     // Positive test
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        final LoginRequest request;
        LoginRequest.Options options = new LoginRequest.Options(true, true);
        request = new LoginRequest("cmajji@gmail.com", "Password123*", options);

        NetworkManager.getInstance().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End " + (System.currentTimeMillis() - prevtime) / 1000);
                    System.out.println("time ** " + (System.currentTimeMillis() - prevtime) / 1000);
                    AuthManager.getInstance().setSessionToken(response.body().getSessionToken());
                    isSuccess = true;
                    lock.countDown();
                } else {
                    isSuccess = false;
                    AuthManager.getInstance().setSessionToken(null);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                isSuccess = false;
                AuthManager.getInstance().setSessionToken(null);
                lock.countDown();
            }
        });

        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(isSuccess);
    }
}
