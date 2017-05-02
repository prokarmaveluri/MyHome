package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordRequest;
import com.dignityhealth.myhome.features.login.forgot.password.ForgotPasswordResponse;
import com.dignityhealth.myhome.features.profile.ProfileResponse;
import com.dignityhealth.myhome.features.login.LoginRequest;
import com.dignityhealth.myhome.features.login.LoginResponse;
import com.dignityhealth.myhome.utils.RESTConstants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by kwelsh on 4/28/17.
 */

public interface RESTService {

    @POST(RESTConstants.CIAM_BASE_URL + "api/users/enrollment")
    Call<Void> register(@Body EnrollmentRequest request);

    @GET(RESTConstants.CIAM_BASE_URL + "api/users/me")
    Call<ProfileResponse> profile(@Header("Authorization") String bearer);

    @POST(RESTConstants.OKTA_BASE_URL + "api/v1/authn")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST(RESTConstants.OKTA_BASE_URL + "api/v1/authn/recovery/password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);
}
