package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.AppointmentResponse;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.features.enrollment.ValidateEmailResponse;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.prokarma.myhome.features.login.AccessTokenResponse;
import com.prokarma.myhome.features.login.LoginRequest;
import com.prokarma.myhome.features.login.LoginResponse;
import com.prokarma.myhome.features.login.RefreshAccessTokenResponse;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileResponse;
import com.prokarma.myhome.features.profile.signout.CreateSessionResponse;
import com.prokarma.myhome.features.tos.Tos;
import com.prokarma.myhome.features.update.UpdateResponse;
import com.prokarma.myhome.utils.RESTConstants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by kwelsh on 4/28/17.
 */

public interface RESTService {

    String PROVIDER_QUERY = "{queryString}&latitude={lat}&longitude={lon}" +
            "&displayName={displayName}&zipCode={zipCode}&page=1&pageSize=20";


    @POST(RESTConstants.CIAM_BASE_URL + "api/users/enrollment")
    Call<Void> register(@Body EnrollmentRequest request);

    @GET(RESTConstants.CIAM_BASE_URL + "api/users/me")
    Call<ProfileResponse> getProfile(@Header("Authorization") String bearer);

    @PATCH(RESTConstants.CIAM_BASE_URL + "api/users/me")
    Call<Void> updateProfile(@Header("Authorization") String bearer, @Body Profile updatedProfileData);

    @POST(RESTConstants.OKTA_BASE_URL + "api/v1/authn")
    Call<LoginResponse> login(@Body LoginRequest request);

    @FormUrlEncoded
    @POST(RESTConstants.OKTA_BASE_URL + "oauth2/aus32qsj5x26YmQN11t7/v1/token")
    Call<AccessTokenResponse> fetchAccessToken(@Field("grant_type") String grantType,
                                               @Field("code") String code,
                                               @Field("client_id") String clientId,
                                               @Field("scope") String scope,
                                               @Field("redirect_uri") String redirectUri,
                                               @Field("code_verifier") String codeUerifier
    );

    @FormUrlEncoded
    @POST(RESTConstants.OKTA_BASE_URL + "oauth2/aus32qsj5x26YmQN11t7/v1/token")
    Call<RefreshAccessTokenResponse> refreshAccessToken(@Field("grant_type") String grantType,
                                                        @Field("refresh_token") String refreshToken,
                                                        @Field("client_id") String clientId,
                                                        @Field("redirect_uri") String redirectUri
    );

    @POST(RESTConstants.OKTA_BASE_URL + "api/v1/authn/recovery/password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @GET(RESTConstants.OKTA_BASE_URL + "api/v1/sessions/me")
    Call<CreateSessionResponse> createSession(@Header("Cookie") String sid);

    @DELETE(RESTConstants.OKTA_BASE_URL + "api/v1/sessions/me")
    Call<Void> logout(@Header("Cookie") String sid);

    @GET(RESTConstants.CIAM_BASE_URL + "api/terms-and-conditions")
    Call<Tos> getTos(@Header("Authorization") String bearer);

    @GET(RESTConstants.CIAM_BASE_URL + "api/appointments")
    Call<AppointmentResponse> getAppointments(@Header("Authorization") String bearer);

    @POST(RESTConstants.SCHEDULING_BASE + "v1/visit")
    Call<Void> createAppointment(@Header("Authorization") String bearer, @Body Appointment appointment);

    @GET(RESTConstants.S2_BASE_URL + "api/locationsuggestion")
    Call<List<LocationResponse>> getLocationSuggestions(@Query("query") String queryString);

    @GET(RESTConstants.S2_BASE_URL + "api/suggestion")
    Call<List<SearchSuggestionResponse>> getSearchSuggestions(@Query("query") String queryString,
                                                              @Query("latitude") String lat,
                                                              @Query("longitude") String lon,
                                                              @Query("displayName") String displayName,
                                                              @Query("zipCode") String zipCode);

    @GET(RESTConstants.S2_BASE_URL + "api/location/")
    Call<LocationResponse> getUserLocation();

    @GET(RESTConstants.S2_BASE_URL + "api/providers")
    Call<ProvidersResponse> getProviders(@Query("query") String queryString,
                                         @Query("latitude") String lat,
                                         @Query("longitude") String lon,
                                         @Query("displayName") String displayName,
                                         @Query("zipCode") String zipCode,
                                         @Query("page") String page,
                                         @Query("pageSize") String pageSize,
                                         @Query("distance") String distance,
                                         @Query("sortby") String sortBy,
                                         @Query("gender") String gender,
                                         @Query("languages") String languages,
                                         @Query("specialties") String specialties,
                                         @Query("facilities") String facilities,
                                         @Query("practices") String practices,
                                         @Query("patients") String patients);

    @GET(RESTConstants.S2_BASE_URL + "api/providerdetails")
    Call<ProviderDetailsResponse> getProviderDetails(@Query("providerid") String id);

    @POST(RESTConstants.SCHEDULING_BASE + RESTConstants.SCHEDULING_VISIT)
    Call<CreateAppointmentResponse> createAppointment(@Header("Authorization") String bearer,
                                                      @Body CreateAppointmentRequest appointment);

    // include = insurance,schedule-properties
    @GET(RESTConstants.SCHEDULING_BASE + RESTConstants.SCHEDULING_VALIDATION)
    Call<RegValidationResponse> getValidationRules(@Path("scheduleID") String scheduleId,
                                                   @Query("include") String include);

    @GET(RESTConstants.CIAM_BASE_URL + "api/users")
    Call<ValidateEmailResponse> findEmail(@Query("email") String email);

    @GET(RESTConstants.VERSIONING_TST_URL + "api/versioning/dependencies")
    Call<UpdateResponse> versionCheck();
}
