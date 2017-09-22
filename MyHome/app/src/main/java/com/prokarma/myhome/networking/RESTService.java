package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.features.appointments.AppointmentResponse;
import com.prokarma.myhome.features.appointments.MyAppointmentsRequest;
import com.prokarma.myhome.features.appointments.MyAppointmentsResponse;
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
import com.prokarma.myhome.features.preferences.MySavedDoctorsRequest;
import com.prokarma.myhome.features.preferences.MySavedDoctorsResponse;
import com.prokarma.myhome.features.preferences.SaveDoctorRequest;
import com.prokarma.myhome.features.preferences.SaveDoctorResponse;
import com.prokarma.myhome.features.profile.MyProfileRequest;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.features.profile.signout.CreateSessionResponse;
import com.prokarma.myhome.features.settings.ChangePasswordRequest;
import com.prokarma.myhome.features.settings.ChangeSesurityQuestionRequest;
import com.prokarma.myhome.features.settings.CommonResponse;
import com.prokarma.myhome.features.tos.Tos;
import com.prokarma.myhome.features.update.UpdateResponse;

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
import retrofit2.http.Url;

/**
 * Created by kwelsh on 4/28/17.
 */

public interface RESTService {

    String PROVIDER_QUERY = "{queryString}&latitude={lat}&longitude={lon}" +
            "&displayName={displayName}&zipCode={zipCode}&page=1&pageSize=20";

    //    @POST(EnviHandler.CIAM_BASE_URL + "api/users/enrollment")
    @POST
    Call<Void> register(@Url String url, @Body EnrollmentRequest request);

//    @GET(EnviHandler.CIAM_BASE_URL + "api/users/me")
//    Call<ProfileResponse> getProfile(@Header("Authorization") String bearer);

    //    @PATCH(EnviHandler.CIAM_BASE_URL + "api/users/me")
    @PATCH
    Call<Void> updateProfile(@Url String url, @Header("Authorization") String bearer,
                             @Body Profile updatedProfileData);

    //    @POST(EnviHandler.OKTA_BASE_URL + "api/v1/authn")
    @POST
    Call<LoginResponse> login(@Url String url, @Body LoginRequest request);

    @FormUrlEncoded
//    @POST(EnviHandler.OKTA_BASE_URL + "oauth2/" + EnviHandler.AUTH_CLIENT_ID + "/v1/token")
    @POST
    Call<AccessTokenResponse> fetchAccessToken(@Url String url,
                                               @Field("grant_type") String grantType,
                                               @Field("code") String code,
                                               @Field("client_id") String clientId,
                                               @Field("scope") String scope,
                                               @Field("redirect_uri") String redirectUri,
                                               @Field("code_verifier") String codeUerifier
    );

    @FormUrlEncoded
//    @POST(EnviHandler.OKTA_BASE_URL + "oauth2/" + EnviHandler.AUTH_CLIENT_ID + "/v1/token")
    @POST
    Call<RefreshAccessTokenResponse> refreshAccessToken(@Url String url,
                                                        @Field("grant_type") String grantType,
                                                        @Field("refresh_token") String refreshToken,
                                                        @Field("client_id") String clientId,
                                                        @Field("redirect_uri") String redirectUri
    );

    //    @POST(EnviHandler.OKTA_BASE_URL + "api/v1/authn/recovery/password")
    @POST
    Call<ForgotPasswordResponse> forgotPassword(@Url String url, @Body ForgotPasswordRequest request);

    //    @GET(EnviHandler.OKTA_BASE_URL + "api/v1/sessions/me")
    @GET
    Call<CreateSessionResponse> createSession(@Url String url, @Header("Cookie") String sid);

    //    @DELETE(EnviHandler.OKTA_BASE_URL + "api/v1/sessions/me")
    @DELETE
    Call<Void> logout(@Url String url, @Header("Cookie") String sid);

    //    @GET(EnviHandler.CIAM_BASE_URL + "api/terms-and-conditions")
    @GET
    Call<Tos> getTos(@Url String url, @Header("Authorization") String bearer);

    //    @GET(EnviHandler.CIAM_BASE_URL + "api/appointments")
    @GET
    Call<AppointmentResponse> getAppointments(@Url String url, @Header("Authorization") String bearer);

