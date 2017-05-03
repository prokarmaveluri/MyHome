package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.features.login.LoginRequest;
import com.dignityhealth.myhome.features.login.LoginResponse;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.utils.RESTConstants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kwelsh on 4/26/17.
 * Network manager class that handles interaction with Retrofit
 */

public class NetworkManager {

    private RESTService service = null;
    private static NetworkManager instance = null;
    private static OkHttpClient.Builder httpClient = null;

    public static NetworkManager getInstance() {

        if (null == instance) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void initService() {

        httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(10, TimeUnit.SECONDS);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json").build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RESTConstants.CIAM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        service = retrofit.create(RESTService.class);
    }

    public Call<Void> register(EnrollmentRequest request) {
        return service.register(request);
    }

    public Call<Profile> getProfile(String bearer){
        return  service.getProfile(bearer);
    }

    public Call<Void> updateProfile(String bearer, Profile updatedProfileData){
        return  service.updateProfile(bearer, updatedProfileData);
    }

    public Call<LoginResponse> login(LoginRequest request) {
        return service.login(request);
    }

    public Call<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request){
        return service.forgotPassword(request);
    }
}
