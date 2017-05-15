package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.features.fad.FadManager;
import com.dignityhealth.myhome.features.fad.LocationResponse;
import com.dignityhealth.myhome.features.fad.LocationSuggestionsResponse;
import com.dignityhealth.myhome.features.fad.ProvidersResponse;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;
import com.dignityhealth.myhome.features.login.LoginRequest;
import com.dignityhealth.myhome.features.login.LoginResponse;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionRequest;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionResponse;
import com.dignityhealth.myhome.features.tos.Tos;
import com.dignityhealth.myhome.utils.RESTConstants;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

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
                Timber.i(" Request Url: " + request.url());
                Timber.i(" Request body: " + request.body());
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

    public Call<Profile> getProfile(String bearer) {
        return service.getProfile(bearer);
    }

    public Call<Void> updateProfile(String bearer, Profile updatedProfileData) {
        return service.updateProfile(bearer, updatedProfileData);
    }

    public Call<LoginResponse> login(LoginRequest request) {
        return service.login(request);
    }

    public Call<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
        return service.forgotPassword(request);
    }

    public Call<CreateSessionResponse> createSession(CreateSessionRequest request) {
        return service.createSession(request);
    }

    public Call<Void> logout(String auth, String id) {
        return service.logout(auth, id);
    }

    public Call<Tos> getTos(String bearer) {
        return service.getTos(bearer);
    }

    public Call<List<LocationSuggestionsResponse>> getLocationSuggestions(String queryString) {
        return service.getLocationSuggestions(queryString);
    }

    public Call<LocationResponse> getLocation() {
        return service.getUserLocation();
    }

    public Call<ProvidersResponse> getProviders(String queryString,
                                                String lat,
                                                String lon,
                                                String displayName,
                                                String zipCode) {
        return service.getProviders(queryString, lat, lon, displayName, zipCode);
    }

    public Call<ProviderDetailsResponse> getProviderDetails(String id) {
        return service.getProviderDetails(id);
    }

    // Network Util

    public void getUserLocation() {
        NetworkManager.getInstance().getLocation().enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, retrofit2.Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    FadManager.getInstance().setLocation(response.body());
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                Timber.i("get user location failed");
            }
        });
    }
}
