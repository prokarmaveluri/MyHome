package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.enrollment.EnrollmentRequest;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by cmajji on 4/26/17.
 */

public interface RESTService {

    @POST("api/users/enrollment")
    Call<Object> register(EnrollmentRequest request);
}
