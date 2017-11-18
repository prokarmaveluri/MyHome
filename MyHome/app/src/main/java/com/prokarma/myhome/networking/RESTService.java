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
import com.prokarma.myhome.features.settings.ChangeSecurityQuestionRequest;
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
    String HEADER_AUTHORIZATION = "Authorization";
    String QUERY_QUERY = "query";
    String QUERY_LATITUDE = "latitude";
    String QUERY_LONGITUDE = "longitude";
    String QUERY_DISPLAY_NAME = "displayName";
    String QUERY_ZIP_CODE = "zipCode";
    String QUERY_PAGE = "page";
    String QUERY_PAGE_SIZE = "pageSize";
    String QUERY_DISTANCE = "distance";
    String QUERY_SORT_BY = "sortby";
    String QUERY_GENDER = "gender";
    String QUERY_LANGUAGES = "languages";
    String QUERY_SPECIALTIES = "specialties";
    String QUERY_FACILITIES = "facilities";
    String QUERY_PRACTICES = "practices";
    String QUERY_PATIENTS = "patients";
    String QUERY_PROVIDER_ID = "providerid";
    String QUERY_INCLUDE = "include";
    String QUERY_EMAIL = "email";
    String QUERY_NPIS = "npis";
    String QUERY_FROM = "from";
    String QUERY_TO = "to";
    String QUERY_ADDRESSES = "addresses";

    @POST
    Call<SignInResponse> signIn(@Url String url, @Body SignInRequest request);

    @POST
    Call<SignInResponse> signInRefresh(@Url String url, @Body RefreshRequest request);

    @POST
    Call<CommonResponse> signOut(@Url String url, @Header(HEADER_AUTHORIZATION) String bearer,
                                 @Body SignOutRequest request);

    @POST
    Call<Void> register(@Url String url, @Body EnrollmentRequest request);

    @PATCH
    Call<Void> updateProfile(@Url String url, @Header(HEADER_AUTHORIZATION) String bearer,
                             @Body Profile updatedProfileData);

    @POST
    Call<ForgotPasswordResponse> forgotPassword(@Url String url, @Body ForgotPasswordRequest request);

    @GET
    Call<Tos> getTos(@Url String url, @Header(HEADER_AUTHORIZATION) String bearer);

    @POST
    Call<Tos> acceptTos(@Url String url, @Header(HEADER_AUTHORIZATION) String bearer);

    @GET
    Call<List<LocationResponse>> getLocationSuggestions(@Url String url,
                                                        @Query(QUERY_QUERY) String queryString);

    @GET
    Call<List<SearchSuggestionResponse>> getSearchSuggestions(@Url String url,
                                                              @Query(QUERY_QUERY) String queryString,
                                                              @Query(QUERY_LATITUDE) String lat,
                                                              @Query(QUERY_LONGITUDE) String lon,
                                                              @Query(QUERY_DISPLAY_NAME) String displayName,
                                                              @Query(QUERY_ZIP_CODE) String zipCode);

    @GET
    Call<LocationResponse> getUserLocation(@Url String url);

    @GET
    Call<ProvidersResponse> getProviders(@Url String url,
                                         @Query(QUERY_QUERY) String queryString,
                                         @Query(QUERY_LATITUDE) String lat,
                                         @Query(QUERY_LONGITUDE) String lon,
                                         @Query(QUERY_DISPLAY_NAME) String displayName,
                                         @Query(QUERY_ZIP_CODE) String zipCode,
                                         @Query(QUERY_PAGE) String page,
                                         @Query(QUERY_PAGE_SIZE) String pageSize,
                                         @Query(QUERY_DISTANCE) String distance,
                                         @Query(QUERY_SORT_BY) String sortBy,
                                         @Query(QUERY_GENDER) String gender,
                                         @Query(QUERY_LANGUAGES) String languages,
                                         @Query(QUERY_SPECIALTIES) String specialties,
                                         @Query(QUERY_FACILITIES) String facilities,
                                         @Query(QUERY_PRACTICES) String practices,
                                         @Query(QUERY_PATIENTS) String patients);

    @GET
    Call<ProviderDetailsResponse> getProviderDetails(@Url String url,
                                                     @Query(QUERY_PROVIDER_ID) String id);

    @POST
    Call<CreateAppointmentResponse> createAppointment(@Url String url,
                                                      @Header(HEADER_AUTHORIZATION) String bearer,
                                                      @Body CreateAppointmentRequest appointment);

    @GET
    Call<RegValidationResponse> getValidationRules(@Url String url,
                                                   @Query(QUERY_INCLUDE) String include);

    @GET
    Call<ValidateEmailResponse> findEmail(@Url String url,
                                          @Query(QUERY_EMAIL) String email);

    @GET
    Call<UpdateResponse> versionCheck(@Url String url);

    @POST
    Call<SaveDoctorResponse> saveDoctor(@Url String url,
                                        @Header(HEADER_AUTHORIZATION) String bearer,
                                        @Body SaveDoctorRequest resuest);

    @DELETE
    Call<SaveDoctorResponse> deleteSavedDoctor(@Url String url,
                                               @Header(HEADER_AUTHORIZATION) String bearer);

    @POST
    Call<MySavedDoctorsResponse> getSavedDoctors(@Url String url,
                                                 @Header(HEADER_AUTHORIZATION) String bearer,
                                                 @Body MySavedDoctorsRequest request);

    @POST
    Call<MyAppointmentsResponse> getMyAppointments(@Url String url,
                                                   @Header(HEADER_AUTHORIZATION) String bearer,
                                                   @Body MyAppointmentsRequest request);

    @POST
    Call<CommonResponse> changePassword(@Url String url,
                                        @Header(HEADER_AUTHORIZATION) String bearer,
                                        @Body ChangePasswordRequest request);

    @PATCH
    Call<CommonResponse> changeSecurityQuestion(
            @Url String url,
            @Header(HEADER_AUTHORIZATION) String bearer,
            @Body ChangeSecurityQuestionRequest request);

    @POST
    Call<ProfileGraphqlResponse> getUserProfile(@Url String url,
                                                @Header(HEADER_AUTHORIZATION) String bearer,
                                                @Body MyProfileRequest request);

    @POST
    Call<CommonResponse> resendEmail(@Url String url, @Header(HEADER_AUTHORIZATION) String bearer);

    @GET
    Call<ProviderDetails> getNewProviderDetails(@Url String url,
                                                @Query(QUERY_NPIS) String id);

    @GET
    Call<AppointmentTimeSlots> getProviderAppointments(@Url String url,
                                                       @Query(QUERY_FROM) String fromDate,
                                                       @Query(QUERY_TO) String toDate);

    @GET
    Call<AppointmentTimeSlots> getProviderAppointments(@Url String url,
                                                       @Query(QUERY_FROM) String fromDate,
                                                       @Query(QUERY_TO) String toDate,
                                                       @Query(QUERY_ADDRESSES) String addressHash);
}
