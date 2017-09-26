package com.prokarma.myhome.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prokarma.myhome.networking.NetworkError;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by kwelsh on 9/26/17.
 */

public class ErrorUtil {

    public static NetworkError parseError(Response<?> response) {
        NetworkError error;
        Gson gson = new GsonBuilder().create();

        try {
            error = gson.fromJson(response.errorBody().string(), NetworkError.class);
        } catch (IOException e) {
            return new NetworkError();
        }

        return error;
    }
}
