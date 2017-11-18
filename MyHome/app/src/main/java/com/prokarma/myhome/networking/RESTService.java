package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.appointments.MyAppointmentsRequest;
import com.prokarma.myhome.features.appointments.MyAppointmentsResponse;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.features.enrollment.ValidateEmailResponse;
import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetails;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentRequest;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.CreateAppointmentResponse;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.prokarma.myhome.features.login.endpoint.RefreshRequest;
import com.prokarma.myhome.features.login.endpoint.SignInRequest;
import com.prokarma.myhome.features.login.endpoint.SignInResponse;
import com.prokarma.myhome.features.login.endpoint.SignOutRequest;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.prokarma.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.prokarma.myhome.features.preferences.MySavedDoctorsRequest;
import com.prokarma.myhome.features.preferences.MySavedDoctorsResponse;
import com.prokarma.myhome.features.preferences.SaveDoctorRequest;
import com.prokarma.myhome.features.preferences.SaveDoctorResponse;
import com.prokarma.myhome.features.profile.MyProfileRequest;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileGraphqlResponse;
import com.prokarma.myhome.features.settings.ChangePasswordRequest;
import com.prokarma.myhome.features.settings.ChangeSesurityQuestionRequest;
import com.prokarma.myhome.features.settings.CommonResponse;
import com.prokarma.myhome.features.tos.Tos;
import com.prokarma.myhome.features.update.UpdateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by kwelsh on 4/28/17.
 */

public interface RESTService {

    @POST
    Call<SignInResponse> signIn(@Url String url, @Body SignInRequest request);

    @POST
    Call<SignInResponse> signInRefresh(@Url String url, @Body RefreshRequest request);

    @POST
    Call<CommonResponse> signOut(@Url String url, @Header("Authorization") String bearer,
                                 @Body SignOutRequest request);

    @POST
    Call<Void> register(@Url String url, @Body EnrollmentRequest request);

    @PATCH
    Call<Void> updateProfile(@Url String url, @Header("Authorization") String bearer,
                             @Body Profile updatedProfileData);

    @POST
    Call<ForgotPasswordResponse> forgotPassword(@Url String url, @Body ForgotPasswordRequest request);

    @GET
    Call<Tos> getTos(@Url String url, @Header("Authorization") String bearer);

    @POST
    Call<Tos> acceptTos(@Url String url, @Header("Authorization") String bearer);

    @GET
    Call<List<LocationResponse>> getLocationSuggestions(@Url String url,
                                                        @Query("query") String queryString);

    @GET
    Call<List<SearchSuggestionResponse>> getSearchSuggestions(@Url String url,
                                                              @Query("query") String queryString,
                                                              @Query("latitude") String lat,
                                                              @Query("longitude") String lon,
                                                              @Query("displayName") String displayName,
                                                              @Query("zipCode") String zipCode);

    @GET
    Call<LocationResponse> getUserLocation(@Url String url);

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

    @GET
    Call<ProviderDetailsResponse> getProviderDetails(@Url String url,
                                                     @Query("providerid") String id);

    @POST
    Call<CreateAppointmentResponse> createAppointment(@Url String url,
                                                      @Header("Authorization") String bearer,
                                                      @Body CreateAppointmentRequest appointment);

    @GET
    Call<RegValidationResponse> getValidationRules(@Url String url,
                                                   @Query("include") String include);

    @GET
    Call<ValidateEmailResponse> findEmail(@Url String url,
                                          @Query("email") String email);

    @GET
    Call<UpdateResponse> versionCheck(@Url String url);

    @POST
    Call<SaveDoctorResponse> saveDoctor(@Url String url,
                                        @Header("Authorization") String bearer,
                                        @Body SaveDoctorRequest resuest);

    @DELETE
    Call<SaveDoctorResponse> deleteSavedDoctor(@Url String url,
                                               @Header("Authorization") String bearer);

    @POST
    Call<MySavedDoctorsResponse> getSavedDocctors(@Url String url,
                                                  @Header("Authorization") String bearer,
                                                  @Body MySavedDoctorsRequest request);

    @POST
    Call<MyAppointmentsResponse> getMyAppointments(@Url String url,
                                                   @Header("Authorization") String bearer,
                                                   @Body MyAppointmentsRequest request);

    @POST
    Call<CommonResponse> changePassword(@Url String url,
                                        @Header("Authorization") String bearer,
                                        @Body ChangePasswordRequest request);

    @PATCH
    Call<CommonResponse> changeSecurityQuestion(
            @Url String url,
            @Header("Authorization") String bearer,
            @Body ChangeSesurityQuestionRequest request);

    @POST
    Call<ProfileGraphqlResponse> getUserProfile(@Url String url,
                                                @Header("Authorization") String bearer,
                                                @Body MyProfileRequest request);

    @POST
    Call<CommonResponse> resendEmail(@Url String url, @Header("Authorization") String bearer);

    @GET
    Call<ProviderDetails> getNewProviderDetails(@Url String url,
                                                @Query("npis") String id);

    @GET
    Call<AppointmentTimeSlots> getProviderAppointments(@Url String url,
                                                       @Query("from") String fromDate,
                                                       @Query("to") String toDate);

    @GET
    Call<AppointmentTimeSlots> getProviderAppointments(@Url String url,
                                                       @Query("from") String fromDate,
                                                       @Query("to") String toDate,
                                                       @Query("addresses") String addressHash);
}
