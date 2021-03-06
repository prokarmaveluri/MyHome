package com.prokarma.myhome.networking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ImageView;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.entities.Tos;
import com.prokarma.myhome.entities.UpdateResponse;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.MyAppointmentsRequest;
import com.prokarma.myhome.features.appointments.MyAppointmentsResponse;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.features.enrollment.ValidateEmailResponse;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetails;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.prokarma.myhome.features.login.endpoint.AmWellResponse;
import com.prokarma.myhome.features.login.endpoint.RefreshRequest;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.login.endpoint.SignOutRequest;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.prokarma.myhome.features.preferences.MySavedDoctorsRequest;
import com.prokarma.myhome.features.preferences.MySavedDoctorsResponse;
import com.prokarma.myhome.features.preferences.ProviderResponse;
import com.prokarma.myhome.features.preferences.SaveDoctorRequest;
import com.prokarma.myhome.features.preferences.SaveDoctorResponse;
import com.prokarma.myhome.features.profile.MyProfileRequest;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.settings.ChangePasswordRequest;
import com.prokarma.myhome.features.settings.ChangeSecurityQuestionRequest;
import com.prokarma.myhome.features.settings.CommonResponse;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.ApiErrorUtil;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.EnviHandler;
import com.prokarma.myhome.utils.RESTConstants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 * Network manager class that handles interaction with Retrofit
 */

public class NetworkManager {
    private RESTService service = null;
    private ISessionExpiry expiryListener;
    private static NetworkManager instance = null;
    private static OkHttpClient.Builder httpClient = null;

    private static final String BEARER = "Bearer ";
    private static Boolean isEmailTaken = false;

    public static NetworkManager getInstance() {

        if (null == instance) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void setExpiryListener(ISessionExpiry listener) {
        this.expiryListener = listener;
    }

    public boolean canMakeNetworkCalls() {
        if (EnviHandler.CIAM_BASE_URL == null || service == null) {
            return false;
        }
        return true;
    }

    public void initService() {
        initHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EnviHandler.CIAM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        service = retrofit.create(RESTService.class);
    }

    public void initMockService(NetworkBehavior networkBehavior) {
        initHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EnviHandler.CIAM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        //Mock Network with given behavior
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit).networkBehavior(networkBehavior).build();
        final BehaviorDelegate<RESTService> delegate = mockRetrofit.create(RESTService.class);
        service = new MockRESTService(delegate);
    }

