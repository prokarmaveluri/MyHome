package com.prokarma.myhome.networking;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.AppointmentResponse;
import com.prokarma.myhome.features.appointments.MyAppointmentsRequest;
import com.prokarma.myhome.features.appointments.MyAppointmentsResponse;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.features.enrollment.ValidateEmailResponse;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.prokarma.myhome.features.login.endpoint.RefreshRequest;
import com.prokarma.myhome.features.login.endpoint.RefreshResponse;
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
import com.prokarma.myhome.features.settings.ChangeSesurityQuestionRequest;
import com.prokarma.myhome.features.settings.CommonResponse;
import com.prokarma.myhome.features.tos.Tos;
import com.prokarma.myhome.features.update.UpdateResponse;
import com.prokarma.myhome.networking.auth.AuthManager;
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
    private static boolean isEmailTaken = false;

    private static final String BEARER = "Bearer ";

    public static NetworkManager getInstance() {

        if (null == instance) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void setExpiryListener(ISessionExpiry listener) {
        this.expiryListener = listener;
    }

    public void initService() {

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
                if (response.code() == 401 && !request.url().toString()
                        .equalsIgnoreCase(EnviHandler.OKTA_BASE_URL + "api/v1/authn") &&
                        !request.url().toString()
                                .equalsIgnoreCase(EnviHandler.OKTA_BASE_URL + "oauth2/" +
                                        EnviHandler.AUTH_CLIENT_ID + "/v1/token")) {
                    AuthManager.getInstance().refreshToken();

                } else if (response.code() == 400 && request.url().toString()
                        .equalsIgnoreCase(EnviHandler.OKTA_BASE_URL + "oauth2/" +
                                EnviHandler.AUTH_CLIENT_ID + "/v1/token")) {

                    if (null != expiryListener)
                        expiryListener.expired();
                }
                return response;
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EnviHandler.CIAM_BASE_URL)
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
        return service.register(EnviHandler.CIAM_BASE_URL + "api/users/enrollment", request);
    }

    /**
     * Get a Profile
     *
     * @param bearer the bearer token of the user whose Profile we want
     * @return a Profile object of the user
     */
//    public Call<ProfileResponse> getProfile(String bearer) {
//        return service.getProfile(BEARER + bearer);
//    }

    /**
     * Update a Profile for a particular user
     *
     * @param bearer             the bearer token of the user whose Profile we wish to modify
     * @param updatedProfileData the new Profile we wish to save to the user
     * @return Void
     */
    public Call<Void> updateProfile(String bearer, Profile updatedProfileData) {
        return service.updateProfile(EnviHandler.CIAM_BASE_URL + "api/users/me",
                BEARER + bearer, updatedProfileData);
    }

    /**
     * Attempt to Log In
     *
     * @param request the Login object of the user we wish to log in
     * @return the LoginReponse with many valuable fields such as expiration dates, session ID...
     */
//    public Call<SignInResponse> login(LoginRequest request) {
//        return service.login(EnviHandler.OKTA_BASE_URL + "api/v1/authn", request);
//    }

    /**
     * Send a Forgot Password request to the server
     *
     * @param request a ForgotPasswordRequest object of the user who forgot their password
     * @return a ForgotPasswordReponse denoting the status of the request
     */
    public Call<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
        return service.forgotPassword(EnviHandler.OKTA_BASE_URL + "api/v1/authn/recovery/password",
                request);
    }

    /**
     * Create a session
     *
     * @param sid the session ID we use to create a Session
     * @return a CreateSessionReponse containing many valuable fields such as status, expiration, cookieToken...
     */
//    public Call<CreateSessionResponse> createSession(String sid) {
//        return service.createSession(EnviHandler.OKTA_BASE_URL + "api/v1/sessions/me", sid);
//    }

    /**
     * Attempt to logout
     *
     * @param id the Session ID we're attempting to log out
     * @return Void
     */
