package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.appointments.Appointment;
import com.dignityhealth.myhome.features.appointments.AppointmentResponse;
import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.features.fad.FadManager;
import com.dignityhealth.myhome.features.fad.LocationResponse;
import com.dignityhealth.myhome.features.fad.ProvidersResponse;
import com.dignityhealth.myhome.features.fad.details.ProviderDetailsResponse;
import com.dignityhealth.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.dignityhealth.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.dignityhealth.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.dignityhealth.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.dignityhealth.myhome.features.login.LoginRequest;
import com.dignityhealth.myhome.features.login.LoginResponse;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.ProfileResponse;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionResponse;
import com.dignityhealth.myhome.features.tos.Tos;
import com.dignityhealth.myhome.utils.AppPreferences;
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

    private final String BEARER = "Bearer ";

    public static NetworkManager getInstance() {

        if (null == instance) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void initService() {

        httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(1, TimeUnit.SECONDS);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json").build();
                Timber.i(" Request Url: " + request.url());
                Timber.i(" Request headers: " + request.headers());
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

    /**
     * Register a user
     *
     * @param request the Enrollment object to be sent
     * @return Void
     */
    public Call<Void> register(EnrollmentRequest request) {
        return service.register(request);
    }

    /**
     * Get a Profile
     *
     * @param bearer the bearer token of the user whose Profile we want
     * @return a Profile object of the user
     */
    public Call<ProfileResponse> getProfile(String bearer) {
        return service.getProfile(BEARER + bearer);
    }

    /**
     * Update a Profile for a particular user
     *
     * @param bearer             the bearer token of the user whose Profile we wish to modify
     * @param updatedProfileData the new Profile we wish to save to the user
     * @return Void
     */
    public Call<Void> updateProfile(String bearer, Profile updatedProfileData) {
        return service.updateProfile(BEARER + bearer, updatedProfileData);
    }

    /**
     * Attempt to Log In
     *
     * @param request the Login object of the user we wish to log in
     * @return the LoginReponse with many valuable fields such as expiration dates, session ID...
     */
    public Call<LoginResponse> login(LoginRequest request) {
        return service.login(request);
    }

    /**
     * Send a Forgot Password request to the server
     *
     * @param request a ForgotPasswordRequest object of the user who forgot their password
     * @return a ForgotPasswordReponse denoting the status of the request
     */
    public Call<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
        return service.forgotPassword(request);
    }

    /**
     * Create a session
     *
     * @param sid the session ID we use to create a Session
     * @return a CreateSessionReponse containing many valuable fields such as status, expiration, cookieToken...
     */
    public Call<CreateSessionResponse> createSession(String sid) {
        return service.createSession(sid);
    }

    /**
     * Attempt to logout
     *
     * @param id the Session ID we're attempting to log out
     * @return Void
     */
    public Call<Void> logout(String id) {
        return service.logout("sid=" + id);
    }

    /**
     * Get Terms of Service.
     * This API is still heavily under construction, but not used currently in MVP
     *
     * @param bearer the bearer token of the user
     * @return a ToS object. Currently not very helpful
     */
    public Call<Tos> getTos(String bearer) {
        return service.getTos(BEARER + bearer);
    }

    /**
     * Get the Appointments for the user.
     *
     * @param bearer the bearer token of the user whose appointments we want
     * @return AppointmentReponse that should contain a user's appointments
     */
    public Call<AppointmentResponse> getAppointments(String bearer) {
        return service.getAppointments(BEARER + bearer);
    }

    /**
     * Create an Appointment
     *
     * @param bearer      the bearer token of the user who we want to create an appointment for
     * @param appointment the appointment we wish to create
     * @return Void
     */
    public Call<Void> createAppointment(String bearer, Appointment appointment) {
        return service.createAppointment(BEARER + bearer, appointment);
    }

    /**
     * Get the Location Suggestions when a user enters their location in the filter
     *
     * @param queryString the query of the user entering their location
     * @return List of Locations to suggest
     */
    public Call<List<LocationResponse>> getLocationSuggestions(String queryString) {
        return service.getLocationSuggestions(queryString);
    }

    /**
     * Get Search suggestions for a query
     *
     * @param queryString the query
     * @param lat         the latitude of the user's location
     * @param lon         the longitude of the user's location
     * @param displayName the display name of user's location
     * @param zipCode     the zipcode of the user's location
     * @return List of Search suggestions
     */
    public Call<List<SearchSuggestionResponse>> getSearchSuggestions(String queryString,
                                                                     String lat,
                                                                     String lon,
                                                                     String displayName,
                                                                     String zipCode) {
        return service.getSearchSuggestions(queryString,
                lat,
                lon,
                displayName,
                zipCode);
    }

    /**
     * Get the location of the device.
     * Dignity Health is most likely using a service to look up addresses via IP address of calls.
     *
     * @return The Location found
     */
    public Call<LocationResponse> getLocation() {
        return service.getUserLocation();
    }

    /**
     * Get a list of providers/doctors based on multiple criteria.
     *
     * @param queryString the query
     * @param lat         the latitude of the user's location
     * @param lon         the longitude of the user's location
     * @param displayName the display name of the user's location
     * @param zipCode     the zip code of the user's location
     * @param page        the page
     * @param pageSize    the size of the page
     * @param distance    the distance
     * @param sortBy      sorting of the reponse
     * @param gender      the gender filter
     * @param languages   the language filter
     * @param specialties the specialties filter
     * @param facilities  the facilities filter
     * @param practices   the practices filter
     * @param patients    the filter that denotes whether to show doctors that only take existing patients or not
     * @return a List of Providers
     */
    public Call<ProvidersResponse> getProviders(String queryString,
                                                String lat,
                                                String lon,
                                                String displayName,
                                                String zipCode,
                                                String page,
                                                String pageSize,
                                                String distance,
                                                String sortBy,
                                                String gender,
                                                String languages,
                                                String specialties,
                                                String facilities,
                                                String practices,
                                                String patients) {
        return service.getProviders(queryString, lat, lon, displayName, zipCode,
                page,
                pageSize,
                distance,
                sortBy,
                gender,
                languages,
                specialties,
                facilities,
                practices,
                patients);
    }

    /**
     * Get a detailed profile of a provider
     *
     * @param id the Provider ID
     * @return a More in-depth look of the provider
     */
    public Call<ProviderDetailsResponse> getProviderDetails(String id) {
        return service.getProviderDetails(id);
    }

    public Call<CreateAppointmentResponse> createAppointment(String bearerToken,
                                                             CreateAppointmentRequest request) {
        return service.createAppointment(bearerToken, request);
    }

    public Call<RegValidationResponse> getValidationRules(String scheduleId, String includeQuery) {
        return service.getValidationRules(scheduleId, includeQuery);
    }

    // Network Util

    /**
     * Attempt to get a user's location
     */
    public void getUserLocation() {
        NetworkManager.getInstance().getLocation().enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, retrofit2.Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    FadManager.getInstance().setCurrentLocation(response.body());
                    FadManager.getInstance().setLocation(response.body());
                    AppPreferences.getInstance().setBooleanPreference("IS_USER_LOCATION", true);
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                    AppPreferences.getInstance().setBooleanPreference("IS_USER_LOCATION", false);
                    FadManager.getInstance().setCurrentLocation(null);
                    FadManager.getInstance().setLocation(null);
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
                Timber.i("get user location failed");
                AppPreferences.getInstance().setBooleanPreference("IS_USER_LOCATION", false);
                FadManager.getInstance().setCurrentLocation(null);
                FadManager.getInstance().setLocation(null);
            }
        });
    }
}
