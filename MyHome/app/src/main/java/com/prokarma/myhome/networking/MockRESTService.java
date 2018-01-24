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
import com.prokarma.myhome.features.login.endpoint.AmWellResponse;
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
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by kwelsh on 1/22/18.
 */

public class MockRESTService implements RESTService {
    private final BehaviorDelegate<RESTService> delegate;

    public MockRESTService(BehaviorDelegate<RESTService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<SignInResponse> signIn(String url, SignInRequest request) {
        //return delegate.returningResponse(signInResponse);
        return null;
    }

    @Override
    public Call<SignInResponse> signInRefresh(String url, RefreshRequest request) {
        return null;
    }

    @Override
    public Call<CommonResponse> signOut(String url, String bearer, SignOutRequest request) {
        return null;
    }

    @Override
    public Call<AmWellResponse> getAmWellToken(String url, String bearer) {
        return null;
    }

    @Override
    public Call<Void> register(String url, EnrollmentRequest request) {
        return null;
    }

    @Override
    public Call<Void> updateProfile(String url, String bearer, Profile updatedProfileData) {
        return null;
    }

    @Override
    public Call<ForgotPasswordResponse> forgotPassword(String url, ForgotPasswordRequest request) {
        return null;
    }

    @Override
    public Call<Tos> getTos(String url, String bearer) {
        return null;
    }

    @Override
    public Call<Tos> acceptTos(String url, String bearer) {
        return null;
    }

    @Override
    public Call<List<LocationResponse>> getLocationSuggestions(String url, String queryString) {
        return null;
    }

    @Override
    public Call<List<SearchSuggestionResponse>> getSearchSuggestions(String url, String queryString, String lat, String lon, String displayName, String zipCode) {
        return null;
    }

    @Override
    public Call<LocationResponse> getUserLocation(String url) {
        return null;
    }

    @Override
    public Call<ProvidersResponse> getProviders(String url, String queryString, String lat, String lon, String displayName, String zipCode, String page, String pageSize, String distance, String sortBy, String gender, String languages, String specialties, String facilities, String practices, String patients) {
        //return delegate.returningResponse(signInResponse);
        return null;
    }

    @Override
    public Call<ProviderDetailsResponse> getProviderDetails(String url, String id) {
        return null;
    }

    @Override
    public Call<CreateAppointmentResponse> createAppointment(String url, String bearer, CreateAppointmentRequest appointment) {
        return null;
    }

    @Override
    public Call<RegValidationResponse> getValidationRules(String url, String include) {
        return null;
    }

    @Override
    public Call<ValidateEmailResponse> findEmail(String url, String email) {
        return null;
    }

    @Override
    public Call<UpdateResponse> versionCheck(String url) {
        return null;
    }

    @Override
    public Call<SaveDoctorResponse> saveDoctor(String url, String bearer, SaveDoctorRequest resuest) {
        return null;
    }

    @Override
    public Call<SaveDoctorResponse> deleteSavedDoctor(String url, String bearer) {
        return null;
    }

    @Override
    public Call<MySavedDoctorsResponse> getSavedDoctors(String url, String bearer, MySavedDoctorsRequest request) {
        return null;
    }

    @Override
    public Call<MyAppointmentsResponse> getMyAppointments(String url, String bearer, MyAppointmentsRequest request) {
        return null;
    }

    @Override
    public Call<CommonResponse> changePassword(String url, String bearer, ChangePasswordRequest request) {
        return null;
    }

    @Override
    public Call<CommonResponse> changeSecurityQuestion(String url, String bearer, ChangeSecurityQuestionRequest request) {
        return null;
    }

    @Override
    public Call<ProfileGraphqlResponse> getUserProfile(String url, String bearer, MyProfileRequest request) {
        return null;
    }

    @Override
    public Call<CommonResponse> resendEmail(String url, String bearer) {
        return null;
    }

    @Override
    public Call<ProviderDetails> getNewProviderDetails(String url, String id) {
        return null;
    }

    @Override
    public Call<AppointmentTimeSlots> getProviderAppointments(String url, String fromDate, String toDate) {
        return null;
    }

    @Override
    public Call<AppointmentTimeSlots> getProviderAppointments(String url, String fromDate, String toDate, String addressHash) {
        return null;
    }
}
