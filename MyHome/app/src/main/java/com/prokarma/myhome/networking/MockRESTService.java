package com.prokarma.myhome.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prokarma.myhome.entities.Tos;
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
import com.prokarma.myhome.features.update.UpdateResponse;
import com.prokarma.myhome.utils.CommonUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;
import timber.log.Timber;

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
        try {
            String json = CommonUtil.readJsonFile(getClass().getClassLoader(), "login.json");

            Gson gson = new GsonBuilder().create();
            SignInResponse signInResponse = gson.fromJson(json, SignInResponse.class);
            return delegate.returningResponse(signInResponse).signIn(url, request);
        } catch (Exception e) {
            Timber.e("Failed to mock " + url);
            Timber.e(e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Call<SignInResponse> signInRefresh(String url, RefreshRequest request) {
        try {
            String json = CommonUtil.readJsonFile(getClass().getClassLoader(), "refresh.json");

            Gson gson = new GsonBuilder().create();
            SignInResponse signInResponse = gson.fromJson(json, SignInResponse.class);
            return delegate.returningResponse(signInResponse).signInRefresh(url, request);
        } catch (Exception e) {
            Timber.e("Failed to mock " + url);
            Timber.e(e);
            e.printStackTrace();
        }

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
        try {
            String json = CommonUtil.readJsonFile(getClass().getClassLoader(), "getproviders.json");

            Gson gson = new GsonBuilder().create();
            ProvidersResponse providersResponse = gson.fromJson(json, ProvidersResponse.class);
            return delegate.returningResponse(providersResponse).getProviders(url, queryString, lat, lon, displayName, zipCode, page, pageSize, distance, sortBy, gender, languages, specialties, facilities, practices, patients);
        } catch (Exception e) {
            Timber.e("Failed to mock " + url);
            Timber.e(e);
            e.printStackTrace();
        }

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
        try {
            String json = CommonUtil.readJsonFile(getClass().getClassLoader(), "versioning.json");

            Gson gson = new GsonBuilder().create();
            UpdateResponse updateResponse = gson.fromJson(json, UpdateResponse.class);
            return delegate.returningResponse(updateResponse).versionCheck(url);
        } catch (Exception e) {
            Timber.e("Failed to mock " + url);
            Timber.e(e);
            e.printStackTrace();
        }

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
        try {
            String json = CommonUtil.readJsonFile(getClass().getClassLoader(), "save_doctor.json");

            Gson gson = new GsonBuilder().create();
            MySavedDoctorsResponse mySavedDoctorsResponse = gson.fromJson(json, MySavedDoctorsResponse.class);
            return delegate.returningResponse(mySavedDoctorsResponse).getSavedDoctors(url, bearer, request);
        } catch (Exception e) {
            Timber.e("Failed to mock " + url);
            Timber.e(e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Call<MyAppointmentsResponse> getMyAppointments(String url, String bearer, MyAppointmentsRequest request) {
        try {
            String json = CommonUtil.readJsonFile(getClass().getClassLoader(), "myappointments_empty.json");

            Gson gson = new GsonBuilder().create();
            MyAppointmentsResponse myAppointmentsResponse = gson.fromJson(json, MyAppointmentsResponse.class);
            return delegate.returningResponse(myAppointmentsResponse).getMyAppointments(url, bearer, request);
        } catch (Exception e) {
            Timber.e("Failed to mock " + url);
            Timber.e(e);
            e.printStackTrace();
        }

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
        try {
            String json = CommonUtil.readJsonFile(getClass().getClassLoader(), "providerdetails_tonychang.json");

            Gson gson = new GsonBuilder().create();
            ProviderDetailsResponse providerDetailsResponse = gson.fromJson(json, ProviderDetailsResponse.class);
            return delegate.returningResponse(providerDetailsResponse).getNewProviderDetails(url, id);
        } catch (Exception e) {
            Timber.e("Failed to mock " + url);
            Timber.e(e);
            e.printStackTrace();
        }

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