//    public Call<Void> logout(String id) {
//        return service.logout(EnviHandler.OKTA_BASE_URL + "api/v1/sessions/me", "sid=" + id);
//    }

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
     * Get the Appointments for the user.
     *
     * @param bearer the bearer token of the user whose appointments we want
     * @return AppointmentReponse that should contain a user's appointments
     */
    public Call<AppointmentResponse> getAppointments(String bearer) {
        return service.getAppointments(EnviHandler.CIAM_BASE_URL + "api/appointments", BEARER + bearer);
    }

    /**
     * Create an Appointment
     *
     * @param bearer      the bearer token of the user who we want to create an appointment for
     * @param appointment the appointment we wish to create
     * @return Void
     */
    public Call<Void> createAppointment(String bearer, Appointment appointment) {
        return service.createAppointment(EnviHandler.SCHEDULING_BASE + "v1/visit",
                BEARER + bearer, appointment);
    }

    /**
     * Get the Location Suggestions when a user enters their location in the filter
     *
     * @param queryString the query of the user entering their location
     * @return List of Locations to suggest
     */
    public Call<List<LocationResponse>> getLocationSuggestions(String queryString) {
        return service.getLocationSuggestions(EnviHandler.S2_BASE_URL + "api/locationsuggestion",
                queryString);
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
        return service.getSearchSuggestions(EnviHandler.S2_BASE_URL + "api/suggestion",
                queryString,
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
        return service.getUserLocation(EnviHandler.S2_BASE_URL + "api/location/");
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

    /**
     * Get a detailed profile of a provider
     *
     * @param id the Provider ID
     * @return a More in-depth look of the provider
     */
    public Call<ProviderDetailsResponse> getProviderDetails(String id) {
        return service.getProviderDetails(EnviHandler.S2_BASE_URL + "api/providerdetails", id);
    }

    public Call<CreateAppointmentResponse> createAppointment(String bearerToken,
                                                             CreateAppointmentRequest request) {
        return service.createAppointment(EnviHandler.SCHEDULING_BASE + RESTConstants.SCHEDULING_VISIT,
                BEARER + bearerToken, request);
    }

    public Call<RegValidationResponse> getValidationRules(String scheduleId, String includeQuery) {
        return service.getValidationRules(EnviHandler.SCHEDULING_BASE + RESTConstants.SCHEDULING_VALIDATION,
                scheduleId, includeQuery);
    }

    public Call<ValidateEmailResponse> findEmail(String email) {
        return service.findEmail(EnviHandler.CIAM_BASE_URL + "api/users", email);
    }

    public Call<UpdateResponse> versionCheck() {
        return service.versionCheck(EnviHandler.VERSIONING_URL + "api/versioning/dependencies");
    }

//    /**
//     * @param grantType
//     * @param code
//     * @param clientId
//     * @param scope
//     * @param redirectUri
//     * @param codeUerifier
//     * @return
//     */
//    public Call<AccessTokenResponse> fetchAccessToken(String grantType,
//                                                      String code,
//                                                      String clientId,
//                                                      String scope,
//                                                      String redirectUri,
//                                                      String codeUerifier) {
//        return service.fetchAccessToken(EnviHandler.OKTA_BASE_URL + "oauth2/" + EnviHandler.AUTH_CLIENT_ID + "/v1/token",
//                grantType,
//                code,
//                clientId,
//                scope,
//                redirectUri,
//                codeUerifier);
//    }

//    public Call<RefreshAccessTokenResponse> refreshAccessToken(String grantType,
//                                                               String refreshToken,
//                                                               String clientId,
//                                                               String redirectUri) {
//        return service.refreshAccessToken(EnviHandler.OKTA_BASE_URL + "oauth2/" + EnviHandler.AUTH_CLIENT_ID + "/v1/token",
//                grantType,
//                refreshToken,
//                clientId,
//                redirectUri);
//    }


    public Call<SaveDoctorResponse> saveDoctor(String bearerToken, SaveDoctorRequest request) {
        return service.saveDoctor(EnviHandler.CIAM_BASE_URL + "api/users/me/favorite-providers",
                BEARER + bearerToken, request);
    }

    public Call<SaveDoctorResponse> deleteSavedDoctor(String bearerToken, String npi) {
        return service.deleteSavedDoctor(EnviHandler.CIAM_BASE_URL + "api/users/me/favorite-providers/" + npi, BEARER + bearerToken);
    }

    public Call<MySavedDoctorsResponse> getSavedDoctors(String bearerToken,
                                                        MySavedDoctorsRequest request) {
        return service.getSavedDocctors(EnviHandler.CIAM_BASE_URL + "api/users/query",
                BEARER + bearerToken, request);
    }

    public Call<MyAppointmentsResponse> getMyAppointments(String bearerToken,
                                                          MyAppointmentsRequest request) {
        return service.getMyAppointments(EnviHandler.CIAM_BASE_URL + "api/users/query",
                BEARER + bearerToken, request);
    }

    //1.2 APIs

    public Call<CommonResponse> changePassword(String bearerToken,
                                               ChangePasswordRequest request) {
        return service.changePassword(EnviHandler.CIAM_BASE_URL + "api/users/me/password",
                BEARER + bearerToken, request);
    }

    public Call<CommonResponse> changeSecurityQuestion(String bearerToken,
                                                       ChangeSesurityQuestionRequest request) {
        return service.changeSecurityQuestion(EnviHandler.CIAM_BASE_URL + "api/users/me/recovery/question",
                BEARER + bearerToken, request);
    }

    /**
     * Get a Profile
     *
     * @param bearer the bearer token of the user whose Profile we want
     * @return a Profile object of the user
     */
    public Call<ProfileGraphqlResponse> getProfile(String bearer) {
        return service.getUserProfile(EnviHandler.CIAM_BASE_URL + "api/users/query",
                BEARER + bearer, new MyProfileRequest());
    }

    /************** New Auth *************************************/

    /**
     * endpoint for SignIn
     *
     * @param request reuest body for SignIn
     * @return login response
     */
    public Call<SignInResponse> SignIn(SignInRequest request) {
        return service.SignIn(EnviHandler.CIAM_BASE_URL + "api/mobile/auth/sign-in", request);
    }


    /**
     * endpoint for SignIn Refresh
     *
     * @param request reuest body for SignIn Refresh
     * @return refresh response
     */
    public Call<RefreshResponse> SignInRefresh(RefreshRequest request, String bearerToken) {
        return service.SignInRefresh(EnviHandler.CIAM_BASE_URL + "api/mobile/auth/refresh",
                BEARER + bearerToken, request);
    }

    /**
     * endpoint for SignOut
     *
     * @param request reuest body for SignOut
     * @return Sign out response
     */
    public Call<CommonResponse> SignOut(SignOutRequest request, String bearerToken) {
        return service.SignOut(EnviHandler.CIAM_BASE_URL + "api/mobile/auth/sign-out",
                BEARER + bearerToken, request);
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
                isEmailTaken = false;
                if (view != null) {
                    view.setError(null);
                }
            }
        });
    }

    public interface ISessionExpiry {
        public void expired();
    }

    private boolean refreshToken() {
        try {
            retrofit2.Response<RefreshResponse> syncResp = NetworkManager.getInstance()
                    .SignInRefresh(new RefreshRequest(AuthManager.getInstance().getRefreshToken()),
                            AuthManager.getInstance().getBearerToken()).execute();
            if (syncResp.isSuccessful() && syncResp.body().getValid()) {
                System.out.println("REQ: syncResp" + syncResp.body().toString());
                AuthManager.getInstance().setBearerToken(syncResp.body().getResult().getAccessToken());
                AuthManager.getInstance().setRefreshToken(syncResp.body().getResult().getRefreshToken());
                return true;
            } else {
                //TODO:Chandra Login on refresh fail
                    /*need handler to post to main thread as it will be updating mybag count*/
                return false;
            }
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    //1.1

    public void getSavedDoctors() {
        NetworkManager.getInstance().getSavedDoctors(AuthManager.getInstance().getBearerToken(),
                new MySavedDoctorsRequest()).enqueue(new Callback<MySavedDoctorsResponse>() {
            @Override
            public void onResponse(Call<MySavedDoctorsResponse> call, retrofit2.Response<MySavedDoctorsResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        ProfileManager.setFavoriteProviders(response.body().getData().getUser().getFavoriteProviders());
                    } catch (NullPointerException ex) {
                        Timber.e("Error onResponse SavedDoctors ");
                        ProfileManager.setFavoriteProviders(null);
                    }
                } else {
                    Timber.e("Error onResponse SavedDoctors with error code");
                    ProfileManager.setFavoriteProviders(null);
                }
            }

            @Override
            public void onFailure(Call<MySavedDoctorsResponse> call, Throwable t) {
                Timber.e("Error onFailure SavedDoctors");
                ProfileManager.setFavoriteProviders(null);
            }
        });
    }

    public void updateFavDoctor(boolean isSave, final String npi, ImageView favProvider,
                                final ProviderResponse provider, final boolean isList,
                                Context context) {

        if (!ConnectionUtil.isConnected(context)) {
            Toast.makeText(context, R.string.no_network_msg,
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (isSave) {
            CommonUtil.updateFavView(isSave, favProvider);
            final SaveDoctorRequest request = new SaveDoctorRequest(npi);
            NetworkManager.getInstance().saveDoctor(AuthManager.getInstance().getBearerToken(),
                    request).enqueue(new Callback<SaveDoctorResponse>() {
                @Override
                public void onResponse(Call<SaveDoctorResponse> call, retrofit2.Response<SaveDoctorResponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            List<ProviderResponse> providerList = ProfileManager.getFavoriteProviders();
                            if (null != provider && !isProviderFound(provider.getNpi())) {
                                Map<String, Object> tealiumData = new HashMap<>();
                                tealiumData.put(Constants.FAVORITE_PROVIDER_NPI, npi);
                                TealiumUtil.trackEvent(Constants.FAVORITE_PROVIDER_EVENT, tealiumData);
                                if (null == providerList)
                                    providerList = new ArrayList<>();

                                providerList.add(0, provider);
                                ProfileManager.setFavoriteProviders(providerList);
                            }
                        } catch (NullPointerException ex) {
                        }
                    }
                }

                @Override
                public void onFailure(Call<SaveDoctorResponse> call, Throwable t) {

                }
            });
        } else { //DELETE saved Doc
            Map<String, Object> tealiumData = new HashMap<>();
            tealiumData.put(Constants.FAVORITE_PROVIDER_NPI, npi);
            TealiumUtil.trackEvent(Constants.UNFAVORITE_PROVIDER_EVENT, tealiumData);  //TODO - KEVIN: Should this be in the onResponse/ response.isSuccessful network call???

            if (!isList)
                CommonUtil.updateFavView(isSave, favProvider);
            NetworkManager.getInstance().deleteSavedDoctor(AuthManager.getInstance().getBearerToken(),
                    npi).enqueue(new Callback<SaveDoctorResponse>() {
                @Override
                public void onResponse(Call<SaveDoctorResponse> call, retrofit2.Response<SaveDoctorResponse> response) {
                    if (response.isSuccessful()) {
                        deleteSavedDocotor(npi);
                    }
                }

                @Override
                public void onFailure(Call<SaveDoctorResponse> call, Throwable t) {

                }
            });
        }
    }

    public void deleteSavedDocotor(String npi) {
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
                    }
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<MyAppointmentsResponse> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
            }
        });
    }
}
