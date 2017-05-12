package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.features.fad.LocationSuggestionsResponse;
import com.dignityhealth.myhome.features.fad.ProvidersResponse;
import com.dignityhealth.myhome.features.login.LoginRequest;
import com.dignityhealth.myhome.features.login.LoginResponse;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionRequest;
import com.dignityhealth.myhome.features.profile.signout.CreateSessionResponse;
import com.dignityhealth.myhome.features.tos.Tos;
import com.dignityhealth.myhome.utils.RESTConstants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    Call<Profile> getProfile(@Header("Authorization") String bearer);

    @PATCH(RESTConstants.CIAM_BASE_URL + "api/users/me")
    Call<Void> updateProfile(@Header("Authorization") String bearer, @Body Profile updatedProfileData);

    @POST(RESTConstants.OKTA_BASE_URL + "api/v1/authn")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST(RESTConstants.OKTA_BASE_URL + "api/v1/authn/recovery/password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST(RESTConstants.OKTA_BASE_URL + "api/v1/sessions?additionalFields=cookieToken")
    Call<CreateSessionResponse> createSession(@Body CreateSessionRequest request);

    @DELETE(RESTConstants.OKTA_BASE_URL + "api/v1/sessions/{id}")
    Call<Void> logout(@Header("Authorization") String auth, @Path("id") String id);

    @GET(RESTConstants.CIAM_BASE_URL + "api/users/me")
    Call<Tos> getTos(@Header("Authorization") String bearer);

    @GET(RESTConstants.S2_BASE_URL + "api/locationsuggestion/?query={queryString}")
    Call<List<LocationSuggestionsResponse>> getLocationSuggestions(@Path("queryString") String queryString);

    @GET(RESTConstants.S2_BASE_URL + "api/providers")
    Call<ProvidersResponse> getProviders(@Query("query") String queryString,
                                         @Query("latitude") String lat,
                                         @Query("longitude") String lon,
                                         @Query("displayName") String displayName,
                                         @Query("zipCode") String zipCode);
}