    private void initHttpClient() {
        httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(20, TimeUnit.SECONDS);
        httpClient.connectTimeout(20, TimeUnit.SECONDS);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json").build();
                if (request.url().toString().contains("oauth2/aus32qsj5x26YmQN11t7/v1/token"))
                    chain.request().newBuilder().addHeader("Content-Type", "application/x-www-form-urlencoded").build();
                Timber.i(" Request Url: " + request.url());
                Timber.i(" Request headers: " + request.headers());
                Timber.i(" Request body: " + request.body());
                Response response = chain.proceed(request);

                Timber.i(" Response Code: " + response.code());
                //Session expired
                if (response.code() == 401 &&
                        !request.url().toString().equalsIgnoreCase(EnviHandler.OKTA_BASE_URL + "api/v1/authn") &&
                        !request.url().toString().equalsIgnoreCase(EnviHandler.CIAM_BASE_URL + "api/mobile/auth/myhome.mobile.consumer/refresh")) {
                    AuthManager.getInstance().refreshToken();

                } else if (response.code() == 400 &&
                        request.url().toString().equalsIgnoreCase(EnviHandler.CIAM_BASE_URL + "api/mobile/auth/myhome.mobile.consumer/refresh")) {

                    if (null != expiryListener)
                        expiryListener.expired();
                }
                return response;
            }
        });
    }

    /**
     * Register a user
     *
     * @param request the Enrollment object to be sent
     * @return Void
     */
    public Call<Void> register(EnrollmentRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_REGISTER_FORCE_ERROR)) {
            return service.register(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/enrollment", request);
        } else {
            return service.register(EnviHandler.CIAM_BASE_URL + "api/users/enrollment", request);
        }
    }

    /**
     * Update a Profile for a particular user
     *
     * @param bearer             the bearer token of the user whose Profile we wish to modify
     * @param updatedProfileData the new Profile we wish to save to the user
     * @return Void
     */
    public Call<Void> updateProfile(String bearer, Profile updatedProfileData) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_PROFILE_UPDATE_FORCE_ERROR)) {
            return service.updateProfile(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/me",
                    BEARER + bearer, updatedProfileData);
        } else {
            return service.updateProfile(EnviHandler.CIAM_BASE_URL + "api/users/me",
                    BEARER + bearer, updatedProfileData);
        }
    }

    /**
     * Send a Forgot Password request to the server
     *
     * @param request a ForgotPasswordRequest object of the user who forgot their password
     * @return a ForgotPasswordReponse denoting the status of the request
     */
    public Call<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_FORGOT_PASSWORD_FORCE_ERROR)) {
            return service.forgotPassword(EnviHandler.OKTA_BASE_URL.concat("messUpUrl123") + "api/v1/authn/recovery/password",
                    request);
        } else {
            return service.forgotPassword(EnviHandler.OKTA_BASE_URL + "api/v1/authn/recovery/password",
                    request);
        }
    }

    /**
     * Get Terms of Service.
     * This API is still heavily under construction, but not used currently in MVP
     *
     * @param bearer the bearer token of the user
     * @return a ToS object. Currently not very helpful
     */
    public Call<Tos> getTos(String bearer) {
        return service.getTos(EnviHandler.CIAM_BASE_URL + "api/terms-and-conditions", BEARER + bearer);
    }

    /**
     * Accept Terms of Service.
     * This API is still heavily under construction, but not used currently in MVP
     *
     * @param bearer the bearer token of the user
     * @return a ToS object. Currently not very helpful
     */
    public Call<Tos> acceptTos(String bearer) {
        return service.acceptTos(EnviHandler.CIAM_BASE_URL + "api/terms-and-conditions", BEARER + bearer);
    }

    /**
     * Get the Location Suggestions when a user enters their location in the filter
     *
     * @param queryString the query of the user entering their location
     * @return List of Locations to suggest
     */
    public Call<List<LocationResponse>> getLocationSuggestions(String queryString) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_LOCATION_SUGGESTIONS_FORCE_ERROR)) {
            return service.getLocationSuggestions(EnviHandler.S2_BASE_URL.concat("messUpUrl123") + "api/locationsuggestion",
                    queryString);
        } else {
            return service.getLocationSuggestions(EnviHandler.S2_BASE_URL + "api/locationsuggestion",
                    queryString);
        }
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
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_SEARCH_SUGGESTIONS_FORCE_ERROR)) {
            return service.getSearchSuggestions(EnviHandler.S2_BASE_URL.concat("messUpUrl123") + "api/suggestion",
                    queryString,
                    lat,
                    lon,
                    displayName,
                    zipCode != null ? zipCode : "");
        } else {
            return service.getSearchSuggestions(EnviHandler.S2_BASE_URL + "api/suggestion",
                    queryString,
                    lat,
                    lon,
                    displayName,
                    zipCode != null ? zipCode : "");
        }
    }

    /**
     * Get the location of the device.
     * Dignity Health is most likely using a service to look up addresses via IP address of calls.
     *
     * @return The Location found
     */
    public Call<LocationResponse> getLocation() {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_LOCATION_FORCE_ERROR)) {
            return service.getUserLocation(EnviHandler.S2_BASE_URL.concat("messUpUrl123") + "api/location/");
        } else {
            return service.getUserLocation(EnviHandler.S2_BASE_URL + "api/location/");
        }
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
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_PROVIDERS_FORCE_ERROR)) {
            return service.getProviders(EnviHandler.S2_BASE_URL.concat("messUpUrl123") + "api/providers",
                    queryString, lat, lon, displayName, zipCode,
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
        } else {
            return service.getProviders(EnviHandler.S2_BASE_URL + "api/providers",
                    queryString, lat, lon, displayName, zipCode,
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
    }

    public Call<CreateAppointmentResponse> createAppointment(String bearerToken,
                                                             CreateAppointmentRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_CREATE_APPOINTMENT_FORCE_ERROR)) {
            return service.createAppointment(EnviHandler.SCHEDULING_BASE.concat("messUpUrl123") + RESTConstants.SCHEDULING_VISIT,
                    BEARER + bearerToken, request);
        } else {
            return service.createAppointment(EnviHandler.SCHEDULING_BASE + RESTConstants.SCHEDULING_VISIT,
                    BEARER + bearerToken, request);
        }
    }

    public Call<RegValidationResponse> getValidationRules(String scheduleId, String includeQuery) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_VALIDATION_RULES_FORCE_ERROR)) {
            return service.getValidationRules(EnviHandler.SCHEDULING_BASE + RESTConstants.SCHEDULING_VALIDATION + scheduleId
                    + RESTConstants.SCHEDULING_VALIDATION_ENDPOINT.concat("messUpUrl123"), includeQuery);
        } else {
            return service.getValidationRules(EnviHandler.SCHEDULING_BASE + RESTConstants.SCHEDULING_VALIDATION + scheduleId
                    + RESTConstants.SCHEDULING_VALIDATION_ENDPOINT, includeQuery);
        }
    }

    public Call<ValidateEmailResponse> findEmail(String email) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_FIND_EMAIL_FORCE_ERROR)) {
            return service.findEmail(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users", email);
        } else {
            return service.findEmail(EnviHandler.CIAM_BASE_URL + "api/users", email);
        }
    }

    public Call<UpdateResponse> versionCheck() {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_VERSION_CHECK_FORCE_ERROR)) {
            return service.versionCheck(EnviHandler.VERSIONING_URL.concat("messUpUrl123") + "api/versioning/dependencies");
        } else {
            return service.versionCheck(EnviHandler.VERSIONING_URL + "api/versioning/dependencies");
        }
    }

    public Call<SaveDoctorResponse> saveDoctor(String bearerToken, SaveDoctorRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_SAVE_DOCTOR_FORCE_ERROR)) {
            return service.saveDoctor(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/me/favorite-providers",
                    BEARER + bearerToken, request);
        } else {
            return service.saveDoctor(EnviHandler.CIAM_BASE_URL + "api/users/me/favorite-providers",
                    BEARER + bearerToken, request);
        }
    }

    public Call<SaveDoctorResponse> deleteSavedDoctor(String bearerToken, String npi) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_DELETE_SAVED_DOCTOR_FORCE_ERROR)) {
            return service.deleteSavedDoctor(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/me/favorite-providers/" + npi, BEARER + bearerToken);
        } else {
            return service.deleteSavedDoctor(EnviHandler.CIAM_BASE_URL + "api/users/me/favorite-providers/" + npi, BEARER + bearerToken);
        }
    }

    public Call<MySavedDoctorsResponse> getSavedDoctors(String bearerToken,
                                                        MySavedDoctorsRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_SAVED_DOCTORS_FORCE_ERROR)) {
            return service.getSavedDoctors(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/query",
                    BEARER + bearerToken, request);
        } else {
            return service.getSavedDoctors(EnviHandler.CIAM_BASE_URL + "api/users/query",
                    BEARER + bearerToken, request);
        }
    }

    public Call<MyAppointmentsResponse> getMyAppointments(String bearerToken,
                                                          MyAppointmentsRequest request) {
        if (EnviHandler.CIAM_BASE_URL == null) {
            return null;
        }
        if (service == null) {
            return null;
        }

        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_MY_APPOINTMENTS_FORCE_ERROR)) {
            return service.getMyAppointments(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/query",
                    BEARER + bearerToken, request);
        } else {
            return service.getMyAppointments(EnviHandler.CIAM_BASE_URL + "api/users/query",
                    BEARER + bearerToken, request);
        }
    }

    //1.2 APIs

    public Call<CommonResponse> changePassword(String bearerToken,
                                               ChangePasswordRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_CHANGE_PASSWORD_FORCE_ERROR)) {
            return service.changePassword(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/me/password",
                    BEARER + bearerToken, request);
        } else {
            return service.changePassword(EnviHandler.CIAM_BASE_URL + "api/users/me/password",
                    BEARER + bearerToken, request);
        }
    }

    public Call<CommonResponse> changeSecurityQuestion(String bearerToken,
                                                       ChangeSecurityQuestionRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_CHANGE_SECURITY_QUESTION_FORCE_ERROR)) {
            return service.changeSecurityQuestion(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/me/recovery/question",
                    BEARER + bearerToken, request);
        } else {
            return service.changeSecurityQuestion(EnviHandler.CIAM_BASE_URL + "api/users/me/recovery/question",
                    BEARER + bearerToken, request);
        }
    }

    /**
     * Get a Profile
     *
     * @param bearer the bearer token of the user whose Profile we want
     * @return a Profile object of the user
     */
    public Call<ProfileGraphqlResponse> getProfile(String bearer) {
        if (EnviHandler.CIAM_BASE_URL == null) {
            return null;
        }
        if (service == null) {
            return null;
        }
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_PROFILE_GET_FORCE_ERROR)) {
            return service.getUserProfile(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/query",
                    BEARER + bearer, new MyProfileRequest());
        } else {
            return service.getUserProfile(EnviHandler.CIAM_BASE_URL + "api/users/query",
                    BEARER + bearer, new MyProfileRequest());
        }
    }

    /**
     * endpoint for signIn
     *
     * @param request request body for signIn
     * @return login response
     */
    public Call<SignInResponse> signIn(SignInRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_IN_FORCE_ERROR)) {
            return service.signIn(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") +
                    "api/mobile/auth/" + BuildConfig.URL_PATH_CLIENT_ID + "/sign-in", request);
        } else {
            return service.signIn(EnviHandler.CIAM_BASE_URL +
                    "api/mobile/auth/" + BuildConfig.URL_PATH_CLIENT_ID + "/sign-in", request);
        }
    }


    /**
     * endpoint for signIn Refresh
     *
     * @param request request body for signIn Refresh
     * @return refresh response
     */
    public Call<SignInResponse> signInRefresh(RefreshRequest request) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_IN_REFRESH_FORCE_ERROR)) {
            return service.signInRefresh(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") +
                            "api/mobile/auth/" + BuildConfig.URL_PATH_CLIENT_ID + "/refresh",
                    request);
        } else {
            return service.signInRefresh(EnviHandler.CIAM_BASE_URL +
                            "api/mobile/auth/" + BuildConfig.URL_PATH_CLIENT_ID + "/refresh",
                    request);
        }
    }

    /**
     * endpoint for signOut
     *
     * @param request request body for signOut
     * @return Sign out response
     */
    public Call<CommonResponse> signOut(SignOutRequest request, String bearerToken) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_OUT_FORCE_ERROR)) {
            return service.signOut(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") +
                            "api/mobile/auth/" + BuildConfig.URL_PATH_CLIENT_ID + "/sign-out",
                    BEARER + bearerToken, request);
        } else {
            return service.signOut(EnviHandler.CIAM_BASE_URL +
                            "api/mobile/auth/" + BuildConfig.URL_PATH_CLIENT_ID + "/sign-out",
                    BEARER + bearerToken, request);
        }
    }

    public Call<AmWellResponse> getAmWellToken(String bearerToken) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_SIGN_OUT_FORCE_ERROR)) {
            return service.getAmWellToken(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") +
                            "api/users/me/security-token",
                    BEARER + bearerToken);
        } else {
            return service.getAmWellToken(EnviHandler.CIAM_BASE_URL +
                            "api/users/me/security-token",
                    BEARER + bearerToken);
        }
    }

    /**
     * resend email to the user.
     *
     * @param bearerToken bearer token to resend email
     * @return
     */
    public Call<CommonResponse> resendEmail(String bearerToken) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_RESEND_EMAIL_FORCE_ERROR)) {
            return service.resendEmail(EnviHandler.CIAM_BASE_URL.concat("messUpUrl123") + "api/users/me/verification-email",
                    BEARER + bearerToken);
        } else {
            return service.resendEmail(EnviHandler.CIAM_BASE_URL + "api/users/me/verification-email",
                    BEARER + bearerToken);
        }
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
                    Timber.e("getUserLocation. Response, but not successful?\n" + response);
                    AppPreferences.getInstance().setBooleanPreference("IS_USER_LOCATION", false);
                    FadManager.getInstance().setCurrentLocation(null);
                    FadManager.getInstance().setLocation(null);
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                Timber.e("getUserLocation. Something failed! :/");
                Timber.e("Throwable = " + t);
                Timber.i("get user location failed");
                AppPreferences.getInstance().setBooleanPreference("IS_USER_LOCATION", false);
                FadManager.getInstance().setCurrentLocation(null);
                FadManager.getInstance().setLocation(null);
            }
        });
    }

    public static boolean isEmailTaken() {
        return isEmailTaken;
    }

    /**
     * Attempt to validate email already registered
     */
    public void findEmail(final String email, final TextInputLayout view, final Context context) {
        NetworkManager.getInstance().findEmail(email).enqueue(new Callback<ValidateEmailResponse>() {
            @Override
            public void onResponse(Call<ValidateEmailResponse> call, retrofit2.Response<ValidateEmailResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult()) {
                        if (context != null && view != null) {
                            isEmailTaken = true;
                            view.setError(context.getString(R.string.email_already_registered));
                            Timber.i("Email already exists!");
                        }
                    } else {
                        ApiErrorUtil.getInstance().findEmailError(context, view, response);
                        isEmailTaken = false;
                        if (view != null) {
                            view.setError(null);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ValidateEmailResponse> call, Throwable t) {
                Timber.i("validateEmail, failed");
                Timber.i("validateEmail, t=" + t.toString());
                ApiErrorUtil.getInstance().findEmailFailed(context, view, t);
                isEmailTaken = false;
                if (view != null) {
                    view.setError(null);
                }
            }
        });
    }

    public interface ISessionExpiry {
        void expired();
    }

    /**
     * Get a detailed profile of a provider
     *
     * @param id the Provider ID
     * @return a More in-depth look of the provider
     */
    public Call<ProviderDetails> getNewProviderDetails(String id) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_PROVIDER_DETAILS_FORCE_ERROR)) {
            return service.getNewProviderDetails(EnviHandler.S2_BASE_URL.concat("messUpUrl123") + "api/providers/full", id);
        } else {
            return service.getNewProviderDetails(EnviHandler.S2_BASE_URL + "api/providers/full", id);
        }
    }

    /**
     * Get time slots for all office locations
     *
     * @param npi
     * @param fromDate
     * @param toDate
     * @return
     */
    public Call<AppointmentTimeSlots> getProviderAppointments(@NonNull String npi, String fromDate, String toDate) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_APPOINTMENT_TIMES_FORCE_ERROR)) {
            //return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE.concat("messUpUrl123") + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate);
            return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE.concat("messUpUrl123") + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate);
        } else {
            //return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate);
            return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate);
        }
    }

    /**
     * Get time slots for a specific address
     *
     * @param npi
     * @param fromDate
     * @param toDate
     * @return
     */
    public Call<AppointmentTimeSlots> getProviderAppointments(@NonNull String npi, String fromDate, String toDate, String addressHash) {
        if (AppPreferences.getInstance().getBooleanPreference(Constants.API_GET_APPOINTMENT_TIMES_FORCE_ERROR)) {
            //return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE.concat("messUpUrl123") + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate);
            return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE.concat("messUpUrl123") + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate, addressHash);
        } else {
            //return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate);
            return service.getProviderAppointments(EnviHandler.SCHEDULING_BASE + "api/v1/provider/npi/" + npi + "/schedule", fromDate, toDate, addressHash);
        }
    }

    //1.1

    public void getSavedDoctors(final Context context, final View view) {
        NetworkManager.getInstance().getSavedDoctors(AuthManager.getInstance().getBearerToken(),
                new MySavedDoctorsRequest()).enqueue(new Callback<MySavedDoctorsResponse>() {
            @Override
            public void onResponse(Call<MySavedDoctorsResponse> call, retrofit2.Response<MySavedDoctorsResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        ProfileManager.setFavoriteProviders(response.body().getData().getUser().getFavoriteProviders());
                    } catch (NullPointerException ex) {
                        Timber.e("Error onResponse SavedDoctors ");
                        ApiErrorUtil.getInstance().getSavedDoctorsError(context, view, response);
                        ProfileManager.setFavoriteProviders(null);
                    }
                } else {
                    Timber.e("Error onResponse SavedDoctors with error code");
                    ApiErrorUtil.getInstance().getSavedDoctorsError(context, view, response);
                    ProfileManager.setFavoriteProviders(null);
                }
            }

            @Override
            public void onFailure(Call<MySavedDoctorsResponse> call, Throwable t) {
                Timber.e("Error onFailure SavedDoctors");
                ApiErrorUtil.getInstance().getSavedDoctorsFailed(context, view, t);
                ProfileManager.setFavoriteProviders(null);
            }
        });
    }

    public void updateFavDoctor(final boolean isSave, final String npi, final ImageView favProvider,
                                final ProviderResponse provider, final boolean isList,
                                final Context context, final View parentView) {

        if (!ConnectionUtil.isConnected(context)) {
            CommonUtil.showToast(context, context.getString(R.string.no_network_msg));
            return;
        }
        if (isSave) {
            CommonUtil.updateFavView(context, isSave, favProvider);
            favProvider.announceForAccessibility(favProvider.getContentDescription());
            final SaveDoctorRequest request = new SaveDoctorRequest(npi);
            NetworkManager.getInstance().saveDoctor(AuthManager.getInstance().getBearerToken(),
                    request).enqueue(new Callback<SaveDoctorResponse>() {
                @Override
                public void onResponse(Call<SaveDoctorResponse> call, retrofit2.Response<SaveDoctorResponse> response) {
                    if (response.isSuccessful()) {
                        List<ProviderResponse> providerList = ProfileManager.getFavoriteProviders();
                        if (provider != null && provider.getNpi() != null && !isProviderFound(provider.getNpi())) {
                            Map<String, Object> tealiumData = new HashMap<>();
                            tealiumData.put(Constants.FAVORITE_PROVIDER_NPI, npi);
                            TealiumUtil.trackEvent(Constants.FAVORITE_PROVIDER_EVENT, tealiumData);

                            if (providerList == null) {
                                providerList = new ArrayList<>();
                            }

                            providerList.add(0, provider);
                            ProfileManager.setFavoriteProviders(providerList);
                        }
                    } else {
                        CommonUtil.updateFavView(context, !isSave, favProvider);
                        favProvider.announceForAccessibility(favProvider.getContentDescription());
                        ApiErrorUtil.getInstance().saveDoctorError(context, parentView, response);
                    }
                }

                @Override
                public void onFailure(Call<SaveDoctorResponse> call, Throwable t) {
                    CommonUtil.updateFavView(context, !isSave, favProvider);
                    favProvider.announceForAccessibility(favProvider.getContentDescription());
                    ApiErrorUtil.getInstance().saveDoctorFailed(context, parentView, t);
                }
            });
        } else { //DELETE saved Doc
            if (!isList) {
                CommonUtil.updateFavView(context, isSave, favProvider);
                favProvider.announceForAccessibility(favProvider.getContentDescription());
            }
            NetworkManager.getInstance().deleteSavedDoctor(AuthManager.getInstance().getBearerToken(),
                    npi).enqueue(new Callback<SaveDoctorResponse>() {
                @Override
                public void onResponse(Call<SaveDoctorResponse> call, retrofit2.Response<SaveDoctorResponse> response) {
                    if (response.isSuccessful()) {
                        Map<String, Object> tealiumData = new HashMap<>();
                        tealiumData.put(Constants.FAVORITE_PROVIDER_NPI, npi);
                        TealiumUtil.trackEvent(Constants.UNFAVORITE_PROVIDER_EVENT, tealiumData);

                        deleteSavedDoctor(npi);
                    } else {
                        CommonUtil.updateFavView(context, !isSave, favProvider);
                        favProvider.announceForAccessibility(favProvider.getContentDescription());
                        ApiErrorUtil.getInstance().deleteSavedDoctorError(context, parentView, response);
                    }
                }

                @Override
                public void onFailure(Call<SaveDoctorResponse> call, Throwable t) {
                    CommonUtil.updateFavView(context, !isSave, favProvider);
                    favProvider.announceForAccessibility(favProvider.getContentDescription());
                    ApiErrorUtil.getInstance().deleteSavedDoctorFailed(context, parentView, t);
                }
            });
        }
    }

    public void deleteSavedDoctor(String npi) {
        try {
            List<ProviderResponse> providerList = ProfileManager.getFavoriteProviders();
            for (int index = 0; index < providerList.size(); index++) {
                if (providerList.get(index).getNpi().contains(npi)) {
                    providerList.remove(index);
                    break;
                }
            }
            ProfileManager.setFavoriteProviders(providerList);
        } catch (NullPointerException ex) {
            Timber.w(ex);
        }
    }

    private boolean isProviderFound(String npi) {
        try {
            List<ProviderResponse> providerList = ProfileManager.getFavoriteProviders();
            if (null == providerList)
                return false;
            for (ProviderResponse provider : providerList) {
                if (provider.getNpi().contains(npi)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public void getMyAppointments() {
        NetworkManager.getInstance().getMyAppointments(AuthManager.getInstance().getBearerToken(),
                new MyAppointmentsRequest()).enqueue(new Callback<MyAppointmentsResponse>() {
            @Override
            public void onResponse(Call<MyAppointmentsResponse> call, retrofit2.Response<MyAppointmentsResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        MyAppointmentsResponse myAppointmentsResponse = response.body();
                        if (myAppointmentsResponse.getData() != null && myAppointmentsResponse.getData().getUser() != null) {

                            ArrayList<Appointment> appointments = (ArrayList<Appointment>) myAppointmentsResponse.getData().getUser().getAppointments();
                            Timber.i("Appointments: " + Arrays.deepToString(appointments.toArray()));

                            //Attempt to sort the appointments by startTime
                            Collections.sort(appointments);
                            ProfileManager.setAppointments(appointments);
                        }
                    } catch (Exception e) {
                        Timber.w(e);
                    }
                } else {
                    Timber.e("getMyAppointments. Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<MyAppointmentsResponse> call, Throwable t) {
                Timber.e("getMyAppointments. Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }
}