    //    @POST(EnviHandler.SCHEDULING_BASE + "v1/visit")
    @POST
    Call<Void> createAppointment(@Url String url, @Header("Authorization") String bearer,
                                 @Body Appointment appointment);

    //    @GET(EnviHandler.S2_BASE_URL + "api/locationsuggestion")
    @GET
    Call<List<LocationResponse>> getLocationSuggestions(@Url String url,
                                                        @Query("query") String queryString);

    //    @GET(EnviHandler.S2_BASE_URL + "api/suggestion")
    @GET
    Call<List<SearchSuggestionResponse>> getSearchSuggestions(@Url String url,
                                                              @Query("query") String queryString,
                                                              @Query("latitude") String lat,
                                                              @Query("longitude") String lon,
                                                              @Query("displayName") String displayName,
                                                              @Query("zipCode") String zipCode);

    //    @GET(EnviHandler.S2_BASE_URL + "api/location/")
    @GET
    Call<LocationResponse> getUserLocation(@Url String url);

    //    @GET(EnviHandler.S2_BASE_URL + "api/providers")
    @GET
    Call<ProvidersResponse> getProviders(@Url String url,
                                         @Query("query") String queryString,
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

    //    @GET(EnviHandler.S2_BASE_URL + "api/providerdetails")
    @GET
    Call<ProviderDetailsResponse> getProviderDetails(@Url String url,
                                                     @Query("providerid") String id);

    //    @POST(EnviHandler.SCHEDULING_BASE + RESTConstants.SCHEDULING_VISIT)
    @POST
    Call<CreateAppointmentResponse> createAppointment(@Url String url,
                                                      @Header("Authorization") String bearer,
                                                      @Body CreateAppointmentRequest appointment);

    // include = insurance,schedule-properties
//    @GET(EnviHandler.SCHEDULING_BASE + RESTConstants.SCHEDULING_VALIDATION)
    @GET
    Call<RegValidationResponse> getValidationRules(@Url String url,
                                                   @Path("scheduleID") String scheduleId,
                                                   @Query("include") String include);

    //    @GET(EnviHandler.CIAM_BASE_URL + "api/users")
    @GET
    Call<ValidateEmailResponse> findEmail(@Url String url,
                                          @Query("email") String email);

    //    @GET(EnviHandler.VERSIONING_URL + "api/versioning/dependencies")
    @GET
    Call<UpdateResponse> versionCheck(@Url String url);


    //1.1 APIs

    //    @POST(EnviHandler.CIAM_BASE_URL + "api/users/me/favorite-providers")
    @POST
    Call<SaveDoctorResponse> saveDoctor(@Url String url,
                                        @Header("Authorization") String bearer,
                                        @Body SaveDoctorRequest resuest);

    //    @DELETE(EnviHandler.CIAM_BASE_URL + "api/users/me/favorite-providers/{npi}")
    @DELETE
    Call<SaveDoctorResponse> deleteSavedDoctor(@Url String url,
                                               @Header("Authorization") String bearer);

    //    @POST(EnviHandler.CIAM_BASE_URL + "api/users/query")
    @POST
    Call<MySavedDoctorsResponse> getSavedDocctors(@Url String url,
                                                  @Header("Authorization") String bearer,
                                                  @Body MySavedDoctorsRequest request);

    //    @POST(EnviHandler.CIAM_BASE_URL + "api/users/query")
    @POST
    Call<MyAppointmentsResponse> getMyAppointments(@Url String url,
                                                   @Header("Authorization") String bearer,
                                                   @Body MyAppointmentsRequest request);

    //1.2 APIs

    //    @POST(EnviHandler.CIAM_BASE_URL + "api/users/me/password")
    @POST
    Call<CommonResponse> changePassword(@Url String url,
                                        @Header("Authorization") String bearer,
                                        @Body ChangePasswordRequest request);

    //    @PATCH(EnviHandler.CIAM_BASE_URL + "api/users/me/recovery/question")
    @PATCH
    Call<CommonResponse> changeSecurityQuestion(
            @Url String url,
            @Header("Authorization") String bearer,
            @Body ChangeSesurityQuestionRequest request);

    //    @POST(EnviHandler.CIAM_BASE_URL + "api/users/query")
    @POST
    Call<ProfileGraphqlResponse> getUserProfile(@Url String url,
                                                @Header("Authorization") String bearer,
                                                @Body MyProfileRequest request);
}
